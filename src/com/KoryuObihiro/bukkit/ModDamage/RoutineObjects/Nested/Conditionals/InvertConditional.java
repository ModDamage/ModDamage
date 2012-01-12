package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

public class InvertConditional extends Conditional
{
	public static final Pattern pattern = Pattern.compile("!\\s*");
	
	Conditional conditional;
	
	public InvertConditional(Conditional conditional)
	{
		this.conditional = conditional;
	}
	
	@Override
	public boolean evaluate(TargetEventInfo eventInfo)
	{
		return !conditional.evaluate(eventInfo);
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.ConditionalBuilder
	{
		@Override
		public CResult getNewFromFront(String string)
		{
			Matcher matcher = pattern.matcher(string);
			if (!matcher.lookingAt()) return null;
			
			CResult res = Conditional.getNewFromFront(string.substring(matcher.end()));
			
			if (res != null)
				return new CResult(new InvertConditional(res.conditional), res.rest);
			
			return null;
		}
	}
}
