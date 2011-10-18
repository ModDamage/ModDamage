package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class DynamicWorldInteger extends DynamicInteger
{
	protected final WorldPropertyMatch propertyMatch;
	enum WorldPropertyMatch //FIXME Merge with DynamicInteger?
	{
		OnlinePlayers
		{
			@Override
			protected Integer getProperty(TargetEventInfo eventInfo)
			{
				return eventInfo.world.getPlayers().size();
			}
		},
		Time(true)
		{
			@Override
			protected Integer getProperty(TargetEventInfo eventInfo)
			{
				return (int)eventInfo.world.getTime();
			}
		};
		
		public boolean settable = false;
		private WorldPropertyMatch(){}
		private WorldPropertyMatch(boolean settable)
		{
			this.settable = settable;
		}
		
		abstract protected Integer getProperty(TargetEventInfo eventInfo);
	}
	
	DynamicWorldInteger(WorldPropertyMatch propertyMatch)
	{
		super(propertyMatch.settable);
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public Integer getValue(TargetEventInfo eventInfo){ return propertyMatch.getProperty(eventInfo);}
	
	@Override
	public String toString()
	{
		return "world." + propertyMatch.name().toLowerCase();
	}
}
