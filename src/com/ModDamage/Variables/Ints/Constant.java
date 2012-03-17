package com.ModDamage.Variables.Ints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;

public class Constant extends IntegerExp
{
	public static void register()
	{
		IntegerExp.register(
				Pattern.compile("[0-9]+"),
				new DynamicIntegerBuilder()
				{
					@Override
					public IntegerExp getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						return sm.acceptIf(new Constant(Integer.parseInt(matcher.group(0))));
					}
				});
	}
	
	int constant;
	
	public Constant(int constant)
	{
		this.constant = constant;
	}
	
	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		return constant;
	}
	
	@Override
	public String toString()
	{
		return ""+constant;
	}
}
