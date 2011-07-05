package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityFalling extends EntityConditionalCalculation 
{
	public EntityFalling(boolean inverted, boolean forAttacker, List<ModDamageCalculation> calculations)
	{  
		this.inverted = inverted;
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getFallDistance() > 3;}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return eventInfo.entity.getFallDistance() > 3;}
	//TODO Check that this is the correct integer for falling.
}
