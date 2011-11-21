package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

public class ProjectileEventInfo extends TargetEventInfo
{	
	public final ModDamageElement rangedElement;	
	public final Projectile projectile;
	
//CONSTRUCTORS
	protected ProjectileEventInfo(LivingEntity eventEntity_target, ModDamageElement eventElement_target, Projectile eventEntity_projectile, ModDamageElement rangedElement, int eventDamage, EventInfoType type) 
	{
		super(eventEntity_target, eventElement_target, eventDamage, type);

		this.projectile = eventEntity_projectile;
		this.rangedElement = rangedElement;
	}
	public ProjectileEventInfo(LivingEntity eventEntity_target, ModDamageElement eventElement_target, Projectile eventEntity_projectile, ModDamageElement rangedElement, int eventDamage) 
	{
		super(eventEntity_target, eventElement_target, eventDamage);

		this.projectile = eventEntity_projectile;
		this.rangedElement = rangedElement;
	}
	
	public ProjectileEventInfo(World world, ModDamageElement eventElement_target, Projectile eventEntity_projectile, ModDamageElement rangedElement, int eventDamage) 
	{
		super(world, eventElement_target, eventDamage);

		this.projectile = eventEntity_projectile;
		this.rangedElement = rangedElement;
	}

	protected ProjectileEventInfo(Projectile projectile, ModDamageElement element, World world, LivingEntity shooter, ModDamageElement element_target, Material materialInHand_target, ArmorSet armorSet_target, List<String> groups_target, int eventValue)
	{
		super(shooter, world, element_target, materialInHand_target, armorSet_target, groups_target, eventValue);
		this.projectile = projectile;
		this.rangedElement = element;
	}
	@Override
	public ProjectileEventInfo clone()
	{
		return new ProjectileEventInfo(projectile, rangedElement, world, entity_target, element_target, materialInHand_target, armorSet_target, groups_target, eventValue);
	}
}