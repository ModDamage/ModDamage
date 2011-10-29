package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.Random;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public abstract class RandomRoutine extends Routine
{
	protected RandomRoutine(String configString)
	{
		super(configString);
	}
	protected final Random random = new Random();
}