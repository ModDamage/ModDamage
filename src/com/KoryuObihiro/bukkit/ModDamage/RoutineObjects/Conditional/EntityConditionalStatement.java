package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public abstract class EntityConditionalStatement<InputType> extends ConditionalStatement
{
	protected final boolean forAttacker;
	protected final InputType value;
	public EntityConditionalStatement(boolean inverted, boolean forAttacker, InputType value)
	{ 
		super(inverted);
		this.forAttacker = forAttacker; 
		this.value = value;
	}
	
	abstract protected InputType getRelevantInfo(DamageEventInfo eventInfo);
	
	abstract protected InputType getRelevantInfo(SpawnEventInfo eventInfo);
	
	protected LivingEntity getRelevantEntity(DamageEventInfo eventInfo){ return forAttacker?eventInfo.entity_attacker:eventInfo.entity_target;}
	
	protected LivingEntity getRelevantEntity(SpawnEventInfo eventInfo){ return eventInfo.entity;}
	
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return getRelevantInfo(eventInfo).equals(value);}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return getRelevantInfo(eventInfo).equals(value);}
	
}
