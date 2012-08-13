package com.ModDamage.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.block.Biome;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Alias.BiomeAliaser;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class LocationBiome extends Conditional<Location>
{
	public static final Pattern pattern = Pattern.compile("\\.biome\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	protected final Collection<Biome> biomes;
	
	public LocationBiome(IDataProvider<Location> locDP, Collection<Biome> biomes)
	{
		super(Location.class, locDP);
		this.biomes = biomes;
	}
	
	@Override
	public Boolean get(Location loc, EventData data)
	{ 
		return biomes.contains(loc.getBlock().getBiome());
	}
	
	@Override
	public String toString()
	{
		return startDP + ".biome." + Utils.joinBy(",", biomes);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Location.class, pattern, new IDataParser<Boolean, Location>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, Class<?> want, IDataProvider<Location> locDP, Matcher m, StringMatcher sm)
				{
					Collection<Biome> biomes = BiomeAliaser.match(m.group(1));
					if(biomes.isEmpty()) return null;
					
					return new LocationBiome(locDP, biomes);
				}
			});
	}
}
