package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityHeal extends EntityCalculatedEffectRoutine
{
	public EntityHeal(boolean forAttacker, List<Routine> routines){ super(forAttacker, routines);}

	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setHealth(affectedObject.getHealth() + input);
	}
	
	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(EntityHeal.class, Pattern.compile("(\\w+)effect\\.heal", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityHeal getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
			return new EntityHeal((ModDamage.matchesValidEntity(matcher.group(1)))?ModDamage.matchEntity(matcher.group(1)):false, routines);
		return null;
	}
}