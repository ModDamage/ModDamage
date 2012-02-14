package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

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
	public boolean evaluate(EventData data){ return Math.abs(random.nextInt()%101) <= probability.getValue(data); }
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public Chance getNew(Matcher matcher, EventInfo info)
		{
			return new Chance(DynamicInteger.getNew(matcher.group(1), info));
		}
	}
}
