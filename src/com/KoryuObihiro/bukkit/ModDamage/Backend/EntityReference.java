package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo.EventInfoType;

public enum EntityReference
{
	Target, Projectile, Attacker;//TODO Merge this with the EventInfoType enum?
	
//Use these when building routines.
	public static boolean isValid(String string)
	{
		for(EntityReference reference : EntityReference.values())
			if(string.equalsIgnoreCase(reference.name()))
				return true;
		ModDamage.addToLogRecord(DebugSetting.QUIET, "String \"" + string + "\" is not a valid entity reference.", LoadState.NOT_LOADED);
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
				if(eventInfo.type.equals(EventInfoType.ATTACKER)) 
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
				if(eventInfo.type.equals(EventInfoType.ATTACKER)) 
					return ((AttackerEventInfo)eventInfo).element_attacker;
		}
		return null;
	}
	
	public Entity getEntity(TargetEventInfo eventInfo)
	{
		switch(this)
		{
			case Target: return eventInfo.entity_target;
			case Projectile: return (eventInfo.equals(EventInfoType.PROJECTILE))?((ProjectileEventInfo)eventInfo).projectile:null;
			case Attacker: return (eventInfo.equals(EventInfoType.ATTACKER))?((AttackerEventInfo)eventInfo).entity_attacker:null;
		}
		return null;//shouldn't happen.
	}
	
	public Entity getEntityOther(TargetEventInfo eventInfo)
	{
		switch(this)
		{
			case Target:
				if(eventInfo.equals(EventInfoType.ATTACKER)) return ((AttackerEventInfo)eventInfo).entity_attacker;
				if(eventInfo.equals(EventInfoType.PROJECTILE)) return ((ProjectileEventInfo)eventInfo).projectile;
				return eventInfo.entity_target;
			case Projectile:
				if(eventInfo.equals(EventInfoType.ATTACKER)) return ((AttackerEventInfo)eventInfo).entity_attacker;
				return eventInfo.entity_target;
			case Attacker: return eventInfo.entity_target;
		}
		return null;//shouldn't happen
	}

	//TODO: KILL THIS REPETITIVE LOGIC
	public List<String> getGroups(TargetEventInfo eventInfo) 
	{
		switch(this)
		{
			case Target: return eventInfo.groups_target;
			case Attacker: 
				if(eventInfo.equals(EventInfoType.ATTACKER)) 
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
				if(eventInfo.equals(EventInfoType.ATTACKER)) 
					return ((AttackerEventInfo)eventInfo).materialInHand_attacker;
		}
		return null;
	}

	public String getName(TargetEventInfo eventInfo) 
	{
		switch(this)
		{
			case Target: return eventInfo.name_target;
			case Attacker: 
				if(eventInfo.equals(EventInfoType.ATTACKER)) 
					return ((AttackerEventInfo)eventInfo).name_attacker;
		}
		return null;
	}
}
