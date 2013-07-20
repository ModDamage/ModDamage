package com.ModDamage.Server;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.json.JSONWriter;

import com.ModDamage.Server.NanoHTTPD.Response;

public class APIHandlers
{
	public static void register()
	{
		MDServer.addHandler("/api/stats$", new WebHandler() {
			public Response handle(Response res, Matcher m, String uri, String method, Properties header, Properties parms, Properties files)
			{
				return send(res, MDServer.HTTP_OK, MDServer.MIME_JSON, new WebWriter() {
						public void write(Writer w) throws IOException
						{
							JSONWriter jw = new JSONWriter(w);
							
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
					});
			}
		});
	}
}
