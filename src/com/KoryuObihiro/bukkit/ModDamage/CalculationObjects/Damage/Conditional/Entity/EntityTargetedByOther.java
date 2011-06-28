package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity;

import java.util.List;

import org.bukkit.entity.Creature;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityTargetedByOther extends EntityConditionaDamageCalculation 
{
	public EntityTargetedByOther(boolean inverted, boolean forAttacker, List<DamageCalculation> calculations)
	{ 
		this.inverted = inverted;
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo)
	{ 
		return (forAttacker?((Creature)eventInfo.entity_attacker).getTarget().equals(eventInfo.entity_target)
				:((Creature)eventInfo.entity_target).getTarget().equals(eventInfo.entity_attacker));
	}
	//TODO Make sure that Slimes work here - they failed in a previous RB.
}
