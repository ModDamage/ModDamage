package com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class WorldMatch extends IntegerMatch
{
	protected final WorldPropertyMatch propertyMatch;
	enum WorldPropertyMatch
	{
		OnlinePlayers,
		Time(true);
		
		public boolean settable = false;
		private WorldPropertyMatch(){}
		private WorldPropertyMatch(boolean settable)
		{
			this.settable = settable;
		}
		
		private int getProperty(TargetEventInfo eventInfo)
		{
			switch(this)
			{
				case OnlinePlayers: return eventInfo.world.getPlayers().size();
				case Time: return (int)eventInfo.world.getTime();
			}
			return 0;
		}
	}
	
	WorldMatch(WorldPropertyMatch propertyMatch)
	{
		super(propertyMatch.settable);
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo){ return propertyMatch.getProperty(eventInfo);}
	
	@Override
	public String toString()
	{
		return "world." + propertyMatch.name().toLowerCase();
	}
}
