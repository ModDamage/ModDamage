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
import java.util.zip.ZipFile;

import javax.xml.bind.DatatypeConverter;

public class MDServer extends NanoHTTPD
{
	private MDServer(int port, String username, String password) throws IOException {
		super(port, null);
		authString = "Basic "+ DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
	}
	
	private static MDServer instance = null;
	
	static Executor executor = Executors.newSingleThreadExecutor();
	
	private Map<Pattern, WebHandler> handlers = new HashMap<Pattern, WebHandler>();
	
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

		Response res = new Response();
		for (Entry<Pattern, WebHandler> entry : handlers.entrySet())
		{
			Matcher m = entry.getKey().matcher(uri);
			if (m.lookingAt()) {
				Response r = entry.getValue().handle(res, m, uri, method, header, parms, files);
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
		
		addHandler("/$", new WebHandler() {
			@Override
			public Response handle(Response res, Matcher m, String uri, String method, Properties header, Properties parms, Properties files)
			{
				res.addHeader("Location", "/static/stats.html");
				
				return send(res, HTTP_REDIRECT, MIME_HTML, "Redirecting to <a href=\"/static/stats.html\">/static/stats.html</a>");
			}
		});
		
		addHandler("/hello$", new WebHandler() {
				@Override
				public Response handle(Response res, Matcher m, String uri, String method, Properties header, Properties parms, Properties files)
				{
					return send(res, new WebWriter() {
							@Override
							public void write(Writer o) throws IOException
							{
								o.write("Hello world!");
							}
						});
				}
			});
		
		APIHandlers.register();
		FileHandlers.register();
	}
	
	public static void stopServer() {
		ZipFile jar = FileHandlers.jar;
		FileHandlers.jar = null;
		if (jar != null) {
			try
			{
				jar.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		if (instance != null)
			instance.stop();
		instance = null;
	}
	
	public static void addHandler(String pathPattern, WebHandler handler) {
		if (instance == null) return;
		
		instance.handlers.put(Pattern.compile(pathPattern), handler);
	}
	
	public static void addHandler(Pattern pathPattern, WebHandler handler) {
		if (instance == null) return;
		
		instance.handlers.put(pathPattern, handler);
	}
}
