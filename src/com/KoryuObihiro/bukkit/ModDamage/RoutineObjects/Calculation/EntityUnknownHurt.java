package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityUnknownHurt extends LivingEntityCalculationRoutine
{
	public EntityUnknownHurt(String configString, EntityReference entityReference, List<Routine> routines)
	{
		super(configString, entityReference, routines);
	}
	
	@Override
	protected void applyEffect(LivingEntity entity, int input){ entity.damage(input);}

	public static void register()
	{
		CalculationRoutine.register(EntityUnknownHurt.class, Pattern.compile("(\\w+)effect\\.unknownhurt", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityUnknownHurt getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null && EntityReference.isValid(matcher.group(1)))
			return new EntityUnknownHurt(matcher.group(), EntityReference.match(matcher.group(1)), routines);
		return null;
	}
}
