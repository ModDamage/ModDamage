package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import org.bukkit.entity.Entity;
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
		ArmorSet(true),
		Group(true),
		Name(true),
		Region,
		Type,
		Wield(true);
		
		boolean requiresPlayer = false;
		private EntityStringPropertyMatch(){}
		private EntityStringPropertyMatch(boolean requiresPlayer)
		{
			this.requiresPlayer = true;
		}
	}
	
	DynamicEntityString(EntityReference entityReference, EntityStringPropertyMatch propertyMatch)
	{
		this.entityReference = entityReference;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public String getString(TargetEventInfo eventInfo)
	{
		if(propertyMatch.equals(EntityStringPropertyMatch.Type))	
			return entityReference.getElement(eventInfo).name();//TODO See the enum for the name property.
		Entity entity = entityReference.getEntity(eventInfo);
		if(entity != null 
				&& (!propertyMatch.requiresPlayer
						|| entityReference.getElement(eventInfo).equals(ModDamageElement.PLAYER)))
		switch(propertyMatch)
		{
			case ArmorSet:
				return entityReference.getArmorSet(eventInfo).toString();
			case Group:
				return entityReference.getGroups(eventInfo).toString();
			case Name:
				return (entityReference.getEntity(eventInfo) instanceof Player?((Player)entityReference.getEntity(eventInfo)).getName():null).toString();
			case Region:
				return ExternalPluginManager.getRegionsManager().getRegions(entityReference.getEntity(eventInfo).getLocation()).toString();
			case Wield:
				return entityReference.getMaterial(eventInfo).name();
		}
		return null;
	}
}
