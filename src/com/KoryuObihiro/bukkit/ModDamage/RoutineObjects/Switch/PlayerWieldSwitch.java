package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class PlayerWieldSwitch extends EntitySwitchCalculation<Material>
{
	public PlayerWieldSwitch(boolean forAttacker, LinkedHashMap<String, List<Routine>> switchStatements) 
	{
		super(forAttacker, switchStatements);
	}

	@Override
	protected Material getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.materialInHand_attacker:eventInfo.materialInHand_target);}
	@Override
	protected Material getRelevantInfo(SpawnEventInfo eventInfo){ return null;}

	@Override
	protected Material matchCase(String switchCase){ return Material.matchMaterial(switchCase);}
	
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
