package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityDrowning extends EntityAirTicksComparison 
{
	public EntityDrowning(boolean inverted, boolean forAttacker)
	{  
		super(inverted, forAttacker, 0, ComparisonType.LESS_THAN_EQUALS);
	}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityDrowning.class, Pattern.compile("(!)?" + ModDamage.entityPart + "drowning", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityDrowning getNew(Matcher matcher)
	{
		if(matcher != null)
			return new EntityDrowning(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"));
		return null;
	}
}
