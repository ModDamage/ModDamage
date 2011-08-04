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
	protected boolean condition(TargetEventInfo eventInfo){ return value.contains(getRelevantEntity(eventInfo).getLocation().getBlock().getBiome());}
	@Override
	protected List<Biome> getRelevantInfo(TargetEventInfo eventInfo){ return Arrays.asList(getRelevantEntity(eventInfo).getLocation().getBlock().getBiome());}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityBiome.class, Pattern.compile("(!)?" + ModDamage.entityRegex + "\\.biome\\." + ModDamage.biomeRegex, Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityBiome getNew(Matcher matcher)
	{
		if(matcher != null)
			return new EntityBiome(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"), ModDamage.matchBiomeAlias(matcher.group(3)));
		return null;
	}
}
