package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Villager;
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
				//SNOWMAN(ANIMAL, CreatureType.SNOWMAN),//FIXME Not part of Bukkit yet?
				SQUID(ANIMAL, CreatureType.SQUID),
				WOLF(ANIMAL, CreatureType.WOLF),
					WOLF_WILD(WOLF, CreatureType.WOLF),
					WOLF_ANGRY(WOLF, CreatureType.WOLF)
					{
						@Override
						public LivingEntity spawnCreature(Location location)
						{
							Wolf wolf = ((Wolf)location.getWorld().spawnCreature(location, this.getCreatureType()));
							wolf.setAngry(true);
							return wolf;
						}
					},
					WOLF_TAME(WOLF, CreatureType.WOLF)
					{
						@Override
						public LivingEntity spawnCreature(Location location)
						{
							Wolf wolf = ((Wolf)location.getWorld().spawnCreature(location, this.getCreatureType()));
							wolf.setTamed(true);//FIXME WTF Does this do? XD
							return wolf;
						}
					},
				
			HUMAN(LIVING),
				PLAYER(HUMAN),
				NPC(HUMAN, CreatureType.MONSTER),//TODO Does this work?
					VILLAGER(NPC, CreatureType.VILLAGER),
			
			MOB(LIVING),
				BLAZE(MOB, CreatureType.BLAZE),
				CAVESPIDER(MOB, CreatureType.CAVE_SPIDER),
				CREEPER(MOB, CreatureType.CREEPER),
					CREEPER_CHARGED(CREEPER, CreatureType.CREEPER)
					{
						@Override
						public LivingEntity spawnCreature(Location location)
						{
							Creeper creeper = ((Creeper)location.getWorld().spawnCreature(location, this.getCreatureType()));
							creeper.setPowered(true);
							return creeper;
						}
					},
					CREEPER_NORMAL(CREEPER, CreatureType.CREEPER),
				ENDER_DRAGON(MOB, CreatureType.ENDER_DRAGON),
				ENDERMAN(MOB, CreatureType.ENDERMAN),
				GHAST(MOB, CreatureType.GHAST),
				GIANT(MOB, CreatureType.GIANT),
				//MAGMACUBE(MOB),TODO Future release
				MUSHROOM_COW(MOB, CreatureType.MUSHROOM_COW),
				SILVERFISH(MOB, CreatureType.SILVERFISH),
				SKELETON(MOB, CreatureType.SKELETON),
				SLIME(MOB, CreatureType.SLIME),
					SLIME_HUGE (SLIME, CreatureType.SLIME)
					{
						@Override
						public LivingEntity spawnCreature(Location location)
						{
							Slime slime = ((Slime)location.getWorld().spawnCreature(location, this.getCreatureType()));
							slime.setSize(SIZE_HUGE);
							return slime;
						}
					},
					SLIME_LARGE (SLIME, CreatureType.SLIME)
					{
						@Override
						public LivingEntity spawnCreature(Location location)
						{
							Slime slime = ((Slime)location.getWorld().spawnCreature(location, this.getCreatureType()));
							slime.setSize(SIZE_LARGE);
							return slime;
						}
					},
					SLIME_MEDIUM(SLIME, CreatureType.SLIME)
					{
						@Override
						public LivingEntity spawnCreature(Location location)
						{
							Slime slime = ((Slime)location.getWorld().spawnCreature(location, this.getCreatureType()));
							slime.setSize(SIZE_MEDIUM);
							return slime;
						}
					},
					SLIME_OTHER(SLIME, CreatureType.SLIME)
					{
						@Override
						public LivingEntity spawnCreature(Location location)
						{
							Slime slime = ((Slime)location.getWorld().spawnCreature(location, this.getCreatureType()));
							slime.setSize((int)Math.random()%10 + SIZE_HUGE);
							return slime;
						}
					},
					SLIME_SMALL(SLIME, CreatureType.SLIME)
					{
						@Override
						public LivingEntity spawnCreature(Location location)
						{
							Slime slime = ((Slime)location.getWorld().spawnCreature(location, this.getCreatureType()));
							slime.setSize(SIZE_SMALL);
							return slime;
						}
					},
				SPIDER(MOB, CreatureType.SPIDER),
					SPIDER_JOCKEY(SPIDER, CreatureType.SPIDER)
					{
						@Override
						public LivingEntity spawnCreature(Location location)
						{
							LivingEntity spider = location.getWorld().spawnCreature(location, this.getCreatureType());
							spider.setPassenger(location.getWorld().spawnCreature(location, SKELETON.getCreatureType()));
							return spider;
						}
					},
					SPIDER_RIDERLESS(SPIDER, CreatureType.SPIDER),
				ZOMBIE(MOB, CreatureType.ZOMBIE),
				ZOMBIEPIGMAN(MOB, CreatureType.PIG_ZOMBIE),
					ZOMBIEPIGMAN_ANGRY(ZOMBIEPIGMAN, CreatureType.PIG_ZOMBIE)
					{
						@Override
						public LivingEntity spawnCreature(Location location)
						{
							PigZombie pigZombie = (PigZombie)location.getWorld().spawnCreature(location, this.getCreatureType());
							pigZombie.setAngry(true);
							return pigZombie;
						}
					},
					ZOMBIEPIGMAN_NORMAL(ZOMBIEPIGMAN, CreatureType.PIG_ZOMBIE),
		
		NONLIVING(GENERIC),
			NATURE(NONLIVING),
				CACTUS(NATURE),
				DROWNING (NATURE),
				EXPLOSION(NATURE),
					EXPLOSION_ENTITY(EXPLOSION),
					EXPLOSION_BLOCK(EXPLOSION),
				FALL(NATURE),
				FIRE(NATURE),
				BURN(NATURE),
				LAVA(NATURE),
				LIGHTNING(NATURE),
				STARVATION(NATURE),
				SUFFOCATION(NATURE),
				SUICIDE(NATURE),
				VOID(NATURE),
			PROJECTILE(NONLIVING),
				ARROW(PROJECTILE),
				EGG(PROJECTILE),
				FIREBALL(PROJECTILE),
					FIREBALL_SMALL(FIREBALL),
				FISHINGROD(PROJECTILE),
				POTION(PROJECTILE),
				SNOWBALL(PROJECTILE),
			
			TRAP(NONLIVING),
				DISPENSER(TRAP);
					

