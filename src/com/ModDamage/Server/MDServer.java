package com.ModDamage.Server;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import com.ModDamage.NanoHTTPD;

public class MDServer extends NanoHTTPD
{
	private MDServer(int port, String username, String password) throws IOException {
		super(port, null);
		authString = "Basic "+ DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
	}
	
	private static MDServer instance = null;
	
	static Executor executor = Executors.newCachedThreadPool();
	
	private Map<Pattern, WebHandler> handlers = new HashMap<Pattern, WebHandler>();
	
	private String authString;
	
	@Override
	public Response serve(String uri, String method, Properties header, Properties parms, Properties files)
	{
		String auth = header.getProperty("authorization");
		if (auth == null || !auth.equalsIgnoreCase(authString))
		{
//			StringBuilder sb = new StringBuilder("You are not authorized: " + auth + " " + authString + "\n");
//			
//			for (Entry<Object, Object> entry : header.entrySet())
//			{
//				sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
//			}
//			
//			Response res = new Response("401 Not Authorized", MIME_PLAINTEXT, sb.toString());
			
			Response res = new Response("401 Not AUthorized", MIME_PLAINTEXT, "You are not allowed to view this content");
			Properties resHeader = res.header;
			
			resHeader.setProperty("WWW-Authenticate", "Basic realm=\"ModDamage\"");
			
			return res;
		}
		
		for (Entry<Pattern, WebHandler> entry : handlers.entrySet())
		{
			Matcher m = entry.getKey().matcher(uri);
			
			if (m.matches()) {
				Response res = new Response();
				Response r = entry.getValue().handle(res, m, method, header, parms, files);
				if (r != null)
					return r;
			}
		}
		
		return new Response(HTTP_NOTFOUND, MIME_PLAINTEXT, "Not found: "+uri);
	}
	
	public static void startServer(int port, String username, String password) {
		stopServer();
		
		try
		{
			instance = new MDServer(port, username, password);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			instance = null;
			return;
		}
		
		addHandler(Pattern.compile("/hello", Pattern.CASE_INSENSITIVE), new WebHandler() {
				@Override
				public Response handle(Response res, Matcher m, String method, Properties header, Properties parms, Properties files)
				{
					return write(res, new WebWriter() {
							@Override
							public void write(Writer o) throws IOException
							{
								o.write("Hello world!");
							}
						});
				}
			});
	}
	
	public static void stopServer() {
		if (instance != null)
			instance.stop();
		instance = null;
	}
	
	public static void addHandler(Pattern pathPattern, WebHandler handler) {
		if (instance == null) return;
		
		instance.handlers.put(pathPattern, handler);
	}

	public static void addHandler(Pattern pathPattern, final WebWriter writer)
	{
		if (instance == null) return;
		
		instance.handlers.put(pathPattern, new WebHandler() {
				public Response handle(Response res, Matcher m, String method, Properties header, Properties parms, Properties files) {
					return write(res, writer);
				}
			});
	}
}
