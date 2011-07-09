package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class WorldTime extends WorldConditionalCalculation 
{
	private long beginningTime;
	private long endTime;
	public WorldTime(boolean inverted, World world, int beginningTime, int endTime, List<ModDamageCalculation> calculations)
	{
		super(beginningTime > endTime | inverted, world, calculations);
		this.beginningTime = beginningTime;
		this.endTime = endTime;
	}
	public WorldTime(boolean inverted, int beginningTime, int endTime, List<ModDamageCalculation> calculations)
	{
		super(beginningTime > endTime | inverted, calculations);
		this.beginningTime = beginningTime;
		this.endTime = endTime;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (world.getTime() > beginningTime && world.getTime() < endTime);}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return (world.getTime() > beginningTime && world.getTime() < endTime);}
}
