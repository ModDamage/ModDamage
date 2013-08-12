package com.ModDamage.Magic.Damage;

import java.lang.reflect.Method;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.ModDamage.Magic.MagicStuff;

public class IntEntityHPMagic extends BaseEntityHPMagic {
	
	public IntEntityHPMagic() {
		super();
		damageable_damage = safeGetMethod(Damageable.class, "damage", int.class);
		damageable_damageByEntity = safeGetMethod(Damageable.class, "damage", int.class, Entity.class);
		damageable_setHealth = safeGetMethod(Damageable.class, "setHealth", int.class);
		damageable_setMaxHealth = safeGetMethod(Damageable.class, "setMaxHealth", int.class);
		entityDamageByEntityConstr = getSafeConstructor(EntityDamageByEntityEvent.class, Entity.class, Entity.class, DamageCause.class, int.class);
		entityDmgEvent_setDamage = safeGetMethod(EntityDamageEvent.class, "setDamage", int.class);
		entityRegainHEvent_setValue = safeGetMethod(EntityRegainHealthEvent.class, "setAmount", int.class);
		
		if (anyNull())
			throw new RuntimeException();
	}

	@Override
	public void safeSetNumber(Class<?> cls, Object object, String name, Number number) {
		Method method = MagicStuff.safeGetMethod(cls, name, int.class);
		
		try {
			method.invoke(object, number.intValue());
		} catch (Exception e) {
			System.err.println("Magic Damage Error: $IEHM41 Could not set value: " + e);
		}
	}

	@Override
	public void setHealth(Damageable entity, Number health) {
		MagicStuff.safeInvoke(entity, damageable_setHealth, health.intValue());
	}

	@Override
	public void setMaxHealth(Damageable entity, Number health) {
		MagicStuff.safeInvoke(entity, damageable_setMaxHealth, health.intValue());
	}

	@Override
	public void damage(Damageable entity, Number damage) {
		MagicStuff.safeInvoke(entity, damageable_damage, damage.intValue());
	}

	@Override
	public void damage(Damageable entity, Number damage, Entity source) {
		MagicStuff.safeInvoke(entity, damageable_damageByEntity, damage.intValue(), source);
	}

	@Override
	public EntityDamageByEntityEvent craftEvent(Entity from, LivingEntity target, DamageCause cause, Number damage) {
		EntityDamageByEntityEvent event = MagicStuff.safeInvokeConstructor(entityDamageByEntityConstr, from, target, cause, damage.intValue());
		return event;
	}

	@Override
	public void setEventValue(Event event, Number value) {
		if (event instanceof EntityDamageEvent)
			MagicStuff.safeInvoke(event, entityDmgEvent_setDamage, value.intValue());
		else if (event instanceof EntityRegainHealthEvent)
			MagicStuff.safeInvoke(event, entityRegainHEvent_setValue, value.intValue());
	}
}
