package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityExplode extends EntityCalculatedEffectRoutine
{
	public EntityExplode(boolean forAttacker, List<Routine> routines){ super(forAttacker, routines);}
	
	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.getWorld().createExplosion(affectedObject.getLocation(), input);
	}
	
	public static void register(ModDamage routineUtility)
	{
		routineUtility.registerBase(EntityExplode.class, Pattern.compile(ModDamage.entityPart + "effect\\.explode", Pattern.CASE_INSENSITIVE));
	}
}
