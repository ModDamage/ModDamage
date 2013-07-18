package com.ModDamage.Magic.Damage;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class NoopEntityHPMagic extends BaseEntityHPMagic {
	private boolean loaded = true;
	public NoopEntityHPMagic()
	{
		super();
		if (damageable_getHealth == null|| damageable_getMaxHealth == null ||
				entityDmgEvent_getDamage == null || entityRegainHEvent_getValue == null)
			loaded = false;
	}
	
	@Override
	public void safeSetNumber(Class<?> cls, Object object, String name, Number number) {
	}
	
	@Override
	public void setHealth(Damageable entity, Number health) {
	}

	@Override
	public void setMaxHealth(Damageable entity, Number health) {
	}

	@Override
	public Number getMaxHealth(Damageable entity) {
		if (loaded)
			return super.getMaxHealth(entity);
		else
			return 0;
	}
	
	@Override
	public Number getHealth(Damageable entity) {
		if (loaded)
			return super.getHealth(entity);
		else
			return 0;
	}

	@Override
	public void damage(Damageable entity, Number damage) {
	}

	@Override
	public void damage(Damageable entity, Number damage, Entity source) {
	}
	
	@Override
	public EntityDamageByEntityEvent craftEvent(Entity from, LivingEntity target, DamageCause cause, Number damage) {
		return null;
	}
	
	@Override
	public Number getEventValue(Event event) {
		if (loaded)
			return super.getEventValue(event);
		else
			return 0;
	}

	@Override
	public void setEventValue(Event event, Number value) {
	}

}
