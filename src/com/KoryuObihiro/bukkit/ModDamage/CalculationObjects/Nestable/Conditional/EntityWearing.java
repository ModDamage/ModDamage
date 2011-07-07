package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityWearing extends EntityConditionalCalculation<String>
{
	public EntityWearing(boolean inverted, boolean forAttacker, String armorSetString, List<ModDamageCalculation> calculations)
	{  
		super(inverted, forAttacker, armorSetString, calculations);
	}
	@Override
	public String getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.armorSetString_attacker:eventInfo.armorSetString_target);}
	@Override
	public String getRelevantInfo(SpawnEventInfo eventInfo){ return null;}
}
