package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalStatement;

public class Chance extends ConditionalStatement
{
	protected final Random random = new Random();
	protected final DynamicInteger probability;
	public Chance(DynamicInteger probability)
	{ 
		super(false);
		this.probability = probability;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return Math.abs(random.nextInt()%101) <= probability.getValue(eventInfo);}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("chance\\.(.*)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public Chance getNew(Matcher matcher)
		{
			return new Chance(DynamicInteger.getNew(matcher.group(1)));
		}
	}
}
