package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;

public abstract class DamageCalculation
{
	public abstract void calculate(DamageEventInfo eventInfo);
}