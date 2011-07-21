package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityHurt extends EntityCalculatedEffectRoutine
{
	public EntityHurt(boolean forAttacker, List<Routine> routines){ super(forAttacker, routines);}
	
	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.damage(input);
	}
	
	@Override
	protected LivingEntity getAffectedObject(SpawnEventInfo eventInfo){ return null;}

	public static void register(RoutineUtility routineUtility)
	{
		routineUtility.registerBase(EntityHurt.class, Pattern.compile(RoutineUtility.entityPart + "effect\\.hurt", Pattern.CASE_INSENSITIVE));
	}
}
