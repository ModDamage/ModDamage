package com.ModDamage.Variables.Ints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;

public class NegativeInt extends IntegerExp
{
	public static void register()
	{
		IntegerExp.register(
				Pattern.compile("-"),
				new DynamicIntegerBuilder()
				{
					@Override
					public IntegerExp getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						IntegerExp integer = IntegerExp.getIntegerFromFront(sm.spawn(), info);
						if (integer == null) return null;
						
						return sm.acceptIf(new NegativeInt(integer));
					}
				});
	}
	
	IntegerExp integer;
	
	public NegativeInt(IntegerExp integer)
	{
		this.integer = integer;
	}
	
	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		return -integer.getValue(data);
	}

	@Override
	public String toString()
	{
		return "-"+integer;
	}
}
