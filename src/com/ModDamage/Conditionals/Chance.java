package com.ModDamage.Conditionals;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;

public class Chance extends Conditional
{
	public static final Pattern pattern = Pattern.compile("chance\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	protected final Random random = new Random();
	protected final IntegerExp probability;
	
	public Chance(String configString, IntegerExp probability)
	{
		super(configString);
		this.probability = probability;
	}
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		return Math.abs(random.nextInt()%101) <= probability.getValue(data);
	}
	
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
			return new Chance(matcher.group(), IntegerExp.getNew(matcher.group(1), info));
		}
	}
}
