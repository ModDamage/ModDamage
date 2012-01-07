package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.TypeNameAliaser;

public class DynamicEntityString extends DynamicString
{
	final EntityReference entityReference;
	final EntityStringPropertyMatch propertyMatch;
	
	public enum EntityStringPropertyMatch
	{
		ARMORSET(true)
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return entityReference.getArmorSet(eventInfo).toString();
			}
		},
		GROUP(true)
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return entityReference.getGroups(eventInfo).toString();
			}
		},
		NAME(true)
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				Entity entity = entityReference.getEntity(eventInfo);
				return entity instanceof Player?((Player)entity).getName():"";
			}
		},
		REGIONS
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return ExternalPluginManager.getRegionsManager().getRegions(entityReference.getEntity(eventInfo).getLocation()).toString();
			}
		},
		TAGS
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return ModDamage.getTagger().getTags(entityReference.getEntity(eventInfo)).toString();
			}
		},
		TYPE
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return TypeNameAliaser.getStaticInstance().toString(entityReference.getElement(eventInfo));
			}
		},
		WIELDING(true)
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return entityReference.getMaterial(eventInfo).name();
			}
		};
		
		boolean requiresPlayer = false;
		private EntityStringPropertyMatch(){}
		private EntityStringPropertyMatch(boolean requiresPlayer){ this.requiresPlayer = true;}
		
		abstract protected String getString(TargetEventInfo eventInfo, EntityReference entityReference);
	}
	
	DynamicEntityString(EntityReference entityReference, EntityStringPropertyMatch propertyMatch)
	{
		this.entityReference = entityReference;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public String getString(TargetEventInfo eventInfo)
	{
		if(propertyMatch.equals(EntityStringPropertyMatch.TYPE) || (entityReference.getEntity(eventInfo) != null && (!propertyMatch.requiresPlayer || entityReference.getElement(eventInfo).equals(ModDamageElement.PLAYER))))
			return propertyMatch.getString(eventInfo, entityReference);
		return null;
	}

	@Override
	public String toString()
	{
		return entityReference.name().toLowerCase() + "_" + propertyMatch.name().toLowerCase();
	}
}
