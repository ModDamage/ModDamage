package com.ModDamage.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Alias.BiomeAliaser;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class EntityBiome extends Conditional<Entity>
{
	public static final Pattern pattern = Pattern.compile("\\.biome\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	protected final Collection<Biome> biomes;
	
	public EntityBiome(IDataProvider<Entity> entityDP, Collection<Biome> biomes)
	{
		super(Entity.class, entityDP);
		this.biomes = biomes;
	}
	
	@Override
	public Boolean get(Entity entity, EventData data)
	{ 
		return biomes.contains(entity.getLocation().getBlock().getBiome());
	}
	
	@Override
	public String toString()
	{
		return startDP + ".biome." + Utils.joinBy(",", biomes);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Entity.class, pattern, new IDataParser<Boolean, Entity>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, Class<?> want, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm)
				{
					Collection<Biome> biomes = BiomeAliaser.match(m.group(1));
					if(biomes.isEmpty()) return null;
					
					return new EntityBiome(entityDP, biomes);
				}
			});
	}
}
