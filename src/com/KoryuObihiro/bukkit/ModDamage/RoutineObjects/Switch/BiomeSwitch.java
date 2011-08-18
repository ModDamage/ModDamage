package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class BiomeSwitch extends EntitySwitchRoutine<List<Biome>>
{
	public BiomeSwitch(boolean forAttacker, LinkedHashMap<String, List<Routine>> switchLabels)
	{
		super(forAttacker, switchLabels);
	}
	@Override
	protected List<Biome> getRelevantInfo(TargetEventInfo eventInfo){ return Arrays.asList(getRelevantEntity(eventInfo).getLocation().getBlock().getBiome());}
	@Override
	protected boolean compare(List<Biome> info_1, List<Biome> info_2){ return info_2.contains(info_1.get(0));}
	@Override
	protected List<Biome> matchCase(String switchCase){ return ModDamage.matchBiomeAlias(switchCase);}
	
	public static void register(ModDamage routineUtility)
	{
		SwitchRoutine.registerStatement(routineUtility, BiomeSwitch.class, Pattern.compile("(\\w+)\\.biome", Pattern.CASE_INSENSITIVE));
	}
	
	public static BiomeSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		if(matcher != null && switchStatements != null)
		{
			boolean forAttacker = (ModDamage.matchesValidEntity(matcher.group(1)))?ModDamage.matchEntity(matcher.group(1)):false;
			return new BiomeSwitch(forAttacker, switchStatements);
		}
		return null;
	}
}
