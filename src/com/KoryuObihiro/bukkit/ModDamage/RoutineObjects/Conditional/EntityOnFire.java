package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityOnFire extends EntityConditionalStatement 
{
	public EntityOnFire(boolean inverted, EntityReference entityReference)
	{  
		super(inverted, entityReference);
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo){ return entityReference.getEntity(eventInfo).getFireTicks() > 0;}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityOnFire.class, Pattern.compile("(!?)(\\w+)\\.onfire", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityOnFire getNew(Matcher matcher)
	{
		if(matcher != null)
			if(EntityReference.isValid(matcher.group(2)))
				return new EntityOnFire(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)));
		return null;
	}
}
