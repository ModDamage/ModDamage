package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityTargetedByOther extends EntityConditionalStatement<LivingEntity>
{
	LivingEntity value;
	public EntityTargetedByOther(boolean inverted, boolean forAttacker)
	{ 
		super(inverted, forAttacker, null);
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return false;}
	@Override
	protected LivingEntity getRelevantInfo(TargetEventInfo eventInfo){ return null;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityTargetedByOther.class, Pattern.compile("(!)?" + ModDamage.entityPart + "targetedbyother", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityTargetedByOther getNew(Matcher matcher)
	{
		if(matcher != null)
			return new EntityTargetedByOther(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"));
		return null;
	}
}
