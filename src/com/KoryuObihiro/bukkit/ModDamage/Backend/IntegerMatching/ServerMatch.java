package com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class ServerMatch extends IntegerMatch
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
	
	ServerMatch(ServerPropertyMatch propertyMatch){ this.propertyMatch = propertyMatch;}
	
	@Override
	public int getValue(TargetEventInfo eventInfo){ return propertyMatch.getProperty(eventInfo);}
}
