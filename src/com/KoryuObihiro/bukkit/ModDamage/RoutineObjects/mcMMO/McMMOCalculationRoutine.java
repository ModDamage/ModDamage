package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.mcMMO;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.gmail.nossr50.mcMMO;

public abstract class McMMOCalculationRoutine extends CalculationRoutine<Player>
{
	final EntityReference entityReference;
	protected McMMOCalculationRoutine(String configString, DynamicInteger match, EntityReference entityReference)
	{
		super(configString, match);
		this.entityReference = entityReference;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
			super.run(eventInfo);
	}
	
	@Override
	protected final void applyEffect(Player player, int input)
	{
		mcMMO mcMMOplugin = ExternalPluginManager.getMcMMOPlugin();
		if(mcMMOplugin != null)
			applyEffect(player, input, mcMMOplugin);
	}

	abstract protected void applyEffect(Player player, int input, mcMMO mcMMOplugin);
}