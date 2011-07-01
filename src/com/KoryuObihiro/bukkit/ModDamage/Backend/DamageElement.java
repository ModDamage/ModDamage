package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftArrow;
import org.bukkit.craftbukkit.entity.CraftEgg;
import org.bukkit.craftbukkit.entity.CraftFireball;
import org.bukkit.craftbukkit.entity.CraftSnowball;
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
	GENERIC 		("generic", null, true),
	GENERIC_HUMAN 	("humans", GENERIC, false),
	GENERIC_ANIMAL 	("animal", GENERIC, true),
	GENERIC_MELEE 	("melee", GENERIC, true),
	GENERIC_RANGED	("ranged", GENERIC, true),
	GENERIC_ARMOR	("armor", GENERIC, false),
	GENERIC_MOB 	("mob", GENERIC, true),
	GENERIC_NATURE 	("nature", GENERIC, true),
//tools
	MELEE_AXE 		("axe", GENERIC_MELEE, false),
	MELEE_FIST 		("fist", GENERIC_MELEE, false),
	MELEE_HOE 		("hoe", GENERIC_MELEE, false),
	MELEE_PICKAXE 	("pickaxe", GENERIC_MELEE, false),
	MELEE_SPADE 	("spade", GENERIC_MELEE, false),
	MELEE_SWORD 	("sword", GENERIC_MELEE, false),
	MELEE_OTHER 	("other", GENERIC_MELEE, false),
	
//ranged items
	RANGED_BOW 		("bow", GENERIC_RANGED, false),
	RANGED_EGG 		("egg", GENERIC_RANGED, false), 
	RANGED_FIREBALL	("fireball", GENERIC_RANGED, false),
	RANGED_SNOWBALL	("snowball", GENERIC_RANGED, false),
	
//armor
	ARMOR_HELMET	("head", GENERIC_ARMOR, false),
	ARMOR_CHESTPLATE("chest", GENERIC_ARMOR, false),
	ARMOR_LEGGINGS	("legs", GENERIC_ARMOR, false),
	ARMOR_BOOTS	("boots", GENERIC_ARMOR, false),
	
//animals
	ANIMAL_CHICKEN ("Chicken", GENERIC_ANIMAL, false),
	ANIMAL_COW ("Cow", GENERIC_ANIMAL, false),
	ANIMAL_PIG ("Pig", GENERIC_ANIMAL, false),
	ANIMAL_SHEEP ("Sheep", GENERIC_ANIMAL, false),
	ANIMAL_SQUID ("Squid", GENERIC_ANIMAL, false),
	ANIMAL_WOLF ("Wolf", GENERIC_ANIMAL, false),
//mobs
	MOB_CREEPER ("Creeper", GENERIC_MOB, false),
	MOB_GHAST ("Ghast", GENERIC_MOB, false),
	MOB_GIANT ("Giant", GENERIC_MOB, false),
	MOB_PIGZOMBIE ("ZombiePigman", GENERIC_MOB, false),
	MOB_SKELETON ("Skeleton", GENERIC_MOB, false),
	MOB_SLIME ("Slime", GENERIC_MOB, false),
	MOB_SPIDER ("Spider", GENERIC_MOB, false),
	MOB_ZOMBIE ("Zombie", GENERIC_MOB, false),
