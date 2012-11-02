package com.ModDamage.Matchables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.*;

import com.ModDamage.ModDamage;

public enum EntityType implements Matchable<EntityType>
{
	NONE(null),
	UNKNOWN(null, org.bukkit.entity.EntityType.UNKNOWN),
	ENTITY(null, Entity.class),
		LIVING(ENTITY, LivingEntity.class),
            AMBIENT(LIVING, Ambient.class),
                BAT(AMBIENT, org.bukkit.entity.EntityType.BAT, Bat.class),
			ANIMAL(LIVING, Animals.class),
				CHICKEN(ANIMAL, org.bukkit.entity.EntityType.CHICKEN, Chicken.class),
				COW(ANIMAL, org.bukkit.entity.EntityType.COW, Cow.class),
				OCELOT(ANIMAL, org.bukkit.entity.EntityType.OCELOT, Ocelot.class),
				PIG(ANIMAL, org.bukkit.entity.EntityType.PIG, Pig.class),
				SHEEP(ANIMAL, org.bukkit.entity.EntityType.SHEEP, Sheep.class),
				SQUID(ANIMAL, org.bukkit.entity.EntityType.SQUID, Squid.class),
				WOLF(ANIMAL, org.bukkit.entity.EntityType.WOLF, Wolf.class)
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
					WOLF_WILD(WOLF, org.bukkit.entity.EntityType.WOLF),
					WOLF_ANGRY(WOLF, org.bukkit.entity.EntityType.WOLF)
					{
						@Override
						public LivingEntity spawn(Location location)
						{
							Wolf wolf = ((Wolf)location.getWorld().spawnEntity(location, this.getCreatureType()));
							wolf.setAngry(true);
							return wolf;
						}
					},
					WOLF_TAME(WOLF, org.bukkit.entity.EntityType.WOLF)
					{
						@Override
						public LivingEntity spawn(Location location)
						{
							Wolf wolf = ((Wolf)location.getWorld().spawnEntity(location, this.getCreatureType()));
							wolf.setTamed(true);
							return wolf;
						}
					},
				
			HUMAN(LIVING, HumanEntity.class),
				PLAYER(HUMAN, org.bukkit.entity.EntityType.PLAYER, Player.class),
				NPC(HUMAN, NPC.class),
					IRONGOLEM(NPC, org.bukkit.entity.EntityType.IRON_GOLEM, IronGolem.class),
					VILLAGER(NPC, org.bukkit.entity.EntityType.VILLAGER, Villager.class),
			
