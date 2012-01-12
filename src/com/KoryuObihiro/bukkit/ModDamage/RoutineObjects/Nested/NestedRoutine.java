package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Parameterized.Delay;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Parameterized.Knockback;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Parameterized.Message;

public abstract class NestedRoutine extends Routine
{
	private static RoutineBuilder builder = new RoutineBuilder();
	private static LinkedHashMap<Pattern, RoutineBuilder> registeredNestedRoutines = new LinkedHashMap<Pattern, RoutineBuilder>();

	protected NestedRoutine(String configString){ super(configString);}
	
	public static void registerVanillaRoutines()
	{
		registeredNestedRoutines.clear();
		Delay.register();
		Message.register();
		Knockback.register();
		CalculationRoutine.register();
		ConditionalRoutine.register();
		SwitchRoutine.register();
		EntitySpawn.register();
	}
	
	protected static void registerRoutine(Pattern pattern, RoutineBuilder builder)
	{
		registeredNestedRoutines.put(pattern, builder);
	}
	
	public static NestedRoutine getNew(String key, Object nestedContent)
	{
		Matcher matcher = anyPattern.matcher(key);
		matcher.matches();
		return builder.getNew(matcher, nestedContent);
	}
	
	protected static class RoutineBuilder
	{
		public NestedRoutine getNew(Matcher anyMatcher, Object nestedContent)
		{
			for(Entry<Pattern, RoutineBuilder> entry : registeredNestedRoutines.entrySet())
			{
				Matcher matcher = entry.getKey().matcher(anyMatcher.group());
				if(matcher.matches())
				{
					NestedRoutine nr = entry.getValue().getNew(matcher, nestedContent);
					if (nr != null)
						return nr;
				}
			}
			ModDamage.addToLogRecord(OutputPreset.FAILURE, " No match found for nested routine \"" + anyMatcher.group() + "\"");		
			return null;
		}
	}
	
	public static void paddedLogRecord(OutputPreset preset, String message)
	{		
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		ModDamage.addToLogRecord(preset, message);
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
	}
}
