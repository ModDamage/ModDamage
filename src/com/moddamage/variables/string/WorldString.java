package com.moddamage.variables.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;

import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.expressions.StringExp;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

public class WorldString extends StringExp<World>
{
	private static Pattern pattern = Pattern.compile("_("+Utils.joinBy("|", WorldStringProperty.values())+")", Pattern.CASE_INSENSITIVE);
	
	public enum WorldStringProperty
	{
		NAME
		{
			@Override
			protected String getString(World world)
			{
				return world.getName();
			}
		},
		ENVIRONMENT
		{
			@Override
			protected String getString(World world)
			{
				return world.getEnvironment().name();
			}
		};
		
		protected String getString(World world){ return null; }
	}
	

	private final WorldStringProperty property;
	
	private WorldString(IDataProvider<World> worldDP, WorldStringProperty property)
	{
		super(World.class, worldDP);
		this.property = property;
	}
	
	public String get(World world, EventData data)
	{
		return property.getString(world);
	}
	
	public static void register()
	{
		DataProvider.register(String.class, World.class, pattern, new IDataParser<String, World>()
			{
				@Override
				public IDataProvider<String> parse(EventInfo info, IDataProvider<World> worldDP, Matcher m, StringMatcher sm)
				{
					return new WorldString(worldDP, WorldStringProperty.valueOf(m.group(1).toUpperCase()));
				}
			});
	}
	
	@Override
	public String toString()
	{
		return startDP + "_" + (property == null? "null" : property.name().toLowerCase());
	}
}