package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityHurt extends EntityCalculatedEffectRoutine
{
	public EntityHurt(boolean forAttacker, List<Routine> routines){ super(forAttacker, routines);}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		if(eventInfo.getRelevantEntity(forAttacker) != null)
			eventInfo.getRelevantEntity(forAttacker).damage(calculateInputValue(eventInfo), eventInfo.getRelevantEntity(!forAttacker));
	}
	
	@Override
	protected void applyEffect(LivingEntity affectedObject, int input){}

	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(EntityHurt.class, Pattern.compile("(\\w+)effect\\.hurt", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityHurt getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
			return new EntityHurt((ModDamage.matchesValidEntity(matcher.group(1)))?ModDamage.matchEntity(matcher.group(1)):false, routines);
		return null;
	}
}
