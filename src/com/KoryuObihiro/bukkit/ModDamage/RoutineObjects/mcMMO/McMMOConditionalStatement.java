package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.mcMMO;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;
import com.gmail.nossr50.mcMMO;

public abstract class McMMOConditionalStatement extends ConditionalStatement
{
	protected final EntityReference entityReference;
	
	protected McMMOConditionalStatement(boolean inverted, EntityReference entityReference)
	{
		super(inverted);
		this.entityReference = entityReference;
	}

	@Override
	protected final boolean condition(TargetEventInfo eventInfo)
	{
		return (ExternalPluginManager.getMcMMOPlugin() != null && entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER)
				?condition(eventInfo, ExternalPluginManager.getMcMMOPlugin(), (Player)entityReference.getEntity(eventInfo))
				:false);
	}
	
	protected abstract boolean condition(TargetEventInfo eventInfo, mcMMO mcMMOplugin, Player player);
	

}
