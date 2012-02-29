package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class NegativeInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("-"),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						DynamicInteger integer = DynamicInteger.getIntegerFromFront(sm.spawn(), info);
						if (integer == null) return null;
						
						return sm.acceptIf(new NegativeInteger(integer));
					}
				});
	}
	
	DynamicInteger integer;
	
	public NegativeInteger(DynamicInteger integer)
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
