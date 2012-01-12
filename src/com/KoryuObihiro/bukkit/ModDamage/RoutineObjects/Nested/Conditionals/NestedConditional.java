package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

public abstract class NestedConditional extends Conditional
{
	public static final Pattern openPattern = Pattern.compile("\\s*\\(\\s*");
	public static final Pattern closePattern = Pattern.compile("\\s*\\)\\s*");
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	static class ConditionalBuilder extends Conditional.ConditionalBuilder
	{
		@Override
		public CResult getNewFromFront(String string)
		{
			Matcher matcher = openPattern.matcher(string);
			if (!matcher.lookingAt()) return null;
			
			CResult res = Conditional.getNewFromFront(string.substring(matcher.end()));
			
			matcher = closePattern.matcher(res.rest);
			if (!matcher.lookingAt())
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Missing closing paren here: \"" + res.rest + "\"");
				return null;
			}
			
			res.rest = res.rest.substring(matcher.end());
			
			return res;
		}
	}
}
