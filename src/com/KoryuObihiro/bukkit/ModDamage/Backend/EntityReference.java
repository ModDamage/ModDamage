package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public enum EntityReference
{
	Target, Projectile, Attacker;
	
//Use these when building routines.
	public static boolean isValid(String string)
	{
		for(EntityReference reference : EntityReference.values())
			if(string.equalsIgnoreCase(reference.name()))
				return true;
		return false;
	}
	
	public static EntityReference match(String string)
	{
		for(EntityReference reference : EntityReference.values())
			if(string.equalsIgnoreCase(reference.name()))
				return reference;
		return null;
	}
//Stuff for matching info.
	public ArmorSet getArmorSet(TargetEventInfo eventInfo) 
	{
		switch(this)
		{
			case Target: return eventInfo.armorSet_target;
			case Attacker: 
				if(eventInfo instanceof AttackerEventInfo) 
					return ((AttackerEventInfo)eventInfo).armorSet_attacker;
		}
		return null;
	}
	
	public ModDamageElement getElement(TargetEventInfo eventInfo) 
	{
		switch(this)
		{
			case Target: return eventInfo.element_target;
			case Attacker: 
				if(eventInfo instanceof AttackerEventInfo) 
					return ((AttackerEventInfo)eventInfo).element_target;
		}
		return null;
	}
	
	public Entity getEntity(TargetEventInfo eventInfo)
	{
		switch(this)
		{
			case Target: return eventInfo.entity_target;
			case Projectile: return (eventInfo instanceof ProjectileEventInfo)?((ProjectileEventInfo)eventInfo).projectile:null;
			case Attacker: return (eventInfo instanceof AttackerEventInfo)?((AttackerEventInfo)eventInfo).entity_attacker:null;
		}
		return null;//shouldn't happen.
	}
	
	public Entity getEntityOther(TargetEventInfo eventInfo)
	{
		switch(this)
		{
			case Target:
				if(eventInfo instanceof AttackerEventInfo) return ((AttackerEventInfo)eventInfo).entity_attacker;
				if(eventInfo instanceof ProjectileEventInfo) return ((ProjectileEventInfo)eventInfo).projectile;
				return eventInfo.entity_target;
			case Projectile:
				if(eventInfo instanceof AttackerEventInfo) return ((AttackerEventInfo)eventInfo).entity_attacker;
				return eventInfo.entity_target;
			case Attacker: return eventInfo.entity_target;
		}
		return null;//shouldn't happen
	}

	public List<String> getGroups(TargetEventInfo eventInfo) 
	{
		switch(this)
		{
			case Target: return eventInfo.groups_target;
			case Attacker: 
				if(eventInfo instanceof AttackerEventInfo) 
					return ((AttackerEventInfo)eventInfo).groups_attacker;
		}
		return ModDamage.emptyList;
	}

	public Material getMaterial(TargetEventInfo eventInfo)
	{
		switch(this)
		{
			case Target: return eventInfo.materialInHand_target;
			case Attacker: 
				if(eventInfo instanceof AttackerEventInfo) 
					return ((AttackerEventInfo)eventInfo).materialInHand_attacker;
		}
		return null;
	}
}
