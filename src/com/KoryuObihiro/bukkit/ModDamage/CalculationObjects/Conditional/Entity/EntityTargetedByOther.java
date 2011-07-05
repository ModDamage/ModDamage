package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity;

import java.util.List;

import org.bukkit.entity.Creature;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityTargetedByOther extends EntityConditionalCalculation 
{
	public EntityTargetedByOther(boolean inverted, boolean forAttacker, List<ModDamageCalculation> calculations)
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
	public boolean condition(SpawnEventInfo eventInfo){ return false;}
	//TODO Make sure that Slimes work here - they failed in a previous RB.
}
