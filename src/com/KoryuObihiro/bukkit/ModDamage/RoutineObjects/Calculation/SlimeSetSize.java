package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Slime;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class SlimeSetSize extends EntityCalculationRoutine<Slime>
{
	public SlimeSetSize(String configString, EntityReference entityReference, IntegerMatch match)
	{
		super(configString, entityReference, match);
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
		CalculationRoutine.registerCalculation(SlimeSetSize.class, Pattern.compile("(\\w+)effect\\.setSize", Pattern.CASE_INSENSITIVE));
	}
	
	public static SlimeSetSize getNew(Matcher matcher, IntegerMatch match)
	{
		if(matcher != null && match != null && EntityReference.isValid(matcher.group(1)))
			return new SlimeSetSize(matcher.group(), EntityReference.match(matcher.group(1)), match);
		return null;
	}
}