package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;

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
		Location location = getRelevantEntity(eventInfo).getLocation();
		switch(coordinateToCompare)
		{
			case X:	return location.getBlockX();
			case Y:	return location.getBlockY();
			case Z:	return location.getBlockZ();
			default:return 0; //shouldn't happen
		}
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
		ConditionalRoutine.registerStatement(routineUtility, EntityCoordinateComparison.class, Pattern.compile("(!)?" + ModDamage.entityPart + "(X|Y|Z)\\." + ModDamage.comparisonRegex + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}

	public static EntityCoordinateComparison getNew(Matcher matcher)
	{
		if(matcher != null)
			return new EntityCoordinateComparison(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"), Coordinate.matchType(matcher.group(3)), Integer.parseInt(matcher.group(5)), ComparisonType.matchType(matcher.group(4)));
		return null;
	}
}
