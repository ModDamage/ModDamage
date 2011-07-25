package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
GENERIC ("generic", null, true),
	GENERIC_ANIMAL 	("animal", GENERIC, true),
		ANIMAL_CHICKEN ("Chicken", GENERIC_ANIMAL, false),
		ANIMAL_COW ("Cow", GENERIC_ANIMAL, false),
		ANIMAL_PIG ("Pig", GENERIC_ANIMAL, false),
		ANIMAL_SHEEP ("Sheep", GENERIC_ANIMAL, false),
		ANIMAL_SQUID ("Squid", GENERIC_ANIMAL, false),
		ANIMAL_WOLF ("Wolf", GENERIC_ANIMAL, false),
			ANIMAL_WOLF_WILD ("Wolf_Wild", ANIMAL_WOLF, false),
			ANIMAL_WOLF_ANGRY ("Wolf_Hostile", ANIMAL_WOLF, false),
			ANIMAL_WOLF_TAME ("Wolf_Tame", ANIMAL_WOLF, false),
		
	GENERIC_HUMAN 	("human", GENERIC, false),
		HUMAN_PLAYER ("Player", GENERIC_HUMAN, false),
		HUMAN_NPC ("NPC", GENERIC_HUMAN, false),
	
	GENERIC_MOB 	("mob", GENERIC, true),
		MOB_CREEPER ("Creeper", GENERIC_MOB, false),
			MOB_CREEPER_CHARGED("Creeper_Charged", GENERIC_MOB, false),
			MOB_CREEPER_NORMAL ("Creeper_Normal", GENERIC_MOB, false),
		MOB_GHAST ("Ghast", GENERIC_MOB, false),
		MOB_GIANT ("Giant", GENERIC_MOB, false),
		MOB_PIGZOMBIE ("ZombiePigman", GENERIC_MOB, false),
		MOB_SKELETON ("Skeleton", GENERIC_MOB, false),
		MOB_SLIME ("Slime", GENERIC_MOB, true),
			MOB_SLIME_HUGE ("Slime_Huge", MOB_SLIME, false),
			MOB_SLIME_LARGE ("Slime_Large", MOB_SLIME, false),
			MOB_SLIME_MEDIUM("Slime_Medium", MOB_SLIME, false),
			MOB_SLIME_OTHER("Slime_Other", MOB_SLIME, false),
			MOB_SLIME_SMALL("Slime_Small", MOB_SLIME, false),
		MOB_SPIDER ("Spider", GENERIC_MOB, false),
		MOB_ZOMBIE ("Zombie", GENERIC_MOB, false),
	
	GENERIC_NATURE 	("nature", GENERIC, true),
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
	
	GENERIC_TRAP	("trap", GENERIC, true),
		TRAP_DISPENSER("dispenser", GENERIC_TRAP, false);

	private final String stringReference;
	private final ModDamageElement genericElement;
	private final boolean hasSubConfig;
	ModDamageElement(String stringReference, ModDamageElement genericElement, boolean hasSubConfig) 
	{
		this.stringReference = stringReference;
		this.genericElement = genericElement;
		this.hasSubConfig = hasSubConfig;
	}
	
	public String getReference(){ return this.stringReference;}
	public ModDamageElement getType(){ return genericElement;}
	public boolean hasSubConfiguration(){ return hasSubConfig;}

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
	public static ModDamageElement matchElement(ModDamageElement genericElement, String elementReference)
	{
		if(genericElement.hasSubConfig)
			for(ModDamageElement element : getElementsOf(genericElement))
				if(elementReference.equalsIgnoreCase(element.getReference())) return element;
		return null;
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
					if(entity.getPassenger() != null)
						Logger.getLogger("Minecraft").info("Found a SPIDER JOCKAY :D");//TODO REMOVE ME
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