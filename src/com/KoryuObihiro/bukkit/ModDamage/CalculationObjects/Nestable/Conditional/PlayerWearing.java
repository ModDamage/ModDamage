package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;

public class PlayerWearing extends EntityConditionalStatement<ArmorSet>
{
	public PlayerWearing(boolean inverted, boolean forAttacker, ArmorSet armorSet)
	{  
		super(inverted, forAttacker, armorSet);
	}
	@Override
	public ArmorSet getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.armorSetString_attacker:eventInfo.armorSetString_target);}
	@Override
	public ArmorSet getRelevantInfo(SpawnEventInfo eventInfo){ return null;}
	
	public static void register()
	{
		ConditionalCalculation.registerStatement(PlayerWearing.class, Pattern.compile(CalculationUtility.entityPart + "wearing\\." + CalculationUtility.armorRegex, Pattern.CASE_INSENSITIVE));
	}
}
