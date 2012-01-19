package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;

public class ConstantInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("[0-9]+"),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm)
					{
						sm.accept();
						return new ConstantInteger(Integer.parseInt(matcher.group(0)));
					}
				});
	}
	
	int constant;
	
	public ConstantInteger(int constant)
	{
		this.constant = constant;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		return constant;
	}
	
	@Override
	public String toString()
	{
		return ""+constant;
	}
}
