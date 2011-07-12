package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
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
	
	public static void register()
	{
		CalculationUtility.register(ServerPlayerCount.class, Pattern.compile(CalculationUtility.ifPart + "server\\.playercount" + CalculationUtility.comparisonPart + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
