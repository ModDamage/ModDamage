package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class DynamicServerInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("server_("+ Utils.joinBy("|", ServerPropertyMatch.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						return sm.acceptIf(new DynamicServerInteger(
								ServerPropertyMatch.valueOf(matcher.group(1).toUpperCase())));
					}
				});
	}
	
	protected final ServerPropertyMatch propertyMatch;
	enum ServerPropertyMatch
	{
		OnlinePlayers { @Override protected int getValue(){ return Bukkit.getOnlinePlayers().length; }},
		MaxPlayers { @Override protected int getValue(){ return Bukkit.getMaxPlayers(); }};
		
		abstract protected int getValue();
	}
	
	DynamicServerInteger(ServerPropertyMatch propertyMatch)
	{
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	protected int myGetValue(EventData data){ return propertyMatch.getValue(); }
	
	@Override
	public String toString(){ return "server_" + propertyMatch.name().toLowerCase(); }
}
