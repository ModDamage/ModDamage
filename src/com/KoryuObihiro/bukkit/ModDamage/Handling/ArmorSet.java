package com.KoryuObihiro.bukkit.ModDamage.Handling;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorSet 
{
	//private boolean inclusive = true;
	private boolean hasSomething = false;
	protected Material armorSet[] = new Material[4];
	
	public ArmorSet(Player player)
	{
		Logger.getLogger("Minecraft").severe("Allocated an armor set (Player)");
		for(ItemStack stack : player.getInventory().getArmorContents())
			this.put(stack.getType());
		Logger.getLogger("Minecraft").severe(this.toString());
	}
	
	public ArmorSet(String armorConfigString)
	{
		String parts[] = armorConfigString.split("\\*");
		for(String part : parts)
			if(Material.matchMaterial(part) != null)
				if(!this.put(Material.matchMaterial(part)))
				{
					clear();
					break;
				}
	}
	
	public boolean put(Material material)
	{
		DamageElement armorType = DamageElement.matchArmorType(material);
		if(armorType != null)
			switch(armorType)
			{
				case ARMOR_HELMET:
					if(armorSet[0] == null) armorSet[0] = material;
					else return false;
					hasSomething = true;
					return true;
					
				case ARMOR_CHESTPLATE:
					if(armorSet[1] == null) armorSet[1] = material;
					else return false;
					hasSomething = true;
					return true;
					
				case ARMOR_LEGGINGS:
					if(armorSet[2] == null) armorSet[2] = material;
					else return false;
					hasSomething = true;
					return true;
					
				case ARMOR_BOOTS:
					if(armorSet[3] == null) armorSet[3] = material;
					else return false;
					hasSomething = true;
					return true;
			}
		return false;
		
	}
	
	public Material get(int i){ return armorSet[i];}
	
	/*public boolean matchesAgainst(ArmorSet someArmorSet)
	{
		boolean inclusion = someArmorSet.isInclusive();
		for(int i = 0; i < 4; i++)
			if(!armorSet[i].equals(someArmorSet.get(i))) && (inclusion?(someArmorSet.get(i) != null):true))
				return false;
		return true;
	}*/
	
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
	
	//public boolean isInclusive(){ return inclusive;}
	public boolean isEmpty(){ return !hasSomething;}
	
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
	
	
}
