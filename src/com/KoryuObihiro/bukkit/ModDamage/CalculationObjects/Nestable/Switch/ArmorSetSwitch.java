package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class ArmorSetSwitch extends EntitySwitchCalculation<ArmorSet>
{
	public ArmorSetSwitch(boolean forAttacker, LinkedHashMap<ArmorSet, List<ModDamageCalculation>> switchStatements){ super(forAttacker, switchStatements);}

	@Override
	protected ArmorSet getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.armorSetString_attacker:eventInfo.armorSetString_target);}

	@Override
	protected ArmorSet getRelevantInfo(SpawnEventInfo eventInfo){ return null;}

}
