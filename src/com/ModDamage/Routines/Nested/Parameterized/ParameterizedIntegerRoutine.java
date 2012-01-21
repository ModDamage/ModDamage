package com.ModDamage.Routines.Nested.Parameterized;

import java.util.List;

import com.ModDamage.Backend.Matching.DynamicInteger;

abstract public class ParameterizedIntegerRoutine extends ParameterizedRoutine
{
	protected final List<DynamicInteger> integers;
	
	protected ParameterizedIntegerRoutine(String configString, List<DynamicInteger> integers)
	{
		super(configString);
		this.integers = integers;
	}
}
