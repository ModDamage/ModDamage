package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntitySetHealth extends EntityCalculatedEffectRoutine
{
	public EntitySetHealth(boolean forAttacker, List<Routine> routines){ super(forAttacker, routines);}

	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setHealth(input);
	}
	
	public static void register(ModDamage routineUtility)
	{
		routineUtility.registerBase(EntitySetHealth.class, Pattern.compile(ModDamage.entityPart + "effect\\.heal", Pattern.CASE_INSENSITIVE));
	}
}
