package com.ModDamage.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Alias.WorldAliaser;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class WorldNamed extends Conditional<World>
{
	public static final Pattern pattern = Pattern.compile("\\.named\\.([\\w,]+)", Pattern.CASE_INSENSITIVE);
	
	protected final Collection<String> worlds;

	public WorldNamed(IDataProvider<World> worldDP, Collection<String> worlds)
	{
		super(World.class, worldDP);
		this.worlds = worlds;
	}

	@Override
	public Boolean get(World world, EventData data)
	{
		return worlds.contains(world.getName());
	}
	
	@Override
	public String toString()
	{
		return startDP + ".named." + Utils.joinBy(",", worlds);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, World.class, pattern, new IDataParser<Boolean, World>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<World> worldDP, Matcher m, StringMatcher sm)
				{
					Collection<String> worlds = WorldAliaser.match(m.group(1));
					if (worlds.isEmpty()) return null;
					
					return new WorldNamed(worldDP, worlds);
				}
			});
	}
}
