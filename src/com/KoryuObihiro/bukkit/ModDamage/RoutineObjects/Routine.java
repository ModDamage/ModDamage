package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Addition;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.DiceRoll;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Division;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.IntervalRange;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.LiteralRange;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Multiplication;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Set;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Tag;

abstract public class Routine
{
	protected static final Pattern anyPattern = Pattern.compile(".*");
	private static final RoutineBuilder builder = new RoutineBuilder();
	private static final HashMap<Pattern, RoutineBuilder> registeredBaseRoutines = new HashMap<Pattern, RoutineBuilder>();
	
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
		else ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: could not load builder for the " + builder.getClass().getEnclosingClass().getSimpleName() + " routine.", LoadState.FAILURE);
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
			for(Pattern pattern : registeredBaseRoutines.keySet())
			{
				Matcher matcher = pattern.matcher(anyMatcher.group());
				if(matcher.matches())
					return registeredBaseRoutines.get(pattern).getNew(matcher);
			}
			ModDamage.addToLogRecord(DebugSetting.QUIET, " No match found for nested routine \"" + anyMatcher.group() + "\"", LoadState.FAILURE);		
			return null;
		}
	}
}