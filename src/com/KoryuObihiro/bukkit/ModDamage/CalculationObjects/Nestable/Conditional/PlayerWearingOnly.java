package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;

public class PlayerWearingOnly extends EntityConditionalStatement<ArmorSet>
{
	public PlayerWearingOnly(boolean inverted, boolean forAttacker, ArmorSet armorSet)
	{  
		super(forAttacker, forAttacker, armorSet);
	}
	
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return false;}
	
	@Override
	protected ArmorSet getRelevantInfo(DamageEventInfo eventInfo){ return(forAttacker?eventInfo.armorSetString_attacker:eventInfo.armorSetString_target);}
	@Override
	protected ArmorSet getRelevantInfo(SpawnEventInfo eventInfo){ return null;}
	
	public static void register()
	{
		ConditionalCalculation.registerStatement(PlayerWearingOnly.class, Pattern.compile(CalculationUtility.entityPart + "wearingonly\\." + CalculationUtility.armorRegex, Pattern.CASE_INSENSITIVE));
	}
}
