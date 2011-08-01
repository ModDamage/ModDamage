package com.KoryuObihiro.bukkit.ModDamage.Backend;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorSet 
{
	//private boolean inclusive = true;
	private boolean hasSomething = false;
	private byte slotsOccupied = 0;
	protected Material armorSet[] = new Material[4];
	
	public ArmorSet(Player player)
	{
		for(ItemStack stack : player.getInventory().getArmorContents())
			if(!this.put(stack.getType()))
			{
				clear();
				break;
			}
	}
	
	public ArmorSet(String armorConfigString)
	{
		String parts[] = armorConfigString.split("\\*");
		for(String part : parts)
		{
			if(!this.put(Material.matchMaterial(part)))
			{
				clear();
				break;
			}
			else slotsOccupied++;
		}
	}
	
	private boolean put(Material material)
	{
		ArmorElement armorType = ArmorElement.matchElement(material);
		if(armorType != null)
			switch(armorType)
			{
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
	
	public Material get(int i){ return armorSet[i];}
	
	public boolean contains(ArmorSet someArmorSet)
	{
		if(this.isEmpty()) return true;
		if(someArmorSet.isEmpty()) return false;		
		for(int i = 0; i < 4; i++)
			if(armorSet[i] != null && !armorSet[i].equals(someArmorSet.get(i)))
				return false;
		return true;
	}
	
	public boolean equals(ArmorSet someArmorSet)
	{
		if(hasSomething)
		{
			for(int i = 0; i < 4; i++)
				if(!armorSet[i].equals(someArmorSet.get(i)))
					return false;
			return true;
		}
		return false;
	}
	
	public void clear()
	{
		for(int i = 0; i < armorSet.length; i++)
			armorSet[i] = null;
		hasSomething = false;
		//inclusive = true;
	}
	
	public boolean isEmpty(){ return !hasSomething;}
	
	@Override
	public String toString()//TODO Format better, mebbe?
	{
		if(hasSomething)
		{
			String output = "[";
			for(Material material : armorSet)
				if(material != null)
					output += material.name() + " ";
			return output + "]";
		}
		return "";
	}
	
	public Material[] toMaterialArray(){ return armorSet;}
	
	private enum ArmorElement
	{
		HELMET, CHESTPLATE, LEGGINGS, BOOTS;
		
		private static ArmorElement matchElement(Material material)
		{
			if(material != null)
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
					
					default:					return null;
				}
			return null;
		}
	}
}
