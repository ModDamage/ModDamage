package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityTypeSwitch extends EntitySwitchCalculation<DamageElement>
{
	public EntityTypeSwitch(boolean forAttacker, LinkedHashMap<DamageElement, List<Routine>> switchStatements){ super(forAttacker, switchStatements);}

	@Override
	protected DamageElement getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.element_attacker:eventInfo.element_target).getType();}

	@Override
	protected DamageElement getRelevantInfo(SpawnEventInfo eventInfo){ return eventInfo.element.getType();}
}
