package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class EntityBiome extends EntityConditionalStatement
{
	protected final Collection<Biome> biomes;
	public EntityBiome(boolean inverted, EntityReference entityReference, Collection<Biome> biomes)
	{ 
		super(inverted, entityReference);
		this.biomes = biomes;
	}
	
	@Override
	public boolean condition(TargetEventInfo eventInfo)
	{ 
		if(entityReference.getEntity(eventInfo) != null)
			return biomes.contains(entityReference.getEntity(eventInfo).getLocation().getBlock().getBiome());
		return false;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)(\\w+)\\.biome\\.(\\w+)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public EntityBiome getNew(Matcher matcher)
		{
			Collection<Biome> biomes = AliasManager.matchBiomeAlias(matcher.group(3));
			EntityReference reference = EntityReference.match(matcher.group(2));
			if(!biomes.isEmpty() && reference != null)
				return new EntityBiome(matcher.group(1).equalsIgnoreCase("!"), reference, biomes);
			return null;
		}
	}
}
