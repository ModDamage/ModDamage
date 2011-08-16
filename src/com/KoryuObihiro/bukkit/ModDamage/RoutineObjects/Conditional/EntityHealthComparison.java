package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityHealthComparison extends EntityComparison
{
	public EntityHealthComparison(boolean inverted, boolean forAttacker, int health, ComparisonType comparisonType)
	{ 
		super(inverted, forAttacker, health, comparisonType);
	}
	@Override
	protected Integer getRelevantInfo(TargetEventInfo eventInfo){ return (eventInfo.getRelevantEntity(forAttacker) instanceof LivingEntity)?eventInfo.getRelevantEntity(forAttacker).getHealth():null;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityHealthComparison.class, Pattern.compile("(!)?(\\w+)\\.health\\.(\\w+)\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityHealthComparison getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			ComparisonType comparisonType = ComparisonType.matchType(matcher.group(3));
			if(comparisonType != null)
				return new EntityHealthComparison(matcher.group(1) != null, (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false, Integer.parseInt(matcher.group(4)), comparisonType);
		}
		return null;
	}
}
