package com.ModDamage.Server;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.json.JSONWriter;

import com.ModDamage.MDEvent;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.DataProvider.ParserData;
import com.ModDamage.Parsing.DataProvider.TransformerData;
import com.ModDamage.Routines.Routine;
import com.ModDamage.Routines.Routine.RoutineBuilder;
import com.ModDamage.Routines.Nested.NestedRoutine;
import com.ModDamage.Server.NanoHTTPD.Response;

public class APIHandlers
{
	public static void register()
	{
		MDServer.addHandler("/api/jarentries$", new WebHandler() {
			public Response handle(Response res, Matcher m, String uri, String method, Properties header, Properties parms, Properties files)
			{
				return send(res, MDServer.HTTP_OK, MDServer.MIME_JSON, new WebWriter() {
						public void write(Writer w) throws IOException
						{
							apiJarEntries(new JSONWriter(w));
						}
					});
			}
		});
		
		MDServer.addHandler("/api/stats$", new WebHandler() {
			public Response handle(Response res, Matcher m, String uri, String method, Properties header, Properties parms, Properties files)
			{
				return send(res, MDServer.HTTP_OK, MDServer.MIME_JSON, new WebWriter() {
						public void write(Writer w) throws IOException
						{
							apiStats(new JSONWriter(w));
						}
					});
			}
		});
		

		MDServer.addHandler("/api/events$", new WebHandler() {
			public Response handle(Response res, Matcher m, String uri, String method, Properties header, Properties parms, Properties files)
			{
				return send(res, MDServer.HTTP_OK, MDServer.MIME_JSON, new WebWriter() {
						public void write(Writer w) throws IOException
						{
							apiEvents(new JSONWriter(w));
						}
					});
			}
		});
		

		MDServer.addHandler("/api/properties$", new WebHandler() {
			public Response handle(Response res, Matcher m, String uri, String method, Properties header, Properties parms, Properties files)
			{
				return send(res, MDServer.HTTP_OK, MDServer.MIME_JSON, new WebWriter() {
						public void write(Writer w) throws IOException
						{
							apiProperties(new JSONWriter(w));
						}
					});
			}
		});
		

		MDServer.addHandler("/api/transformers$", new WebHandler() {
			public Response handle(Response res, Matcher m, String uri, String method, Properties header, Properties parms, Properties files)
			{
				return send(res, MDServer.HTTP_OK, MDServer.MIME_JSON, new WebWriter() {
						public void write(Writer w) throws IOException
						{
							apiTransformers(new JSONWriter(w));
						}
					});
			}
		});
		

		MDServer.addHandler("/api/routines$", new WebHandler() {
			public Response handle(Response res, Matcher m, String uri, String method, Properties header, Properties parms, Properties files)
			{
				return send(res, MDServer.HTTP_OK, MDServer.MIME_JSON, new WebWriter() {
						public void write(Writer w) throws IOException
						{
							apiRoutines(new JSONWriter(w));
						}
					});
			}
		});
		
		MDServer.addHandler("/api/class/(.+)$", new WebHandler() {
			public Response handle(Response res, final Matcher m, String uri, String method, Properties header, Properties parms, Properties files)
			{
				return send(res, MDServer.HTTP_OK, MDServer.MIME_JSON, new WebWriter() {
						public void write(Writer w) throws IOException
						{
							apiClass(new JSONWriter(w), m.group(1));
						}
					});
			}
		});
	}
	
	public static void apiJarEntries(JSONWriter jw)
	{
		FileHandlers.openJar();
		
		if (FileHandlers.jar == null) {
			jw.object();
			
			jw.key("error").value("Unable to open jar at: " + FileHandlers.jarFile.getAbsolutePath());
			jw.key("exists").value(FileHandlers.jarFile.exists());
			jw.key("canRead").value(FileHandlers.jarFile.canRead());
			jw.key("canWrite").value(FileHandlers.jarFile.canWrite());
			jw.key("canExecute").value(FileHandlers.jarFile.canExecute());
			jw.key("length").value(FileHandlers.jarFile.length());
			
			jw.endObject();
			return;
		}
		
		jw.array();
		

		for (Enumeration<? extends ZipEntry> e = FileHandlers.jar.entries(); e.hasMoreElements();)
		    jw.value(e.nextElement());
		
		jw.endArray();
		
	}

	public static void apiStats(JSONWriter jw)
	{
		Server server = Bukkit.getServer();
		
		jw.object();

			jw.key("name").value(server.getServerName());
			jw.key("id").value(server.getServerId());
			jw.key("ip").value(server.getIp());
			jw.key("port").value(server.getPort());
			
			jw.key("implementation").object();
				jw.key("name").value(server.getName());
				jw.key("version").value(server.getVersion());
				jw.key("bukkitVersion").value(server.getBukkitVersion());
			jw.endObject();
			
			
			jw.key("numPlayers").value(server.getOnlinePlayers().length);
			jw.key("maxPlayers").value(server.getMaxPlayers());
			
		
		jw.endObject();
	}
	
