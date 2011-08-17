package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.WaterMob;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public enum ModDamageElement 
{
GENERIC ("generic", null),
	GENERIC_ANIMAL 	("animal", GENERIC),
		ANIMAL_CHICKEN ("Chicken", GENERIC_ANIMAL),
		ANIMAL_COW ("Cow", GENERIC_ANIMAL),
		ANIMAL_PIG ("Pig", GENERIC_ANIMAL),
		ANIMAL_SHEEP ("Sheep", GENERIC_ANIMAL),
		ANIMAL_SQUID ("Squid", GENERIC_ANIMAL),
		ANIMAL_WOLF ("Wolf", GENERIC_ANIMAL),
			ANIMAL_WOLF_WILD ("Wolf_Wild", ANIMAL_WOLF),
			ANIMAL_WOLF_ANGRY ("Wolf_Hostile", ANIMAL_WOLF),
			ANIMAL_WOLF_TAME ("Wolf_Tame", ANIMAL_WOLF),
		
	GENERIC_HUMAN 	("Human", GENERIC),
		HUMAN_PLAYER ("Player", GENERIC_HUMAN),
		HUMAN_NPC ("NPC", GENERIC_HUMAN),
	
	GENERIC_MOB 	("Mob", GENERIC),
		MOB_CREEPER ("Creeper", GENERIC_MOB),
			MOB_CREEPER_CHARGED("Creeper_Charged", MOB_CREEPER),
			MOB_CREEPER_NORMAL ("Creeper_Normal", MOB_CREEPER),
		MOB_GHAST ("Ghast", GENERIC_MOB),
		MOB_GIANT ("Giant", GENERIC_MOB),
		MOB_PIGZOMBIE ("ZombiePigman", GENERIC_MOB),
		MOB_SKELETON ("Skeleton", GENERIC_MOB),
		MOB_SLIME ("Slime", GENERIC_MOB),
			MOB_SLIME_HUGE ("Slime_Huge", MOB_SLIME),
			MOB_SLIME_LARGE ("Slime_Large", MOB_SLIME),
			MOB_SLIME_MEDIUM("Slime_Medium", MOB_SLIME),
			MOB_SLIME_OTHER("Slime_Other", MOB_SLIME),
			MOB_SLIME_SMALL("Slime_Small", MOB_SLIME),
		MOB_SPIDER ("Spider", GENERIC_MOB),
			MOB_SPIDER_JOCKEY("Spider_Jockey", MOB_SPIDER),
			MOB_SPIDER_RIDERLESS("Spider_Riderless", MOB_SPIDER),
		MOB_ZOMBIE ("Zombie", GENERIC_MOB),
	
	GENERIC_NATURE 	("Nature", GENERIC),
		NATURE_EXPLOSION ("Explosion", GENERIC_NATURE),
			NATURE_EXPLOSION_ENTITY ("Explosion_Entity", NATURE_EXPLOSION),
			NATURE_EXPLOSION_BLOCK ("Explosion_Block", NATURE_EXPLOSION),
		NATURE_CONTACT("Cactus", GENERIC_NATURE),
		NATURE_DROWNING ("Drowning", GENERIC_NATURE),
		NATURE_FALL ("Fall", GENERIC_NATURE),
		NATURE_FIRE ("Fire", GENERIC_NATURE),
		NATURE_FIRE_TICK ("Burn", GENERIC_NATURE),
		NATURE_LAVA ("Lava", GENERIC_NATURE),
		NATURE_LIGHTNING ("Lightning", GENERIC_NATURE),
		NATURE_SUFFOCATION ("Suffocation", GENERIC_NATURE),
		NATURE_VOID ("Void", GENERIC_NATURE),
	
	GENERIC_TRAP("Trap", GENERIC),
		TRAP_DISPENSER("Dispenser", GENERIC_TRAP);

	private final String stringReference;
	private final ModDamageElement genericElement;
	ModDamageElement(String stringReference, ModDamageElement genericElement) 
	{
		this.stringReference = stringReference;
		this.genericElement = genericElement;
	}
	
	public String getReference(){ return this.stringReference;}
	public ModDamageElement getType(){ return genericElement;}

	public boolean isElementReference(String string)
	{
		for(ModDamageElement element : values())
			if(element.getReference().equals(string))
				return true;
		return false;
	}
	
	public static ModDamageElement matchNonlivingElement(DamageCause cause)
	{
		switch(cause) 
		{
			case BLOCK_EXPLOSION:   return NATURE_EXPLOSION_BLOCK;
			case CONTACT: 			return NATURE_CONTACT;
			case DROWNING: 			return NATURE_DROWNING;
			case ENTITY_EXPLOSION: 	return NATURE_EXPLOSION_ENTITY;
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
	
	//Returns true if this is equals or a subtype of the inputted element
	public boolean matchesType(ModDamageElement element)
	{
		if(element != null)
		{
			ModDamageElement temp = this;
			while(true)
			{
				if(temp.equals(element)) return true;
				if(temp.equals(ModDamageElement.GENERIC)) break;
				temp = temp.getType();
			}
		}
		return false;
	}

	public static ModDamageElement matchMobType(LivingEntity entity)
	{
		if(entity instanceof Slime)	
		{
			switch(((CraftSlime)entity).getSize())
			{
				case 0: return MOB_SLIME_SMALL;
				case 1: return MOB_SLIME_MEDIUM;
				case 2: return MOB_SLIME_LARGE;
				case 3: return MOB_SLIME_HUGE;
				default:return MOB_SLIME_OTHER;
			}
		}
		if(entity instanceof Creature) 
		{
			if(entity instanceof Animals) 
			{
				if(entity instanceof Chicken) 	return ANIMAL_CHICKEN;
				if(entity instanceof Cow) 		return ANIMAL_COW; 
				if(entity instanceof Pig) 		return ANIMAL_PIG; 
				if(entity instanceof Sheep) 	return ANIMAL_SHEEP;
				if(entity instanceof Wolf)
				{
					if(((CraftWolf)entity).getOwner() != null) return ANIMAL_WOLF_TAME;
					if(((CraftWolf)entity).isAngry()) return ANIMAL_WOLF_ANGRY;
					return ANIMAL_WOLF_WILD;
				}
			}
			if(entity instanceof Monster) 
			{
				if(entity instanceof Zombie) 	return (entity instanceof PigZombie?MOB_PIGZOMBIE:MOB_ZOMBIE);
				if(entity instanceof Creeper)	return ((CraftCreeper)entity).isPowered()?MOB_CREEPER_CHARGED:MOB_CREEPER_NORMAL;;
				if(entity instanceof Giant) 	return MOB_GIANT;
				if(entity instanceof Skeleton)	return MOB_SKELETON;
				if(entity instanceof Spider)
				{
					if(entity.getPassenger() != null) return MOB_SPIDER_JOCKEY;
					return MOB_SPIDER; 
				}
			}
			if(entity instanceof WaterMob) 
				if(entity instanceof Squid) 	return ANIMAL_SQUID;
		}
		if(entity instanceof Flying) 
			if(entity instanceof Ghast)			return MOB_GHAST;
		if(entity instanceof HumanEntity)		return (entity instanceof Player)?HUMAN_PLAYER:HUMAN_NPC;
		return null;
	}

	public static List<ModDamageElement> getElementsOf(ModDamageElement element){ return getElementsOf(element.getReference());}
	public static List<ModDamageElement> getElementsOf(String elementType)
	{
		List<ModDamageElement> typeStrings = new ArrayList<ModDamageElement>();
		for(ModDamageElement element : values())
			if(element.getType() != null && element.getType().getReference().equals(elementType))
				typeStrings.add(element);
		return typeStrings;
	}
	
	public static ModDamageElement matchElement(String nodeName)
	{
		for(ModDamageElement element : values())
			if(element.getReference().equalsIgnoreCase(nodeName))
				return element;
		return null;
	}
}