package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class WorldEnvironment extends Conditional<World> 
{
	public static final Pattern pattern = Pattern.compile("\\.environment\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	protected final Environment environment;
	
	public WorldEnvironment(IDataProvider<?> worldDP, Environment environment)
	{
		super(World.class, worldDP);
		this.environment = environment;
	}
	@Override
	public Boolean get(World world, EventData data)
	{
		return world.getEnvironment().equals(environment);
	}
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Entity.class, pattern, new IDataParser<Boolean>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<?> worldDP, Matcher m, StringMatcher sm)
				{
					try
					{
						Environment environment = Environment.valueOf(m.group(1).toUpperCase());
						
						return new WorldEnvironment(worldDP, environment);
					}
					catch(IllegalArgumentException e)
					{
						return null;
					}
				}
			});
	}
}
