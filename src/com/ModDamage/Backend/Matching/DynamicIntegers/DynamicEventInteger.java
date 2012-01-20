package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;

public class DynamicEventInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("event_value", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm)
					{
						sm.accept();
						return new DynamicEventInteger();
					}
				});
	}
	
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		return eventInfo.eventValue;
	}
	
	@Override
	public void setValue(TargetEventInfo eventInfo, int value)
	{
		eventInfo.eventValue = value;
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}

}
