package com.KoryuObihiro.bukkit.ModDamage.Backend;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;

public class ArmorSet 
{
	private boolean hasSomething = false;
	protected final Material[] armorSet;
	
	public ArmorSet(){ armorSet = new Material[4];}
	public ArmorSet(Material[] materials)
	{
		assert(materials.length == 4);
		armorSet = materials;
	}
	public ArmorSet(Player player)
	{
		armorSet = new Material[4];
		Material material;
		ItemStack[] equipment = player.getInventory().getArmorContents();
		for(int i = 0; i < equipment.length; i++)
		{
			material = equipment[i].getType();
			if(!ArmorSet.put(material, armorSet))
				ModDamage.log.severe("Invalid ArmorSet loaded from player \"" + player.getName() + "\" while attempting to add material of type " + material.name() + "!");
		}
	}
	
	public static ArmorSet getNew(String armorSetString)
	{
		if(armorSetString.equalsIgnoreCase("EMPTY")) return new ArmorSet();
		
		boolean failFlag = false;
		Material[] materials = new Material[4];
		String parts[] = armorSetString.split("\\*");
		for(String part : parts)
			if(!put(Material.matchMaterial(part), materials))
			{
				failFlag = true;
				ModDamage.addToLogRecord(DebugSetting.QUIET, "Unrecognized armor part \"" + part + "\"", LoadState.FAILURE);
				break;
			}
		if(failFlag)
		{
			ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid ArmorSet \"" + armorSetString + "\"", LoadState.FAILURE);
			return null;
		}
		else return new ArmorSet(materials);
	}
	
	private static boolean put(Material material, Material[] armorArray)
	{
		ArmorElement armorType = ArmorElement.matchElement(material);
		if(armorType != null)
			switch(armorType)
			{
				case EMPTY: return true;
				case HELMET:
					if(armorArray[0] == null) armorArray[0] = material;
					else return false;
					return true;
					
				case CHESTPLATE:
					if(armorArray[1] == null) armorArray[1] = material;
					else return false;
					return true;
					
				case LEGGINGS:
					if(armorArray[2] == null) armorArray[2] = material;
					else return false;
					return true;
					
				case BOOTS:
					if(armorArray[3] == null) armorArray[3] = material;
					else return false;
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
		if(someArmorSet == null) return false;
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
	
	private boolean isEmpty(){ return !hasSomething;}
	
	@Override
	public String toString()
	{
		String output = "[";
		if(hasSomething)
			for(Material material : armorSet)
				if(material != null)
					output += material.name() + " ";
		return output.substring(0, output.length()) + "]";
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
				
				case AIR:					return EMPTY;
				
				default:					return null;
			}
		}
	}
}
