package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.Random;

import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;

public abstract class RandomRoutine extends ValueChangeRoutine
{
	protected RandomRoutine(String configString, ValueChangeType changeType, DynamicInteger number)
	{
		super(configString, changeType, number);
	}
	protected final Random random = new Random();
}