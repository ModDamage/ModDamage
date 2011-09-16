package com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class WorldMatch extends IntegerMatch
{
	protected final WorldPropertyMatch propertyMatch;
	enum WorldPropertyMatch implements MatcherEnum
	{
		OnlinePlayers,
		Time;
		
		private long getProperty(TargetEventInfo eventInfo)
		{
			switch(this)
			{
				case OnlinePlayers: return eventInfo.world.getPlayers().size();
				case Time: return eventInfo.world.getTime();
			}
			return 0;
		}
	}
	
	WorldMatch(WorldPropertyMatch propertyMatch){ this.propertyMatch = propertyMatch;}
	
	@Override
	public long getValue(TargetEventInfo eventInfo){ return propertyMatch.getProperty(eventInfo);}
}
