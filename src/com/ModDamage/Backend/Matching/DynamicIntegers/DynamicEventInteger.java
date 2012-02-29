package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.IntRef;
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
						DataRef<IntRef> intRef = info.get(IntRef.class, matcher.group(0).toLowerCase(), false);
						if (intRef == null) return null;
						
						return sm.acceptIf(new DynamicEventInteger(intRef));
					}
				});
	}
	
	private final DataRef<IntRef> intRef;
	
	public DynamicEventInteger(DataRef<IntRef> integerRef)
	{
		this.intRef = integerRef;
	}
	
	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		return intRef.get(data).value;
	}
	
	@Override
	public void setValue(EventData data, int value)
	{
		intRef.get(data).value = value;
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}

	@Override
	public String toString()
	{
		return intRef.toString();
	}

}
