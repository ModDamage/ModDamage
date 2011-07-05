package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityFallen extends EntityConditionalCalculation 
{
	final int fallDistance;
	public EntityFallen(boolean inverted, boolean forAttacker, int fallDistance, List<ModDamageCalculation> calculations)
	{ 
		this.inverted = inverted;
		this.fallDistance = fallDistance;
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getFallDistance() > fallDistance;}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return eventInfo.entity.getFallDistance() > fallDistance;}
}
