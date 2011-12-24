package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;

public enum EntityReference
{
	TARGET, PROJECTILE, ATTACKER;
	
//Use these when building routines.
	public static EntityReference match(String string){ return match(string, true);}
	public static EntityReference match(String string, boolean shouldOutput)
	{
		for(EntityReference reference : EntityReference.values())
			if(reference.name().equalsIgnoreCase(string))
				return reference;
		if(shouldOutput) ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: \"" + string + "\" is not a valid entity reference.");
		return null;
	}
//Stuff for matching info.
	public ArmorSet getArmorSet(TargetEventInfo eventInfo) 
	{
		switch(this)
		{
			case TARGET: return eventInfo.armorSet_target;
			case ATTACKER: 
				if(eventInfo.type.equals(EntityReference.ATTACKER)) 
					return ((AttackerEventInfo)eventInfo).armorSet_attacker;
		}
		return null;
	}
	
	public ModDamageElement getElement(TargetEventInfo eventInfo) 
	{
		switch(this)
		{
			case TARGET:		return eventInfo.element_target;
			case PROJECTILE:	return ((ProjectileEventInfo)eventInfo).rangedElement;
			case ATTACKER: 
				if(eventInfo.type.equals(EntityReference.ATTACKER)) 
					return ((AttackerEventInfo)eventInfo).element_attacker;
		}
		return null;
	}
	
	public Entity getEntity(TargetEventInfo eventInfo)
	{
		switch(this)
		{
			case TARGET:		return eventInfo.entity_target;
			case PROJECTILE:	return (eventInfo.type == this)?((ProjectileEventInfo)eventInfo).projectile:null;
			case ATTACKER:		return (eventInfo.type == this)?((AttackerEventInfo)eventInfo).entity_attacker:null;
		}
		return null;//shouldn't happen.
	}
	
	public Entity getEntityOther(TargetEventInfo eventInfo)
	{
		switch(this)
		{
			case TARGET:
				switch(eventInfo.type)
				{
					case PROJECTILE:	return ((ProjectileEventInfo)eventInfo).projectile;
					case ATTACKER:		return ((AttackerEventInfo)eventInfo).entity_attacker;
				}
				break;
			case PROJECTILE:
				if(eventInfo.type == ATTACKER) return ((AttackerEventInfo)eventInfo).entity_attacker;
				break;
			case ATTACKER:
				return eventInfo.entity_target;
		}
		return null;
	}

	//XXX A lot of repetitive logic. :\
	public List<String> getGroups(TargetEventInfo eventInfo) 
	{
		switch(this)
		{
			case TARGET: return eventInfo.groups_target;
			case ATTACKER: 
				if(eventInfo.type == (ATTACKER))
					return ((AttackerEventInfo)eventInfo).groups_attacker;
		}
		return Arrays.asList();
	}

	public Material getMaterial(TargetEventInfo eventInfo)
	{
		switch(this)
		{
			case TARGET: return eventInfo.materialInHand_target;
			case ATTACKER: 
				if(eventInfo.type == ATTACKER)
					return ((AttackerEventInfo)eventInfo).materialInHand_attacker;
		}
		return null;
	}

	public String getName(TargetEventInfo eventInfo) 
	{
		return this.getElement(eventInfo).matchesType(ModDamageElement.PLAYER)?((Player)this.getEntity(eventInfo)).getName():null;
	}
}
