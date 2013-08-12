package com.ModDamage.Magic.Damage;

import java.lang.reflect.Constructor;
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

public abstract class BaseEntityHPMagic {
	protected static Method safeGetMethod(Class<?> cls, String name, Class<?>... params) {
		try
		{
			Method method = cls.getDeclaredMethod(name, params);
			method.setAccessible(true);
			return method;
		}
		catch (Exception e) {} //Supress this message. Its part of our version checks..
		return null;
	}
	
	protected static <T> Constructor<T> getSafeConstructor(Class<T> cls, Class<?>... params)
	{
		Constructor<T> constr = null;
		try
		{
			constr = cls.getConstructor(params);
			constr.setAccessible(true);
			return constr;
		} catch (Exception e) {} //Suppress this message. Its part of our version checks.
		return null;
	}

	protected Method damageable_setHealth;
	protected Method damageable_setMaxHealth;
	protected Method damageable_getHealth;
	protected Method damageable_getMaxHealth;
	protected Method damageable_damage;
	protected Method damageable_damageByEntity;
	protected Method entityDmgEvent_setDamage;
	protected Method entityDmgEvent_getDamage;
	protected Method entityRegainHEvent_getValue;
	protected Method entityRegainHEvent_setValue;
	
	public BaseEntityHPMagic()
	{
		damageable_getHealth = safeGetMethod(Damageable.class, "getHealth");
		damageable_getMaxHealth = safeGetMethod(Damageable.class, "getMaxHealth");
		entityDmgEvent_getDamage = safeGetMethod(EntityDamageEvent.class, "getDamage");
		entityRegainHEvent_getValue = safeGetMethod(EntityRegainHealthEvent.class, "getAmount");
	}
	
	protected Constructor<EntityDamageByEntityEvent> entityDamageByEntityConstr;
	
	public abstract void setHealth(Damageable entity, Number health);
	public abstract void setMaxHealth(Damageable entity, Number health);

	public Number getHealth(Damageable entity) {
		Number ret = null;
		
		try {
			ret = (Number) MagicStuff.safeInvoke(entity, damageable_getHealth);
		} catch (Exception e) {
			if (e instanceof ClassCastException)
				System.err.println("Magic Damage Error at $BEHM56: Class not instance of number");
			else
				System.err.println("Magic Damage Error at $BEHM58: " + e.getClass().getSimpleName() + ": " + e);
		}
		return ret;
	}

	public Number getMaxHealth(Damageable entity) {
		Number ret = null;
		
		try {
			ret = (Number) MagicStuff.safeInvoke(entity, damageable_getMaxHealth);
		} catch (Exception e) {
			if (e instanceof ClassCastException)
				System.err.println("Magic Damage Error at $BEHM70: Class not instance of number");
			else
				System.err.println("Magic Damage Error at $BEHM72: " + e.getClass().getSimpleName() + ": " + e);
		}
		return ret;
	}
	
	
	public abstract void damage(Damageable entity, Number damage);
	public abstract void damage(Damageable entity, Number damage, Entity source);
	
	public abstract void setEventValue(Event event, Number value);
	public Number getEventValue(Event event)
	{
		if (event instanceof EntityDamageEvent)
		{
			try 
			{
				return (Number) MagicStuff.safeInvoke(event, entityDmgEvent_getDamage);
			} catch (Exception e) {
				if (e instanceof ClassCastException)
					System.err.println("Magic Damage Error at $BEHM97: Class not instance of number");
				else
					System.err.println("Magic Damage Error at $BEHM99: " + e.getClass().getSimpleName() + ": " + e);
			}
		} else if (event instanceof EntityRegainHealthEvent) {
			try {
				return (Number) MagicStuff.safeInvoke(event, entityRegainHEvent_getValue);
			} catch (Exception e) {
				if (e instanceof ClassCastException)
					System.err.println("Magic Damage Error at $BEHM107: Class not instance of number");
				else
					System.err.println("Magic Damage Error at $BEHM109: " + e.getClass().getSimpleName() + ": " + e);
			}
		} 
		return null;
	}
	
	public abstract void safeSetNumber(Class<?> cls, Object object, String name, Number number);
	public Number safeGetNumber(Class<?> cls, Object object, String name) {
		Method method = MagicStuff.safeGetMethod(cls, name);
		Number ret = null;
		
		try {
			ret = (Number) MagicStuff.safeInvoke(object, method);
		} catch (Exception e) {
			if (e instanceof ClassCastException)
				System.err.println("Magic Damage Error: Class not instance of number $IEHM28");
			else
				System.err.println("Magic Damage Error: " + e.getClass().getSimpleName() + ": " + e);
		}
		return ret;
	}
	
	public abstract EntityDamageByEntityEvent craftEvent(Entity from, LivingEntity target, DamageCause cause, Number damage);
	
	//Helper Method
	protected boolean anyNull()
	{
		return (damageable_damage == null ||
				damageable_damageByEntity == null||
				damageable_getHealth == null ||
				damageable_getMaxHealth == null ||
				damageable_setHealth == null ||
				damageable_setMaxHealth == null ||
				entityDamageByEntityConstr == null ||
				entityDmgEvent_getDamage == null ||
				entityDmgEvent_setDamage == null ||
				entityRegainHEvent_getValue == null ||
				entityRegainHEvent_setValue == null
			);
	}
}
