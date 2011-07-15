package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Effect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityExplode extends EntityEffectCalculation
{
	public EntityExplode(boolean forAttacker, List<Routine> calculations){ super(forAttacker, calculations);}
	
	@Override
	void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.getWorld().createExplosion(affectedObject.getLocation(), input);
	}
	
	public static void register()
	{
		RoutineUtility.register(EntityExplode.class, Pattern.compile(RoutineUtility.entityPart + "effect\\.explode", Pattern.CASE_INSENSITIVE));
	}
}
