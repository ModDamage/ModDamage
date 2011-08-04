package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.RangedElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class RangedElementEvaluation extends ConditionalStatement 
{
	final RangedElement rangedElement;
	protected RangedElementEvaluation(boolean inverted, RangedElement rangedElement)
	{
		super(inverted);
		this.rangedElement = rangedElement;
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo){ return eventInfo.rangedElement.equals(rangedElement);}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, RangedElementEvaluation.class, Pattern.compile("(!)?rangedElement\\." + ModDamage.rangedElementRegex, Pattern.CASE_INSENSITIVE));
	}
	
	public static RangedElementEvaluation getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			RangedElement element = RangedElement.matchElement(matcher.group(2));
			return new RangedElementEvaluation(matcher.group(1) != null, element);
		}
		return null;
	}
}
