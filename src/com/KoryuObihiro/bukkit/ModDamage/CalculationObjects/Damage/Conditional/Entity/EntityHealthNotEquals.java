package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity;

import java.util.List;


import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityHealthNotEquals extends EntityDamageConditionalCalculation 
{
	int value;
	public EntityHealthNotEquals(boolean inverted, boolean forAttacker, int compareTo, List<DamageCalculation> calculations)
	{ 
		this.inverted = inverted;
		this.forAttacker = forAttacker;
		this.value = compareTo;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getHealth() != value;}
}
