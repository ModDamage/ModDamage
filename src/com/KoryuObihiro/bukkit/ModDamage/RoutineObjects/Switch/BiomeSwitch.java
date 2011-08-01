package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class BiomeSwitch extends EntitySwitchRoutine<Biome>
{
	public BiomeSwitch(boolean forAttacker, LinkedHashMap<String, List<Routine>> switchLabels)
	{
		super(forAttacker, switchLabels);
	}
	@Override
	protected Biome getRelevantInfo(TargetEventInfo eventInfo){ return getRelevantEntity(eventInfo).getLocation().getBlock().getBiome();}
	@Override
	protected Biome matchCase(String switchCase){ return ModDamage.matchBiome(switchCase);}
	
	public static void register(ModDamage routineUtility)
	{
		SwitchRoutine.registerStatement(routineUtility, BiomeSwitch.class, Pattern.compile(ModDamage.entityPart + "environment", Pattern.CASE_INSENSITIVE));
	}
	
	public static BiomeSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		if(matcher != null && switchStatements != null)
		{
			boolean forAttacker = matcher.group(1).equalsIgnoreCase("attacker");
			return new BiomeSwitch(forAttacker, switchStatements);
		}
		return null;
	}
}
