package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntitySetAirTicks extends EntityCalculatedEffectRoutine
{
	public EntitySetAirTicks(boolean forAttacker, List<Routine> routines){ super(forAttacker, routines);}

	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setRemainingAir(input);
	}
	
	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(EntitySetAirTicks.class, Pattern.compile(ModDamage.entityPart + "effect\\.setairticks", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntitySetAirTicks getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
			return new EntitySetAirTicks(matcher.group(1).equalsIgnoreCase("attacker"), routines);
		return null;
	}
}
