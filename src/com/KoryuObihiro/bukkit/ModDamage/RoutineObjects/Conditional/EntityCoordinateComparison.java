package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityCoordinateComparison extends EntityComparison
{
	protected final Coordinate coordinateToCompare;
	public EntityCoordinateComparison(boolean inverted, boolean forAttacker, Coordinate coordinateToCompare, int value, ComparisonType comparisonType)
	{
		super(inverted, forAttacker, value, comparisonType);
		this.coordinateToCompare = coordinateToCompare;
	}
	
	@Override
	protected Integer getRelevantInfo(TargetEventInfo eventInfo)
	{
		if(eventInfo.getRelevantEntity(forAttacker) != null)	
			switch(coordinateToCompare)
			{
				case X:	return eventInfo.getRelevantEntity(forAttacker).getLocation().getBlockX();
				case Y:	return eventInfo.getRelevantEntity(forAttacker).getLocation().getBlockY();
				case Z:	return eventInfo.getRelevantEntity(forAttacker).getLocation().getBlockZ();
			}
		return 0; //shouldn't happen
	}
	
	private enum Coordinate
	{
		X, Y, Z;
		private static Coordinate matchType(String string)
		{
			for(Coordinate type : Coordinate.values())
				if(string.equalsIgnoreCase(type.name()))
					return type;
			return null;
		}
	}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityCoordinateComparison.class, Pattern.compile("(!?)(\\w+)\\.(X|Y|Z)\\.(\\w+)\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}

	public static EntityCoordinateComparison getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			ComparisonType comparisonType = ComparisonType.matchType(matcher.group(4));
			if(comparisonType != null)
				return new EntityCoordinateComparison(matcher.group(1).equalsIgnoreCase("!"), (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false, Coordinate.matchType(matcher.group(3)), Integer.parseInt(matcher.group(5)), comparisonType);
		}
		return null;
	}
}
