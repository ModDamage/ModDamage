package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ProjectileEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo.EventInfoType;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicEntityString.EntityStringPropertyMatch;

public class DynamicString
{
	protected static final Pattern dynamicPattern;
	
	public static final String dynamicPart;
	static
	{
		String tempString = "(?:";
		for(EntityReference reference : EntityReference.values())
			tempString += reference.name() + "|";
		tempString += "event|world|server)";
		dynamicPart = "((?:-?[0-9]+)|(?:" + tempString + "(?:\\.\\w+))|(?:_\\w+))";
		dynamicPattern = Pattern.compile(DynamicString.dynamicPart, Pattern.CASE_INSENSITIVE);
	}
	
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
		},
		Event_RangedElement
		{
			@Override
			protected String getString(TargetEventInfo eventInfo)
			{
				return (eventInfo.type.equals(EventInfoType.PROJECTILE) && ((ProjectileEventInfo)eventInfo).rangedElement != null?((ProjectileEventInfo)eventInfo).rangedElement.name():null).toString();//FIXME null.toString() does what?
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
		Matcher matcher = dynamicPattern.matcher(string);
		if(matcher.matches())
		{
			String commonAttempt = matcher.group().replace('.', '_');
			for(CommonDynamicProperty property : CommonDynamicProperty.values())
				if(commonAttempt.equalsIgnoreCase(property.name()))
					return new DynamicString(property);
			String[] matches = matcher.group().split("\\.");
			if(EntityReference.isValid(matches[0], true))
				for(EntityStringPropertyMatch match : EntityStringPropertyMatch.values())
					if(matches[1].equalsIgnoreCase(match.name()))
						return new DynamicEntityString(EntityReference.match(matches[0]), match);
			DynamicInteger dynamicInteger = DynamicInteger.getNew(string);
			if(dynamicInteger != null) return dynamicInteger;
		}
		return null;
	}
	
	@Override
	public String toString()
	{
		return dynamicProperty.name().toLowerCase().replace('_', '.');
	}
}