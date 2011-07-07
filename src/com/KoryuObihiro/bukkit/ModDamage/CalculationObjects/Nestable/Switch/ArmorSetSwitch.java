package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

public class ArmorSetSwitch extends EntitySwitchCalculation<String>
{
	public ArmorSetSwitch(boolean forAttacker, LinkedHashMap<String, List<Object>> switchStatements){ super(forAttacker, switchStatements);}

	@Override
	protected String useMatcher(String key){ return new ArmorSet(key).toString();}

	@Override
	protected String getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.armorSetString_attacker:eventInfo.armorSetString_target);}

	@Override
	protected String getRelevantInfo(SpawnEventInfo eventInfo){ return null;}

}
