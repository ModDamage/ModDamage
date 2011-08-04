package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class PlayerWieldSwitch extends EntitySwitchRoutine<List<Material>>
{
	public PlayerWieldSwitch(boolean forAttacker, LinkedHashMap<String, List<Routine>> switchStatements) 
	{
		super(forAttacker, switchStatements);
	}
	@Override
	protected List<Material> getRelevantInfo(TargetEventInfo eventInfo){ return Arrays.asList((forAttacker && eventInfo instanceof AttackerEventInfo)?((AttackerEventInfo)eventInfo).materialInHand_attacker:eventInfo.materialInHand_target);}
	@Override
	protected boolean compare(List<Material> info_1, List<Material> info_2){ return info_2.contains(info_1.get(0));}
	@Override
	protected List<Material> matchCase(String switchCase)
	{
		List<Material> items = ModDamage.matchItemAlias(switchCase);
		return items.isEmpty()?null:items;
	}
	
	public static void register(ModDamage routineUtility)
	{
		SwitchRoutine.registerStatement(routineUtility, PlayerWieldSwitch.class, Pattern.compile(ModDamage.entityRegex + "wielding", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerWieldSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		if(matcher != null && switchStatements != null)
		{
			boolean forAttacker = matcher.group(1).equalsIgnoreCase("attacker");
			return new PlayerWieldSwitch(forAttacker, switchStatements);
		}
		return null;
	}
}
