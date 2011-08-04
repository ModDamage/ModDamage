package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class PlayerWearing extends EntityConditionalStatement<List<ArmorSet>>
{
	public PlayerWearing(boolean inverted, boolean forAttacker, List<ArmorSet> armorSet)
	{  
		super(inverted, forAttacker, armorSet);
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo)
	{ 
		for(ArmorSet armorSet : value)
			if(shouldGetAttacker(eventInfo)?armorSet.contains(((AttackerEventInfo)eventInfo).armorSet_attacker):(armorSet.contains(eventInfo.armorSet_target)))
				return true;
		return false;
	}
	@Override
	public List<ArmorSet> getRelevantInfo(TargetEventInfo eventInfo){ return null;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, PlayerWearing.class, Pattern.compile("(!)?" + ModDamage.entityRegex + "\\.wearing\\." + ModDamage.armorRegex, Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerWearing getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<ArmorSet> armorSet = ModDamage.matchArmorAlias(matcher.group(3));
			if(armorSet.isEmpty())
				return new PlayerWearing(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"), armorSet);
		}
		return null;
	}
}
