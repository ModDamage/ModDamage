package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class BiomeSwitch extends EntitySwitchRoutine<HashSet<Biome>, Biome>
{
	public BiomeSwitch(String configString, EntityReference entityReference, LinkedHashMap<String, Object> switchLabels)
	{
		super(configString, entityReference, switchLabels);
	}
	@Override
	protected Biome getRelevantInfo(TargetEventInfo eventInfo){ return getRelevantEntity(eventInfo).getLocation().getBlock().getBiome();}
	@Override
	protected boolean compare(Biome info_event, HashSet<Biome> info_case){ return info_case.contains(info_event);}
	@Override
	protected HashSet<Biome> matchCase(String switchCase){ return ModDamage.matchBiomeAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerStatement(BiomeSwitch.class, Pattern.compile("switch\\.(\\w+)\\.biome", Pattern.CASE_INSENSITIVE));
	}
	
	public static BiomeSwitch getNew(Matcher matcher, LinkedHashMap<String, Object> switchStatements)
	{
		if(matcher != null && switchStatements != null && EntityReference.isValid(matcher.group(1)))
			return new BiomeSwitch(matcher.group(), EntityReference.match(matcher.group(1)), switchStatements);
		return null;
	}
}
