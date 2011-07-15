package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import org.bukkit.Location;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;

public class EntityCoordinateComparison extends EntityComparison
{
	protected final byte coordinateToCompare;
	public EntityCoordinateComparison(boolean inverted, boolean forAttacker, byte coordinateToCompare, int value, ComparisonType comparisonType)
	{
		super(inverted, forAttacker, value, comparisonType);
		this.coordinateToCompare = coordinateToCompare;
	}
	@Override
	protected Integer getRelevantInfo(DamageEventInfo eventInfo)
	{
		Location location = (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation();
		switch(coordinateToCompare)
		{
			case COORDINATE_X:	return location.getBlockX();
			case COORDINATE_Y:	return location.getBlockY();
			case COORDINATE_Z:	return location.getBlockZ();
			default:			return 0; //shouldn't happen
		}
	}
	@Override
	protected Integer getRelevantInfo(SpawnEventInfo eventInfo)
	{
		Location location = eventInfo.entity.getLocation();
		switch(coordinateToCompare)
		{
			case COORDINATE_X:	return location.getBlockX();
			case COORDINATE_Y:	return location.getBlockY();
			case COORDINATE_Z:	return location.getBlockZ();
			default:			return 0; //shouldn't happen
		}
	}
	
	public static void register()
	{
		ConditionalRoutine.registerStatement(EntityCoordinateComparison.class, Pattern.compile(RoutineUtility.entityPart + "(X|Y|Z)\\." + RoutineUtility.comparisonRegex + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static final byte COORDINATE_X = 0;
	public static final byte COORDINATE_Y = 1;
	public static final byte COORDINATE_Z = 2;
}
