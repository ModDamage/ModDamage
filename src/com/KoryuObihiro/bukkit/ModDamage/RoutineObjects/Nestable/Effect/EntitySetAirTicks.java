package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Effect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntitySetAirTicks extends EntityEffectCalculation
{
	public EntitySetAirTicks(boolean forAttacker, List<Routine> calculations){ super(forAttacker, calculations);}

	@Override
	void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setRemainingAir(input);
	}
	
	public static void register()
	{
		RoutineUtility.register(EntitySetAirTicks.class, Pattern.compile(RoutineUtility.entityPart + "effect\\.setairticks", Pattern.CASE_INSENSITIVE));
	}
}
