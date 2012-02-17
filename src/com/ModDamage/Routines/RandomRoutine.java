package com.ModDamage.Routines;

import java.util.Random;

import com.ModDamage.Backend.IntRef;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;

public abstract class RandomRoutine extends ValueChangeRoutine
{
	protected RandomRoutine(String configString, DataRef<IntRef> defaultRef, ValueChangeType changeType, DynamicInteger number)
	{
		super(configString, defaultRef, changeType, number);
	}
	protected final Random random = new Random();
}