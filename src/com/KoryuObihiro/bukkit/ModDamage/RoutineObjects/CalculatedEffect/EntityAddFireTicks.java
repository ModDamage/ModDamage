package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityAddFireTicks extends EntityCalculatedEffectRoutine 
{
	public EntityAddFireTicks(boolean forAttacker, List<Routine> routines) 
	{
		super(forAttacker, routines);
	}

	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setFireTicks(affectedObject.getFireTicks() + input);
	}

	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(EntityAddFireTicks.class, Pattern.compile("(\\w+)effect\\.addFireTicks", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityAddFireTicks getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
			return new EntityAddFireTicks((ModDamage.matchesValidEntity(matcher.group(1)))?ModDamage.matchEntity(matcher.group(1)):false, routines);
		return null;
	}

}
