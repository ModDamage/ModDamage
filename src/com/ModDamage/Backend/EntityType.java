package com.ModDamage.Backend;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.ModDamage.ModDamage;

public enum EntityType implements Matchable<EntityType>
{
	UNKNOWN(null),
	ENTITY(null, Entity.class),
		LIVING(ENTITY, LivingEntity.class),
			ANIMAL(LIVING, Animals.class),
				CHICKEN(ANIMAL, CreatureType.CHICKEN, Chicken.class),
				COW(ANIMAL, CreatureType.COW, Cow.class),
				PIG(ANIMAL, CreatureType.PIG, Pig.class),
				SHEEP(ANIMAL, CreatureType.SHEEP, Sheep.class),
				SQUID(ANIMAL, CreatureType.SQUID, Squid.class),
				WOLF(ANIMAL, CreatureType.WOLF, Wolf.class)
				{
					@Override
					protected EntityType getMostSpecificType(Object obj)
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
				NPC(HUMAN, NPC.class),//FIXME Does this work?
					VILLAGER(NPC, CreatureType.VILLAGER, Villager.class),
			
			MOB(LIVING, Monster.class),
				BLAZE(MOB, CreatureType.BLAZE, Blaze.class),
				CAVESPIDER(MOB, CreatureType.CAVE_SPIDER, CaveSpider.class),
				CREEPER(MOB, CreatureType.CREEPER, Creeper.class)
				{
					@Override
					protected EntityType getMostSpecificType(Object obj)
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
					protected EntityType getMostSpecificType(Object obj)
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
					protected EntityType getMostSpecificType(Object obj)
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
					protected EntityType getMostSpecificType(Object obj)
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
			SNOWMAN(LIVING, CreatureType.SNOWMAN, Snowman.class),
		
		NONLIVING(ENTITY),
			PROJECTILE(NONLIVING, Projectile.class),
				ARROW(PROJECTILE, Arrow.class),
				EGG(PROJECTILE, Egg.class),
				FIREBALL(PROJECTILE, Fireball.class),
					FIREBALL_SMALL(FIREBALL, SmallFireball.class),
				FISHINGROD(PROJECTILE, Fish.class),
				POTION(PROJECTILE, ThrownPotion.class),
				SNOWBALL(PROJECTILE, Snowball.class),
			
			TRAP(NONLIVING),
				DISPENSER(TRAP);
	
	private static Map<Class<?>, EntityType> byClass = new HashMap<Class<?>, EntityType>();
	
	static {
		for (EntityType element : EntityType.values())
		{
			for (Class<?> myClass : element.myClasses)
			{
				if (byClass.containsKey(myClass))
					ModDamage.getPluginConfiguration().printToLog(Level.SEVERE, "Duplicate " + myClass + ": " + byClass.get(myClass) + ", " + element);
				byClass.put(myClass, element);
			}
		}
	}

	// Slime spawn constants
	public static final int SIZE_HUGE = 3;
	public static final int SIZE_LARGE = 2;
	public static final int SIZE_MEDIUM = 1;
	public static final int SIZE_SMALL = 0;
	
	
	
	private final EntityType parent;
	private final CreatureType creatureType;
	public final Class<?>[] myClasses;
	
	EntityType(EntityType parent, Class<?>... myClasses) 
	{
		this(parent, null, myClasses);
	}
	EntityType(EntityType parent, CreatureType creatureType, Class<?>... myClasses) 
	{
		this.parent = parent;
		this.creatureType = creatureType;
		this.myClasses = myClasses;
	}

	
	public CreatureType getCreatureType(){ return creatureType; }
	
	//Returns true if this is equals or a subtype of the inputted element
	public boolean matches(Matchable<?> other)
	{
		if (other == null || !EntityType.class.isAssignableFrom(other.getClass())) return false;
		EntityType type = (EntityType)other;
		
		
		EntityType temp = this;
		while (temp != null)
		{
			if(temp == type) return true;
			temp = temp.parent;
		}
		return false;
	}
	
	protected EntityType getMostSpecificType(Object obj)
	{
		return this;
	}
	
	public static EntityType get(Object obj)
	{
		if(obj == null) throw new IllegalArgumentException("Object cannot be null for getElementFor method!");
		Class<?> cls = obj.getClass();
		EntityType mde = byClass.get(cls);
		if (mde == null)
		{
			mde = EntityType.matchByInterfaces(Arrays.asList(cls.getInterfaces()));
			if (mde != null) 
				byClass.put(cls, mde);
		}
		if (mde != null) return mde.getMostSpecificType(obj);
		
		ModDamage.getPluginConfiguration().printToLog(Level.WARNING, "Uncaught mob type " + obj.getClass().getName() + "!");
		byClass.put(cls, UNKNOWN);
		return UNKNOWN;
	}

	
	public static EntityType getElementNamed(String string)
	{
		try
		{
			return EntityType.valueOf(string.toUpperCase());
		} catch (IllegalArgumentException e) { }
		return null;
	}
	
	private static EntityType matchByInterfaces(List<Class<?>> interfaces)
	{
		while (interfaces.size() > 0)
		{
			for (Class<?> cls : interfaces)
			{
				EntityType mde = byClass.get(cls);
				if (mde != null) return mde;
			}
			
			List<Class<?>> nextInterfaces = new ArrayList<Class<?>>();
			for (Class<?> cls : interfaces)
				nextInterfaces.addAll(Arrays.asList(cls.getInterfaces()));
			interfaces = nextInterfaces;
		}
		
		return null;
	}
	
	public boolean canSpawnCreature(){ return creatureType != null; }
	public LivingEntity spawnCreature(Location location)
	{
		if(creatureType != null)
			return location.getWorld().spawnCreature(location, creatureType);
		else throw new IllegalArgumentException("Cannot spawn " + (this.matches(LIVING)?"nonliving":"abstract living") + " element" + name() + "!");
	}
}