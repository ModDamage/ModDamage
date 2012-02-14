package com.ModDamage.Routines.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class InvertConditional extends Conditional
{
	public static final Pattern pattern = Pattern.compile("!\\s*");
	
	Conditional conditional;
	
	public InvertConditional(Conditional conditional)
	{
		this.conditional = conditional;
	}
	
	@Override
	public boolean evaluate(EventData data)
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
				return new InvertConditional(conditional);
			}
			
			return null;
		}
	}
}
