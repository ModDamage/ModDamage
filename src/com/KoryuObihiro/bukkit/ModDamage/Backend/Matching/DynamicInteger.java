package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import java.util.List;
import java.util.regex.Matcher;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicEntityInteger.EntityIntegerPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicPlayerInteger.PlayerIntegerPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicServerInteger.ServerPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicWorldInteger.WorldPropertyMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class DynamicInteger extends DynamicString
{
	//TODO: Interface to make subclass enum code cleaner?
	protected final Integer value;
	protected final boolean settable;
	private final boolean usingStatic;
	
	private DynamicInteger()
	{ 
		this.value = null;
		this.usingStatic = false;
		this.settable = true;
	}
	
	private DynamicInteger(int value)
	{ 
		this.value = value;
		this.usingStatic = true;
		this.settable = false;
	}
	
	protected DynamicInteger(boolean settable)
	{
		this.value = null;
		this.usingStatic = false;
		this.settable = true;
	}
	
	public boolean isSettable(){ return settable;}
	
	public Integer getValue(TargetEventInfo eventInfo)
	{
		return usingStatic?value:eventInfo.eventValue;
	}

	public void setValue(TargetEventInfo eventInfo, int value)
	{
		if(!usingStatic) eventInfo.eventValue = value;
	}
	
	public static DynamicInteger getNew(List<Routine> routines) 
	{
		if(routines != null && !routines.isEmpty())
			return new DynamicRoutineInteger(routines);
		ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: attempted to use invalid routine list for a dynamic integer reference.", LoadState.FAILURE);
		return null;
	}
	
	public static DynamicInteger getNew(String string)
	{
		try
		{
			int value = Integer.parseInt(string);
			return new DynamicInteger(value);
		}
		catch(NumberFormatException e)
		{
			Matcher matcher = dynamicPattern.matcher(string);
			if(string.startsWith("_"))
			{
				List<Routine> routines = ModDamage.matchRoutineAlias(string);
				if(routines != null)
					return new DynamicRoutineInteger(routines);
			}
			else if(matcher.matches())
			{
				String[] matches = matcher.group().split("\\.");
				if(matches[0].equalsIgnoreCase("event"))
				{ 
					if(matches[1].equalsIgnoreCase("value"))//XXX Can't think of any more properties, so no need to be dynamic here.
						return new DynamicInteger();
				}
				else if(matches[0].equalsIgnoreCase("world"))
				{ 
					for(WorldPropertyMatch match : WorldPropertyMatch.values())
						if(matches[1].equalsIgnoreCase(match.name()))
							return new DynamicWorldInteger(match);
				}
				else if(matches[0].equalsIgnoreCase("server"))
				{
					for(ServerPropertyMatch match : ServerPropertyMatch.values())
						if(matches[1].equalsIgnoreCase(match.name()))
							return new DynamicServerInteger(match);
				}
				else if(EntityReference.isValid(matches[0]))
				{
					for(EntityIntegerPropertyMatch match : EntityIntegerPropertyMatch.values())
						if(matches[1].equalsIgnoreCase(match.name()))
							return new DynamicEntityInteger(EntityReference.match(matches[0]), match);
					for(PlayerIntegerPropertyMatch match : PlayerIntegerPropertyMatch.values())
						if(matches[1].equalsIgnoreCase(match.name()))
						{
							DynamicPlayerInteger yayMatch = new DynamicPlayerInteger(EntityReference.match(matches[0]), match);
							if(ExternalPluginManager.getMcMMOPlugin() != null == yayMatch.propertyMatch.usesMcMMO)
								return yayMatch;
							else if(yayMatch.propertyMatch.usesMcMMO)
								ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: attempted to use McMMO-dependent player property without McMMO.", LoadState.FAILURE);
						}
				}
				ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: unrecognized integer reference \"" + string + "\".", LoadState.FAILURE);
			}
			//These shouldn't ever happen.
			else ModDamage.addToLogRecord(DebugSetting.QUIET, "Critical error: unrecognized property \"" + string + "\". Bug Koryu about this one.", LoadState.FAILURE);
			return null;
		}
	}
	
	@Override
	public String toString()
	{
		return usingStatic?"" + value:"event.value";
	}

	@Override
	public String getString(TargetEventInfo eventInfo){ return getValue(eventInfo) + "";}
}
