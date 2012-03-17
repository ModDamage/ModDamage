package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class InvertConditional extends Conditional
{
	public static final Pattern pattern = Pattern.compile("!\\s*");
	
	Conditional conditional;
	
	public InvertConditional(String configString, Conditional conditional)
	{
		super(configString);
		this.conditional = conditional;
	}
	
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		return !conditional.evaluate(data);
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.ConditionalBuilder
	{
		@Override
		public Conditional getNewFromFront(StringMatcher sm, EventInfo info)
		{
			Matcher matcher = sm.matchFront(pattern);
			if (matcher == null) return null;
			
			Conditional conditional = Conditional.getNewFromFront(sm.spawn(), info);
			
			if (conditional != null)
			{
				sm.accept();
				return new InvertConditional(matcher.group(), conditional);
			}
			
			return null;
		}
	}
}
