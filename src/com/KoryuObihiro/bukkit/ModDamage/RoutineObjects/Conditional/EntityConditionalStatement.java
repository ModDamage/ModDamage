package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
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
	
	abstract protected InputType getRelevantInfo(TargetEventInfo eventInfo);
	
	protected LivingEntity getRelevantEntity(TargetEventInfo eventInfo){ return (forAttacker && eventInfo instanceof AttackerEventInfo)?((AttackerEventInfo)eventInfo).entity_attacker:eventInfo.entity_target;}
	
	@Override
	protected boolean condition(TargetEventInfo eventInfo){ return getRelevantInfo(eventInfo).equals(value);}	
}
