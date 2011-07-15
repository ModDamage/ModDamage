package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Effect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntitySetFireTicks extends EntityEffectCalculation
{
	public EntitySetFireTicks(boolean forAttacker, List<Routine> calculations){ super(forAttacker, calculations);}

	@Override
	void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setFireTicks(input);
	}

	public static void register()
	{
		RoutineUtility.register(EntitySetFireTicks.class, Pattern.compile(RoutineUtility.entityPart + "effect\\.setfireticks", Pattern.CASE_INSENSITIVE));
	}
}
