package com.ModDamage.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;

import org.apache.commons.io.IOUtils;

import com.ModDamage.MDEvent;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Server.NanoHTTPD.Response;

public class Events extends WebHandler
{

	@Override
	public Response handle(Response res, Matcher m, String uri, String method, Properties header, Properties parms, Properties files)
	{
		return send(res, new WebWriter() {
				@Override
				public void write(Writer o) throws IOException
				{
					InputStream in = FileHandlers.getStream("/web/header.html");
					IOUtils.copy(in, o); in.close();
					
					in = FileHandlers.getStream("/web/header2.html");
					IOUtils.copy(in, o); in.close();
					
					o.write("<div class=\"container\"><div id=\"accordion\" class=\"panel-group\">");
					
					for (Entry<String, List<MDEvent>> category : MDEvent.eventCategories.entrySet()) {
						o.write("<div class=\"panel panel-default\"><div class=\"panel-heading\">");
						o.write("<h2 class=\"panel-title\"><a data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#collapse"+category.getKey()+"\">"+category.getKey()+"</a></h2>");
						
						o.write("</div><div id=\"collapse"+category.getKey()+"\" class=\"panel-collapse collapse\">");
						o.write("<div class=\"panel-body\">");
						
						for (MDEvent event : category.getValue()) {
							o.write("<h3>"+event.name()+"</h3>");
							
							EventInfo info = event.getInfo();
							
							if (info != null) {
								o.write("<ul>");
								
								int i = 0;
								
								for (List<String> namesList : info.getNamesLists()) {
									Class<?> cls = info.getClass(i++);
									
									o.write("<li><span style=\"font-weight: bold;\">"+cls.getSimpleName()+"</span>: ");
									
									boolean first = true;
									for (String name : namesList) {
										if (first) first = false;
										else o.write(", ");
										
										o.write(name);
									}
									
									o.write("</li>");
								}
								
								o.write("</ul>");
							}
						}

						o.write("</div>");
						o.write("</div>");
						o.write("</div>");
					}
					
					o.write("</div></div>");
					
					in = FileHandlers.getStream("/web/footer.html");
					IOUtils.copy(in, o); in.close();
				}
			});
	}

}
