package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftArrow;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.craftbukkit.entity.CraftEgg;
import org.bukkit.craftbukkit.entity.CraftFireball;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.craftbukkit.entity.CraftSnowball;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
	GENERIC_TRAP	("trap", GENERIC, true),
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
	RANGED_FISHINGROD("fishingrod", GENERIC_RANGED, false),
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
//humans
	HUMAN_PLAYER ("Player", GENERIC_HUMAN, false),
	HUMAN_NPC ("NPC", GENERIC_HUMAN, false),
//mobs
	MOB_CREEPER ("Creeper", GENERIC_MOB, false),
	MOB_GHAST ("Ghast", GENERIC_MOB, false),
	MOB_GIANT ("Giant", GENERIC_MOB, false),
	MOB_PIGZOMBIE ("ZombiePigman", GENERIC_MOB, false),
	MOB_SKELETON ("Skeleton", GENERIC_MOB, false),
	MOB_SLIME ("Slime", GENERIC_MOB, true),
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
	NATURE_VOID ("void", GENERIC_NATURE, false),
//Dispenser/Spikes
	TRAP_DISPENSER("dispenser", GENERIC_TRAP, false),
//mob-specific stuff
	ANIMAL_WOLF_WILD ("Wolf_Wild", ANIMAL_WOLF, false),
	ANIMAL_WOLF_ANGRY ("Wolf_Hostile", ANIMAL_WOLF, false),
	ANIMAL_WOLF_TAME ("Wolf_Tame", ANIMAL_WOLF, false),

	MOB_CREEPER_CHARGED("Creeper_Charged", GENERIC_MOB, false),
	MOB_CREEPER_NORMAL ("Creeper_Normal", GENERIC_MOB, false),

	MOB_SLIME_HUGE ("Slime_Huge", MOB_SLIME, false),
	MOB_SLIME_LARGE ("Slime_Large", MOB_SLIME, false),
	MOB_SLIME_MEDIUM("Slime_Medium", MOB_SLIME, false),
	MOB_SLIME_OTHER("Slime_Other", MOB_SLIME, false),
	MOB_SLIME_SMALL("Slime_Small", MOB_SLIME, false);
	
	
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
		for(DamageElement element : values())
			if(element.getReference().equals(string))
				return true;
		return false;
	}
	
	public static DamageElement matchNonlivingElement(DamageCause cause)
	{
		switch(cause) 
		{
			case BLOCK_EXPLOSION:   return NATURE_BLOCK_EXPLOSION;
			case CONTACT: 			return NATURE_CONTACT;
			case DROWNING: 			return NATURE_DROWNING;
			case ENTITY_EXPLOSION: 	return NATURE_EXPLOSION;
			case FALL: 				return NATURE_FALL;
			case FIRE: 				return NATURE_FIRE;
			case FIRE_TICK:			return NATURE_FIRE_TICK;
			case LAVA: 				return NATURE_LAVA;
			case LIGHTNING: 		return NATURE_LIGHTNING;
			case SUFFOCATION: 		return NATURE_SUFFOCATION;
			case VOID: 				return NATURE_VOID;
			default: 				return null;//shouldn't happen
		}
	}
	public static DamageElement matchElement(DamageElement genericElement, String elementReference)
	{
		if(genericElement.hasSubConfig)
			for(DamageElement element : getElementsOf(genericElement))
				if(elementReference.equalsIgnoreCase(element.getReference())) return element;
		return null;
	}

	public static DamageElement matchMobType(LivingEntity entity)
	{
		if(entity instanceof Slime)				return MOB_SLIME;//XXX Not sure why, but Slimes aren't technically Creatures.
		if(entity instanceof Creature) 
		{
			if(entity instanceof Animals) 
			{
				if(entity instanceof Chicken) 	return ANIMAL_CHICKEN;
				if(entity instanceof Cow) 		return ANIMAL_COW; 
				if(entity instanceof Pig) 		return ANIMAL_PIG; 
				if(entity instanceof Sheep) 	return ANIMAL_SHEEP;
				if(entity instanceof Wolf)		return ANIMAL_WOLF;
			}
			if(entity instanceof Monster) 
			{
				if(entity instanceof Zombie) 	return (entity instanceof PigZombie?MOB_PIGZOMBIE:MOB_ZOMBIE);
				if(entity instanceof Creeper)	return MOB_CREEPER_NORMAL;
				if(entity instanceof Giant) 	return MOB_GIANT;
				if(entity instanceof Skeleton)	return MOB_SKELETON;
				if(entity instanceof Spider)	return MOB_SPIDER; 
			}
			if(entity instanceof WaterMob) 
				if(entity instanceof Squid) 	return ANIMAL_SQUID;
		}
		if(entity instanceof Flying) 
			if(entity instanceof Ghast)			return MOB_GHAST;
		if(entity instanceof HumanEntity)		return GENERIC_HUMAN;
		return null;
	}
	
	public static DamageElement matchMobState(LivingEntity entity)
	{
		if(entity != null)
		{
			if(entity instanceof Creeper)		return((CraftCreeper)entity).isPowered()?MOB_CREEPER_CHARGED:MOB_CREEPER_NORMAL;
			if(entity instanceof HumanEntity)	return (entity instanceof Player)?HUMAN_PLAYER:HUMAN_NPC;
			if(entity instanceof Slime)
			{
				Logger.getLogger("Minecraft").info("FOUND SLIME, size: " + ((CraftSlime)entity).getSize());//TODO REMOVE ME
				switch(((CraftSlime)entity).getSize())
				{
					case 0: return MOB_SLIME_SMALL;
					case 1: return MOB_SLIME_MEDIUM;
					case 2: return MOB_SLIME_LARGE;
					case 3: return MOB_SLIME_HUGE;
					default:return MOB_SLIME_OTHER;
				}
			}
			if(entity instanceof Wolf)
			{
				if(((CraftWolf)entity).getOwner() != null) return ANIMAL_WOLF_TAME;
				if(((CraftWolf)entity).isAngry()) return ANIMAL_WOLF_ANGRY;
				return ANIMAL_WOLF_WILD;
			}
		}
		return null;
	}

	public static DamageElement matchMeleeElement(Material material)
	{
		if(material != null)
			switch(material)
			{
			//Fist
				case AIR:			return MELEE_FIST;
			//Axes
				case WOOD_AXE:
				case STONE_AXE:
				case IRON_AXE:
				case GOLD_AXE:
				case DIAMOND_AXE: 	return MELEE_AXE;
			//Hoes
				case WOOD_HOE:
				case STONE_HOE:
				case IRON_HOE:
				case GOLD_HOE:
				case DIAMOND_HOE: 	return MELEE_HOE;
			//Picks
				case WOOD_PICKAXE:
				case STONE_PICKAXE:
				case IRON_PICKAXE:
				case GOLD_PICKAXE:
				case DIAMOND_PICKAXE:return MELEE_PICKAXE;
			//Shovels	
				case WOOD_SPADE:
				case STONE_SPADE:
				case IRON_SPADE:
				case GOLD_SPADE:
				case DIAMOND_SPADE:	return MELEE_SPADE;
			//Swords	
				case WOOD_SWORD:
				case STONE_SWORD:
				case IRON_SWORD:
				case GOLD_SWORD:
				case DIAMOND_SWORD:	return MELEE_SWORD;
				
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
				case LEATHER_HELMET:
				case IRON_HELMET:
				case GOLD_HELMET:
				case DIAMOND_HELMET:
				case CHAINMAIL_HELMET:		return ARMOR_HELMET;
			//Chest
				case LEATHER_CHESTPLATE:
				case IRON_CHESTPLATE:
				case GOLD_CHESTPLATE:
				case DIAMOND_CHESTPLATE:
				case CHAINMAIL_CHESTPLATE:	return ARMOR_CHESTPLATE;
			//Legs
				case LEATHER_LEGGINGS:
				case IRON_LEGGINGS:
				case GOLD_LEGGINGS:
				case DIAMOND_LEGGINGS:
				case CHAINMAIL_LEGGINGS:	return ARMOR_LEGGINGS;
			//Boots
				case LEATHER_BOOTS:
				case IRON_BOOTS:
				case GOLD_BOOTS:
				case DIAMOND_BOOTS:
				case CHAINMAIL_BOOTS:		return ARMOR_BOOTS;
				
				default:					return null;
			}
		return null;
	}
	
	public static DamageElement matchRangedElement(Entity entity)
	{
		if(entity instanceof CraftArrow)	return RANGED_BOW;
		if(entity instanceof CraftEgg)		return RANGED_EGG;
		if(entity instanceof CraftSnowball)	return RANGED_SNOWBALL;
		if(entity instanceof CraftFireball)	return RANGED_FIREBALL;
		if(entity instanceof Projectile)	return RANGED_FISHINGROD; //XXX Deeefinitely sure this isn't going to work.
		return null;
	}

	public static List<DamageElement> getElementsOf(DamageElement element){ return getElementsOf(element.getReference());}
	public static List<DamageElement> getElementsOf(String elementType)
	{
		List<DamageElement> typeStrings = new ArrayList<DamageElement>();
		for(DamageElement element : values())
			if(element.getType() != null && element.getType().getReference().equals(elementType))
				typeStrings.add(element);
		return typeStrings;
	}
	
	public static DamageElement matchDamageElement(String nodeName)
	{
		for(DamageElement element : values())
			if(element.getReference().equals(nodeName))
				return element;
		return null;
	}
}