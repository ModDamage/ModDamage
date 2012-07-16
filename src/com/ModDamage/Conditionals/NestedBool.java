package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataParser;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public abstract class NestedBool
{
	public static final Pattern openPattern = Pattern.compile("\\s*\\(\\s*");
	public static final Pattern closePattern = Pattern.compile("\\s*\\)\\s*");
	
	public static void register()
	{
		DataProvider.register(Boolean.class, null, openPattern, new IDataParser<Boolean>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<?> nullDP, Matcher m, StringMatcher sm)
				{
					IDataProvider<Boolean> bool = DataProvider.parse(info, Boolean.class, sm.spawn());
					
					if (!sm.matchesFront(closePattern))
					{
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Missing closing paren here: \"" + sm.string + "\"");
						return null;
					}
					
					sm.accept();
					return bool;
				}
			});
	}
}
