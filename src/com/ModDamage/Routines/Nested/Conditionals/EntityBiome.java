package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;

import com.ModDamage.Backend.Aliasing.BiomeAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class EntityBiome extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.biome\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	final DataRef<Entity> entityRef;
	protected final Collection<Biome> biomes;
	public EntityBiome(DataRef<Entity> entityRef, Collection<Biome> biomes)
	{ 
		this.entityRef = entityRef;
		this.biomes = biomes;
	}
	
	@Override
	public boolean evaluate(EventData data)
	{ 
		if(entityRef.get(data) != null)
			return biomes.contains(entityRef.get(data).getLocation().getBlock().getBiome());
		return false;
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EntityBiome getNew(Matcher matcher, EventInfo info)
		{
			Collection<Biome> biomes = BiomeAliaser.match(matcher.group(2));
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());
			if(!biomes.isEmpty() && entityRef != null)
				return new EntityBiome(entityRef, biomes);
			return null;
		}
	}
}
