package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Slime;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class SlimeSetSize extends EntityCalculationRoutine<Slime>
{
	public SlimeSetSize(String configString, EntityReference entityReference, List<Routine> routines)
	{
		super(configString, entityReference, routines);
	}
	@Override
	protected void applyEffect(Slime affectedObject, int input){ if(affectedObject != null) affectedObject.setSize(input);}
	@Override
	protected Slime getAffectedObject(TargetEventInfo eventInfo)
	{ 
		return (entityReference.getEntity(eventInfo) instanceof Slime)?((Slime)entityReference.getEntity(eventInfo)):null;
	}


	public static void register()
	{
		CalculationRoutine.registerStatement(SlimeSetSize.class, Pattern.compile("(\\w+)effect\\.setSize", Pattern.CASE_INSENSITIVE));
	}
	
	public static SlimeSetSize getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null && EntityReference.isValid(matcher.group(1)))
			return new SlimeSetSize(matcher.group(), EntityReference.match(matcher.group(1)), routines);
		return null;
	}
}
