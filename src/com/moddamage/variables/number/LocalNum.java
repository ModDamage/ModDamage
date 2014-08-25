package com.moddamage.variables.number;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.StringMatcher;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.BaseDataParser;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

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