package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class DynamicServerInteger extends DynamicInteger
{
	protected final ServerPropertyMatch propertyMatch;
	enum ServerPropertyMatch
	{
		MaxPlayers,
		OnlinePlayers;
		private int getProperty(TargetEventInfo eventInfo)
		{
			
			switch(this)
			{
				case MaxPlayers: return TargetEventInfo.server.getMaxPlayers();
				case OnlinePlayers: return TargetEventInfo.server.getOnlinePlayers().length;
			}
			return 0;
		}
	}
	
	DynamicServerInteger(ServerPropertyMatch propertyMatch)
	{
		super(false);
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo){ return propertyMatch.getProperty(eventInfo);}
	
	@Override
	public String toString()
	{
		return "server." + propertyMatch.name().toLowerCase();
	}
}
