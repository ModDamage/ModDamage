package com.ModDamage.Variables.String;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.StringExp;

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
	
	private WorldString(IDataProvider<?> worldDP, WorldStringProperty property)
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
		DataProvider.register(String.class, Player.class, pattern, new IDataParser<String>()
			{
				@Override
				public IDataProvider<String> parse(EventInfo info, IDataProvider<?> worldDP, Matcher m, StringMatcher sm)
				{
					return new WorldString(worldDP, WorldStringProperty.valueOf(m.group(1).toUpperCase()));
				}
			});
	}
	
	@Override
	public String toString()
	{
		return property == null? "null" : property.name().toLowerCase();
	}
}