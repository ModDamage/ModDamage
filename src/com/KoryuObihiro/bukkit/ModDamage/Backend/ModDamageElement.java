package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Animals;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
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
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public enum ModDamageElement 
{
	UNKNOWN(null),
	GENERIC(null),
		LIVING(GENERIC),
			ANIMAL(LIVING),
				CHICKEN(ANIMAL, CreatureType.CHICKEN),
				COW(ANIMAL, CreatureType.COW),
				PIG(ANIMAL, CreatureType.PIG),
				SHEEP(ANIMAL, CreatureType.SHEEP),
				SNOW_GOLEM(ANIMAL),//FIXME
				SQUID(ANIMAL, CreatureType.SQUID),
				WOLF(ANIMAL, CreatureType.WOLF),
					WOLF_WILD(WOLF, CreatureType.WOLF),
					WOLF_ANGRY(WOLF, CreatureType.WOLF),
					WOLF_TAME(WOLF, CreatureType.WOLF),
				
			HUMAN(LIVING),
				PLAYER(HUMAN),
				NPC(HUMAN, CreatureType.MONSTER),//TODO Does this work?
				VILLAGER(HUMAN),//FIXME
			
			MOB(LIVING),
				BLAZE(MOB),//FIXME
				CAVESPIDER(MOB, CreatureType.CAVE_SPIDER),
				CREEPER(MOB, CreatureType.CREEPER),
					CREEPER_CHARGED(CREEPER, CreatureType.CREEPER),
					CREEPER_NORMAL(CREEPER, CreatureType.CREEPER),
				ENDERMAN(MOB, CreatureType.ENDERMAN),
				GHAST(MOB, CreatureType.GHAST),
				GIANT(MOB, CreatureType.GIANT),
				MAGMA_CUBE(MOB),//FIXME
				MOOSHROM(MOB),//FIXME
				SILVERFISH(MOB, CreatureType.SILVERFISH),
				SKELETON(MOB, CreatureType.SKELETON),
				SLIME(MOB, CreatureType.SLIME),
					SLIME_HUGE (SLIME, CreatureType.SLIME),
					SLIME_LARGE (SLIME, CreatureType.SLIME),
					SLIME_MEDIUM(SLIME, CreatureType.SLIME),
					SLIME_OTHER(SLIME, CreatureType.SLIME),
					SLIME_SMALL(SLIME, CreatureType.SLIME),
				SPIDER(MOB, CreatureType.SPIDER),
					SPIDER_JOCKEY(SPIDER, CreatureType.SPIDER),
					SPIDER_RIDERLESS(SPIDER, CreatureType.SPIDER),
				ZOMBIE(MOB, CreatureType.ZOMBIE),
				ZOMBIEPIGMAN(MOB, CreatureType.PIG_ZOMBIE),
		
		NONLIVING(GENERIC),
			NATURE(NONLIVING),
				EXPLOSION(NATURE),
					EXPLOSION_ENTITY(EXPLOSION),
					EXPLOSION_BLOCK(EXPLOSION),
				CACTUS(NATURE),
				DROWNING (NATURE),
				FALL(NATURE),
				FIRE(NATURE),
				BURN(NATURE),
				LAVA(NATURE),
				LIGHTNING(NATURE),
				SUFFOCATION(NATURE),
				VOID(NATURE),
			
			TRAP(NONLIVING),
				DISPENSER(TRAP);

	private final ModDamageElement genericElement;
	private final CreatureType creatureType;
	ModDamageElement(ModDamageElement parentElement) 
	{
		this.genericElement = parentElement;
		this.creatureType = null;
	}
	ModDamageElement(ModDamageElement genericElement, CreatureType creatureType) 
	{
		this.genericElement = genericElement;
		this.creatureType = creatureType;
	}
	public ModDamageElement getParentType(){ return genericElement;}

	public boolean isElementReference(String string)
	{
		for(ModDamageElement element : values())
			if(element.name().equalsIgnoreCase(string))
				return true;
		return false;
	}
	
	public CreatureType getCreatureType(){ return creatureType;}
	
	public static ModDamageElement matchNonlivingElement(DamageCause cause)
	{
		switch(cause) 
		{
			case BLOCK_EXPLOSION:   return EXPLOSION_BLOCK;
			case CONTACT: 			return CACTUS;
			case DROWNING: 			return DROWNING;
			case ENTITY_EXPLOSION: 	return EXPLOSION_ENTITY;
			case FALL: 				return FALL;
			case FIRE: 				return FIRE;
			case FIRE_TICK:			return BURN;
			case LAVA: 				return LAVA;
			case LIGHTNING: 		return LIGHTNING;
			case SUFFOCATION: 		return SUFFOCATION;
			case VOID: 				return VOID;
			default: 				return UNKNOWN;//shouldn't happen
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
				temp = temp.getParentType();
			}
		}
		return false;
	}

	public static ModDamageElement matchMobType(LivingEntity entity) throws IllegalArgumentException
	{
		if(entity == null) throw new IllegalArgumentException("Entity cannot be null for matchMobType method!");
		if(entity instanceof Slime)	
		{
			switch(((Slime)entity).getSize())
			{
				case 0: return SLIME_SMALL;
				case 1: return SLIME_MEDIUM;
				case 2: return SLIME_LARGE;
				case 3: return SLIME_HUGE;
				default:return SLIME_OTHER;
			}
		}
		if(entity instanceof Creature) 
		{
			if(entity instanceof Animals) 
			{
				if(entity instanceof Chicken) 	return CHICKEN;
				if(entity instanceof Cow) 		return COW; 
				if(entity instanceof Pig) 		return PIG; 
				if(entity instanceof Sheep) 	return SHEEP;
				if(entity instanceof Wolf)
				{
					if(((Wolf)entity).getOwner() != null) return WOLF_TAME;
					if(((Wolf)entity).isAngry()) return WOLF_ANGRY;
					return WOLF_WILD;
				}
			}
			if(entity instanceof Monster) 
			{
				if(entity instanceof CaveSpider)return CAVESPIDER;
				if(entity instanceof Creeper)	return ((Creeper)entity).isPowered()?CREEPER_CHARGED:CREEPER_NORMAL;
				if(entity instanceof Enderman)	return ENDERMAN;
				if(entity instanceof Giant) 	return GIANT;
				if(entity instanceof Silverfish)return SILVERFISH;
				if(entity instanceof Skeleton)	return SKELETON;
				if(entity instanceof Spider)
				{
					if(entity.getPassenger() != null) return SPIDER_JOCKEY;
					return SPIDER_RIDERLESS; 
				}
				if(entity instanceof Zombie) 	return (entity instanceof PigZombie?ZOMBIEPIGMAN:ZOMBIE);
			}
			//if(entity instanceof WaterMob) - Uncomment when there's more watermobs. :P
				if(entity instanceof Squid) 	return SQUID;
		}
		if(entity instanceof Flying) 
			if(entity instanceof Ghast)			return GHAST;
		if(entity instanceof HumanEntity)		return (entity instanceof Player)?PLAYER:NPC;
		ModDamage.log.severe("[ModDamage] Uncaught mob type " + entity.getClass().getName() + " for an event!");
		return UNKNOWN;
	}

	public static List<ModDamageElement> getElementsOf(ModDamageElement element){ return getElementsOf(element.name());}
	public static List<ModDamageElement> getElementsOf(String elementType)
	{
		List<ModDamageElement> typeStrings = new ArrayList<ModDamageElement>();
		for(ModDamageElement element : values())
			if(element.getParentType() != null && element.getParentType().name().equals(elementType))
				typeStrings.add(element);
		return typeStrings;
	}
	
	public static ModDamageElement matchElement(String nodeName)
	{
		for(ModDamageElement element : values())
			if(element.name().equalsIgnoreCase(nodeName))
				return element;
		return UNKNOWN;
	}
}