package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

public interface ModDamageCalculation
{
	public void calculate(DamageEventInfo eventInfo);
	public void calculate(SpawnEventInfo eventInfo);
}