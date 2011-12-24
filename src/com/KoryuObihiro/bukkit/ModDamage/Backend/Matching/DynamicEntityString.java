package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

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
		ArmorSet(true)
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return entityReference.getArmorSet(eventInfo).toString();
			}
		},
		Group(true)
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return entityReference.getGroups(eventInfo).toString();
			}
		},
		Name(true)
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return (entityReference.getEntity(eventInfo) instanceof Player?((Player)entityReference.getEntity(eventInfo)).getName():null).toString();
			}
		},
		Region
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return ExternalPluginManager.getRegionsManager().getRegions(entityReference.getEntity(eventInfo).getLocation()).toString();
			}
		},
		Tag
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return ModDamage.getTagger().getTags(entityReference.getEntity(eventInfo)).toString();
			}
		},
		Type
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return TypeNameAliaser.getStaticInstance().toString(entityReference.getElement(eventInfo));
			}
		},
		Wielding(true)
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
		if(propertyMatch.equals(EntityStringPropertyMatch.Type) || (entityReference.getEntity(eventInfo) != null && (!propertyMatch.requiresPlayer || entityReference.getElement(eventInfo).equals(ModDamageElement.PLAYER))))
			return propertyMatch.getString(eventInfo, entityReference);
		return null;
	}

	@Override
	public String toString()
	{
		return entityReference.name().toLowerCase() + "." + propertyMatch.name().toLowerCase();
	}
}
