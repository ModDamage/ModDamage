package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Addition;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.DiceRoll;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Division;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.IntervalRange;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.LiteralRange;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Multiplication;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Set;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Tag;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Parameterized.Delay;

abstract public class Routine
{
	protected static final Pattern anyPattern = Pattern.compile(".*");
	private static final RoutineBuilder builder = new RoutineBuilder();
	private static final LinkedHashMap<Pattern, RoutineBuilder> registeredBaseRoutines = new LinkedHashMap<Pattern, RoutineBuilder>();
	
	protected static String statementPart = "!?[\\w_\\.\\*]+";
	
	final String configString;
	protected Routine(String configString){ this.configString = configString;}
	public final String getConfigString(){ return configString;}
	abstract public void run(TargetEventInfo eventInfo);
	
	public static void registerVanillaRoutines()
	{
		registeredBaseRoutines.clear();		
		Addition.register();
		Delay.register();
		DiceRoll.register();
		Division.register();
		IntervalRange.register();
		LiteralRange.register();
		Multiplication.register();
		Set.register();
		Tag.register();
	}
	
	protected static void registerRoutine(Pattern pattern, RoutineBuilder builder)
	{
		registerRoutine(registeredBaseRoutines, pattern, builder);
	}
	
	protected static <BuilderClass> void registerRoutine(HashMap<Pattern, BuilderClass> registry, Pattern pattern, BuilderClass builder)
	{
		if(pattern != null && builder != null) registry.put(pattern, builder);
		else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: could not load builder for the " + builder.getClass().getEnclosingClass().getSimpleName() + " routine.");
	}
	
	public static Routine getNew(String string)
	{
		Matcher matcher = anyPattern.matcher(string);
		matcher.matches();
		return builder.getNew(matcher);
	}
	
	protected static class RoutineBuilder
	{
		public Routine getNew(Matcher anyMatcher)
		{
			for(Entry<Pattern, RoutineBuilder> entry : registeredBaseRoutines.entrySet())
			{
				Matcher matcher = entry.getKey().matcher(anyMatcher.group());
				if(matcher.matches())
					return entry.getValue().getNew(matcher);
			}
			ModDamage.addToLogRecord(OutputPreset.FAILURE, " No match found for base routine \"" + anyMatcher.group() + "\"");
			return null;
		}
	}
}