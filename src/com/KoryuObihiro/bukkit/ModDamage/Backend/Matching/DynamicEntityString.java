package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

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
		Type
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return entityReference.getElement(eventInfo).name();//TODO See the enum for the name property.
			}
		},
		Wield(true)
		{
			@Override
			protected String getString(TargetEventInfo eventInfo, EntityReference entityReference)
			{
				return entityReference.getMaterial(eventInfo).name();
			}
		};
		
		boolean requiresPlayer = false;
		private EntityStringPropertyMatch(){}
		private EntityStringPropertyMatch(boolean requiresPlayer)
		{
			this.requiresPlayer = true;
		}
		
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
}