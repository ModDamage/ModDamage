package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityHurt extends EntityCalculatedEffectRoutine
{
	public EntityHurt(boolean forAttacker, List<Routine> routines){ super(forAttacker, routines);}
	
	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.damage(input);
	}

	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(EntityHurt.class, Pattern.compile(ModDamage.entityRegex + "effect\\.hurt", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityHurt getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
			return new EntityHurt(matcher.group(1).equalsIgnoreCase("attacker"), routines);
		return null;
	}
}
