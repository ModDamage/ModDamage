package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamageTagger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class DynamicEntityTagInteger extends DynamicInteger
{
	protected static final ModDamageTagger tagger = ModDamage.getTagger();
	
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
		Entity entity = entityReference.getEntity(eventInfo);
		return tagger.isTagged(entity, tag)?(isNegative?-1:1) * tagger.getTagValue(entity, tag):0;
	}
	
	@Override
	public void setValue(TargetEventInfo eventInfo, int value)
	{
		tagger.addTag(tag, entityReference.getEntity(eventInfo), value);
	}
	
	@Override
	public String toString()
	{
		return isNegative?"-":"" + entityReference.name().toLowerCase() + "_tagvalue_" + tag;
	}
}