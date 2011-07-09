package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.HashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class ArmorSetSwitch extends EntitySwitchCalculation<String>
{
	public ArmorSetSwitch(boolean forAttacker, HashMap<String, List<ModDamageCalculation>> switchStatements){ super(forAttacker, switchStatements);}

	@Override
	protected String getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.armorSetString_attacker:eventInfo.armorSetString_target);}

	@Override
	protected String getRelevantInfo(SpawnEventInfo eventInfo){ return null;}

}
