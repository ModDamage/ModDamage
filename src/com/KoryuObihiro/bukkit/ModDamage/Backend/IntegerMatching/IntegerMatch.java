package com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.EntityMatch.EntityPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.ServerMatch.ServerPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.WorldMatch.WorldPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;

public class IntegerMatch
{
	protected final long value;
	private final boolean isDynamic;
	
	private static final Pattern dynamicPattern = Pattern.compile("(\\w+)\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	protected interface MatcherEnum {}
	
	protected IntegerMatch(int value)
	{ 
		this.value = value;
		this.isDynamic = false;
	}
	protected IntegerMatch()
	{ 
		this.value = 0;
		this.isDynamic = true;
	}
	
	public long getValue(TargetEventInfo eventInfo){ return isDynamic?value:eventInfo.eventValue;}
	
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
			if(matcher.matches())
			{
				if(matcher.group(1).equalsIgnoreCase("event"))
				{ 
					if(matcher.group(2).equalsIgnoreCase("value"))//XXX Can't think of any more properties, so no need to be dynamic here.
						return new IntegerMatch();
					ModDamage.addToLogRecord(DebugSetting.QUIET, "Error - couldn't match event property \"" + matcher.group(2) + "\"", LoadState.FAILURE);
				}
				else if(matcher.group(1).equalsIgnoreCase("world"))
				{ 
					for(WorldPropertyMatch match : WorldPropertyMatch.values())
						if(matcher.group(1).equalsIgnoreCase(match.name()))
							return new WorldMatch(match);
					ModDamage.addToLogRecord(DebugSetting.QUIET, "Error - couldn't match world property \"" + matcher.group(2) + "\"", LoadState.FAILURE);
				}
				else if(matcher.group(1).equalsIgnoreCase("server"))
				{
					for(ServerPropertyMatch match : ServerPropertyMatch.values())
						if(matcher.group(1).equalsIgnoreCase(match.name()))
							return new ServerMatch(match);
					ModDamage.addToLogRecord(DebugSetting.QUIET, "Error - couldn't match server property \"" + matcher.group(2) + "\"", LoadState.FAILURE);
				}
				else if(EntityReference.isValid(matcher.group(1)))
				{
					for(EntityPropertyMatch match : EntityPropertyMatch.values())
						if(matcher.group(2).equalsIgnoreCase(match.name()))
							return new EntityMatch(EntityReference.match(matcher.group(1)), match);
					ModDamage.addToLogRecord(DebugSetting.QUIET, "Error - couldn't match entity property \"" + matcher.group(2) + "\"", LoadState.FAILURE);
				}
			}
			else ModDamage.addToLogRecord(DebugSetting.QUIET, "Error - unrecognized number reference \"" + matcher.group(2) + "\". Format is $entity.$property", LoadState.FAILURE);
			return null;
		}
	}
}
