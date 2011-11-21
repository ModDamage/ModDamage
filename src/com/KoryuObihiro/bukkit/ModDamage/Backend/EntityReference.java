package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo.EventInfoType;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;

public enum EntityReference
{
	Target, Projectile, Attacker;//TODO Merge this with the EventInfoType enum?
	
//Use these when building routines.
	public static boolean isValid(String string)
	{
		return isValid(string, false);
	}
	public static boolean isValid(String string, boolean suppressWarning)
	{
		for(EntityReference reference : EntityReference.values())
			if(string.equalsIgnoreCase(reference.name()))
				return true;
		if(!suppressWarning) ModDamage.addToLogRecord(OutputPreset.FAILURE, "String \"" + string + "\" is not a valid entity reference.");
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
			case Projectile: return ((ProjectileEventInfo)eventInfo).rangedElement;
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
			case Projectile: return (eventInfo.type.equals(EventInfoType.PROJECTILE))?((ProjectileEventInfo)eventInfo).projectile:null;
			case Attacker: return (eventInfo.type.equals(EventInfoType.ATTACKER))?((AttackerEventInfo)eventInfo).entity_attacker:null;
		}
		return null;//shouldn't happen.
	}
	
	public Entity getEntityOther(TargetEventInfo eventInfo)
	{
		switch(this)
		{
			case Target:
				switch(eventInfo.type)
				{
					case PROJECTILE:	return ((ProjectileEventInfo)eventInfo).projectile;
					case ATTACKER:		return ((AttackerEventInfo)eventInfo).entity_attacker;
				}
				break;
			case Projectile:
				if(eventInfo.type.equals(EventInfoType.ATTACKER)) return ((AttackerEventInfo)eventInfo).entity_attacker;
				break;
			case Attacker:
				return eventInfo.entity_target;
		}
		return null;
	}

	//TODO: KILL THIS REPETITIVE LOGIC
	public List<String> getGroups(TargetEventInfo eventInfo) 
	{
		switch(this)
		{
			case Target: return eventInfo.groups_target;
			case Attacker: 
				if(eventInfo.type.equals(EventInfoType.ATTACKER)) 
					return ((AttackerEventInfo)eventInfo).groups_attacker;
		}
		return Arrays.asList();
	}

	public Material getMaterial(TargetEventInfo eventInfo)
	{
		switch(this)
		{
			case Target: return eventInfo.materialInHand_target;
			case Attacker: 
				if(eventInfo.type.equals(EventInfoType.ATTACKER))
					return ((AttackerEventInfo)eventInfo).materialInHand_attacker;
		}
		return null;
	}

	public String getName(TargetEventInfo eventInfo) 
	{
		return this.getElement(eventInfo).matchesType(ModDamageElement.PLAYER)?((Player)this.getEntity(eventInfo)).getName():null;
	}
}