	public static void apiEvents(JSONWriter jw)
	{
		jw.array();

		for (Entry<String, List<MDEvent>> category : MDEvent.eventCategories.entrySet()) {
			jw.object();
			jw.key("name").value(category.getKey());
			
			jw.key("events").array();
			
			for (MDEvent event : category.getValue()) {
				jw.object();
				
				jw.key("name").value(event.name());
				
				EventInfo info = event.getInfo();
				if (info != null) {
					jw.key("info").array();
					
					int i = 0;
					
					for (List<String> namesList : info.getNamesLists()) {
						Class<?> cls = info.getClass(i++);
						
						jw.object();
						
						jw.key("class"); writeClass(jw, cls);
						
						jw.key("names").array();
							for (String name : namesList)
								jw.value(name);
						jw.endArray();
						
						jw.endObject();
					}
					
					jw.endArray();
				}
				
				jw.endObject();
			}

			jw.endArray();
			jw.endObject();
		}
		
		jw.endArray();
	}
	
	public static void apiProperties(JSONWriter jw)
	{
		jw.array();
		
		for (ParserData<?, ?> parser : DataProvider.parsers) {
			jw.object();
			
			
			jw.key("wants"); writeClass(jw, parser.wants);
			
			jw.key("provides"); writeClass(jw, parser.provides);
			
			jw.key("pattern").value(parser.pattern.pattern());
			
			jw.key("class"); writeClass(jw, parser.parser.getClass());
			
			jw.key("toString").value(parser.parser.toString());
			
//			jw.key("settable").value(???);
			
			
			jw.endObject();
		}
		
		jw.endArray();
	}
	
	public static void apiTransformers(JSONWriter jw)
	{
		jw.array();
		
		for (Entry<Class<?>, ArrayList<TransformerData<?, ?>>> entry : DataProvider.transformersByStart.entrySet()) {
			
			for (TransformerData<?, ?> transformer : entry.getValue()) {
				jw.object();
			
				jw.key("wants"); writeClass(jw, transformer.wants);
				
				jw.key("provides"); writeClass(jw, transformer.provides);
				
				jw.key("class"); writeClass(jw, transformer.transformer.getClass());
				
				jw.key("toString").value(transformer.transformer.toString());
				
				jw.endObject();
			}
			
		}
		
		jw.endArray();
	}
	
	public static void apiRoutines(JSONWriter jw)
	{
		jw.array();
		
		for (Entry<Pattern, RoutineBuilder> entry : Routine.registeredBaseRoutines.entrySet()) {
			
			jw.object();
		
			jw.key("pattern").value(entry.getKey().pattern());
			
			jw.key("builder"); writeClass(jw, entry.getValue().getClass().getEnclosingClass());
			
			jw.key("toString").value(entry.getValue().toString());
			
			jw.endObject();
		}
		
		for (Entry<Pattern, com.ModDamage.Routines.Nested.NestedRoutine.RoutineBuilder> entry : NestedRoutine.registeredNestedRoutines.entrySet()) {
			
			jw.object();
		
			jw.key("pattern").value(entry.getKey().pattern());
			
			jw.key("builder"); writeClass(jw, entry.getValue().getClass().getEnclosingClass());
			
			jw.key("toString").value(entry.getValue().toString());
			
			jw.endObject();
		}
		
		jw.endArray();
	}
	

	public static void apiClass(JSONWriter jw, String clsName)
	{
		jw.object();
		
		Class<?> cls;
		try
		{
			cls = Class.forName(clsName);
		}
		catch (Exception e)
		{
			jw.key("error").value(e.toString());
			
			jw.endObject();
			return;
		}
		
		
		jw.key("simpleName").value(cls.getSimpleName());
		jw.key("name").value(cls.getName());
		jw.key("canonicalName").value(cls.getCanonicalName());
		jw.key("superclass"); writeClass(jw, cls.getSuperclass());
		jw.key("enclosingClass"); writeClass(jw, cls.getEnclosingClass());
		
		jw.key("classes").array();
		@SuppressWarnings("rawtypes")
		Class[] classes = cls.getClasses();
		for (Class<?> clss : classes)
			writeClass(jw, clss);
		jw.endArray();

		jw.key("interfaces").array();
		@SuppressWarnings("rawtypes")
		Class[] interfaces = cls.getInterfaces();
		for (Class<?> iface : interfaces)
			writeClass(jw, iface);
		jw.endArray();

		Object[] enumConsts = cls.getEnumConstants();
		if (enumConsts != null) {
			jw.key("enumConstants").array();
			
			for (Object object : enumConsts)
			{
				jw.object();
				
				jw.key("name").value(object.toString());
				jw.key("class"); writeClass(jw, object.getClass());
				
				jw.endObject();
			}
			
			jw.endArray();
		}
		
		jw.key("componentType"); writeClass(jw, cls.getComponentType());
		
		jw.endObject();
	}
	
	
	// helpers
	
	public static void writeClass(JSONWriter jw, Class<?> cls)
	{
		if (cls == null) {
			jw.value(cls);
			return;
		}
		jw.object();
		String name = cls.getName();
		
		jw.key("name").value(cls.getName());
		
//		jw.key("simpleName").value(cls.getSimpleName()); // this isn't good enough. Returns empty for some things, not enough for others
		jw.key("simpleName");
		if (cls.getSimpleName().equals(""))
			jw.value(name.substring(name.lastIndexOf('.')+1));
		else
			jw.value(cls.getSimpleName());
		
		jw.endObject();
	}
}
