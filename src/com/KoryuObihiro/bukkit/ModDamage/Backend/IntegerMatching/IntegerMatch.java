package com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.EntityMatch.EntityPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.ServerMatch.ServerPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.WorldMatch.WorldPropertyMatch;

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
				if(matcher.group().equalsIgnoreCase("event.value"))
				{ 
					return new IntegerMatch();
				}
				else if(matcher.group(1).equalsIgnoreCase("world"))
				{ 
					for(WorldPropertyMatch match : WorldPropertyMatch.values())
						if(matcher.group(1).equalsIgnoreCase(match.name()))
							return new WorldMatch(match);
				}
				else if(matcher.group(1).equalsIgnoreCase("server"))
				{
					for(ServerPropertyMatch match : ServerPropertyMatch.values())
						if(matcher.group(1).equalsIgnoreCase(match.name()))
							return new ServerMatch(match);
				}
				else if(EntityReference.isValid(matcher.group(2)))
				{
					for(EntityPropertyMatch match : EntityPropertyMatch.values())
						if(matcher.group(1).equalsIgnoreCase(match.name()))
							return new EntityMatch(EntityReference.match(matcher.group(2)), match);
					//FIXME Debug for unrecognized property of the entity
				}
				else
				{
					//FIXME Debugging for unrecognized first stuff.
				}
			}
			else
			{
				//FIXME Debug for unmatched format
			}
			return null;
		}
	}
}
