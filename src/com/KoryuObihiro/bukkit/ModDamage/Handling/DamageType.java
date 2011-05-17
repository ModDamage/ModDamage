package com.KoryuObihiro.bukkit.ModDamage.Handling;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
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

public enum DamageType 
{
	//TODO Add equipment types
//animals
	ANIMAL_CHICKEN ("Chicken", "animal"),
	ANIMAL_COW ("Cow", "animal"),
	ANIMAL_PIG ("Pig", "animal"),
	ANIMAL_SHEEP ("Sheep", "animal"),
	ANIMAL_SQUID ("Squid", "animal"),
	ANIMAL_WOLF ("Wolf", "animal"),
//item-type
	ITEM_AXE ("axe", "item"),
	ITEM_BOW ("bow", "item"),
	ITEM_HOE ("hoe", "item"),
	ITEM_PICKAXE ("pickaxe", "item"),
	ITEM_SPADE ("spade", "item"),
	ITEM_SWORD ("sword", "item"),
//mob-type
	MOB_CREEPER ("Creeper", "mob"),
	MOB_GHAST ("Ghast", "mob"),
	MOB_GIANT ("Giant", "mob"),
	MOB_PIGZOMBIE ("ZombiePigman", "mob"),
	MOB_SKELETON ("Skeleton", "mob"),
	MOB_SLIME ("Slime", "mob"),
	MOB_SPIDER ("Spider", "mob"),
	MOB_ZOMBIE ("Zombie", "mob"),
//nature-type
	NATURE_BLOCK_EXPLOSION ("blockexplosion", "nature"),
	NATURE_CONTACT("cactus", "nature"),
	NATURE_DROWNING ("drowning", "nature"),
	NATURE_EXPLOSION ("explosion", "nature"),
	NATURE_FALL ("fall", "nature"),
	NATURE_FIRE ("fire", "nature"),
	NATURE_FIRE_TICK ("burn", "nature"),
	NATURE_LAVA ("lava", "nature"),
	NATURE_LIGHTNING ("lightning", "nature"),
	NATURE_PLAYER ("humans", "nature"),
	NATURE_SUFFOCATION ("suffocation", "nature");
	
	private final String nodeName;
	private final String damage_description;
	DamageType(String nodeName, String damage_description) 
	{
		this.damage_description = damage_description;
		this.nodeName = nodeName;
	}
	
	public String getConfigReference(){ return this.nodeName;}
	public String getDescriptor(){ return damage_description;}

	public static DamageType matchDamageCause(DamageCause cause)
	{
		switch(cause) 
		{
			case BLOCK_EXPLOSION:   return DamageType.NATURE_BLOCK_EXPLOSION; //unused
			case CONTACT: 			return DamageType.NATURE_CONTACT; //unused
			case ENTITY_EXPLOSION: 	return DamageType.NATURE_EXPLOSION;
			case FALL: 				return DamageType.NATURE_FALL;
			case FIRE: 				return DamageType.NATURE_FIRE;
			case FIRE_TICK:			return DamageType.NATURE_FIRE_TICK;
			case LAVA: 				return DamageType.NATURE_LAVA;
			case LIGHTNING: 		return DamageType.NATURE_LIGHTNING;
			case SUFFOCATION: 		return DamageType.NATURE_SUFFOCATION;
			case DROWNING: 			return DamageType.NATURE_DROWNING;
			default: 				return null;//shouldn't happen
		}
	}
	

	//WinSock's nice algorithm for determining mob type, adapted for this plugin
	public static DamageType matchEntityType(Entity entity)
	{
		if (entity instanceof LivingEntity) 
		{
			if(entity instanceof Slime)return DamageType.MOB_SLIME;
			else if(entity instanceof Creature) 
			{
				if(entity instanceof Animals) 
				{
					if(entity instanceof Chicken) 		return DamageType.ANIMAL_CHICKEN;
					else if(entity instanceof Cow) 	return DamageType.ANIMAL_COW; 
					else if(entity instanceof Pig) 	return DamageType.ANIMAL_PIG; 
					else if(entity instanceof Sheep) 	return DamageType.ANIMAL_SHEEP;
					else if(entity instanceof Wolf)    return DamageType.ANIMAL_WOLF;
				}
				else if(entity instanceof Monster) 
				{
					if(entity instanceof Zombie) 
					{
						if(entity instanceof PigZombie)return DamageType.MOB_PIGZOMBIE;
						else 							return DamageType.MOB_ZOMBIE;
					} 
					else if(entity instanceof Creeper) return DamageType.MOB_CREEPER;
					else if(entity instanceof Giant) 	return DamageType.MOB_GIANT;
					else if(entity instanceof Skeleton)return DamageType.MOB_SKELETON;
					else if(entity instanceof Spider)	return DamageType.MOB_SPIDER; 
				}
				else if(entity instanceof WaterMob) 
					if(entity instanceof Squid) 		return DamageType.ANIMAL_SQUID;
			}
			else if(entity instanceof Flying) 
				if(entity instanceof Ghast)return DamageType.MOB_GHAST;
		}
		return null;
	}
	
	public static DamageType matchItemType(Material material)
	{
		switch(material)
		{
		//Axes
			case WOOD_AXE: 		return DamageType.ITEM_AXE;
			case STONE_AXE: 	return DamageType.ITEM_AXE;
			case IRON_AXE:		return DamageType.ITEM_AXE;
			case DIAMOND_AXE: 	return DamageType.ITEM_AXE;
		//Bow
			case BOW:			return DamageType.ITEM_BOW; //TODO Not sure if this is necessary yet
		//Hoes
			case WOOD_HOE: 		return DamageType.ITEM_HOE;
			case STONE_HOE: 	return DamageType.ITEM_HOE;
			case IRON_HOE:		return DamageType.ITEM_HOE;
			case DIAMOND_HOE: 	return DamageType.ITEM_HOE;
		//Picks
			case WOOD_PICKAXE: 	return DamageType.ITEM_PICKAXE;
			case STONE_PICKAXE: return DamageType.ITEM_PICKAXE;
			case IRON_PICKAXE:	return DamageType.ITEM_PICKAXE;
			case DIAMOND_PICKAXE:return DamageType.ITEM_PICKAXE;
		//Shovels	
			case WOOD_SPADE: 	return DamageType.ITEM_SPADE;
			case STONE_SPADE: 	return DamageType.ITEM_SPADE;
			case IRON_SPADE:	return DamageType.ITEM_SPADE;
			case DIAMOND_SPADE: return DamageType.ITEM_SPADE;
		//Swords	
			case WOOD_SWORD: 	return DamageType.ITEM_SWORD;
			case STONE_SWORD: 	return DamageType.ITEM_SWORD;
			case IRON_SWORD:	return DamageType.ITEM_SWORD;
			case DIAMOND_SWORD: return DamageType.ITEM_SWORD;
			
			default: 			return null;
		}
	}
}