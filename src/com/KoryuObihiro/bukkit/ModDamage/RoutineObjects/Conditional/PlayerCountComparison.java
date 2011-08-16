package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class PlayerCountComparison extends ServerComparison
{
	public PlayerCountComparison(boolean inverted, int value, ComparisonType comparisonType)
	{
		super(inverted, value, comparisonType);
	}
	@Override
	protected int getRelevantInfo(TargetEventInfo eventInfo){ return server.getOnlinePlayers().length;}
	@Override
	protected int getRelevantInfo(AttackerEventInfo eventInfo){ return server.getOnlinePlayers().length;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, PlayerCountComparison.class, Pattern.compile("(!)?server\\.playercount\\.(\\w+)\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerCountComparison getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			ComparisonType comparisonType = ComparisonType.matchType(matcher.group(2));
			if(comparisonType != null)
				return new PlayerCountComparison(matcher.group(1) != null, Integer.parseInt(matcher.group(2)), comparisonType);
		}
		return null;
	}
}
