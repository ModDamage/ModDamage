package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

abstract public class ModDamageCalculation
{	
	abstract public void calculate(DamageEventInfo eventInfo);
	abstract public void calculate(SpawnEventInfo eventInfo);
}