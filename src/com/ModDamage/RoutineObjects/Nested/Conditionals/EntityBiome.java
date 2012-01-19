package com.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Biome;

import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.AliasManager;
import com.ModDamage.RoutineObjects.Nested.Conditional;

public class EntityBiome extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.biome\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	final EntityReference entityReference;
	protected final Collection<Biome> biomes;
	public EntityBiome(EntityReference entityReference, Collection<Biome> biomes)
	{ 
		this.entityReference = entityReference;
		this.biomes = biomes;
	}
	
	@Override
	public boolean evaluate(TargetEventInfo eventInfo)
	{ 
		if(entityReference.getEntity(eventInfo) != null)
			return biomes.contains(entityReference.getEntity(eventInfo).getLocation().getBlock().getBiome());
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
		public EntityBiome getNew(Matcher matcher)
		{
			Collection<Biome> biomes = AliasManager.matchBiomeAlias(matcher.group(2));
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(!biomes.isEmpty() && reference != null)
				return new EntityBiome(reference, biomes);
			return null;
		}
	}
}
