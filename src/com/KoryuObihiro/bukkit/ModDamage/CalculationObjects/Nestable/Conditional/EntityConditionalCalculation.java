package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;


import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public abstract class EntityConditionalCalculation<InputType> extends ConditionalCalculation
{
	protected final boolean forAttacker;
	protected final InputType value;
	
	public EntityConditionalCalculation(boolean inverted, boolean forAttacker, InputType value, List<ModDamageCalculation> calculations)
	{ 
		super(inverted, calculations);
		this.forAttacker = forAttacker; 
		this.value = value;
	}
	
	abstract protected InputType getRelevantInfo(DamageEventInfo eventInfo);
	
	abstract protected InputType getRelevantInfo(SpawnEventInfo eventInfo);
	
	protected LivingEntity getRelevantEntity(DamageEventInfo eventInfo){ return forAttacker?eventInfo.entity_attacker:eventInfo.entity_target;}
	
	protected LivingEntity getRelevantEntity(SpawnEventInfo eventInfo){ return eventInfo.entity;}
	
	@Override
	protected boolean condition(DamageEventInfo eventInfo){ return getRelevantInfo(eventInfo).equals(value);}
	
	@Override
	protected boolean condition(SpawnEventInfo eventInfo){ return getRelevantInfo(eventInfo).equals(value);}
}
