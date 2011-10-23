package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityBiome extends EntityConditionalStatement
{
	protected final HashSet<Biome> biomes;
	public EntityBiome(boolean inverted, EntityReference entityReference, HashSet<Biome> biomes)
	{ 
		super(inverted, entityReference);
		this.biomes = biomes;
	}
	
	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{ 
		if(entityReference.getEntity(eventInfo) != null)
			return biomes.contains(entityReference.getEntity(eventInfo).getLocation().getBlock().getBiome());
		return false;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityBiome.class, Pattern.compile("(!?)(\\w+)\\.biome\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityBiome getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			HashSet<Biome> biomes = ModDamage.matchBiomeAlias(matcher.group(3));
			if(!biomes.isEmpty() && EntityReference.isValid(matcher.group(2)))
				return new EntityBiome(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), biomes);
		}
		return null;
	}
}
