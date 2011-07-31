package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class PlayerWearing extends EntityConditionalStatement<ArmorSet>
{
	public PlayerWearing(boolean inverted, boolean forAttacker, ArmorSet armorSet)
	{  
		super(inverted, forAttacker, armorSet);
	}
	@Override
	public ArmorSet getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.armorSet_attacker:eventInfo.armorSet_target);}
	@Override
	public ArmorSet getRelevantInfo(SpawnEventInfo eventInfo){ return null;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, PlayerWearing.class, Pattern.compile("(!)?" + ModDamage.entityPart + "wearing\\." + ModDamage.armorRegex, Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerWearing getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			ArmorSet armorSet = new ArmorSet(matcher.group(3));
			if(armorSet.isEmpty())
			return new PlayerWearing(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"), armorSet);
		}
		return null;
	}
}
