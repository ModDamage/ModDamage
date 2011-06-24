package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculation;

public class Set extends SpawnCalculation 
{
	private int setValue;
	public Set(int value){ setValue = value;}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth = setValue;}
}
