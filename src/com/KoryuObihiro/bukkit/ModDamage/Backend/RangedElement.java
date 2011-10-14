package com.KoryuObihiro.bukkit.ModDamage.Backend;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Snowball;

public enum RangedElement 
{
	ARROW, EGG, FIREBALL, FISHINGROD, SNOWBALL;
	
	public static RangedElement matchElement(Entity entity)
	{
		if(entity instanceof Arrow)	return ARROW;
		if(entity instanceof Egg)		return EGG;
		if(entity instanceof Fireball)	return FIREBALL;
		if(entity instanceof Fish)		return FISHINGROD; 
		if(entity instanceof Snowball)	return SNOWBALL;
		return null;
	}
	public static RangedElement matchElement(String key)
	{
		for(RangedElement element : RangedElement.values())
			if(element.name().equalsIgnoreCase(key))
				return element;
		return null;
	}
}
