package com.ModDamage.Matchables;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public enum DamageType implements Matchable<DamageType>
{
	UNKNOWN(null, null),
	NATURE(null, null),
		CONTACT(NATURE, DamageCause.CONTACT),
		CUSTOM(NATURE, DamageCause.CUSTOM),
		DROWNING(NATURE, DamageCause.DROWNING),
		EXPLOSION(NATURE, null),
			EXPLOSION_ENTITY(EXPLOSION, DamageCause.ENTITY_EXPLOSION),
			EXPLOSION_BLOCK(EXPLOSION, DamageCause.BLOCK_EXPLOSION),
		FALL(NATURE, DamageCause.FALL),
		FIRE(NATURE, DamageCause.FIRE),
		BURN(NATURE, DamageCause.FIRE_TICK),
		LAVA(NATURE, DamageCause.LAVA),
		LIGHTNING(NATURE, DamageCause.LIGHTNING),
		MAGIC(NATURE, DamageCause.MAGIC),
		POISON(NATURE, DamageCause.POISON),
		STARVATION(NATURE, DamageCause.STARVATION),
		SUFFOCATION(NATURE, DamageCause.SUFFOCATION),
		SUICIDE(NATURE, DamageCause.SUICIDE),
		VOID(NATURE, DamageCause.VOID),
	LIVING(null, DamageCause.ENTITY_ATTACK),
	PROJECTILE(null, DamageCause.PROJECTILE);
	
	private static final Map<DamageCause, DamageType> causeMap = new HashMap<DamageCause, DamageType>();
	
	static {
		for (DamageType type : values())
			if (type.cause != null)
				causeMap.put(type.cause, type);
	}
	
	private final DamageType parent;
	private final DamageCause cause;
	
	private DamageType(DamageType parent, DamageCause cause)
	{
		this.parent = parent;
		this.cause = cause;
	}

	@Override
	public boolean matches(Matchable<?> other)
	{
		if (other == null || !(other instanceof DamageType)) return false;
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
		DamageType type = causeMap.get(cause);
		if (type == null) return UNKNOWN;
		return type;
	}
}
