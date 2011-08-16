package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityBiome extends EntityConditionalStatement<List<Biome>>
{
	public EntityBiome(boolean inverted, boolean forAttacker, List<Biome> value)
	{ 
		super(inverted, forAttacker, value);
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo){ return value.contains(getRelevantInfo(eventInfo));}
	@Override
	protected List<Biome> getRelevantInfo(TargetEventInfo eventInfo)
	{
		return Arrays.asList((eventInfo.getRelevantEntity(forAttacker) != null)?eventInfo.getRelevantEntity(forAttacker).getLocation().getBlock().getBiome():null);
	}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityBiome.class, Pattern.compile("(!)?(\\w+)\\.biome\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityBiome getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<Biome> biomes = ModDamage.matchBiomeAlias(matcher.group(3));
			if(!biomes.isEmpty())
				return new EntityBiome(matcher.group(1) != null, (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false, biomes);
		}
		return null;
	}
}
