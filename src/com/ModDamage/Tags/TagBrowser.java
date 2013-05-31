package com.ModDamage.Tags;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.NanoHTTPD.Response;
import com.ModDamage.Server.MDServer;
import com.ModDamage.Server.WebHandler;
import com.ModDamage.Server.WebWriter;

public class TagBrowser implements WebWriter
{
	public static void register()
	{
		MDServer.addHandler(Pattern.compile("/tags", Pattern.CASE_INSENSITIVE), new TagBrowser());
		MDServer.addHandler(Pattern.compile("/tags/(int|string)", Pattern.CASE_INSENSITIVE), new TagType());
	}
	
	public static class TagType extends WebHandler
	{
		@Override
		public Response handle(Response res, Matcher m, String method, Properties header, Properties parms, Properties files)
		{
			return null;
		}
	}

	@Override
	public void write(Writer o) throws IOException
	{
		o.write("<script src=\"//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js\"></script>");
		o.write("<style type=\"text/css\">" +
				".section.closed .children {" +
				"  display: none;" +
				"}" +
				"</style>");
		
		o.write("<div class=\"section closed\"></div>");
	}

}
