package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

abstract public class ModDamageCalculation
{	
	final boolean isLoaded;
	ModDamageCalculation(boolean isLoaded){ this.isLoaded = isLoaded;}
	public boolean loaded(){ return isLoaded;}
	
	abstract public void calculate(DamageEventInfo eventInfo);
	abstract public void calculate(SpawnEventInfo eventInfo);
}