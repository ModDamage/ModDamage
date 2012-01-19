package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.StringMatcher;
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
		public Conditional getNewFromFront(StringMatcher sm)
		{
			if (!sm.matchesFront(openPattern)) return null;
			
			Conditional conditional = Conditional.getNewFromFront(sm.spawn());
			
			if (!sm.matchesFront(closePattern))
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Missing closing paren here: \"" + sm.string + "\"");
				return null;
			}
			
			sm.accept();
			return conditional;
		}
	}
}
