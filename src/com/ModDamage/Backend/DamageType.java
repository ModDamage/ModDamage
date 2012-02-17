package com.ModDamage.Backend;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public enum DamageType implements Matchable<DamageType>
{
	UNKNOWN(null),
	NATURE(null),
		CACTUS(NATURE),
		CUSTOM(NATURE),
		DROWNING(NATURE),
		EXPLOSION(NATURE),
			EXPLOSION_ENTITY(EXPLOSION),
			EXPLOSION_BLOCK(EXPLOSION),
		FALL(NATURE),
		FIRE(NATURE),
		BURN(NATURE),
		LAVA(NATURE),
		LIGHTNING(NATURE),
		MAGIC(NATURE),
		POISON(NATURE),
		STARVATION(NATURE),
		SUFFOCATION(NATURE),
		SUICIDE(NATURE),
		VOID(NATURE),
	LIVING(null),
	PROJECTILE(null);
	
	private final DamageType parent;
	
	private DamageType(DamageType parent)
	{
		this.parent = parent;
	}

	@Override
	public boolean matches(Matchable<?> other)
	{
		if (other == null || other.getClass() != DamageType.class) return false;
		DamageType type = (DamageType)other;
		
		DamageType temp = this;
		while (temp != null)
		{
			if(temp == type) return true;
			temp = temp.parent;
		}
		return false;
	}
	
	public static DamageType get(DamageCause cause) 
	{
		switch(cause)
		{
		case CONTACT: 			return CACTUS;
		case ENTITY_ATTACK:		return LIVING;
		case PROJECTILE:		return PROJECTILE;
		case SUFFOCATION: 		return SUFFOCATION;
		case FALL: 				return FALL;
		case FIRE: 				return FIRE;
		case FIRE_TICK:			return BURN;
		case LAVA: 				return LAVA;
		case DROWNING: 			return DROWNING;
		case BLOCK_EXPLOSION:   return EXPLOSION_BLOCK;
		case ENTITY_EXPLOSION: 	return EXPLOSION_ENTITY;
		case VOID: 				return VOID;
		case LIGHTNING: 		return LIGHTNING;
		case SUICIDE:			return SUICIDE;
		case STARVATION:		return STARVATION;
		case POISON: 			return POISON;
		case MAGIC:				return MAGIC;
		case CUSTOM:			return CUSTOM;
		}
		return null;
	}
}
