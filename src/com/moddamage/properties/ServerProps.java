package com.moddamage.properties;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import com.moddamage.StringMatcher;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.BaseDataParser;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.parsing.property.Properties;
import com.moddamage.parsing.property.Property;

public class ServerProps
{
	public static void register()
	{
		DataProvider.register(Server.class, Pattern.compile("server", Pattern.CASE_INSENSITIVE), 
				new BaseDataParser<Server>() {
					public IDataProvider<Server> parse(EventInfo info, Matcher m, StringMatcher sm)
					{
						return sm.acceptIf(new IDataProvider<Server>() {
							public Server get(EventData data) {
								return Bukkit.getServer();
							}
							
							public Class<? extends Server> provides() {
								return Server.class;
							}
							
							public String toString() {
								return "server";
							}
						});
					}
				});
		
		Properties.register(new Property<Integer, Server>("onlineplayers", Integer.class, Server.class) {
			public Integer get(Server server, EventData data) {
				return server.getOnlinePlayers().size();
			}
			
		});
		Properties.register("maxplayers", Server.class, "getMaxPlayers");

		Properties.register(new Property<Double, Server>("time", Double.class, Server.class) {
			public Double get(Server server, EventData data) {
				return System.currentTimeMillis() / 1000.0;
			}
			
		});
		Properties.register(new Property<Long, Server>("timemillis", Long.class, Server.class) {
			public Long get(Server server, EventData data) {
				return System.currentTimeMillis();
			}
			
		});
	}
}
