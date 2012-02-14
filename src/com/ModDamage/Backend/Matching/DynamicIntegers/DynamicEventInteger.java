package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class DynamicEventInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("\\w+"),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						DataRef<Integer> integerRef = info.get(Integer.class, matcher.group(0).toLowerCase(), false);
						if (integerRef == null) return null;
						
						return sm.acceptIf(new DynamicEventInteger(integerRef));
					}
				});
	}
	
	final DataRef<Integer> integerRef;
	
	public DynamicEventInteger(DataRef<Integer> integerRef)
	{
		this.integerRef = integerRef;
	}
	
	@Override
	public int getValue(EventData data)
	{
		return integerRef.get(data);
	}
	
	@Override
	public void setValue(EventData data, int value)
	{
		integerRef.set(data, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}

	@Override
	public String toString()
	{
		return integerRef.toString();
	}

}
