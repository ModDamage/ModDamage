package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Effect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntitySetHealth extends EntityEffectCalculation
{
	public EntitySetHealth(boolean forAttacker, List<Routine> calculations){ super(forAttacker, calculations);}

	@Override
	void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setHealth(input);
	}
	
	public static void register()
	{
		RoutineUtility.register(EntitySetHealth.class, Pattern.compile(RoutineUtility.entityPart + "effect\\.heal", Pattern.CASE_INSENSITIVE));
	}
}
