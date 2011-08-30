package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityExplode extends EntityCalculatedEffectRoutine
{
	public EntityExplode(String configString, boolean forAttacker, List<Routine> routines)
	{
		super(configString, forAttacker, routines);
	}
	
	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.getWorld().createExplosion(affectedObject.getLocation(), (float)input/10);
	}
	
	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(EntityExplode.class, Pattern.compile("(\\w+)effect\\.explode", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityExplode getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
			return new EntityExplode(matcher.group(), (ModDamage.matchesValidEntity(matcher.group(1)))?ModDamage.matchEntity(matcher.group(1)):false, routines);
		return null;
	}
}
