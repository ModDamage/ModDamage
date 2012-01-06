package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
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
import org.bukkit.entity.Snowman;
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
	GENERIC(null, Entity.class),
		LIVING(GENERIC, LivingEntity.class),
			ANIMAL(LIVING, Animals.class),
				CHICKEN(ANIMAL, CreatureType.CHICKEN, Chicken.class),
				COW(ANIMAL, CreatureType.COW, Cow.class),
				PIG(ANIMAL, CreatureType.PIG, Pig.class),
				SHEEP(ANIMAL, CreatureType.SHEEP, Sheep.class),
				SNOWMAN(ANIMAL, CreatureType.SNOWMAN, Snowman.class),
				SQUID(ANIMAL, CreatureType.SQUID, Squid.class),
				WOLF(ANIMAL, CreatureType.WOLF, Wolf.class)
				{
					@Override
					protected ModDamageElement getMostSpecificType(Object obj)
					{
						Wolf wolf = (Wolf) obj;
						if (wolf.isTamed()) return WOLF_TAME;
						if (wolf.isAngry()) return WOLF_ANGRY;
						return WOLF_WILD;
					}
				},
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
							wolf.setTamed(true);//FIXME Who would this belong to?
							return wolf;
						}
					},
				
			HUMAN(LIVING, HumanEntity.class),
				PLAYER(HUMAN, Player.class),
				NPC(HUMAN, CreatureType.MONSTER, NPC.class),//FIXME Does this work?
					VILLAGER(NPC, CreatureType.VILLAGER, Villager.class),
			
			MOB(LIVING, Monster.class),
				BLAZE(MOB, CreatureType.BLAZE, Blaze.class),
				CAVESPIDER(MOB, CreatureType.CAVE_SPIDER, CaveSpider.class),
				CREEPER(MOB, CreatureType.CREEPER, Creeper.class)
				{
					@Override
					protected ModDamageElement getMostSpecificType(Object obj)
					{
						Creeper creeper = (Creeper) obj;
						if (creeper.isPowered()) return CREEPER_CHARGED;
						return CREEPER_NORMAL;
					}
				},
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
				ENDER_DRAGON(MOB, CreatureType.ENDER_DRAGON, EnderDragon.class),
				ENDERMAN(MOB, CreatureType.ENDERMAN, Enderman.class),
				GHAST(MOB, CreatureType.GHAST, Ghast.class),
				GIANT(MOB, CreatureType.GIANT, Giant.class),
				MAGMA_CUBE(MOB, CreatureType.MAGMA_CUBE, MagmaCube.class),
				MUSHROOM_COW(MOB, CreatureType.MUSHROOM_COW, MushroomCow.class),
				SILVERFISH(MOB, CreatureType.SILVERFISH, Silverfish.class),
				SKELETON(MOB, CreatureType.SKELETON, Skeleton.class),
				SLIME(MOB, CreatureType.SLIME, Slime.class)
				{
					@Override
					protected ModDamageElement getMostSpecificType(Object obj)
					{
						switch(((Slime)obj).getSize())
						{
							case SIZE_SMALL: 	return SLIME_SMALL;
							case SIZE_MEDIUM: 	return SLIME_MEDIUM;
							case SIZE_LARGE: 	return SLIME_LARGE;
							case SIZE_HUGE: 	return SLIME_HUGE;
							default:			return SLIME_OTHER;
						}
					}
				},
					SLIME_HUGE(SLIME, CreatureType.SLIME)
					{
						@Override
						public LivingEntity spawnCreature(Location location)
						{
							Slime slime = ((Slime)location.getWorld().spawnCreature(location, this.getCreatureType()));
							slime.setSize(SIZE_HUGE);
							return slime;
						}
					},
					SLIME_LARGE(SLIME, CreatureType.SLIME)
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
				SPIDER(MOB, CreatureType.SPIDER, Spider.class)
				{
					@Override
					protected ModDamageElement getMostSpecificType(Object obj)
					{
						if(((Spider)obj).getPassenger() != null) return SPIDER_JOCKEY;
						return SPIDER_RIDERLESS;
					}
				},
					SPIDER_JOCKEY(SPIDER, CreatureType.SPIDER)
					{
						@Override
						public LivingEntity spawnCreature(Location location)
						{
							LivingEntity spider = location.getWorld().spawnCreature(location, this.getCreatureType());
							spider.setPassenger(location.getWorld().spawnCreature(location, CreatureType.SKELETON));
							return spider;
						}
					},
					SPIDER_RIDERLESS(SPIDER, CreatureType.SPIDER),
				ZOMBIE(MOB, CreatureType.ZOMBIE, Zombie.class),
				ZOMBIEPIGMAN(MOB, CreatureType.PIG_ZOMBIE, PigZombie.class)
				{
					@Override
					protected ModDamageElement getMostSpecificType(Object obj)
					{
						if (((PigZombie)obj).isAngry()) return ZOMBIEPIGMAN_ANGRY;
						return ZOMBIEPIGMAN_NORMAL;
					}
				},
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
				DROWNING(NATURE),
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
			PROJECTILE(NONLIVING, Projectile.class),
				ARROW(PROJECTILE, Arrow.class),
				EGG(PROJECTILE, Egg.class),
				FIREBALL(PROJECTILE, Fireball.class),
					FIREBALL_SMALL(FIREBALL, SmallFireball.class),
				FISHINGROD(PROJECTILE, Fish.class),
				POTION(PROJECTILE, ThrownPotion.class),
				SNOWBALL(PROJECTILE, Snowball.class),
			
			TRAP(NONLIVING),
				DISPENSER(TRAP),
		
		_DAMAGE_CAUSE(GENERIC, DamageCause.class)
		{
			@Override
			protected ModDamageElement getMostSpecificType(Object obj)
			{
				switch((DamageCause)obj)
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
		};
	
	public static Map<String, ModDamageElement> byName = new HashMap<String, ModDamageElement>();
	public static Map<Class<?>, ModDamageElement> byClass = new HashMap<Class<?>, ModDamageElement>();
	
	static {
		for (ModDamageElement element : ModDamageElement.values())
		{
			byName.put(element.name(), element);
			for (Class<?> myClass : element.myClasses)
			{
				if (byClass.containsKey(myClass))
					ModDamage.getPluginConfiguration().printToLog(Level.SEVERE, "Duplicate " + myClass + ": " + byClass.get(myClass) + ", " + element);
				byClass.put(myClass, element);
			}
			
			element.subElements = Collections.unmodifiableList(element.subElements);
		}
		
		byName = Collections.unmodifiableMap(byName);
		//byClass = Collections.unmodifiableMap(byClass);
	}

	//Some spawn constants
	public static final int SIZE_HUGE = 3;
	public static final int SIZE_LARGE = 2;
	public static final int SIZE_MEDIUM = 1;
	public static final int SIZE_SMALL = 0;
	
	
	
	private final ModDamageElement genericElement;
	private final CreatureType creatureType;
	private final Class<?>[] myClasses;
	private List<ModDamageElement> subElements;
	
	ModDamageElement(ModDamageElement genericElement, Class<?>... myClasses) 
	{
		this(genericElement, null, myClasses);
	}
	ModDamageElement(ModDamageElement genericElement, CreatureType creatureType, Class<?>... myClasses) 
	{
		this.genericElement = genericElement;
		this.creatureType = creatureType;
		this.myClasses = myClasses;
		
		this.subElements = new ArrayList<ModDamageElement>();
		if (genericElement != null)
			genericElement.subElements.add(this);
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
		/*switch(cause)
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
		}*/
		return matchType(cause);
	}
	
	public static ModDamageElement matchRangedElement(Projectile projectile)
	{
		/*if(projectile instanceof Arrow)			return ARROW;
		if(projectile instanceof Egg)			return EGG;
		if(projectile instanceof Fireball)		return projectile instanceof SmallFireball?FIREBALL_SMALL:FIREBALL;
		if(projectile instanceof Fish)			return FISHINGROD; 
		if(projectile instanceof Snowball)		return SNOWBALL;
		if(projectile instanceof ThrownPotion)	return POTION;
		return null;*/
		return matchType(projectile);
	}
	
	//Returns true if this is equals or a subtype of the inputted element
	public boolean matchesType(ModDamageElement element)
	{
		if(element != null)
		{
			ModDamageElement temp = this;
			while (true)
			{
				if(temp == element) return true;
				if (temp == ModDamageElement.GENERIC) return false;
				temp = temp.getParentType();
			}
		}
		return false;
	}
	
	protected ModDamageElement getMostSpecificType(Object obj)
	{
		return this;
	}
	
	public static ModDamageElement matchType(Object obj)
	{
		if(obj == null) throw new IllegalArgumentException("Object cannot be null for matchType method!");
		ModDamageElement mde = byClass.get(obj.getClass());
		if (mde == null)
		{
			mde = ModDamageElement.matchByInterfaces(Arrays.asList(obj.getClass().getInterfaces()));
			if (mde != null) 
			{
				byClass.put(obj.getClass(), mde);
				ModDamage.getPluginConfiguration().printToLog(Level.INFO, "matchType " + obj.getClass() + " -> " + mde);
			}
		}
		if (mde != null) return mde.getMostSpecificType(obj);
		
		ModDamage.getPluginConfiguration().printToLog(Level.SEVERE, "Uncaught type " + obj.getClass().getName() + " for an event!");
		return UNKNOWN;
	}
	
	private static ModDamageElement matchByInterfaces(List<Class<?>> interfaces)
	{
		while (interfaces.size() > 0)
		{
			for (Class<?> cls : interfaces)
			{
				ModDamageElement mde = byClass.get(cls);
				if (mde != null) return mde;
			}
			
			List<Class<?>> nextInterfaces = new ArrayList<Class<?>>();
			for (Class<?> cls : interfaces)
			{
				nextInterfaces.addAll(Arrays.asList(cls.getInterfaces()));
			}
			interfaces = nextInterfaces;
		}
		
		return null;
	}
	
	public static ModDamageElement matchMobType(LivingEntity entity) throws IllegalArgumentException
	{
		return matchType(entity);
		/*
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
				if(entity instanceof Wolf)
				{
					if(((Wolf)entity).getOwner() != null) return WOLF_TAME;
					if(((Wolf)entity).isAngry()) 	return WOLF_ANGRY;
					return WOLF_WILD;
				}
			}
			if(entity instanceof Monster) 
			{
				if(entity instanceof Blaze)			return BLAZE;
				if(entity instanceof CaveSpider)	return CAVESPIDER;
				if(entity instanceof Creeper)		return ((Creeper)entity).isPowered()?CREEPER_CHARGED:CREEPER_NORMAL;
				if(entity instanceof Enderman)		return ENDERMAN;
				if(entity instanceof Giant) 		return GIANT;
				if(entity instanceof MagmaCube) 	return MAGMA_CUBE;
				if(entity instanceof Silverfish)	return SILVERFISH;
				if(entity instanceof Skeleton)		return SKELETON;
				if(entity instanceof Spider)
				{
					if(entity.getPassenger() != null) return SPIDER_JOCKEY;
					return SPIDER_RIDERLESS; 
				}
				if(entity instanceof Zombie) 		return (entity instanceof PigZombie?ZOMBIEPIGMAN:ZOMBIE);
			}
			//if(entity instanceof WaterMob) - Uncomment when there's more watermobs.
			{
				if(entity instanceof Squid) 		return SQUID;
			}
			if(entity instanceof Snowman)		return SNOWMAN;
		}
		//if(entity instanceof Flying) - Uncomment when there's more flying mobs.
		{
			if(entity instanceof Ghast)				return GHAST;
		}
			
		if(entity instanceof Player)				return PLAYER;
		if(entity instanceof NPC)					return entity instanceof Villager?VILLAGER:NPC;//TODO Fix this if/when Villager is not the only kind of NPC.
		//if(entity instanceof ComplexLivingEntity)
		{
			if(entity instanceof EnderDragon)			return ENDER_DRAGON;
		}
		ModDamage.getPluginConfiguration().printToLog(Level.SEVERE, "Uncaught mob type " + entity.getClass().getName() + " for an event!");
		return UNKNOWN;*/
	}
	
	public static ModDamageElement matchElement(String string)
	{
		//for(ModDamageElement element : ModDamageElement.values())
		//	if(string.equalsIgnoreCase(element.name()))
		//		return element;
		ModDamageElement mde = byName.get(string.toUpperCase());
		if (mde != null) return mde;
		return UNKNOWN;
	}
	
	public boolean canSpawnCreature(){ return creatureType != null;}
	public LivingEntity spawnCreature(Location location)
	{
		if(creatureType != null)
			return location.getWorld().spawnCreature(location, creatureType);
		else throw new IllegalArgumentException("Cannot spawn " + (this.matchesType(LIVING)?"nonliving":"abstract living") + " element" + name() + "!");
	}
}