//Some spawn constants
	public static final int SIZE_HUGE = 3;
	public static final int SIZE_LARGE = 2;
	public static final int SIZE_MEDIUM = 1;
	public static final int SIZE_SMALL = 0;
	
	/* TODO 0.9.7
	private String displayName = null;
	
	static
	{
		for(ModDamageElement element : ModDamageElement.values())
			if(element.displayName == null)
			{
				if(element.)
			}
	}
	*/
	
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
	
	public static ModDamageElement matchEventElement(DamageCause cause)
	{
		switch(cause)
		{
			case BLOCK_EXPLOSION:   return EXPLOSION_BLOCK;
			case CONTACT: 			return CACTUS;
			case DROWNING: 			return DROWNING;
			case ENTITY_ATTACK:		return LIVING;
			case ENTITY_EXPLOSION: 	return EXPLOSION_ENTITY;
			case FALL: 				return FALL;
			case FIRE: 				return FIRE;
			case FIRE_TICK:			return BURN;
			case LAVA: 				return LAVA;
			case LIGHTNING: 		return LIGHTNING;
			case PROJECTILE:		return PROJECTILE;
			case STARVATION:		return STARVATION;
			case SUFFOCATION: 		return SUFFOCATION;
			case SUICIDE:			return SUICIDE;
			case VOID: 				return VOID;
			default: 				return UNKNOWN;//shouldn't happen
		}
	}
	
	public static ModDamageElement matchRangedElement(Projectile projectile)
	{
		if(projectile instanceof Arrow)			return ARROW;
		if(projectile instanceof Egg)			return EGG;
		if(projectile instanceof Fireball)		return projectile instanceof SmallFireball?FIREBALL_SMALL:FIREBALL;
		if(projectile instanceof Fish)			return FISHINGROD; 
		if(projectile instanceof Snowball)		return SNOWBALL;
		if(projectile instanceof ThrownPotion)	return POTION;
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
				temp = temp.getParentType();
			}
		}
		return false;
	}
	
	public static ModDamageElement matchMobType(LivingEntity entity) throws IllegalArgumentException
	{
		//XXX Optimization - grab classname once, use string comparisons?
		if(entity == null) throw new IllegalArgumentException("Entity cannot be null for matchMobType method!");
		if(entity instanceof Slime)
		{
			switch(((Slime)entity).getSize())
			{
				case SIZE_SMALL: return SLIME_SMALL;
				case SIZE_MEDIUM: return SLIME_MEDIUM;
				case SIZE_LARGE: return SLIME_LARGE;
				case SIZE_HUGE: return SLIME_HUGE;
				default:return SLIME_OTHER;
			}
		}
		if(entity instanceof Creature) 
		{
			if(entity instanceof Animals) 
			{
				if(entity instanceof Chicken) 		return CHICKEN;
				if(entity instanceof Cow) 			return COW; 
				if(entity instanceof Pig) 			return PIG;
				if(entity instanceof MushroomCow)	return MUSHROOM_COW;
				if(entity instanceof Sheep) 		return SHEEP;
				//if(entity instanceof Snowman)		return SNOWMAN; //FIXME Not part of Bukkit yet?
				if(entity instanceof Wolf)
				{
					if(((Wolf)entity).getOwner() != null) return WOLF_TAME;
					if(((Wolf)entity).isAngry()) 	return WOLF_ANGRY;
					return WOLF_WILD;
				}
			}
			if(entity instanceof Monster) 
			{
				if(entity instanceof Blaze)				return BLAZE;
				if(entity instanceof CaveSpider)	return CAVESPIDER;
				if(entity instanceof Creeper)		return ((Creeper)entity).isPowered()?CREEPER_CHARGED:CREEPER_NORMAL;
				if(entity instanceof Enderman)		return ENDERMAN;
				if(entity instanceof Giant) 		return GIANT;
				if(entity instanceof Silverfish)	return SILVERFISH;
				if(entity instanceof Skeleton)		return SKELETON;
				if(entity instanceof Spider)
				{
					if(entity.getPassenger() != null) return SPIDER_JOCKEY;
					return SPIDER_RIDERLESS; 
				}
				if(entity instanceof Zombie) 		return (entity instanceof PigZombie?ZOMBIEPIGMAN:ZOMBIE);
			}
			//if(entity instanceof WaterMob) - Uncomment when there's more watermobs. :P
				if(entity instanceof Squid) 		return SQUID;
		}
		if(entity instanceof Flying) 
		{
			if(entity instanceof Ghast)				return GHAST;
		}
			
		if(entity instanceof Player)				return PLAYER;
		if(entity instanceof NPC)					return entity instanceof Villager?VILLAGER:NPC;//TODO Fix this if/when Villager is not the only kind of NPC.
		//if(entity instanceof ComplexLivingEntity)
		if(entity instanceof EnderDragon)			return ENDER_DRAGON;
		ModDamage.getPluginConfiguration().printToLog(Level.SEVERE, "Uncaught mob type " + entity.getClass().getName() + " for an event!");
		return UNKNOWN;
	}
	
	public static ModDamageElement matchElement(String nodeName)
	{
		ModDamageElement element = ModDamageElement.valueOf(nodeName.toUpperCase());
		if(element != null)	
			return element;
		return UNKNOWN;
	}
	
	public boolean canSpawnCreature(){ return creatureType != null;}
	public LivingEntity spawnCreature(Location location)
	{
		if(creatureType != null)
			return location.getWorld().spawnCreature(location, creatureType);
		else throw new IllegalArgumentException("Cannot spawn abstract element" + name() + "!");
	}
}