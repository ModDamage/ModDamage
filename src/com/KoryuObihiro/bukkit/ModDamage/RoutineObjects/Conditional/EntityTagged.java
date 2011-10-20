package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageTag;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityTagged extends EntityConditionalStatement
{
	private final String tag;
	public EntityTagged(boolean inverted, EntityReference entityReference, String tag)
	{
		super(inverted, entityReference);
		this.tag = tag;
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{
		return entityReference.getEntity(eventInfo) != null && ModDamageTag.getTags(entityReference.getEntity(eventInfo)).contains(tag);
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityTagged.class, Pattern.compile("(!?)(\\w+)\\.tagged\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityTagged getNew(Matcher matcher)
	{
		if(matcher != null && EntityReference.isValid(matcher.group(2)))
		{
			ModDamageTag.generateTag(matcher.group(3));
			return new EntityTagged(matcher.group(1).equals("!"), EntityReference.match(matcher.group(2)), matcher.group(3));
		}
		return null;
	}
}
