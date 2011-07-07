package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

public class EntityGenericTypeSwitch extends EntitySwitchCalculation<DamageElement>
{
	public EntityGenericTypeSwitch(boolean forAttacker,LinkedHashMap<String, List<Object>> switchStatements){ super(forAttacker, switchStatements);}

	@Override
	protected DamageElement getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.damageElement_attacker:eventInfo.damageElement_target);}

	@Override
	protected DamageElement getRelevantInfo(SpawnEventInfo eventInfo){ return eventInfo.spawnedElement;}

	@Override
	protected DamageElement useMatcher(String key){ return DamageElement.matchLivingElement(key).getType();}
}
