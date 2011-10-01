package com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.EntityMatch.EntityPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.ServerMatch.ServerPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.WorldMatch.WorldPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class IntegerMatch
{
	protected final int value;
	private final List<Routine> routines;
	private final BasicIntegerMatch propertyMatch;
	private enum BasicIntegerMatch{ StaticValue, DynamicValue, RoutineValue;}
	
	private static final Pattern dynamicPattern;
	
	public static final String dynamicIntegerPart;
	static
	{
		String tempString = "(?:";
		for(EntityReference reference : EntityReference.values())
			tempString += reference.name() + "|";
		tempString += "event|world|server)";
		dynamicIntegerPart = "((?:-?[0-9]+)|(?:" + tempString + "(?:\\.\\w+))|(?:_\\w+))";
		dynamicPattern = Pattern.compile(dynamicIntegerPart, Pattern.CASE_INSENSITIVE);
	}
	
	protected interface MatcherEnum {}
	
	protected IntegerMatch(int value)
	{ 
		this.value = value;
		this.routines = null;
		this.propertyMatch = BasicIntegerMatch.StaticValue;
	}
	
	protected IntegerMatch()
	{ 
		this.value = 0;
		this.routines = null;
		this.propertyMatch = BasicIntegerMatch.DynamicValue;
	}
	
	protected IntegerMatch(List<Routine> routines)
	{
		this.value = 0;
		this.routines = routines;
		this.propertyMatch = BasicIntegerMatch.RoutineValue;
	}
	
	public int getValue(TargetEventInfo eventInfo)
	{ 
		switch(propertyMatch)
		{
			case StaticValue:	return value;
			case RoutineValue:
				for(Routine routine : routines)
					routine.run(eventInfo);
			case DynamicValue:	return eventInfo.eventValue;
			default: return 0;
		}
	}
	
	public static IntegerMatch getNew(List<Routine> routines) 
	{
		if(routines != null && !routines.isEmpty())
			return new IntegerMatch(routines);
		ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: attempted to use invalid routine list for a dynamic integer reference.", LoadState.FAILURE);
		return null;
	}
	
	public static IntegerMatch getNew(String string)
	{
		try
		{
			int value = Integer.parseInt(string);
			return new IntegerMatch(value);
		}
		catch(NumberFormatException e)
		{
			Matcher matcher = dynamicPattern.matcher(string);
			if(string.startsWith("_"))
			{
				List<Routine> potentialAlias = ModDamage.matchRoutineAlias(string);
				if(!potentialAlias.isEmpty()) return new RoutineMatch(potentialAlias);
			}
			else if(matcher.matches())
			{
				String[] matches = matcher.group().split("\\.");
				if(matches[0].equalsIgnoreCase("event"))
				{ 
					if(matches[1].equalsIgnoreCase("value"))//XXX Can't think of any more properties, so no need to be dynamic here.
						return new IntegerMatch();
					ModDamage.addToLogRecord(DebugSetting.QUIET, "Error - couldn't match event property \"" + matches[1] + "\"", LoadState.FAILURE);
				}
				else if(matches[0].equalsIgnoreCase("world"))
				{ 
					for(WorldPropertyMatch match : WorldPropertyMatch.values())
						if(matches[1].equalsIgnoreCase(match.name()))
							return new WorldMatch(match);
					ModDamage.addToLogRecord(DebugSetting.QUIET, "Error - couldn't match world property \"" + matches[1] + "\"", LoadState.FAILURE);
				}
				else if(matches[0].equalsIgnoreCase("server"))
				{
					for(ServerPropertyMatch match : ServerPropertyMatch.values())
						if(matches[1].equalsIgnoreCase(match.name()))
							return new ServerMatch(match);
					ModDamage.addToLogRecord(DebugSetting.QUIET, "Error - couldn't match server property \"" + matches[1] + "\"", LoadState.FAILURE);
				}
				else if(EntityReference.isValid(matches[0]))
				{
					for(EntityPropertyMatch match : EntityPropertyMatch.values())
						if(matches[1].equalsIgnoreCase(match.name()))
							return new EntityMatch(EntityReference.match(matches[1]), match);
					ModDamage.addToLogRecord(DebugSetting.QUIET, "Error - couldn't match entity property \"" + matches[1] + "\"", LoadState.FAILURE);
				}
			}
			//These shouldn't ever happen.
			else ModDamage.addToLogRecord(DebugSetting.QUIET, "Critical error - unrecognized number reference \"" + string + "\". Bug Koryu about this one.", LoadState.FAILURE);
			return null;
		}
	}
}
