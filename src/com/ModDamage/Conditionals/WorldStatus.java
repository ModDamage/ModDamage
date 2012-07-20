package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class WorldStatus extends Conditional<World> 
{
	public static final Pattern pattern = Pattern.compile("\\.is("+Utils.joinBy("|", WorldStatusType.values())+")", Pattern.CASE_INSENSITIVE);
	
	enum WorldStatusType
	{
		STORMY { boolean get(World world) { return world.hasStorm(); } },
		THUNDERING { boolean get(World world) { return world.hasStorm(); } },;
		
		abstract boolean get(World world);
	}
	
	private final WorldStatusType statusType;
	
	public WorldStatus(IDataProvider<World> worldDP, WorldStatusType statusType)
	{
		super(World.class, worldDP);
		this.statusType = statusType;
	}
	@Override
	public Boolean get(World world, EventData data)
	{
		return statusType.get(world);
	}
	
	@Override
	public String toString()
	{
		return startDP + ".is" + statusType.name().toLowerCase();
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, World.class, pattern, new IDataParser<Boolean, World>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, Class<?> want, IDataProvider<World> worldDP, Matcher m, StringMatcher sm)
				{
					WorldStatusType statusType = WorldStatusType.valueOf(m.group(1).toUpperCase());
						
					return new WorldStatus(worldDP, statusType);
				}
			});
	}
}
