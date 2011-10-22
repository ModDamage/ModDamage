package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public class TargetEventInfo implements Cloneable
{
	public static final Logger log = ModDamage.log;

	public final EventInfoType type;
	public enum EventInfoType{ ATTACKER, PROJECTILE, TARGET;}
	
	public int eventValue;
	public final World world;
	
	public final ModDamageElement element_target;
	public final LivingEntity entity_target;
	public final Material materialInHand_target;
	public final ArmorSet armorSet_target;
	public final String name_target;
	public final List<String> groups_target;
	
//CONSTRUCTORS
	public TargetEventInfo(LivingEntity entity, ModDamageElement eventElement_target, int eventValue)
	{
		this.type = EventInfoType.TARGET;
		this.eventValue = eventValue;
		this.entity_target = entity;
		this.element_target = eventElement_target;
		if(entity instanceof Player)
		{
			Player player_target = (Player)entity;
			this.materialInHand_target = player_target.getItemInHand().getType();
			this.armorSet_target = new ArmorSet(player_target);
			this.name_target = player_target.getName();
			this.groups_target = ExternalPluginManager.getPermissionsManager().getGroups(player_target);
		}
		else
		{
			this.materialInHand_target = null;
			this.armorSet_target = null;
			this.name_target = null;
			this.groups_target = ModDamage.emptyList;
		}
		
		this.world = entity.getWorld();	
	}
	protected TargetEventInfo(LivingEntity entity, ModDamageElement element, int eventValue, EventInfoType type) 
	{
		this.type = type;
		this.eventValue = eventValue;
		this.entity_target = entity;
		this.element_target = element;
		if(element_target.matchesType(ModDamageElement.PLAYER))
		{
			Player player_target = (Player)entity;
			this.materialInHand_target = player_target.getItemInHand().getType();
			this.armorSet_target = new ArmorSet(player_target);
			this.name_target = player_target.getName();
			this.groups_target = ExternalPluginManager.getPermissionsManager().getGroups(player_target);
		}
		else
		{
			this.materialInHand_target = element_target.matchesType(ModDamageElement.ENDERMAN)?((Enderman)entity).getCarriedMaterial().getItemType():null;
			this.armorSet_target = null;
			this.name_target = null;
			this.groups_target = ModDamage.emptyList;
		}
		
		this.world = entity.getWorld();
	}
	
	public TargetEventInfo(World world, ModDamageElement element, int eventValue) 
	{
		this.type = EventInfoType.PROJECTILE;
		this.eventValue = eventValue;
		this.entity_target = null;
		this.element_target = element;
		this.materialInHand_target = null;
		this.armorSet_target = null;
		this.name_target = null;
		this.groups_target = ModDamage.emptyList;
		
		this.world = world;	
	}
	
	protected TargetEventInfo(LivingEntity entity, World world, ModDamageElement element, Material material, ArmorSet armorSet, String name, List<String> groups, int eventValue)
	{
		this.type = EventInfoType.TARGET;
		this.eventValue = eventValue;
		this.entity_target = entity;
		this.element_target = element;
		this.materialInHand_target = material;
		this.armorSet_target = armorSet;
		this.name_target = name;
		this.groups_target = groups;
		this.world = world;
	}
	
	@Override
	public TargetEventInfo clone()
	{
		return new TargetEventInfo(this.entity_target, this.world, this.element_target, this.materialInHand_target, this.armorSet_target, this.name_target, this.groups_target, this.eventValue);
	}
}