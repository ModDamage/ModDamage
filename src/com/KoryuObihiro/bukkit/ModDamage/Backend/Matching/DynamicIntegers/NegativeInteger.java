package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.StringMatcher;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;

public class NegativeInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("-"),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm)
					{
						DynamicInteger integer = DynamicInteger.getIntegerFromFront(sm.spawn());
						if (integer != null)
						{
							sm.accept();
							return new NegativeInteger(integer);
						}
						return null;
					}
				});
	}
	
	DynamicInteger integer;
	
	public NegativeInteger(DynamicInteger integer)
	{
		this.integer = integer;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		return -integer.getValue(eventInfo);
	}
}
