package com.ModDamage.Variables.Ints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;

public class EventInt extends IntegerExp
{
	public static void register()
	{
		IntegerExp.register(
				Pattern.compile("\\w+"),
				new DynamicIntegerBuilder()
				{
					@Override
					public IntegerExp getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						DataRef<IntRef> intRef = info.get(IntRef.class, matcher.group(0).toLowerCase(), false);
						if (intRef == null) return null;
						
						return sm.acceptIf(new EventInt(intRef));
					}
				});
	}
	
	private final DataRef<IntRef> intRef;
	
	public EventInt(DataRef<IntRef> integerRef)
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
