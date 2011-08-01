package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class PlayerWearingOnly extends EntityConditionalStatement<ArmorSet>
{
	public PlayerWearingOnly(boolean inverted, boolean forAttacker, ArmorSet armorSet)
	{  
		super(forAttacker, forAttacker, armorSet);
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return false;}
	@Override
	protected ArmorSet getRelevantInfo(TargetEventInfo eventInfo){ return null;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, PlayerWearingOnly.class, Pattern.compile(ModDamage.entityPart + "wearingonly\\." + ModDamage.armorRegex, Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerWearingOnly getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			ArmorSet armorSet = new ArmorSet(matcher.group(3));
			if(armorSet.isEmpty())
			return new PlayerWearingOnly(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"), armorSet);
		}
		return null;
	}
}
