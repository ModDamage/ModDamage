package com.ModDamage.Backend;

import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public enum HealType implements Matchable<HealType>
{
	REGEN,
    SATIATED,
    EATING,
    MAGIC,
    MAGIC_REGEN,
    CUSTOM;
	
	public static HealType get(RegainReason reason)
	{
		switch (reason)
		{
		case REGEN: return REGEN;
		case SATIATED: return SATIATED;
		case EATING: return EATING;
		case MAGIC: return MAGIC;
		case MAGIC_REGEN: return MAGIC_REGEN;
		case CUSTOM: return CUSTOM;
		}
		return null;
	}

	@Override
	public boolean matches(Matchable<?> other)
	{
		//if (other == null || !HealType.class.isAssignableFrom(other.getClass())) return false;
		//HealType type = (HealType)other;
		
		return this == other;
	}
}
