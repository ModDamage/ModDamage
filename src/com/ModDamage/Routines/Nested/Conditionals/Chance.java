package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.Routines.Nested.Conditional;

public class Chance extends Conditional
{
	public static final Pattern pattern = Pattern.compile("chance\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	protected final Random random = new Random();
	protected final DynamicInteger probability;
	public Chance(DynamicInteger probability)
	{ 
		this.probability = probability;
	}
	@Override
	public boolean evaluate(TargetEventInfo eventInfo){ return Math.abs(random.nextInt()%101) <= probability.getValue(eventInfo);}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public Chance getNew(Matcher matcher)
		{
			return new Chance(DynamicInteger.getNew(matcher.group(1)));
		}
	}
}
