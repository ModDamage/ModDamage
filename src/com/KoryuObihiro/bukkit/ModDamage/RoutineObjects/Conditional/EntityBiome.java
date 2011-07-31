package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityBiome extends EntityConditionalStatement<Biome>
{
	public EntityBiome(boolean inverted, boolean forAttacker, Biome biome)
	{ 
		super(inverted, forAttacker, biome);
	}
	
	@Override
	protected Biome getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlock().getBiome();}
	@Override
	protected Biome getRelevantInfo(SpawnEventInfo eventInfo){ return eventInfo.entity.getLocation().getBlock().getBiome();}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityBiome.class, Pattern.compile("(!)?" + ModDamage.entityPart + "biome\\." + ModDamage.biomeRegex, Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityBiome getNew(Matcher matcher)
	{
		if(matcher != null)
			return new EntityBiome(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"), ModDamage.matchBiome(matcher.group(3)));
		return null;
	}
}
