package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public class DamageEventInfo
{
	Logger log = Logger.getLogger("Minecraft");
	String[] emptyStringArray = {};
	
	public int eventDamage;
	public final World world;
	public final Environment environment;
	
	//Having everything public may not be a good idea, but I don't intend to change anything later.
	public final EventType eventType;
	public final DamageElement rangedElement;
	
	public final DamageElement element_target;
	public final LivingEntity entity_target;
	public final Material materialInHand_target;
	public final DamageElement elementInHand_target;
	public final String armorSetString_target;
	public final String name_attacker;
	public final String[] groups_target;

	public final DamageElement element_attacker;
	public final LivingEntity entity_attacker;
	public final String armorSetString_attacker;
	public final Material materialInHand_attacker;
	public final DamageElement elementInHand_attacker;
	public final String name_target;
	public final String[] groups_attacker;
	
	public final boolean shouldScan;
	
//CONSTRUCTORS
	public DamageEventInfo(Player player_target, Player player_attacker, DamageElement rangedElement, int eventDamage) 
	{
		this.eventDamage = eventDamage;
		eventType = EventType.PLAYER_PLAYER;
		this.rangedElement = rangedElement;
		
		entity_target = player_target;
		element_target = DamageElement.GENERIC_HUMAN;
		materialInHand_target = player_target.getItemInHand().getType();
		elementInHand_target = DamageElement.matchMeleeElement(materialInHand_target);
		armorSetString_target = new ArmorSet(player_target).toString();
		name_target = player_target.getName();
		groups_target = (ModDamage.using_Permissions?ModDamage.multigroupPermissions
				?ModDamage.Permissions.getGroups(player_target.getWorld().getName(), player_target.getName())
				:ModDamage.Permissions.getGroup(player_target.getWorld().getName(), player_target.getName()).split(" "):emptyStringArray);
		
		entity_attacker = player_attacker;
		element_attacker = DamageElement.GENERIC_HUMAN;
		materialInHand_attacker = player_attacker.getItemInHand().getType();
		elementInHand_attacker = DamageElement.matchMeleeElement(materialInHand_attacker);
		armorSetString_attacker = new ArmorSet(player_attacker).toString();
		name_attacker = player_attacker.getName();
		groups_attacker = (ModDamage.using_Permissions?ModDamage.multigroupPermissions
							?ModDamage.Permissions.getGroups(player_attacker.getWorld().getName(), player_attacker.getName())
							:ModDamage.Permissions.getGroup(player_attacker.getWorld().getName(), player_attacker.getName()).split(" "):emptyStringArray);
		
		world = entity_target.getWorld();
		environment = world.getEnvironment();

		shouldScan = (ModDamage.hasPermission(player_attacker, "moddamage.scan.pvp") && ModDamage.serverHandler.canScan(player_attacker));
	}
	
	public DamageEventInfo(LivingEntity entity_target, DamageElement mobType_target, Player player_attacker, DamageElement rangedElement, int eventDamage) 
	{
		this.eventDamage = eventDamage;
		eventType = EventType.PLAYER_MOB;
		this.rangedElement = rangedElement;
		
		this.entity_target = entity_target;
		element_target= mobType_target;
		materialInHand_target = null;
		elementInHand_target = null;
		armorSetString_target = null;
		name_target = null;
		groups_target = null;
		
		entity_attacker = player_attacker;
		element_attacker = DamageElement.GENERIC_HUMAN;
		materialInHand_attacker = player_attacker.getItemInHand().getType();
		elementInHand_attacker = DamageElement.matchMeleeElement(materialInHand_attacker);
		armorSetString_attacker = new ArmorSet(player_attacker).toString();
		name_attacker = player_attacker.getName();
		groups_attacker = (ModDamage.using_Permissions?ModDamage.multigroupPermissions
				?ModDamage.Permissions.getGroups(player_attacker.getWorld().getName(), player_attacker.getName())
				:ModDamage.Permissions.getGroup(player_attacker.getWorld().getName(), player_attacker.getName()).split(" "):emptyStringArray);
		
		world = entity_target.getWorld();
		environment = world.getEnvironment();
		
		shouldScan = (ModDamage.hasPermission(player_attacker, "moddamage.scan." + mobType_target.getReference().toLowerCase()) && ModDamage.serverHandler.canScan(player_attacker));		
	}
	
	public DamageEventInfo(Player player_target, LivingEntity entity_attacker, DamageElement mobType_attacker, int eventDamage) 
	{
		this.eventDamage = eventDamage;
		eventType = EventType.MOB_PLAYER;
		this.rangedElement = null;
		
		this.entity_target = player_target;
		element_target = DamageElement.GENERIC_HUMAN;
		materialInHand_target = player_target.getItemInHand().getType();
		elementInHand_target = DamageElement.matchMeleeElement(materialInHand_target);
		armorSetString_target = new ArmorSet(player_target).toString();
		name_target = player_target.getName();
		groups_target = (ModDamage.using_Permissions?ModDamage.multigroupPermissions
				?ModDamage.Permissions.getGroups(player_target.getWorld().getName(), player_target.getName())
				:ModDamage.Permissions.getGroup(player_target.getWorld().getName(), player_target.getName()).split(" "):emptyStringArray);

		this.entity_attacker = entity_attacker;
		element_attacker = mobType_attacker;
		materialInHand_attacker = null;
		elementInHand_attacker = null;
		armorSetString_attacker = null;
		name_attacker = null;
		groups_attacker = null;
		
		world = entity_target.getWorld();
		environment = world.getEnvironment();
		
		shouldScan = false;
	}
	
	public DamageEventInfo(LivingEntity entity_target, DamageElement mobType_target, LivingEntity entity_attacker, DamageElement mobType_attacker, int eventDamage) 
	{
		this.eventDamage = eventDamage;
		eventType = EventType.MOB_MOB;
		rangedElement = null;

		this.entity_target = entity_target;
		element_target = mobType_target;
		materialInHand_target = null;
		elementInHand_target = null;
		armorSetString_target = null;
		name_target = null;
		groups_target = null;

		this.entity_attacker = entity_attacker;
		element_attacker = mobType_target;
		materialInHand_attacker = null;
		elementInHand_attacker = null;
		armorSetString_attacker = null;
		name_attacker = null;
		groups_attacker = null;
		
		world = entity_target.getWorld();
		environment = world.getEnvironment();
		
		shouldScan = false;
	}
	
	public DamageEventInfo(Player player_target, DamageElement damageType, int eventDamage) 
	{
		this.eventDamage = eventDamage;
		eventType = EventType.NONLIVING_PLAYER;
		this.rangedElement = null;
		
		entity_target = player_target;
		element_target = DamageElement.GENERIC_HUMAN;
		materialInHand_target = player_target.getItemInHand().getType();
		elementInHand_target = DamageElement.matchMeleeElement(materialInHand_target);
		armorSetString_target = new ArmorSet(player_target).toString();
		name_target = player_target.getName();
		groups_target = (ModDamage.using_Permissions?ModDamage.multigroupPermissions
				?ModDamage.Permissions.getGroups(player_target.getWorld().getName(), player_target.getName())
				:ModDamage.Permissions.getGroup(player_target.getWorld().getName(), player_target.getName()).split(" "):emptyStringArray);
		
		entity_attacker = null;
		element_attacker = damageType;
		materialInHand_attacker = null;
		elementInHand_attacker = null;
		armorSetString_attacker = null;
		name_attacker = null;
		groups_attacker = null;
		
		world = entity_target.getWorld();
		environment = world.getEnvironment();
		
		shouldScan = false;
	}
	
	public DamageEventInfo(LivingEntity entity_target, DamageElement mobType_target, DamageElement damageType, int eventDamage) 
	{
		this.eventDamage = eventDamage;
		eventType = EventType.NONLIVING_MOB;
		this.rangedElement = null;

		this.entity_target = entity_target;
		element_target = mobType_target;
		materialInHand_target = null;
		elementInHand_target = null;
		armorSetString_target = null;
		name_target = null;
		groups_target = null;
		
		entity_attacker = null;
		element_attacker = damageType;
		materialInHand_attacker = null;
		elementInHand_attacker = null;
		armorSetString_attacker = null;
		name_attacker = null;
		groups_attacker = null;
		
		world = entity_target.getWorld();
		environment = world.getEnvironment();
		
		shouldScan = false;
	}
}