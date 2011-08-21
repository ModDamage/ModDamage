package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class SlimeSetSize extends CalculationRoutine<Slime>
{
	final boolean forAttacker;
	public SlimeSetSize(boolean forAttacker, List<Routine> routines)
	{
		super(routines);
		this.forAttacker = forAttacker;
	}
	@Override
	protected void applyEffect(Slime affectedObject, int input){ if(affectedObject != null) affectedObject.setSize(input);}
	@Override
	protected Slime getAffectedObject(TargetEventInfo eventInfo)
	{ 
		LivingEntity entity = (forAttacker && eventInfo instanceof AttackerEventInfo)?((AttackerEventInfo)eventInfo).entity_attacker:eventInfo.entity_target;
		return (entity instanceof Slime)?((Slime)entity):null;
	}


	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(SlimeSetSize.class, Pattern.compile("(\\w+)effect\\.setSize", Pattern.CASE_INSENSITIVE));
	}
	
	public static SlimeSetSize getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
			return new SlimeSetSize((ModDamage.matchesValidEntity(matcher.group(1)))?ModDamage.matchEntity(matcher.group(1)):false, routines);
		return null;
	}
}
