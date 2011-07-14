package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;

public class ServerPlayerCount extends ServerComparison
{
	public ServerPlayerCount(boolean inverted, int value, ComparisonType comparisonType)
	{
		super(inverted, value, comparisonType);
	}
	@Override
	protected int getRelevantInfo(SpawnEventInfo eventInfo){ return server.getOnlinePlayers().length;}
	@Override
	protected int getRelevantInfo(DamageEventInfo eventInfo){ return server.getOnlinePlayers().length;}
	
	public static void register()
	{
		ConditionalCalculation.registerStatement(ServerPlayerCount.class, Pattern.compile("server\\.playercount" + CalculationUtility.comparisonRegex + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
