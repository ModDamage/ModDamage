package com.ModDamage.Variables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class EntityWorld
{
	public static void register()
	{
		DataProvider.register(World.class, Entity.class, Pattern.compile("_world", Pattern.CASE_INSENSITIVE),
				new IDataParser<World, Entity>() {
					public IDataProvider<World> parse(EventInfo info, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm) {
						return new DataProvider<World, Entity>(Entity.class, entityDP) {
								public World get(Entity entity, EventData data) { return entity.getWorld(); }
								public Class<World> provides() { return World.class; }
                                public String toString() { return startDP + "_world"; }
							};
					}
				});
	}
}
