package com.ModDamage.Variables.Ints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;

public class ServerInt extends IntegerExp
{
	public static void register()
	{
		IntegerExp.register(
				Pattern.compile("server_("+ Utils.joinBy("|", ServerProperty.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public IntegerExp getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						return sm.acceptIf(new ServerInt(
								ServerProperty.valueOf(matcher.group(1).toUpperCase())));
					}
				});
	}
	
	protected final ServerProperty propertyMatch;
	enum ServerProperty
	{
		ONLINEPLAYERS { @Override protected int getValue(){ return Bukkit.getOnlinePlayers().length; }},
		MAXPLAYERS { @Override protected int getValue(){ return Bukkit.getMaxPlayers(); }},
		TIME { @Override protected int getValue(){ return (int) (System.currentTimeMillis() / 1000); }},
		TIMEMILLIS { @Override protected int getValue(){ return (int) (System.currentTimeMillis()); }};
		
		abstract protected int getValue();
	}
	
	ServerInt(ServerProperty propertyMatch)
	{
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	protected int myGetValue(EventData data){ return propertyMatch.getValue(); }
	
	@Override
	public String toString(){ return "server_" + propertyMatch.name().toLowerCase(); }
}
