package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;

public class DynamicEntityTagInteger extends DynamicInteger
{	
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("("+ Utils.joinBy("|", EntityReference.values()) +")_tagvalue_(\\w+)", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm)
					{
						return new DynamicEntityTagInteger(
								EntityReference.valueOf(matcher.group(1).toUpperCase()), 
								matcher.group(2));
					}
				});
	}
	
	protected final EntityReference entityReference;
	protected final String tag;
	
	DynamicEntityTagInteger(EntityReference reference, String tag)
	{
		this.entityReference = reference;
		this.tag = tag;
	}
	
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.LIVING))
		{
			Entity entity = entityReference.getEntity(eventInfo);
			return ModDamage.getTagger().isTagged(entity, tag)? ModDamage.getTagger().getTagValue(entity, tag) : 0;
		}
		return 0;
	}
	
	@Override
	public void setValue(TargetEventInfo eventInfo, int value)
	{
		ModDamage.getTagger().addTag(entityReference.getEntity(eventInfo), tag, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return entityReference.name().toLowerCase() + "_tagvalue_" + tag;
	}
}