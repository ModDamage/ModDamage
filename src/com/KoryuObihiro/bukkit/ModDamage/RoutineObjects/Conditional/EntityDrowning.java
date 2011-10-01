package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityDrowning extends EntityConditionalStatement 
{
	public EntityDrowning(boolean inverted, EntityReference entityReference)
	{  
		super(inverted, entityReference);
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{ 
		return entityReference.getEntity(eventInfo) instanceof LivingEntity && ((LivingEntity)entityReference.getEntity(eventInfo)).getRemainingAir() <= 0;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityDrowning.class, Pattern.compile("(!?)(\\w+)\\.drowning", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityDrowning getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			if(EntityReference.isValid(matcher.group(2)))
				return new EntityDrowning(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)));
		}
		return null;
	}
}
