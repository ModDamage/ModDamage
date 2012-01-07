package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicEntityString.EntityStringPropertyMatch;

public class DynamicString
{	
	final CommonDynamicProperty dynamicProperty;
	public enum CommonDynamicProperty
	{
		EVENT_WORLD
		{
			@Override
			protected String getString(TargetEventInfo eventInfo)
			{
				return eventInfo.world.getName();
			}
		},
		EVENT_ENVIRONMENT
		{
			@Override
			protected String getString(TargetEventInfo eventInfo)
			{
				return eventInfo.world.getEnvironment().name();
			}
		};
		
		protected String getString(TargetEventInfo eventInfo){ return null;}
	}
	
	protected DynamicString(){ dynamicProperty = null;}
	
	private DynamicString(CommonDynamicProperty dynamicProperty)
	{
		this.dynamicProperty = dynamicProperty;
	}
	
	public String getString(TargetEventInfo eventInfo)
	{
		return dynamicProperty.getString(eventInfo);
	}
	
	public static DynamicString getNew(String string)
	{
		try {
			return new DynamicString(CommonDynamicProperty.valueOf(string.toUpperCase()));
		}
		catch (IllegalArgumentException e) {}
		
		String[] matches = string.split("_");
		EntityReference reference = EntityReference.match(matches[0], false);
		try {
			if(matches.length == 2 && reference != null)
				return new DynamicEntityString(reference, EntityStringPropertyMatch.valueOf(matches[1]));
		}
		catch (IllegalArgumentException e) {}
		
		return DynamicInteger.getNew(string);
	}
	
	@Override
	public String toString()
	{
		return dynamicProperty == null? "null" : dynamicProperty.name().toLowerCase();
	}
}