package com.KoryuObihiro.bukkit.ModDamage.Handling;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.WaterMob;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public enum DamageElement 
{
//TODO Add equipment types
	GENERIC_ALL ("all", "generic"),
	GENERIC_PLAYER ("humans", "generic"),
	GENERIC_ANIMAL ("animal", "generic"),
	GENERIC_MOB ("mob", "generic"),
	GENERIC_NATURE ("nature", "generic"),
//tools
	TOOL_AXE ("axe", "item"),
	TOOL_BOW ("bow", "item"),
	TOOL_HOE ("hoe", "item"),
	TOOL_PICKAXE ("pickaxe", "item"),
	TOOL_SPADE ("spade", "item"),
	TOOL_SWORD ("sword", "item"),
//armor
	ARMOR_HELMET ("head", "armor"),
	ARMOR_CHESTPLATE ("chest", "armor"),
	ARMOR_LEGGINGS ("legs", "armor"),
	ARMOR_BOOTS ("boots", "armor"),
//animals
	ANIMAL_CHICKEN ("Chicken", "animal"),
	ANIMAL_COW ("Cow", "animal"),
	ANIMAL_PIG ("Pig", "animal"),
	ANIMAL_SHEEP ("Sheep", "animal"),
	ANIMAL_SQUID ("Squid", "animal"),
	ANIMAL_WOLF ("Wolf", "animal"),
//mobs
	MOB_CREEPER ("Creeper", "mob"),
	MOB_GHAST ("Ghast", "mob"),
	MOB_GIANT ("Giant", "mob"),
	MOB_PIGZOMBIE ("ZombiePigman", "mob"),
	MOB_SKELETON ("Skeleton", "mob"),
	MOB_SLIME ("Slime", "mob"),
	MOB_SPIDER ("Spider", "mob"),
	MOB_ZOMBIE ("Zombie", "mob"),
//nature
	NATURE_BLOCK_EXPLOSION ("blockexplosion", "nature"),
	NATURE_CONTACT("cactus", "nature"),
	NATURE_DROWNING ("drowning", "nature"),
	NATURE_EXPLOSION ("explosion", "nature"),
	NATURE_FALL ("fall", "nature"),
	NATURE_FIRE ("fire", "nature"),
	NATURE_FIRE_TICK ("burn", "nature"),
	NATURE_LAVA ("lava", "nature"),
	NATURE_LIGHTNING ("lightning", "nature"),
	NATURE_SUFFOCATION ("suffocation", "nature");
	
	
	private final String nodeName;
	private final String damageType;
	DamageElement(String nodeName, String damage_description) 
	{
		this.damageType = damage_description;
		this.nodeName = nodeName;
	}
	
	public String getConfigReference(){ return this.nodeName;}
	public String getType(){ return damageType;}

	public static DamageElement matchDamageCause(DamageCause cause)
	{
		switch(cause) 
		{
			case BLOCK_EXPLOSION:   return DamageElement.NATURE_BLOCK_EXPLOSION; //unused
			case CONTACT: 			return DamageElement.NATURE_CONTACT; //unused
			case ENTITY_EXPLOSION: 	return DamageElement.NATURE_EXPLOSION;
			case FALL: 				return DamageElement.NATURE_FALL;
			case FIRE: 				return DamageElement.NATURE_FIRE;
			case FIRE_TICK:			return DamageElement.NATURE_FIRE_TICK;
			case LAVA: 				return DamageElement.NATURE_LAVA;
			case LIGHTNING: 		return DamageElement.NATURE_LIGHTNING;
			case SUFFOCATION: 		return DamageElement.NATURE_SUFFOCATION;
			case DROWNING: 			return DamageElement.NATURE_DROWNING;
			default: 				return null;//shouldn't happen
		}
	}
	

	//WinSock's nice algorithm for determining mob type, adapted for this plugin
	public static DamageElement matchEntityType(Entity entity)
	{
		if (entity instanceof LivingEntity) 
		{
			if(entity instanceof Slime)return DamageElement.MOB_SLIME;
			else if(entity instanceof Creature) 
			{
				if(entity instanceof Animals) 
				{
					if(entity instanceof Chicken) 		return DamageElement.ANIMAL_CHICKEN;
					else if(entity instanceof Cow) 	return DamageElement.ANIMAL_COW; 
					else if(entity instanceof Pig) 	return DamageElement.ANIMAL_PIG; 
					else if(entity instanceof Sheep) 	return DamageElement.ANIMAL_SHEEP;
					else if(entity instanceof Wolf)    return DamageElement.ANIMAL_WOLF;
				}
				else if(entity instanceof Monster) 
				{
					if(entity instanceof Zombie) 
					{
						if(entity instanceof PigZombie)return DamageElement.MOB_PIGZOMBIE;
						else 							return DamageElement.MOB_ZOMBIE;
					} 
					else if(entity instanceof Creeper) return DamageElement.MOB_CREEPER;
					else if(entity instanceof Giant) 	return DamageElement.MOB_GIANT;
					else if(entity instanceof Skeleton)return DamageElement.MOB_SKELETON;
					else if(entity instanceof Spider)	return DamageElement.MOB_SPIDER; 
				}
				else if(entity instanceof WaterMob) 
					if(entity instanceof Squid) 		return DamageElement.ANIMAL_SQUID;
			}
			else if(entity instanceof Flying) 
				if(entity instanceof Ghast)return DamageElement.MOB_GHAST;
		}
		return null;
	}
	
	public static DamageElement matchItemType(Material material)
	{
		switch(material)
		{
		//Axes
			case WOOD_AXE: 		return DamageElement.TOOL_AXE;
			case STONE_AXE: 	return DamageElement.TOOL_AXE;
			case IRON_AXE:		return DamageElement.TOOL_AXE;
			case GOLD_AXE: 		return DamageElement.TOOL_AXE;
			case DIAMOND_AXE: 	return DamageElement.TOOL_AXE;
		//Bow
			case BOW:			return DamageElement.TOOL_BOW; //TODO Not sure if this is necessary yet
		//Hoes
			case WOOD_HOE: 		return DamageElement.TOOL_HOE;
			case STONE_HOE: 	return DamageElement.TOOL_HOE;
			case IRON_HOE:		return DamageElement.TOOL_HOE;
			case GOLD_HOE:		return DamageElement.TOOL_HOE;
			case DIAMOND_HOE: 	return DamageElement.TOOL_HOE;
		//Picks
			case WOOD_PICKAXE: 	return DamageElement.TOOL_PICKAXE;
			case STONE_PICKAXE: return DamageElement.TOOL_PICKAXE;
			case IRON_PICKAXE:	return DamageElement.TOOL_PICKAXE;
			case GOLD_PICKAXE:	return DamageElement.TOOL_PICKAXE;
			case DIAMOND_PICKAXE:return DamageElement.TOOL_PICKAXE;
		//Shovels	
			case WOOD_SPADE: 	return DamageElement.TOOL_SPADE;
			case STONE_SPADE: 	return DamageElement.TOOL_SPADE;
			case IRON_SPADE:	return DamageElement.TOOL_SPADE;
			case GOLD_SPADE:	return DamageElement.TOOL_SPADE;
			case DIAMOND_SPADE: return DamageElement.TOOL_SPADE;
		//Swords	
			case WOOD_SWORD: 	return DamageElement.TOOL_SWORD;
			case STONE_SWORD: 	return DamageElement.TOOL_SWORD;
			case IRON_SWORD:	return DamageElement.TOOL_SWORD;
			case GOLD_SWORD:	return DamageElement.TOOL_SWORD;
			case DIAMOND_SWORD: return DamageElement.TOOL_SWORD;
			
			default: 			return null;
		}
	}
	
	public static DamageElement matchArmorType(Material material)
	{
		switch(material)
		{
		//Headwear
			case LEATHER_HELMET:		return DamageElement.ARMOR_HELMET;
			case IRON_HELMET:			return DamageElement.ARMOR_HELMET;
			case GOLD_HELMET:			return DamageElement.ARMOR_HELMET;
			case DIAMOND_HELMET:		return DamageElement.ARMOR_HELMET;
		//Chest
			case LEATHER_CHESTPLATE:	return DamageElement.ARMOR_CHESTPLATE;
			case IRON_CHESTPLATE:		return DamageElement.ARMOR_CHESTPLATE;
			case GOLD_CHESTPLATE:		return DamageElement.ARMOR_CHESTPLATE;
			case DIAMOND_CHESTPLATE:	return DamageElement.ARMOR_CHESTPLATE;
		//Legs
			case LEATHER_LEGGINGS:		return DamageElement.ARMOR_LEGGINGS;
			case IRON_LEGGINGS:			return DamageElement.ARMOR_LEGGINGS;
			case GOLD_LEGGINGS:			return DamageElement.ARMOR_LEGGINGS;
			case DIAMOND_LEGGINGS:		return DamageElement.ARMOR_LEGGINGS;
		//Boots
			case LEATHER_BOOTS:			return DamageElement.ARMOR_BOOTS;
			case IRON_BOOTS:			return DamageElement.ARMOR_BOOTS;
			case GOLD_BOOTS:			return DamageElement.ARMOR_BOOTS;
			case DIAMOND_BOOTS:			return DamageElement.ARMOR_BOOTS;
		}
		return null;
	}
	
	public static List<String> getTypeStrings(){ return getTypeStrings("generic");}
	public static List<String> getTypeStrings(String elementType)
	{
		List<String> typeStrings = new ArrayList<String>();
		for(DamageElement element : DamageElement.values())
			if(element.getType().equals(elementType))
				typeStrings.add(element.getConfigReference());
		
		if(typeStrings.isEmpty()) typeStrings = null;
		return typeStrings;
	}

	public static List<DamageElement> getTypeElements(){ return getTypeElements("generic");}
	public static List<DamageElement> getTypeElements(DamageElement element){ return getTypeElements(element.getConfigReference());}
	public static List<DamageElement> getTypeElements(String elementType)
	{
		List<DamageElement> typeStrings = new ArrayList<DamageElement>();
		for(DamageElement element : DamageElement.values())
			if(element.getType().equals(elementType))
				typeStrings.add(element);
		
		if(typeStrings.isEmpty()) typeStrings = null;
		return typeStrings;
	}
	
	public static DamageElement matchDamageElement(String nodeName)
	{
		for(DamageElement element : DamageElement.values())
			if(element.getConfigReference().equals(nodeName))
				return element;
		return null;
	}
}