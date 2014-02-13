package com.ModDamage.Variables.Number;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.BaseDataParser;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class LocalNum
{	
	public static void register()
	{
		DataProvider.register(Number.class, null, Pattern.compile("\\$(\\w+)", Pattern.CASE_INSENSITIVE), new BaseDataParser<Number>()
				{
					@Override
					public IDataProvider<Number> parse(EventInfo info, Matcher m, StringMatcher sm)
					{
						return info.getLocal(m.group(1).toLowerCase());
					}
				});
	}
}