			MOB(LIVING, Monster.class),
				BLAZE(MOB, org.bukkit.entity.EntityType.BLAZE, Blaze.class),
				CAVESPIDER(MOB, org.bukkit.entity.EntityType.CAVE_SPIDER, CaveSpider.class),
				CREEPER(MOB, org.bukkit.entity.EntityType.CREEPER, Creeper.class)
				{
					@Override
					protected EntityType getMostSpecificType(Object obj)
					{
						Creeper creeper = (Creeper) obj;
						if (creeper.isPowered()) return CREEPER_CHARGED;
						return CREEPER_NORMAL;
					}
				},
					CREEPER_CHARGED(CREEPER, org.bukkit.entity.EntityType.CREEPER)
					{
						@Override
						public LivingEntity spawn(Location location)
						{
							Creeper creeper = ((Creeper)location.getWorld().spawnEntity(location, this.getCreatureType()));
							creeper.setPowered(true);
							return creeper;
						}
					},
					CREEPER_NORMAL(CREEPER, org.bukkit.entity.EntityType.CREEPER),
				ENDER_DRAGON(MOB, org.bukkit.entity.EntityType.ENDER_DRAGON, EnderDragon.class),
				ENDERMAN(MOB, org.bukkit.entity.EntityType.ENDERMAN, Enderman.class),
				GHAST(MOB, org.bukkit.entity.EntityType.GHAST, Ghast.class),
				GIANT(MOB, org.bukkit.entity.EntityType.GIANT, Giant.class),
				MAGMA_CUBE(MOB, org.bukkit.entity.EntityType.MAGMA_CUBE, MagmaCube.class),
				MUSHROOM_COW(MOB, org.bukkit.entity.EntityType.MUSHROOM_COW, MushroomCow.class),
				SILVERFISH(MOB, org.bukkit.entity.EntityType.SILVERFISH, Silverfish.class),
				SKELETON(MOB, org.bukkit.entity.EntityType.SKELETON, Skeleton.class),
				SLIME(MOB, org.bukkit.entity.EntityType.SLIME, Slime.class)
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
					SLIME_HUGE(SLIME, org.bukkit.entity.EntityType.SLIME)
					{
						@Override
						public LivingEntity spawn(Location location)
						{
							Slime slime = ((Slime)location.getWorld().spawnEntity(location, this.getCreatureType()));
							slime.setSize(SIZE_HUGE);
							return slime;
						}
					},
					SLIME_LARGE(SLIME, org.bukkit.entity.EntityType.SLIME)
					{
						@Override
						public LivingEntity spawn(Location location)
						{
							Slime slime = ((Slime)location.getWorld().spawnEntity(location, this.getCreatureType()));
							slime.setSize(SIZE_LARGE);
							return slime;
						}
					},
					SLIME_MEDIUM(SLIME, org.bukkit.entity.EntityType.SLIME)
					{
						@Override
						public LivingEntity spawn(Location location)
						{
							Slime slime = ((Slime)location.getWorld().spawnEntity(location, this.getCreatureType()));
							slime.setSize(SIZE_MEDIUM);
							return slime;
						}
					},
					SLIME_SMALL(SLIME, org.bukkit.entity.EntityType.SLIME)
					{
						@Override
						public LivingEntity spawn(Location location)
						{
							Slime slime = ((Slime)location.getWorld().spawnEntity(location, this.getCreatureType()));
							slime.setSize(SIZE_SMALL);
							return slime;
						}
					},
					SLIME_OTHER(SLIME, org.bukkit.entity.EntityType.SLIME)
					{
						@Override
						public LivingEntity spawn(Location location)
						{
							Slime slime = ((Slime)location.getWorld().spawnEntity(location, this.getCreatureType()));
							slime.setSize((int)Math.random()%10 + SIZE_HUGE);
							return slime;
						}
					},
				SPIDER(MOB, org.bukkit.entity.EntityType.SPIDER, Spider.class)
				{
					@Override
					protected EntityType getMostSpecificType(Object obj)
					{
						if(((Spider)obj).getPassenger() != null) return SPIDER_JOCKEY;
						return SPIDER_RIDERLESS;
					}
				},
					SPIDER_JOCKEY(SPIDER, org.bukkit.entity.EntityType.SPIDER)
					{
						@Override
						public LivingEntity spawn(Location location)
						{
							LivingEntity spider = (LivingEntity) location.getWorld().spawnEntity(location, this.getCreatureType());
							spider.setPassenger(location.getWorld().spawnEntity(location, org.bukkit.entity.EntityType.SKELETON));
							return spider;
						}
					},
					SPIDER_RIDERLESS(SPIDER, org.bukkit.entity.EntityType.SPIDER),
				ZOMBIE(MOB, org.bukkit.entity.EntityType.ZOMBIE, Zombie.class),
				PIGZOMBIE(MOB, org.bukkit.entity.EntityType.PIG_ZOMBIE, PigZombie.class)
				{
					@Override
					protected EntityType getMostSpecificType(Object obj)
					{
						if (((PigZombie)obj).isAngry()) return PIGZOMBIE_ANGRY;
						return PIGZOMBIE_NORMAL;
					}
				},
					PIGZOMBIE_ANGRY(PIGZOMBIE, org.bukkit.entity.EntityType.PIG_ZOMBIE)
					{
						@Override
						public LivingEntity spawn(Location location)
						{
							PigZombie pigZombie = (PigZombie)location.getWorld().spawnEntity(location, this.getCreatureType());
							pigZombie.setAngry(true);
							return pigZombie;
						}
					},
					PIGZOMBIE_NORMAL(PIGZOMBIE, org.bukkit.entity.EntityType.PIG_ZOMBIE),
                WITCH(MOB, org.bukkit.entity.EntityType.WITCH, Witch.class),
                WITHER(MOB, org.bukkit.entity.EntityType.WITHER, Wither.class),
			SNOWMAN(LIVING, org.bukkit.entity.EntityType.SNOWMAN, Snowman.class),
		
