package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class DynamicEntityTagInteger extends DynamicInteger
{	
	protected final EntityReference entityReference;
	protected final String tag;
	
	DynamicEntityTagInteger(EntityReference reference, String tag, boolean isNegative)
	{
		super(isNegative, true);
		this.entityReference = reference;
		this.tag = tag;
	}
	
	@Override
	public Integer getValue(TargetEventInfo eventInfo)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.LIVING))
		{
			Entity entity = entityReference.getEntity(eventInfo);
			return ModDamage.getTagger().isTagged(entity, tag)?(isNegative?-1:1) * ModDamage.getTagger().getTagValue(entity, tag):0;
		}
		return 0;
	}
	
	@Override
	public void setValue(TargetEventInfo eventInfo, int value)
	{
		ModDamage.getTagger().addTag(tag, entityReference.getEntity(eventInfo), value);
	}
	
	@Override
	public String toString()
	{
		return isNegative?"-":"" + entityReference.name().toLowerCase() + "_tagvalue_" + tag;
	}
}