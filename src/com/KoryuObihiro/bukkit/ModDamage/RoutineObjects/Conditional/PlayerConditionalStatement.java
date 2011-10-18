package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public abstract class PlayerConditionalStatement extends EntityConditionalStatement
{
	public PlayerConditionalStatement(boolean inverted, EntityReference entityReference) 
	{
		super(inverted, entityReference);
	}
	
	protected Player getRelevantPlayer(TargetEventInfo eventInfo)
	{
		return (entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER)?(Player)entityReference.getEntity(eventInfo):null);
	}
}
