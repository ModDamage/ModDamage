package com.KoryuObihiro.bukkit.ModDamage.Backend;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;

public class ArmorSet 
{
	//private boolean inclusive = true;
	private boolean isValid = true;
	private boolean hasSomething = false;
	protected Material armorSet[] = new Material[4];
	
	public ArmorSet(Player player)
	{
		ItemStack[] equipment = player.getInventory().getArmorContents();
		for(int i = 0; i < equipment.length; i++)
		{
			Material material = equipment[i].getType();
			if(!this.put(material))
				ModDamage.log.severe("Invalid ArmorSet loaded from player \"" + player.getName() + "\" while attempting to add material of type " + material.name() + "!");
		}
	}
	
	public ArmorSet(String armorSetString)
	{
		String parts[] = armorSetString.split("\\*");
		for(String part : parts)
		{
			Material material = Material.matchMaterial(part);
			if(material == null || !this.put(material))
			{
				isValid = false;
				ModDamage.addToConfig(DebugSetting.QUIET, 0, "Invalid ArmorSet \"" + armorSetString + "\"", LoadState.FAILURE);
				break;
			}
		}
	}
	
	private boolean put(Material material)
	{
		ArmorElement armorType = ArmorElement.matchElement(material);
		if(armorType != null)
			switch(armorType)
			{
				case EMPTY: return true;
				case HELMET:
					if(armorSet[0] == null) armorSet[0] = material;
					else return false;
					hasSomething = true;
					return true;
					
				case CHESTPLATE:
					if(armorSet[1] == null) armorSet[1] = material;
					else return false;
					hasSomething = true;
					return true;
					
				case LEGGINGS:
					if(armorSet[2] == null) armorSet[2] = material;
					else return false;
					hasSomething = true;
					return true;
					
				case BOOTS:
					if(armorSet[3] == null) armorSet[3] = material;
					else return false;
					hasSomething = true;
					return true;
			}
		return false;
		
	}
	
	private Material get(int i){ return armorSet[i];}
	
	public boolean contains(ArmorSet someArmorSet)
	{
		if(this.isEmpty()) return true;
		if(someArmorSet.isEmpty()) return false;		
		for(int i = 0; i < 4; i++)
			if(armorSet[i] != null)
				if(!armorSet[i].equals(someArmorSet.get(i)))
					return false;
		return true;
	}
	
	public boolean equals(ArmorSet someArmorSet)
	{
		for(int i = 0; i < 4; i++)
		{
			if(armorSet[i] != null)
			{
				if(!armorSet[i].equals(someArmorSet.get(i)))
					return false;
			}
			else if(someArmorSet.get(i) != null)
				return false;
		}
		return true;
	}
	
	public boolean isValid(){ return isValid;}
	
	private boolean isEmpty(){ return !hasSomething;}
	
	@Override
	public String toString()
	{
		String output = "[";
		if(hasSomething)
		{
			for(Material material : armorSet)
				if(material != null)
					output += material.name() + " ";
		}
		return output + "]";
	}
	
	public Material[] toMaterialArray(){ return armorSet;}
	
	private enum ArmorElement
	{
		EMPTY, HELMET, CHESTPLATE, LEGGINGS, BOOTS;
		
		private static ArmorElement matchElement(Material material)
		{
			switch(material)
			{
			//Headwear
				case LEATHER_HELMET:
				case IRON_HELMET:
				case GOLD_HELMET:
				case DIAMOND_HELMET:
				case CHAINMAIL_HELMET:		return HELMET;
			//Chest
				case LEATHER_CHESTPLATE:
				case IRON_CHESTPLATE:
				case GOLD_CHESTPLATE:
				case DIAMOND_CHESTPLATE:
				case CHAINMAIL_CHESTPLATE:	return CHESTPLATE;
			//Legs
				case LEATHER_LEGGINGS:
				case IRON_LEGGINGS:
				case GOLD_LEGGINGS:
				case DIAMOND_LEGGINGS:
				case CHAINMAIL_LEGGINGS:	return LEGGINGS;
			//Boots
				case LEATHER_BOOTS:
				case IRON_BOOTS:
				case GOLD_BOOTS:
				case DIAMOND_BOOTS:
				case CHAINMAIL_BOOTS:		return BOOTS;
				
				default:					return EMPTY;
			}
		}
	}
}