		NONLIVING(ENTITY),
			PROJECTILE(NONLIVING, Projectile.class),
				ARROW(PROJECTILE, org.bukkit.entity.EntityType.ARROW,  Arrow.class),
				EGG(PROJECTILE, org.bukkit.entity.EntityType.EGG, Egg.class),
				ENDERPEARL(PROJECTILE, org.bukkit.entity.EntityType.ENDER_PEARL, EnderPearl.class),
                EXPBOTTLE(PROJECTILE, org.bukkit.entity.EntityType.THROWN_EXP_BOTTLE, ThrownExpBottle.class),
				FIREBALL(PROJECTILE, org.bukkit.entity.EntityType.FIREBALL, Fireball.class),
					FIREBALL_SMALL(FIREBALL, org.bukkit.entity.EntityType.SMALL_FIREBALL, SmallFireball.class),
				FISHINGROD(PROJECTILE, org.bukkit.entity.EntityType.FISHING_HOOK, Fish.class),
				POTION(PROJECTILE, org.bukkit.entity.EntityType.SPLASH_POTION, ThrownPotion.class),
				SNOWBALL(PROJECTILE, org.bukkit.entity.EntityType.SNOWBALL, Snowball.class),
                WITHER_SKULL(PROJECTILE, org.bukkit.entity.EntityType.WITHER_SKULL, WitherSkull.class),
			
			TRAP(NONLIVING),
				DISPENSER(TRAP),
				
			VEHICLE(NONLIVING, Vehicle.class),
				BOAT(NONLIVING, org.bukkit.entity.EntityType.BOAT, Boat.class),
				MINECART(NONLIVING, org.bukkit.entity.EntityType.MINECART, Minecart.class),
				
			ENDERSIGNAL(NONLIVING, org.bukkit.entity.EntityType.ENDER_SIGNAL, EnderSignal.class),
			EXPERIENCEORB(NONLIVING, org.bukkit.entity.EntityType.EXPERIENCE_ORB, ExperienceOrb.class),
			FALLINGBLOCK(NONLIVING, org.bukkit.entity.EntityType.FALLING_BLOCK, FallingBlock.class),
			ITEM(NONLIVING, org.bukkit.entity.EntityType.DROPPED_ITEM, Item.class),
            ITEM_FRAME(NONLIVING, org.bukkit.entity.EntityType.ITEM_FRAME, ItemFrame.class),
			PAINTING(NONLIVING, org.bukkit.entity.EntityType.PAINTING, Painting.class),
			TNTPRIMED(NONLIVING, org.bukkit.entity.EntityType.PRIMED_TNT, TNTPrimed.class);
	
	private static Map<Class<?>, EntityType> byClass = new HashMap<Class<?>, EntityType>();
	
	static {
		for (EntityType element : EntityType.values())
		{
			if (element.myClass == null) continue;
			if (byClass.containsKey(element.myClass))
				ModDamage.getPluginConfiguration().printToLog(Level.SEVERE, "Duplicate " + element.myClass + ": " + byClass.get(element.myClass) + ", " + element);
			byClass.put(element.myClass, element);
		}
	}

	// Slime spawn constants
	public static final int SIZE_HUGE = 3;
	public static final int SIZE_LARGE = 2;
	public static final int SIZE_MEDIUM = 1;
	public static final int SIZE_SMALL = 0;
	
	
	
	private final EntityType parent;
	protected final org.bukkit.entity.EntityType creatureType;
	public final Class<?> myClass;
	
	EntityType(EntityType parent) 
	{
		this(parent, null, null);
	}
	EntityType(EntityType parent, Class<?> myClass) 
	{
		this(parent, null, myClass);
	}
	EntityType(EntityType parent, org.bukkit.entity.EntityType creatureType) 
	{
		this(parent, creatureType, null);
	}
	EntityType(EntityType parent, org.bukkit.entity.EntityType creatureType, Class<?> myClass)
	{
		this.parent = parent;
		this.creatureType = creatureType;
		this.myClass = myClass;
	}

	
	public org.bukkit.entity.EntityType getCreatureType(){ return creatureType; }
	
	//Returns true if this is equals or a subtype of the inputted element
	public boolean matches(Matchable<?> other)
	{
		if (other == null || !(other instanceof EntityType)) return false;
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
		if(obj == null) return NONE; //throw new IllegalArgumentException("Object cannot be null for EntityType.get method!");
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
	
	public boolean canSpawn(){ return creatureType != null && creatureType.isSpawnable(); }
	public LivingEntity spawn(Location location)
	{
		if(creatureType != null)
			return (LivingEntity) location.getWorld().spawnEntity(location, creatureType);
		else throw new IllegalArgumentException("Cannot spawn " + name() + "!");
	}
}