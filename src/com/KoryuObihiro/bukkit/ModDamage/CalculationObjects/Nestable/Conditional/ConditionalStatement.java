package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.Set;

public class ConditionalStatement
{
	final boolean inverted;
	public ConditionalStatement(boolean inverted)
	{
		this.inverted = inverted;
	}
	
	protected boolean condition(DamageEventInfo eventInfo) 
	{
		return Math.abs(random.nextInt()%101) <= chance;
	}
	protected boolean condition(SpawnEventInfo eventInfo) 
	{
		return Math.abs(random.nextInt()%101) <= chance;
	}
}
