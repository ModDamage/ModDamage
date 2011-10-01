package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class Binomial extends ConditionalStatement
{
	protected final Random random = new Random();
	protected final IntegerMatch probability;
	public Binomial(IntegerMatch probability)
	{ 
		super(false);
		this.probability = probability;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return Math.abs(random.nextInt()%101) <= probability.getValue(eventInfo);}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Binomial.class, Pattern.compile("binom\\." + IntegerMatch.dynamicIntegerPart, Pattern.CASE_INSENSITIVE));
	}
	
	public static Binomial getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new Binomial(IntegerMatch.getNew(matcher.group(1)));
		return null;
	}
}
