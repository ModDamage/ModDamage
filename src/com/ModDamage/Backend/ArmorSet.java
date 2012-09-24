package com.ModDamage.Backend;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;

public class ArmorSet 
{
	private boolean hasSomething = false;
	protected final Material[] armorSet;
	
	public ArmorSet(){ armorSet = new Material[4]; }
	public ArmorSet(Material[] materials)
	{
		if (materials.length != 4) throw new Error("Materials.length != 4 $AS18");
		armorSet = materials;
		for (Material material : armorSet)
		{
			if (material != null && material != Material.AIR)
			{
				hasSomething = true;
				break;
			}
		}
	}
	public ArmorSet(Player player)
	{
		armorSet = new Material[4];
		ItemStack[] armor = player.getInventory().getArmorContents();
		for(int i = 0; i < armor.length; i++)
		{
			Material material = armor[i].getType();
			if(ArmorSet.put(material, armorSet))
			{
				if (material != null && material != Material.AIR)
					hasSomething = true;
			}
			//// This can happen when players are wearing odd blocks on their head.
			//else
			//	PluginConfiguration.log.severe("Invalid ArmorSet loaded from player \"" + player.getName() + "\" while attempting to add material of type " + material.name() + "!");
			
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
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unrecognized armor part \"" + part + "\"");
				break;
			}
		if(failFlag)
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid ArmorSet \"" + armorSetString + "\"");
			return null;
		}
		else return new ArmorSet(materials);
	}
	
	private static boolean put(Material material, Material[] armorArray)
	{
		ArmorElement armorType = ArmorElement.matchElement(material);
		if (armorType == null) return false;
		if (armorType == ArmorElement.EMPTY) return true;
		
		if(armorArray[armorType.index] != null)
			return false;
		armorArray[armorType.index] = material;
		return true;
	}
	
	private Material get(int i){ return armorSet[i]; }
	
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
	
	private boolean isEmpty(){ return !hasSomething; }
	
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
	
	public Material[] toMaterialArray(){ return armorSet; }
	
	private enum ArmorElement
	{
		EMPTY(-1), HELMET(0), CHESTPLATE(1), LEGGINGS(2), BOOTS(3);
		
		public final int index;
		private ArmorElement(int index) { this.index = index; }
		
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
