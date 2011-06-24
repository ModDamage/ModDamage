package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity;

import java.util.List;


import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityFallen extends EntityDamageConditionalCalculation 
{
	final int fallDistance;
	public EntityFallen(int fallDistance, boolean forAttacker, List<DamageCalculation> calculations)
	{ 
		this.fallDistance = fallDistance;
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getFallDistance() > fallDistance;}
	//TODO Check that this is the correct integer for falling.
}
