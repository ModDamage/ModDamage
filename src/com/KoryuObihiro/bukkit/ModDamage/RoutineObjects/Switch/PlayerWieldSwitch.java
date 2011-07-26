package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class PlayerWieldSwitch extends EntitySwitchRoutine<List<Material>>
{
	public PlayerWieldSwitch(boolean forAttacker, LinkedHashMap<String, List<Routine>> switchStatements) 
	{
		super(forAttacker, switchStatements);
	}

	@Override
	protected List<Material> getRelevantInfo(DamageEventInfo eventInfo){ return Arrays.asList((forAttacker?eventInfo.materialInHand_attacker:eventInfo.materialInHand_target));}
	@Override
	protected List<Material> getRelevantInfo(SpawnEventInfo eventInfo){ return null;}
	@Override
	protected boolean compare(List<Material> info_1, List<Material> info_2){ return info_2.contains(info_1.get(0));}
	@Override
	protected List<Material> matchCase(String switchCase){ return ModDamage.matchItemAlias(switchCase);}
	
	public static void register(RoutineUtility routineUtility)
	{
		SwitchRoutine.registerStatement(routineUtility, PlayerWieldSwitch.class, Pattern.compile(RoutineUtility.entityPart + "wielding", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerWieldSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		PlayerWieldSwitch routine = null;
		if(matcher != null && switchStatements != null)
		{
			boolean forAttacker = matcher.group(1).equalsIgnoreCase("attacker");
			routine = new PlayerWieldSwitch(forAttacker, switchStatements);
			return (routine.isLoaded?routine:null);
		}
		return null;
	}
}
