package com.ModDamage.Magic.Damage;

import java.lang.reflect.Method;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import com.ModDamage.Magic.MagicStuff;

public class DoubleEntityHPMagic extends BaseEntityHPMagic {
	
	public DoubleEntityHPMagic()
	{
		super();
		damageable_damage = safeGetMethod(Damageable.class, "damage", double.class);
		damageable_damageByEntity = safeGetMethod(Damageable.class, "damage", double.class, Entity.class);
		damageable_setHealth = safeGetMethod(Damageable.class, "setHealth", double.class);
		damageable_setMaxHealth = safeGetMethod(Damageable.class, "setMaxHealth", double.class);
		entityDamageByEntityConstr = getSafeConstructor(EntityDamageByEntityEvent.class, Entity.class, Entity.class, DamageCause.class, double.class);
		entityDmgEvent_setDamage = safeGetMethod(EntityDamageEvent.class, "setDamage", double.class);
		entityRegainHEvent_setValue = safeGetMethod(EntityRegainHealthEvent.class, "setAmount", double.class);
		
		if (anyNull())
			throw new RuntimeException();
	}

	@Override
	public void safeSetNumber(Class<?> cls, Object object, String name, Number number) {
		Method method = MagicStuff.safeGetMethod(cls, name, double.class);
		
		try {
			method.invoke(object, number.doubleValue());
		} catch (Exception e) {
			System.err.println("Magic Damage Error: $DEHM35 Could not set value: " + e);
		}
	}

	@Override
	public void setHealth(Damageable entity, Number health) {
		MagicStuff.safeInvoke(entity, damageable_setHealth, health.doubleValue());
	}

	@Override
	public void setMaxHealth(Damageable entity, Number health) {
		MagicStuff.safeInvoke(entity, damageable_setMaxHealth, health.doubleValue());
	}

	@Override
	public void damage(Damageable entity, Number damage) {
		MagicStuff.safeInvoke(entity, damageable_damage, damage.doubleValue());
	}

	@Override
	public void damage(Damageable entity, Number damage, Entity source) {
		MagicStuff.safeInvoke(entity, damageable_damageByEntity, damage.doubleValue());
	}
	
	@Override
	public EntityDamageByEntityEvent craftEvent(Entity from, LivingEntity target, DamageCause cause, Number damage) {
		EntityDamageByEntityEvent event = MagicStuff.safeInvokeConstructor(entityDamageByEntityConstr, from, target, cause, damage.doubleValue());
		return event;
	}

	@Override
	public void setEventValue(Event event, Number value) {
		if (event instanceof EntityDamageEvent)
			MagicStuff.safeInvoke(event, entityDmgEvent_setDamage, value.doubleValue());
		else if (event instanceof EntityRegainHealthEvent)
			MagicStuff.safeInvoke(event, entityRegainHEvent_setValue, value.doubleValue());
	}
	
}
