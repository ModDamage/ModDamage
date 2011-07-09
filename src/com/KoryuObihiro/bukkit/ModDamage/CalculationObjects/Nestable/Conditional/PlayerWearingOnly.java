package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class PlayerWearingOnly extends EntityConditionalCalculation<String>
{
	public PlayerWearingOnly(boolean inverted, boolean forAttacker, String armorSetString, List<ModDamageCalculation> calculations)
	{  
		super(forAttacker, forAttacker, armorSetString, calculations);
	}
	
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return false;}
	
	@Override
	protected String getRelevantInfo(DamageEventInfo eventInfo){ return(forAttacker?eventInfo.armorSetString_attacker:eventInfo.armorSetString_target);}
	@Override
	protected String getRelevantInfo(SpawnEventInfo eventInfo){ return null;}
}
