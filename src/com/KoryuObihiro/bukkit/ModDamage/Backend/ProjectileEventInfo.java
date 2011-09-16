package com.KoryuObihiro.bukkit.ModDamage.Backend;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

public class ProjectileEventInfo extends TargetEventInfo
{	
	public final RangedElement rangedElement;	
	public final Projectile projectile;
	
//CONSTRUCTORS
	public ProjectileEventInfo(LivingEntity eventEntity_target, ModDamageElement eventElement_target, Projectile eventEntity_projectile, RangedElement rangedElement, int eventDamage) 
	{
		super(eventEntity_target, eventElement_target, eventDamage);

		this.projectile = eventEntity_projectile;
		this.rangedElement = rangedElement;
	}
}