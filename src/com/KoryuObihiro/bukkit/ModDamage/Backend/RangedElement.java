package com.KoryuObihiro.bukkit.ModDamage.Backend;

import org.bukkit.craftbukkit.entity.CraftArrow;
import org.bukkit.craftbukkit.entity.CraftEgg;
import org.bukkit.craftbukkit.entity.CraftFireball;
import org.bukkit.craftbukkit.entity.CraftSnowball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;

public enum RangedElement 
{
	BOW, EGG, FIREBALL, FISHINGROD, SNOWBALL;
	
	public static RangedElement matchElement(Entity entity)
	{
		if(entity instanceof CraftArrow)	return BOW;
		if(entity instanceof CraftEgg)		return EGG;
		if(entity instanceof CraftSnowball)	return SNOWBALL;
		if(entity instanceof CraftFireball)	return FIREBALL;
		if(entity instanceof Projectile)	return FISHINGROD; //XXX Deeefinitely sure this isn't going to work.
		return null;
	}
}
