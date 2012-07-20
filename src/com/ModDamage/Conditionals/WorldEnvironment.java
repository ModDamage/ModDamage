package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.World.Environment;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class WorldEnvironment extends Conditional<World> 
{
	public static final Pattern pattern = Pattern.compile("\\.environment\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	protected final Environment environment;
	
	public WorldEnvironment(IDataProvider<World> worldDP, Environment environment)
	{
		super(World.class, worldDP);
		this.environment = environment;
	}
	@Override
	public Boolean get(World world, EventData data)
	{
		return world.getEnvironment().equals(environment);
	}
	
	@Override
	public String toString()
	{
		return startDP + ".environment." + environment.name().toLowerCase();
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, World.class, pattern, new IDataParser<Boolean, World>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, Class<?> want, IDataProvider<World> worldDP, Matcher m, StringMatcher sm)
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
