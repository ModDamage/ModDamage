package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityAddAirTicks extends EntityCalculatedEffectRoutine 
{
	public EntityAddAirTicks(String configString, boolean forAttacker, List<Routine> routines)
	{
		super(configString, forAttacker, routines);
	}

	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setRemainingAir(affectedObject.getRemainingAir() + input);
	}

	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(EntityAddAirTicks.class, Pattern.compile("(\\w+)effect\\.addAirTicks", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityAddAirTicks getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
			return new EntityAddAirTicks(matcher.group(), (ModDamage.matchesValidEntity(matcher.group(1)))?ModDamage.matchEntity(matcher.group(1)):false, routines);
		return null;
	}

}