//nature
	NATURE_BLOCK_EXPLOSION ("blockexplosion", GENERIC_NATURE, false),
	NATURE_CONTACT("cactus", GENERIC_NATURE, false),
	NATURE_DROWNING ("drowning", GENERIC_NATURE, false),
	NATURE_EXPLOSION ("explosion", GENERIC_NATURE, false),
	NATURE_FALL ("fall", GENERIC_NATURE, false),
	NATURE_FIRE ("fire", GENERIC_NATURE, false),
	NATURE_FIRE_TICK ("burn", GENERIC_NATURE, false),
	NATURE_LAVA ("lava", GENERIC_NATURE, false),
	NATURE_LIGHTNING ("lightning", GENERIC_NATURE, false),
	NATURE_SUFFOCATION ("suffocation", GENERIC_NATURE, false),
	NATURE_VOID ("void", GENERIC_NATURE, false);
	
	
	private final String stringReference;
	private final DamageElement genericElement;
	private final boolean hasSubConfig;
	DamageElement(String stringReference, DamageElement genericElement, boolean hasSubConfig) 
	{
		this.stringReference = stringReference;
		this.genericElement = genericElement;
		this.hasSubConfig = hasSubConfig;
	}
	
	public String getReference(){ return this.stringReference;}
	public DamageElement getType(){ return genericElement;}
	public boolean hasSubConfiguration(){ return hasSubConfig;}

	public boolean isElementReference(String string)
	{
		for(DamageElement element : DamageElement.values())
			if(element.getReference().equals(string))
				return true;
		return false;
	}
	public static DamageElement matchNonlivingElement(DamageCause cause)
	{
		switch(cause) 
		{
			case BLOCK_EXPLOSION:   return DamageElement.NATURE_BLOCK_EXPLOSION;
			case CONTACT: 			return DamageElement.NATURE_CONTACT;
			case DROWNING: 			return DamageElement.NATURE_DROWNING;
			case ENTITY_EXPLOSION: 	return DamageElement.NATURE_EXPLOSION;
			case FALL: 				return DamageElement.NATURE_FALL;
			case FIRE: 				return DamageElement.NATURE_FIRE;
			case FIRE_TICK:			return DamageElement.NATURE_FIRE_TICK;
			case LAVA: 				return DamageElement.NATURE_LAVA;
			case LIGHTNING: 		return DamageElement.NATURE_LIGHTNING;
			case SUFFOCATION: 		return DamageElement.NATURE_SUFFOCATION;
			case VOID: 				return DamageElement.NATURE_VOID;
			default: 				return null;//shouldn't happen
		}
	}

	public static DamageElement matchLivingElement(Entity entity)
	{
		if (entity instanceof LivingEntity) 
		{
			if(entity instanceof Slime)return DamageElement.MOB_SLIME;//XXX Not sure why, but Slimes aren't technically Creatures.
			else if(entity instanceof Creature) 
			{
				if(entity instanceof Animals) 
				{
					if(entity instanceof Chicken) 		return DamageElement.ANIMAL_CHICKEN;
					else if(entity instanceof Cow) 		return DamageElement.ANIMAL_COW; 
					else if(entity instanceof Pig) 		return DamageElement.ANIMAL_PIG; 
					else if(entity instanceof Sheep) 	return DamageElement.ANIMAL_SHEEP;
					else if(entity instanceof Wolf)    	return DamageElement.ANIMAL_WOLF;
				}
				else if(entity instanceof Monster) 
				{
					if(entity instanceof Zombie) 		return (entity instanceof PigZombie?DamageElement.MOB_PIGZOMBIE:DamageElement.MOB_ZOMBIE);
					else if(entity instanceof Creeper)	return DamageElement.MOB_CREEPER;
					else if(entity instanceof Giant) 	return DamageElement.MOB_GIANT;
					else if(entity instanceof Skeleton)	return DamageElement.MOB_SKELETON;
					else if(entity instanceof Spider)	return DamageElement.MOB_SPIDER; 
				}
				else if(entity instanceof WaterMob) 
					if(entity instanceof Squid) 		return DamageElement.ANIMAL_SQUID;
			}
			else if(entity instanceof Flying) 
				if(entity instanceof Ghast)				return DamageElement.MOB_GHAST;
		}
		return null;
	}
	
	public static DamageElement matchMeleeElement(Material material)
	{
		if(material != null)
			switch(material)
			{
			//Fist
				case AIR:			return DamageElement.MELEE_FIST;
			//Axes
				case WOOD_AXE: 		return DamageElement.MELEE_AXE;
				case STONE_AXE: 	return DamageElement.MELEE_AXE;
				case IRON_AXE:		return DamageElement.MELEE_AXE;
				case GOLD_AXE: 		return DamageElement.MELEE_AXE;
				case DIAMOND_AXE: 	return DamageElement.MELEE_AXE;
			//Hoes
				case WOOD_HOE: 		return DamageElement.MELEE_HOE;
				case STONE_HOE: 	return DamageElement.MELEE_HOE;
				case IRON_HOE:		return DamageElement.MELEE_HOE;
				case GOLD_HOE:		return DamageElement.MELEE_HOE;
				case DIAMOND_HOE: 	return DamageElement.MELEE_HOE;
			//Picks
				case WOOD_PICKAXE: 	return DamageElement.MELEE_PICKAXE;
				case STONE_PICKAXE: return DamageElement.MELEE_PICKAXE;
				case IRON_PICKAXE:	return DamageElement.MELEE_PICKAXE;
				case GOLD_PICKAXE:	return DamageElement.MELEE_PICKAXE;
				case DIAMOND_PICKAXE:return DamageElement.MELEE_PICKAXE;
			//Shovels	
				case WOOD_SPADE: 	return DamageElement.MELEE_SPADE;
				case STONE_SPADE: 	return DamageElement.MELEE_SPADE;
				case IRON_SPADE:	return DamageElement.MELEE_SPADE;
				case GOLD_SPADE:	return DamageElement.MELEE_SPADE;
				case DIAMOND_SPADE:	return DamageElement.MELEE_SPADE;
			//Swords	
				case WOOD_SWORD: 	return DamageElement.MELEE_SWORD;
				case STONE_SWORD: 	return DamageElement.MELEE_SWORD;
				case IRON_SWORD:	return DamageElement.MELEE_SWORD;
				case GOLD_SWORD:	return DamageElement.MELEE_SWORD;
				case DIAMOND_SWORD:	return DamageElement.MELEE_SWORD;
			//All others
				default: 			return MELEE_OTHER;
			}
		return null;
	}

	public static DamageElement matchArmorElement(Material material)
	{
		if(material != null)
			switch(material)
			{
			//Headwear
				case LEATHER_HELMET:		return DamageElement.ARMOR_HELMET;
				case IRON_HELMET:			return DamageElement.ARMOR_HELMET;
				case GOLD_HELMET:			return DamageElement.ARMOR_HELMET;
				case DIAMOND_HELMET:		return DamageElement.ARMOR_HELMET;
				case CHAINMAIL_HELMET:		return DamageElement.ARMOR_HELMET;
			//Chest
				case LEATHER_CHESTPLATE:	return DamageElement.ARMOR_CHESTPLATE;
				case IRON_CHESTPLATE:		return DamageElement.ARMOR_CHESTPLATE;
				case GOLD_CHESTPLATE:		return DamageElement.ARMOR_CHESTPLATE;
				case DIAMOND_CHESTPLATE:	return DamageElement.ARMOR_CHESTPLATE;
				case CHAINMAIL_CHESTPLATE:	return DamageElement.ARMOR_CHESTPLATE;
			//Legs
				case LEATHER_LEGGINGS:		return DamageElement.ARMOR_LEGGINGS;
				case IRON_LEGGINGS:			return DamageElement.ARMOR_LEGGINGS;
				case GOLD_LEGGINGS:			return DamageElement.ARMOR_LEGGINGS;
				case DIAMOND_LEGGINGS:		return DamageElement.ARMOR_LEGGINGS;
				case CHAINMAIL_LEGGINGS:	return DamageElement.ARMOR_LEGGINGS;
			//Boots
				case LEATHER_BOOTS:			return DamageElement.ARMOR_BOOTS;
				case IRON_BOOTS:			return DamageElement.ARMOR_BOOTS;
				case GOLD_BOOTS:			return DamageElement.ARMOR_BOOTS;
				case DIAMOND_BOOTS:			return DamageElement.ARMOR_BOOTS;
				case CHAINMAIL_BOOTS:		return DamageElement.ARMOR_BOOTS;
				
				default:					return null;
			}
		return null;
	}
	
	public static DamageElement matchRangedElement(Entity entity)
	{
		if(entity instanceof CraftArrow) return DamageElement.RANGED_BOW;
		if(entity instanceof CraftEgg) return DamageElement.RANGED_EGG;
		if(entity instanceof CraftSnowball) return DamageElement.RANGED_SNOWBALL;
		if(entity instanceof CraftFireball) return DamageElement.RANGED_FIREBALL;
		return null;
	}

	public static List<String> getStringsOf(DamageElement element){ return getStringsOf(element.getReference());}
	public static List<String> getGenericTypeStrings(){ return getStringsOf("generic");}
	public static List<String> getStringsOf(String elementType)
	{
		List<String> typeStrings = new ArrayList<String>();
		for(DamageElement element : DamageElement.values())
			if(element.getType() != null
					&& element.getType().getReference().equals(elementType))
				typeStrings.add(element.getReference());
		return typeStrings;
	}

	public static List<DamageElement> getElementsOf(DamageElement element){ return getElementsOf(element.getReference());}
	public static List<DamageElement> getGenericElements(){ return getElementsOf("generic");}
	public static List<DamageElement> getElementsOf(String elementType)
	{
		List<DamageElement> typeStrings = new ArrayList<DamageElement>();
		for(DamageElement element : DamageElement.values())
			if(element.getType() != null
					&& element.getType().getReference().equals(elementType))
				typeStrings.add(element);
		return typeStrings;
	}
	
	public static DamageElement matchDamageElement(String nodeName)
	{
		for(DamageElement element : DamageElement.values())
			if(element.getReference().equals(nodeName))
				return element;
		return null;
	}
}