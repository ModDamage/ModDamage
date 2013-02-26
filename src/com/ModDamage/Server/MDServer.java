package com.ModDamage.Server;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.bind.DatatypeConverter;

import com.ModDamage.NanoHTTPD;

public class MDServer extends NanoHTTPD
{
	private MDServer(int port, String username, String password) throws IOException {
		super(port, null);
		authString = "Basic "+ DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
	}
	
	private static MDServer instance = null;
	
	static Executor executor = Executors.newSingleThreadExecutor();
	
	private Map<String, WebHandler> handlers = new HashMap<String, WebHandler>();
	
	private String authString;
	
	@Override
	public Response serve(String uri, String method, Properties header, Properties parms, Properties files)
	{
		String auth = header.getProperty("authorization");
		if (auth == null || !auth.equalsIgnoreCase(authString))
		{
			StringBuilder sb = new StringBuilder("You are not authorized: " + auth + " " + authString + "\n");
			
			for (Entry<Object, Object> entry : header.entrySet())
			{
				sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
			}
			
			Response res = new Response("401 Not Authorized", MIME_PLAINTEXT, sb.toString());
			Properties resHeader = res.header;
			
			resHeader.setProperty("WWW-Authenticate", "Basic realm=\"ModDamage\"");
			
			return res;
		}
		
		for (Entry<String, WebHandler> entry : handlers.entrySet())
		{
			Response res = new Response();
			if (uri.startsWith(entry.getKey())) {
				Response r = entry.getValue().handle(res, uri, method, header, parms, files);
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
		
		addHandler("/hello", new WebHandler() {
				@Override
				public Response handle(Response res, String uri, String method, Properties header, Properties parms, Properties files)
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
	
	public static void addHandler(String path, WebHandler handler) {
		if (instance == null) return;
		
		instance.handlers.put(path, handler);
	}
}
