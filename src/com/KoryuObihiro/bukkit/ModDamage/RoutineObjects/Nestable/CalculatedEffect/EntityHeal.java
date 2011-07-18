package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.CalculatedEffect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityHeal extends EntityCalculatedEffectRoutine
{
	public EntityHeal(boolean forAttacker, List<Routine> calculations){ super(forAttacker, calculations);}

	@Override
	void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setHealth(affectedObject.getHealth() + input);
	}
	
	public static void register(RoutineUtility routineUtility)
	{
		routineUtility.registerBase(EntityHeal.class, Pattern.compile(entityPart + "effect\\.heal", Pattern.CASE_INSENSITIVE));
	}
}
