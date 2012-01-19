package com.ModDamage.Backend;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.ModDamage;
import com.ModDamage.Utils;
import com.ModDamage.PluginConfiguration.OutputPreset;

public enum EntityReference
{
	TARGET
	{
		public ArmorSet getArmorSet(TargetEventInfo eventInfo) 
		{
			return eventInfo.armorSet_target;
		}
		
		public ModDamageElement getElement(TargetEventInfo eventInfo) 
		{
			return eventInfo.element_target;
		}
		
		public Entity getEntity(TargetEventInfo eventInfo)
		{
			return eventInfo.entity_target;
		}
		
		public Entity getEntityOther(TargetEventInfo eventInfo)
		{
			switch(eventInfo.type)
			{
				case PROJECTILE:	return ((ProjectileEventInfo)eventInfo).projectile;
				case ATTACKER:		return ((AttackerEventInfo)eventInfo).entity_attacker;
			}
			return null;
		}

		public List<String> getGroups(TargetEventInfo eventInfo) 
		{
			return eventInfo.groups_target;
		}

		public Material getMaterial(TargetEventInfo eventInfo)
		{
			return eventInfo.materialInHand_target;
		}
	},
	PROJECTILE
	{
		public ArmorSet getArmorSet(TargetEventInfo eventInfo) 
		{
			return null;
		}
		
		public ModDamageElement getElement(TargetEventInfo eventInfo) 
		{
			return (eventInfo instanceof ProjectileEventInfo)? ((ProjectileEventInfo)eventInfo).rangedElement : null;
		}
		
		public Entity getEntity(TargetEventInfo eventInfo)
		{
			return (eventInfo instanceof ProjectileEventInfo)? ((ProjectileEventInfo)eventInfo).projectile : null;
		}
		
		public Entity getEntityOther(TargetEventInfo eventInfo)
		{
			return (eventInfo instanceof AttackerEventInfo)? ((AttackerEventInfo)eventInfo).entity_attacker : null;
		}

		public List<String> getGroups(TargetEventInfo eventInfo) 
		{
			return new ArrayList<String>();
		}

		public Material getMaterial(TargetEventInfo eventInfo)
		{
			return null;
		}
	},
	ATTACKER
	{
		public ArmorSet getArmorSet(TargetEventInfo eventInfo) 
		{
			return (eventInfo instanceof AttackerEventInfo)? ((AttackerEventInfo)eventInfo).armorSet_attacker : null;
		}
		
		public ModDamageElement getElement(TargetEventInfo eventInfo) 
		{
			return (eventInfo instanceof AttackerEventInfo)? ((AttackerEventInfo)eventInfo).element_attacker : null;
		}
		
		public Entity getEntity(TargetEventInfo eventInfo)
		{
			return (eventInfo instanceof AttackerEventInfo)? ((AttackerEventInfo)eventInfo).entity_attacker : null;
		}
		
		public Entity getEntityOther(TargetEventInfo eventInfo)
		{
			return eventInfo.entity_target;
		}

		public List<String> getGroups(TargetEventInfo eventInfo) 
		{
			return (eventInfo instanceof AttackerEventInfo)? ((AttackerEventInfo)eventInfo).groups_attacker : new ArrayList<String>();
		}

		public Material getMaterial(TargetEventInfo eventInfo)
		{
			return (eventInfo instanceof AttackerEventInfo)? ((AttackerEventInfo)eventInfo).materialInHand_attacker : null;
		}
	};
	
	public static final String regexString = Utils.joinBy("|", EntityReference.values());
	
//Use these when building routines.
	public static EntityReference match(String string){ return match(string, true);}
	public static EntityReference match(String string, boolean shouldOutput)
	{
		try
		{
			return EntityReference.valueOf(string.toUpperCase());
		} catch (IllegalArgumentException e) {
			if(shouldOutput)
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: \"" + string + "\" is not a valid entity reference.");
		}
		return null;
	}
	
	public String getName(TargetEventInfo eventInfo) 
	{
		if (getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
			return ((Player)this.getEntity(eventInfo)).getName();
		return null;
	}
	
//Stuff for matching info.
	public abstract ArmorSet getArmorSet(TargetEventInfo eventInfo);
	public abstract ModDamageElement getElement(TargetEventInfo eventInfo);
	public abstract Entity getEntity(TargetEventInfo eventInfo);
	public abstract Entity getEntityOther(TargetEventInfo eventInfo);
	public abstract List<String> getGroups(TargetEventInfo eventInfo);
	public abstract Material getMaterial(TargetEventInfo eventInfo);
}
