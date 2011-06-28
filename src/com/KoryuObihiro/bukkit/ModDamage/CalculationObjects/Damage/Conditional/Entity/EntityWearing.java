package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityWearing extends EntityConditionaDamageCalculation 
{
	final String armorSetString;
	public EntityWearing(boolean inverted, boolean forAttacker, String armorSetString, List<DamageCalculation> calculations)
	{  
		this.inverted = inverted;
		this.forAttacker = forAttacker;
		this.armorSetString = armorSetString;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.armorSetString_attacker:eventInfo.armorSetString_target).contains(armorSetString);}
	//TODO Check that this is the correct integer for falling.
}
