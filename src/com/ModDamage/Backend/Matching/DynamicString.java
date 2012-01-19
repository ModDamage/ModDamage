package com.ModDamage.Backend.Matching;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.Utils;
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicEntityString.EntityStringPropertyMatch;

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
	
	private static Pattern entityStringPattern = Pattern.compile("("+ EntityReference.regexString +")_("+ Utils.joinBy("|", EntityStringPropertyMatch.values()) +")", Pattern.CASE_INSENSITIVE);
	
	public static DynamicString getNew(String string)
	{
		try {
			return new DynamicString(CommonDynamicProperty.valueOf(string.toUpperCase()));
		}
		catch (IllegalArgumentException e) {}
		
		Matcher matcher = entityStringPattern.matcher(string);
		if (matcher.matches())
		{
			return new DynamicEntityString(EntityReference.match(matcher.group(1).toUpperCase(), false), EntityStringPropertyMatch.valueOf(matcher.group(2).toUpperCase()));
		}
		
		return DynamicInteger.getNew(string);
	}
	
	@Override
	public String toString()
	{
		return dynamicProperty == null? "null" : dynamicProperty.name().toLowerCase();
	}
}