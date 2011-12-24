package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicEntityString.EntityStringPropertyMatch;

public class DynamicString
{	
	final CommonDynamicProperty dynamicProperty;
	public enum CommonDynamicProperty
	{
		Event_World
		{
			@Override
			protected String getString(TargetEventInfo eventInfo)
			{
				return eventInfo.world.getName();
			}
		},
		Event_Environment
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
		for(CommonDynamicProperty property : CommonDynamicProperty.values())
			if(string.equalsIgnoreCase(property.name()))
				return new DynamicString(property);
		String[] matches = string.split("_");
		EntityReference reference = EntityReference.match(matches[0], false);
		if(matches.length == 2 && reference != null)
			for(EntityStringPropertyMatch match : EntityStringPropertyMatch.values())
				if(matches[1].equalsIgnoreCase(match.name()))
					return new DynamicEntityString(EntityReference.match(matches[0]), match);
		return DynamicInteger.getNew(string);
	}
	
	@Override
	public String toString()
	{
		return dynamicProperty.name().toLowerCase();
	}
}