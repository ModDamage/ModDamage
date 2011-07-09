package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.HashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityTypeSwitch extends EntitySwitchCalculation<DamageElement>
{
	public EntityTypeSwitch(boolean forAttacker, HashMap<DamageElement, List<ModDamageCalculation>> switchStatements){ super(forAttacker, switchStatements);}

	@Override
	protected DamageElement getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.damageElement_attacker:eventInfo.damageElement_target).getType();}

	@Override
	protected DamageElement getRelevantInfo(SpawnEventInfo eventInfo){ return eventInfo.spawnedElement.getType();}
}
