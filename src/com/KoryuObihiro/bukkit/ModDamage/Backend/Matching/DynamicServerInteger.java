package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import org.bukkit.Bukkit;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class DynamicServerInteger extends DynamicInteger
{
	protected final ServerPropertyMatch propertyMatch;
	enum ServerPropertyMatch //FIXME Merge with DynamicInteger?
	{
		OnlinePlayers{ @Override protected Integer getValue(){ return Bukkit.getOnlinePlayers().length;}},
		MaxPlayers{ @Override protected Integer getValue(){ return Bukkit.getMaxPlayers();}};
		abstract protected Integer getValue();
	}
	
	DynamicServerInteger(ServerPropertyMatch propertyMatch, boolean isNegative)
	{
		super(isNegative, false);
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public Integer getValue(TargetEventInfo eventInfo){ return (isNegative?-1:1) * propertyMatch.getValue();}
	
	@Override
	public String toString(){ return isNegative?"-":"" + "server_" + propertyMatch.name().toLowerCase();}
}
