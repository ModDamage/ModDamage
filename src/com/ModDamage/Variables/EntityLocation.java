package com.ModDamage.Variables;

import java.util.regex.Matcher;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class EntityLocation
{
	public static void register()
	{
		DataProvider.registerTransformer(Location.class, Entity.class,
				new IDataParser<Location, Entity>() {
					public IDataProvider<Location> parse(EventInfo info, Class<?> want, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm) {
						return new DataProvider<Location, Entity>(Entity.class, entityDP) {
								public Location get(Entity entity, EventData data) { return entity.getLocation(); }
								public Class<Location> provides() { return Location.class; }
							};
					}
				});
	}
}
