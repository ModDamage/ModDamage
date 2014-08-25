package com.moddamage.conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.moddamage.StringMatcher;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.BaseDataParser;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

public class ServerOnlineMode implements IDataProvider<Boolean>
{
	public static final Pattern pattern = Pattern.compile("server\\.onlineMode", Pattern.CASE_INSENSITIVE);
	
	@Override
	public Boolean get(EventData data)
	{
		return Bukkit.getOnlineMode();
	}

	@Override
	public Class<Boolean> provides() { return Boolean.class; }
	
	public static void register()
	{
		DataProvider.register(Boolean.class, pattern, new BaseDataParser<Boolean>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, Matcher m, StringMatcher sm)
				{
					return new ServerOnlineMode();
				}
			});
	}
}
