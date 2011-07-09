package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class ServerPlayerCount extends ServerComparison
{
	public ServerPlayerCount(boolean inverted, int value, ComparisonType comparisonType, List<ModDamageCalculation> calculations)
	{
		super(inverted, value, comparisonType, calculations);
	}
	@Override
	protected int getRelevantInfo(SpawnEventInfo eventInfo){ return server.getOnlinePlayers().length;}
	@Override
	protected int getRelevantInfo(DamageEventInfo eventInfo){ return server.getOnlinePlayers().length;}
}
