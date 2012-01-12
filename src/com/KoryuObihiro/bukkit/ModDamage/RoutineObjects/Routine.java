package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.DiceRoll;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Division;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.IntervalRange;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.LiteralRange;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Multiplication;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Tag;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.ValueChangeRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Parameterized.Delay;

abstract public class Routine
{
	protected static final Pattern anyPattern = Pattern.compile(".*");
	private static final RoutineBuilder builder = new RoutineBuilder();
	private static final LinkedHashMap<Pattern, RoutineBuilder> registeredBaseRoutines = new LinkedHashMap<Pattern, RoutineBuilder>();
	
	final String configString;
	protected Routine(String configString){ this.configString = configString;}
	public final String getConfigString(){ return configString;}
	abstract public void run(final TargetEventInfo eventInfo);
	
	public static void registerVanillaRoutines()
	{
		registeredBaseRoutines.clear();		
		ValueChangeRoutine.register();
		Delay.register();
		DiceRoll.register();
		Division.register();
		IntervalRange.register();
		LiteralRange.register();
		Multiplication.register();
		Tag.register();
	}
	
	protected static void registerRoutine(Pattern pattern, RoutineBuilder builder)
	{
		registeredBaseRoutines.put(pattern, builder);
	}
	
	public static Routine getNew(final String string)
	{
		Matcher matcher = anyPattern.matcher(string);
		matcher.matches();
		return builder.getNew(matcher);
	}
	
	protected static class RoutineBuilder
	{
		public Routine getNew(Matcher matcher)
		{
			for(Entry<Pattern, RoutineBuilder> entry : registeredBaseRoutines.entrySet())
			{
				Matcher anotherMatcher = entry.getKey().matcher(matcher.group());
				if(anotherMatcher.matches())
				{
					Routine routine = entry.getValue().getNew(anotherMatcher);
					if(routine != null)
						return routine;
				}
			}
			ModDamage.addToLogRecord(OutputPreset.FAILURE, " No match found for base routine \"" + matcher.group() + "\"");
			return null;
		}
	}
}