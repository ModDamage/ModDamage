package com.ModDamage.Routines.Nested.Calculation;

import org.bukkit.entity.Player;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.Routines.Nested.CalculationRoutine;
import com.gmail.nossr50.mcMMO;

public abstract class McMMOCalculationRoutine extends CalculationRoutine
{
	final EntityReference entityReference;
	protected McMMOCalculationRoutine(String configString, DynamicInteger match, EntityReference entityReference)
	{
		super(configString, match);
		this.entityReference = entityReference;
	}
	
	@Override
	protected final void doCalculation(TargetEventInfo eventInfo, int input)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
		{
			mcMMO mcMMOplugin = ExternalPluginManager.getMcMMOPlugin();
			if(mcMMOplugin != null)
				applyEffect(((Player)entityReference.getEntity(eventInfo)), input, mcMMOplugin);
		}
	}

	abstract protected void applyEffect(Player player, int input, mcMMO mcMMOplugin);
}
