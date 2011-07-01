package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityWearingOnly extends EntityConditionaDamageCalculation 
{
	final String armorSetString;
	public EntityWearingOnly(boolean inverted, boolean forAttacker, String armorSetString, List<DamageCalculation> calculations)
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
}
