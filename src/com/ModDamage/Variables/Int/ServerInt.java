package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.BaseDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class ServerInt implements IDataProvider<Integer>
{
	public static void register()
	{
		DataProvider.register(Integer.class, 
				Pattern.compile("server_("+ Utils.joinBy("|", ServerProperty.values()) +")", Pattern.CASE_INSENSITIVE), 
				new BaseDataParser<Integer>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, Matcher m, StringMatcher sm)
					{
						return sm.acceptIf(new ServerInt(
								ServerProperty.valueOf(m.group(1).toUpperCase())));
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
	public Integer get(EventData data) { return propertyMatch.getValue(); }
	
	@Override
	public Class<Integer> provides() { return Integer.class; }
	
	@Override
	public String toString(){ return "server_" + propertyMatch.name().toLowerCase(); }
}
