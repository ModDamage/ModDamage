package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalStatement;

public abstract class EntityConditionalStatement extends ConditionalStatement
{
	protected final EntityReference entityReference;
	public EntityConditionalStatement(boolean inverted, EntityReference entityReference)
	{ 
		super(inverted);
		this.entityReference = entityReference;
	}
}
