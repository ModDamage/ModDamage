package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;
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
