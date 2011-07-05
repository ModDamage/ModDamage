package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityWearingOnly extends EntityConditionalCalculation 
{
	final String armorSetString;
	public EntityWearingOnly(boolean inverted, boolean forAttacker, String armorSetString, List<ModDamageCalculation> calculations)
	{  
		this.inverted = inverted;
		this.forAttacker = forAttacker;
		this.armorSetString = armorSetString;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo)
	{ 
		return ((forAttacker?eventInfo.armorSetString_attacker:eventInfo.armorSetString_target) != null) 
				?(forAttacker?eventInfo.armorSetString_attacker:eventInfo.armorSetString_target).equals(armorSetString)
				:false;
	}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return false;}
}
