package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import org.bukkit.Bukkit;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class DynamicServerInteger extends DynamicInteger
{
	protected final ServerPropertyMatch propertyMatch;
	enum ServerPropertyMatch //FIXME Merge with DynamicInteger?
	{
		OnlinePlayers
		{
			@Override
			protected Integer getValue()
			{
				return Bukkit.getOnlinePlayers().length;
			}
		},
		MaxPlayers
		{
			@Override
			protected Integer getValue()
			{
				return Bukkit.getMaxPlayers();
			}
		};
		abstract protected Integer getValue();
	}
	
	DynamicServerInteger(ServerPropertyMatch propertyMatch)
	{
		super(false);
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public Integer getValue(TargetEventInfo eventInfo){ return propertyMatch.getValue();}
	
	@Override
	public String toString()
	{
		return "server." + propertyMatch.name().toLowerCase();
	}
}
