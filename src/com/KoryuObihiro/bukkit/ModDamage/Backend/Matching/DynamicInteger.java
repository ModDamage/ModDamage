package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicEntityInteger.EntityIntegerPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicPlayerInteger.PlayerIntegerPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicServerInteger.ServerPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicWorldInteger.WorldPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.gmail.nossr50.datatypes.SkillType;

public class DynamicInteger extends DynamicString
{
	public static final String dynamicIntegerPart;
	public static final String dynamicIntegerPart_silent;
	static
	{
		String tempString = "(?:";
		for(EntityReference reference : EntityReference.values())
			tempString += reference.name() + "|";
		tempString += "event|world|server)";
		dynamicIntegerPart = "(-?(?:[0-9]+|(?:" + tempString + "_\\w+)|(?:_\\w+)|(?:\\(.*\\))))";//FIXME The greediness at the end blocks all 
		dynamicIntegerPart_silent = dynamicIntegerPart.substring(0, 1) + "?:" + dynamicIntegerPart.substring(1); 
	}
	private static final Pattern dynamicIntegerPattern = Pattern.compile(dynamicIntegerPart, Pattern.CASE_INSENSITIVE);
	
	private final Integer value;
	private final boolean usingStatic;

	protected final boolean settable;
	protected final boolean isNegative;
	
	private DynamicInteger(boolean isNegative)
	{ 
		this.value = null;
		this.usingStatic = false;
		this.settable = true;
		this.isNegative = isNegative;
	}
	
	private DynamicInteger(int value)
	{ 
		this.value = value;
		this.usingStatic = true;
		this.settable = false;
		this.isNegative = false;
	}
	
	protected DynamicInteger(boolean isNegative, boolean settable)
	{
		this.value = null;
		this.usingStatic = false;
		this.settable = true;
		this.isNegative = isNegative;
	}
	
	public boolean isSettable(){ return settable;}
	
	public Integer getValue(TargetEventInfo eventInfo)
	{
		return (isNegative?-1:1) * (usingStatic?value:eventInfo.eventValue);
	}

	public void setValue(TargetEventInfo eventInfo, int value)
	{
		if(!usingStatic) eventInfo.eventValue = value;
	}
	
	public static DynamicInteger getNew(List<Routine> routines) 
	{
		if(routines != null && !routines.isEmpty())
			return new DynamicRoutineInteger(routines, false);
		ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: attempted to use invalid routine list for a dynamic integer reference.");//shouldn't happen
		return null;
	}
	
	public static DynamicInteger getNew(String string){ return getNew(string, true);}
	public static DynamicInteger getNew(String string, boolean outputError)
	{
		if(string != null)
		{
			try
			{
				int value = Integer.parseInt(string);
				return new DynamicInteger(value);
			}
			catch(NumberFormatException e)
			{
				boolean isNegative = false;
				if(string.startsWith("-"))
				{
					isNegative = true;
					string = string.substring(1);
				}
				Matcher matcher = dynamicIntegerPattern.matcher(string);
				if(string.startsWith("_"))
				{
					Collection<Routine> routines = AliasManager.matchRoutineAlias(string);
					if(routines != null)
						return new DynamicRoutineInteger(routines, isNegative);
				}
				else if(string.startsWith("("))
				{
					String tempString = string.substring(1);
					if(string.endsWith(")")) tempString = tempString.substring(0, tempString.length() - 1);
					DynamicCalculatedInteger integer = DynamicCalculatedInteger.getNew(tempString, isNegative);
					if(integer != null)
						return integer;
				}
				else if(matcher.matches())//shouldn't return false by this point
				{
					String[] matches = matcher.group().split("_");
					if(matches[0].equalsIgnoreCase("event"))
					{ 
						if(matches[1].equalsIgnoreCase("value"))//XXX Can't think of any more properties, so no need to be dynamic here.
							return new DynamicInteger(isNegative);
					}
					else if(matches[0].equalsIgnoreCase("world"))
					{ 
						for(WorldPropertyMatch match : WorldPropertyMatch.values())
							if(matches[1].equalsIgnoreCase(match.name()))
								return new DynamicWorldInteger(match, isNegative);
					}
					else if(matches[0].equalsIgnoreCase("server"))
					{
						for(ServerPropertyMatch match : ServerPropertyMatch.values())
							if(matches[1].equalsIgnoreCase(match.name()))
								return new DynamicServerInteger(match, isNegative);
					}
					else
					{
						EntityReference entityReference = EntityReference.match(matches[0]);
						if(entityReference != null)
						{
							if(matches[1].equalsIgnoreCase("SKILL"))//TODO Make this more dynamic when necessary.
							{
								if(ExternalPluginManager.getMcMMOPlugin() != null)
								{
									String skillString = matches[2];
									for(SkillType skillType : SkillType.values())
										if(skillString.equalsIgnoreCase(skillType.name()))
											return new DynamicMcMMOInteger(entityReference, skillType, isNegative);
								}
								else//TODO Merge with a single boolean?
								{
									ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: attempting to use McMMO-dependent player property without McMMO!");
									return null;
								}
							}
							else if(matches[1].equalsIgnoreCase("tagvalue"))
							{
								if(matches.length == 2)
								{
									ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: no tag specified.");
									return null;
								}
								else
								{
									String tagname = matches[2];
									for(int i = 3; i < matches.length; i++)
										tagname += "_" + matches[i];
									return new DynamicEntityTagInteger(entityReference, tagname, isNegative);
								}
							}
							for(EntityIntegerPropertyMatch match : EntityIntegerPropertyMatch.values())
								if(matches[1].equalsIgnoreCase(match.name()))
									return new DynamicEntityInteger(entityReference, match, isNegative);
							for(PlayerIntegerPropertyMatch match : PlayerIntegerPropertyMatch.values())
								if(matches[1].equalsIgnoreCase(match.name()))
								{
									DynamicPlayerInteger yayMatch = new DynamicPlayerInteger(entityReference, match, isNegative);
									if(yayMatch.propertyMatch.usesMcMMO && ExternalPluginManager.getMcMMOPlugin() == null)
									{
										ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: attempting to use McMMO-dependent player property without McMMO!");
										return null;
									}
									else return yayMatch;
								}
						}
					}
				}
				if(outputError) ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unrecognized integer reference \"" + string + "\".");
			}
		}
		return null;
	}
	
	@Override
	public String toString()
	{
		return usingStatic?"" + (isNegative?value * -1:value):(isNegative?"-":"" + "event_value");
	}

	@Override
	public String getString(TargetEventInfo eventInfo){ return getValue(eventInfo) + "";}
}