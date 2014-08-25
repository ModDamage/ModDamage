package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.BaseDataParser;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

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
				public IDataProvider<Boolean> parse(ScriptLine scriptLine, EventInfo info, Matcher m, StringMatcher sm)
				{
					return new ServerOnlineMode();
				}
			});
	}
}
