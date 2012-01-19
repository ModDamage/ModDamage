package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.KoryuObihiro.bukkit.ModDamage.StringMatcher;
import com.KoryuObihiro.bukkit.ModDamage.Utils;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;

public class DynamicServerInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("server_("+ Utils.joinBy("|", ServerPropertyMatch.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm)
					{
						return new DynamicServerInteger(
								ServerPropertyMatch.valueOf(matcher.group(1).toUpperCase()));
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
	public int getValue(TargetEventInfo eventInfo){ return propertyMatch.getValue(); }
	
	@Override
	public String toString(){ return "server_" + propertyMatch.name().toLowerCase(); }
}
