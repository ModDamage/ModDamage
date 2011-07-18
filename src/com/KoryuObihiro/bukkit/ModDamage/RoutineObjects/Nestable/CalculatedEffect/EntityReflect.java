package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.CalculatedEffect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityReflect extends EntityCalculatedEffectRoutine
{
	public EntityReflect(boolean forAttacker, List<Routine> calculations){ super(forAttacker, calculations);}
	
	@Override
	void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.getWorld().createExplosion(affectedObject.getLocation(), input);
	}
	
	@Override
	protected LivingEntity getAffectedObject(SpawnEventInfo eventInfo){ return null;}

	public static void register(RoutineUtility routineUtility)
	{
		routineUtility.registerBase(EntityReflect.class, Pattern.compile("effect\\.reflect", Pattern.CASE_INSENSITIVE));
	}
}
