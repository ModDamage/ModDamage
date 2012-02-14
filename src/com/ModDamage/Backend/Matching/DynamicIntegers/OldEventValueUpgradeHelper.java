package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.Backend.Matching.DynamicInteger.DynamicIntegerBuilder;
import com.ModDamage.EventInfo.EventInfo;

public class OldEventValueUpgradeHelper
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("event_value", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						Set<String> names = info.getAllNames(Integer.class, "-default");
						names.remove("-default");
						for (String name : names)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "event_value has been removed. Please use \"" + name + "\" instead.");
							break;
						}
						
						return null;
					}
				});
	}
}
