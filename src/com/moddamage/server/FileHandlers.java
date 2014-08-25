package com.ModDamage.Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import com.ModDamage.ModDamage;
import com.ModDamage.Server.NanoHTTPD.Response;

public class FileHandlers
{
	public static File jarFile = null;
	public static ZipFile jar = null;
	
	public static void openJar()
	{
		if (jar != null) closeJar();
		
		jarFile = new File(ModDamage.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		
		try
		{
			jar = new ZipFile(jarFile);
		}
		catch (ZipException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void closeJar()
	{
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
		jar = null;
		jarFile = null;
	}
	
	private static InputStream getResourceStream(String path)
	{
		return MDServer.class.getResourceAsStream(path);  // this fails a lot, such as after reload
	}

	private static InputStream getZipStream(String path)
	{
		return getZipStream(path, false);
	}
	private static InputStream getZipStream(String path, boolean triedAgain)
	{
		if (jar == null)
			openJar();
		
		if (jar != null) {
			if (path.startsWith("/")) path = path.substring(1);
			
			ZipEntry entry = jar.getEntry(path);
			
			if (entry != null) {
				try
				{
					return jar.getInputStream(entry);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			/*else {
				System.out.println();
				System.out.println("No entry for "+path);
				for (Enumeration<? extends ZipEntry> e = jar.entries(); e.hasMoreElements();)
				       System.out.println(e.nextElement());
				System.out.println();
			}*/
		}
		if (!triedAgain) {
			closeJar();
			
			return getZipStream(path, true);
		}
		return null;
	}
	
	private static InputStream getPluginStream(String path)
	{
		File f = new File(ModDamage.getPluginConfiguration().plugin.getDataFolder(), path);
		if (f.exists()) {
			try
			{
				return new FileInputStream(f);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static InputStream getStream(String path)
	{
		InputStream in = getResourceStream(path);
		
		if (in == null)
			in = getZipStream(path);
		
		if (in == null)
			in = getPluginStream(path);
		
//		InputStream in = getPluginStream(path);
//		if (in == null)
//			in = getPluginStream(path);
		
		return in;
	}
	
	
	public static void register()
	{
		MDServer.addOnShutdown(new Runnable() {
			@Override
			public void run()
			{
				if (jar != null)
					try { jar.close(); } catch (IOException e) { e.printStackTrace(); }
				jar = null;
				jarFile = null;
			}
		});
		
		MDServer.addHandler("/static/(.+)$", new WebHandler() {
			@Override
			public Response handle(Response res, Matcher m, String uri, String method, Properties header, Properties parms, Properties files)
			{
				String path = "/web/"+m.group(1);
				
				InputStream in = getStream(path);
				
				if (in == null)
					// not found
					return send( res, MDServer.HTTP_NOTFOUND, MDServer.MIME_PLAINTEXT,
							"Error 404, file not found: " + path);
				
				
				// Get MIME type from file name extension, if possible
				String mime = null;
				int dot = uri.lastIndexOf( '.' );
				if ( dot >= 0 )
					mime = MDServer.theMimeTypes.get( uri.substring( dot + 1 ).toLowerCase());
				if ( mime == null )
					mime = MDServer.MIME_DEFAULT_BINARY;
				
				
				return send(res, MDServer.HTTP_OK, mime, in);
			}
		});
		
		MDServer.addHandler("/plugin/(.*)$", new WebHandler() {
			@Override
			public Response handle(Response res, Matcher m, String uri, String method, Properties header, Properties parms, Properties files)
			{
				String path = m.group(1);
				final File f;
				if (path.length() == 0)
					f = ModDamage.getPluginConfiguration().plugin.getDataFolder();
				else
					f = new File(ModDamage.getPluginConfiguration().plugin.getDataFolder(), path);
				
				if (!f.exists()) {
					// not found
					return send( res, MDServer.HTTP_NOTFOUND, MDServer.MIME_PLAINTEXT,
							"Error 404, file not found: " + path);
				}
				
				if (f.isDirectory()) {
					final File[] list = f.listFiles();
					
					Arrays.sort(list, new Comparator<File>() {
							public int compare(File a, File b)
							{
								if (a.isDirectory() == b.isDirectory())
									return a.getName().compareToIgnoreCase(b.getName());
								
								if (a.isDirectory() && !b.isDirectory())
									return -1;
								return 1;
							}
						});
					
					return send(res, MDServer.HTTP_OK, MDServer.MIME_PLAINTEXT, new WebWriter() {
							public void write(Writer o) throws IOException
							{
								for (File subf : list)
								{
									o.append(subf.getName());
									
									if (subf.isDirectory())
										o.append('/');
									
									o.append('\n');
								}
							}
						});
				}
				
				if (method.equals("GET")) {
					InputStream in;
					try {
						in = new FileInputStream(f);
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
						return send( res, MDServer.HTTP_NOTFOUND, MDServer.MIME_PLAINTEXT,
								"Error 404, file not found: " + path);
					}
	
					// Get MIME type from file name extension, if possible
					String mime = null;
					int dot = uri.lastIndexOf( '.' );
					if ( dot >= 0 )
						mime = MDServer.theMimeTypes.get( uri.substring( dot + 1 ).toLowerCase());
					if ( mime == null )
						mime = MDServer.MIME_DEFAULT_BINARY;
					
					
					return send(res, MDServer.HTTP_OK, mime, in);
				}
				else if (method.equals("PUT")) {
					String contentpath = files.getProperty("content");
					if (contentpath == null) {
						return send( res, MDServer.HTTP_INTERNALERROR, MDServer.MIME_PLAINTEXT,
								"Did not receive full file");
					}
					File file = new File(contentpath);
					Long paramLength = Long.parseLong(parms.getProperty("length"));
					if (file.length() != paramLength) {
						return send( res, MDServer.HTTP_INTERNALERROR, MDServer.MIME_PLAINTEXT,
								"Did not receive full file: " + file.length() + " | " + paramLength);
					}
					
					try
					{
						FileUtils.copyFile(file, f);
					}
					catch (IOException e)
					{
						e.printStackTrace();
						return send( res, MDServer.HTTP_INTERNALERROR, MDServer.MIME_PLAINTEXT,
								"Unable to write to file");
					}
					
					return send(res, MDServer.HTTP_OK, MDServer.MIME_PLAINTEXT, "Saved OK");
				}
				
				return send(res, MDServer.HTTP_BADREQUEST, MDServer.MIME_PLAINTEXT, "Method not supported: " + method);
			}
		});
	}
}
