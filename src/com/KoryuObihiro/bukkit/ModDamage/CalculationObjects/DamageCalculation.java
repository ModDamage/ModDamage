package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;

public abstract class DamageCalculation
{
	public abstract int calculate(EventInfo eventInfo, int eventDamage);
}