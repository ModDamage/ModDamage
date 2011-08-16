package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class PlayerWearingOnly extends EntityConditionalStatement<List<ArmorSet>>
{
	public PlayerWearingOnly(boolean inverted, boolean forAttacker, List<ArmorSet> armorSetList)
	{  
		super(forAttacker, forAttacker, armorSetList);
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo)
	{
		for(ArmorSet armorSet : value)
			if(shouldGetAttacker(eventInfo)?armorSet.equals(((AttackerEventInfo)eventInfo).armorSet_attacker):(armorSet.equals(eventInfo.armorSet_target)))
				return true;
		return false;
	}
	@Override
	protected List<ArmorSet> getRelevantInfo(TargetEventInfo eventInfo){ return null;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, PlayerWearingOnly.class, Pattern.compile("(\\w+)\\.wearingonly\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerWearingOnly getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<ArmorSet> armorSetList = ModDamage.matchArmorAlias(matcher.group(3));
			if(armorSetList.isEmpty())
				return new PlayerWearingOnly((ModDamage.matchesValidEntity(matcher.group(2))?ModDamage.matchEntity(matcher.group(2)):false), matcher.group(2).equalsIgnoreCase("attacker"), armorSetList);
		}
		return null;
	}
}
