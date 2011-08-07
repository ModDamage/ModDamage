package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;

public class EntityFalling extends EntityFallComparison 
{
	public EntityFalling(boolean inverted, boolean forAttacker)
	{  
		super(inverted, forAttacker, 3, ComparisonType.GREATERTHANEQUALS);
	}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityFalling.class, Pattern.compile("(!)?" + ModDamage.entityRegex + "\\.falling", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityFalling getNew(Matcher matcher)
	{
		if(matcher != null)
			return new EntityFalling(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"));
		return null;
	}
}
