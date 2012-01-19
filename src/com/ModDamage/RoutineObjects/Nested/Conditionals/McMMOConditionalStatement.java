package com.ModDamage.RoutineObjects.Nested.Conditionals;

import org.bukkit.entity.Player;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.RoutineObjects.Nested.Conditional;
import com.gmail.nossr50.mcMMO;

public abstract class McMMOConditionalStatement extends Conditional
{
	protected final EntityReference entityReference;
	
	protected McMMOConditionalStatement(EntityReference entityReference)
	{
		this.entityReference = entityReference;
	}

	@Override
	public boolean evaluate(TargetEventInfo eventInfo)
	{
		mcMMO mcmmo = ExternalPluginManager.getMcMMOPlugin();
		if (mcmmo != null && entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
			return evaluate(eventInfo, mcmmo, (Player)entityReference.getEntity(eventInfo));
		return false;
	}
	
	protected abstract boolean evaluate(TargetEventInfo eventInfo, mcMMO mcMMOplugin, Player player);
}
