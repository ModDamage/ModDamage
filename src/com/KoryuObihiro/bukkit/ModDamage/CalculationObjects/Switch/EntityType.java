package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Switch;

import java.util.HashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityType extends SwitchCalculation
{
	final boolean forAttacker;
	protected final HashMap<DamageElement, List<ModDamageCalculation>> switchLabels;
	public EntityType(boolean forAttacker, HashMap<DamageElement, List<ModDamageCalculation>> switchLabels)
	{
		this.forAttacker = forAttacker;
		this.switchLabels = switchLabels;
	}
	
	@Override
	public void calculate(DamageEventInfo eventInfo) 
	{
		DamageElement damageElement = (forAttacker?eventInfo.damageElement_attacker:eventInfo.damageElement_target);
		if(damageElement != null && switchLabels.containsKey(damageElement))
			for(ModDamageCalculation calculation : switchLabels.get(damageElement))
				calculation.calculate(eventInfo);
	}

	@Override
	public void calculate(SpawnEventInfo eventInfo) 
	{
		DamageElement damageElement = eventInfo.spawnedElement;
		if(damageElement != null && switchLabels.containsKey(damageElement))
			for(ModDamageCalculation calculation : switchLabels.get(damageElement))
				calculation.calculate(eventInfo);
	}

}
