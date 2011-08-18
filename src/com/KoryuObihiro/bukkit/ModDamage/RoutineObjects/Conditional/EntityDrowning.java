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
		super(inverted, forAttacker, 0, ComparisonType.LESSTHANEQUALS);
	}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityDrowning.class, Pattern.compile("(!?)(\\w+)\\.drowning", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityDrowning getNew(Matcher matcher)
	{
		if(matcher != null)
			return new EntityDrowning(matcher.group(1).equalsIgnoreCase("!"), (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false);
		return null;
	}
}