package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntitySetAirTicks extends EntityCalculatedEffectRoutine
{
	public EntitySetAirTicks(String configString, boolean forAttacker, List<Routine> routines)
	{
		super(configString, forAttacker, routines);
	}

	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setRemainingAir(input);
	}
	
	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(EntitySetAirTicks.class, Pattern.compile("(\\w+)effect\\.setairticks", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntitySetAirTicks getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
			return new EntitySetAirTicks(matcher.group(), (ModDamage.matchesValidEntity(matcher.group(1)))?ModDamage.matchEntity(matcher.group(1)):false, routines);
		return null;
	}
}
