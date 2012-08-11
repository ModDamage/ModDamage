package com.ModDamage.Variables;

import java.util.regex.Matcher;

import org.bukkit.Location;
import org.bukkit.World;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class LocationWorld
{
	public static void register()
	{
		DataProvider.registerTransformer(World.class, Location.class,
				new IDataParser<World, Location>() {
					public IDataProvider<World> parse(EventInfo info, Class<?> want, IDataProvider<Location> locDP, Matcher m, StringMatcher sm) {
						return new DataProvider<World, Location>(Location.class, locDP) {
								public World get(Location loc, EventData data) { return loc.getWorld(); }
								public Class<World> provides() { return World.class; }
							};
					}
				});
	}
}
