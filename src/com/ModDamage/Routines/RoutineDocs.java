package com.ModDamage.Routines;

import java.io.IOException;
import java.io.Writer;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.NanoHTTPD;
import com.ModDamage.NanoHTTPD.Response;
import com.ModDamage.Routines.Routine.RoutineBuilder;
import com.ModDamage.Routines.Nested.NestedRoutine;
import com.ModDamage.Server.MDServer;
import com.ModDamage.Server.WebHandler;
import com.ModDamage.Server.WebWriter;

public class RoutineDocs extends WebHandler
{
	public static void register() {
		MDServer.addHandler(Pattern.compile("/routines", Pattern.CASE_INSENSITIVE), new RoutineDocs());
	}

	@Override
	public Response handle(Response res, final Matcher m, final String method, Properties header, Properties parms, Properties files)
	{
		res.mimeType = NanoHTTPD.MIME_HTML;
		
		return write(res, new WebWriter()
		{
			@Override
			public void write(Writer o) throws IOException
			{
				o.write("<ul>");
				for (Entry<Pattern, RoutineBuilder> entry : Routine.registeredBaseRoutines.entrySet())
				{
					o.write("<li><a href=\"/routine/" + entry.getValue().getClass().getEnclosingClass().getSimpleName()
							+ "\">"+ entry.getValue().getClass().getEnclosingClass().getSimpleName() +"</a></li>\n");
				}
				
				for (Entry<Pattern, NestedRoutine.RoutineBuilder> entry : NestedRoutine.registeredNestedRoutines.entrySet())
				{
					o.write("<li><a href=\"/routine/" + entry.getValue().getClass().getEnclosingClass().getSimpleName()
							+ "\">"+ entry.getValue().getClass().getEnclosingClass().getSimpleName() +"</a></li>\n");
				}
				o.write("</ul>");
			}
		});
	}

}
