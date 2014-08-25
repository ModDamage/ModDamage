package com.ModDamage.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;

public class MDServer extends NanoHTTPD
{
	private MDServer(String bindaddr, int port, String username, String password) throws IOException {
		super(new ServerSocket(port, 0, InetAddress.getByName(bindaddr)), null);
		authString = "Basic "+ DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
	}
	
	private static MDServer instance = null;
	
	static Executor executor = Executors.newSingleThreadExecutor();
	
	private Map<Pattern, WebHandler> handlers = new HashMap<Pattern, WebHandler>();
	
	private String authString; //Should not be made public in forbidden access text.

	private List<Runnable> onShutdown = new ArrayList<Runnable>();
	
	@Override
	public Response serve(String uri, String method, Properties header, Properties parms, Properties files)
	{
		String auth = header.getProperty("authorization");
		if (auth == null || !auth.equalsIgnoreCase(authString))
		{
			StringBuilder sb = new StringBuilder("You are not authorized: " + auth + "\n");
			
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
	
	public static void startServer(String bindaddr, int port, String username, String password) {
		stopServer();
		
		try
		{
			instance = new MDServer(bindaddr, port, username, password);
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
				final InputStream in = FileHandlers.getStream("/web/app.html");

				if (in == null)
					return send( res, MDServer.HTTP_NOTFOUND, MDServer.MIME_PLAINTEXT,
							"Error 404, file not found: " + "/web/app.html");
					
				return send(res, new WebWriter() {
					@Override
					public void write(Writer o) throws IOException
					{
						IOUtils.copy(in, o);
						in.close();
					}
				});
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
		if (instance != null) {
			for (Runnable r : instance.onShutdown) {
				r.run();
			}
		
			instance.stop();
			instance = null;
		}
	}
	
	public static void addHandler(String pathPattern, WebHandler handler) {
		if (instance == null) return;
		
		instance.handlers.put(Pattern.compile(pathPattern), handler);
	}
	
	public static void addHandler(Pattern pathPattern, WebHandler handler) {
		if (instance == null) return;
		
		instance.handlers.put(pathPattern, handler);
	}

	public static void addOnShutdown(Runnable runnable)
	{
		instance.onShutdown .add(runnable);
	}
}
