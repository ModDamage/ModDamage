package com.ModDamage.Routines;

import java.util.Random;

import com.ModDamage.Backend.Matching.DynamicInteger;

public abstract class RandomRoutine extends ValueChangeRoutine
{
	protected RandomRoutine(String configString, ValueChangeType changeType, DynamicInteger number)
	{
		super(configString, changeType, number);
	}
	protected final Random random = new Random();
}