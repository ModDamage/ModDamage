package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public class TargetEventInfo
{
	public static final Logger log = ModDamage.log;

	public final EventInfoType type;
	public enum EventInfoType{ ATTACKER, PROJECTILE, TARGET;}
	
	public int eventValue;
	public final World world;
	public final Environment environment;
	
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
		this.environment = world.getEnvironment();
	}
	protected TargetEventInfo(LivingEntity entity, ModDamageElement eventElement_target, int eventValue, EventInfoType type) 
	{
		this.type = type;
		this.eventValue = eventValue;
		this.entity_target = entity;
		this.element_target = eventElement_target;
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
		this.environment = world.getEnvironment();
	}
	
	public TargetEventInfo(World world, ModDamageElement eventElement_target, int eventValue) 
	{
		this.type = EventInfoType.PROJECTILE;
		this.eventValue = eventValue;
		this.entity_target = null;
		this.element_target = eventElement_target;
		this.materialInHand_target = null;
		this.armorSet_target = null;
		this.name_target = null;
		this.groups_target = ModDamage.emptyList;
		
		this.world = world;	
		this.environment = world.getEnvironment();
	}
}