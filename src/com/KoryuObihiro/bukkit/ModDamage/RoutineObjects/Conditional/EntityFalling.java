package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityFalling extends EntityConditionalStatement 
{
	public EntityFalling(boolean inverted, EntityReference entityReference)
	{  
		super(inverted, entityReference);
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo) { return entityReference.getEntity(eventInfo).getFallDistance() > 3;}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityFalling.class, Pattern.compile("(!?)(\\w+)\\.falling", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityFalling getNew(Matcher matcher)
	{
		if(matcher != null)
			if(EntityReference.isValid(matcher.group(2)))
				return new EntityFalling(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)));
		return null;
	}
}
