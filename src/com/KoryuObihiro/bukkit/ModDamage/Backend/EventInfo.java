package com.KoryuObihiro.bukkit.ModDamage.Backend;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public class EventInfo
{
	public int eventDamage;
	//Having everything public may not be a good idea, but I don't intend to change anything later.
	public final EventType eventType;
	public final EventType getEventType(){ return eventType;}
	public final DamageElement rangedElement;
	
	public final DamageElement damageElement_target;
	public final LivingEntity entity_target;
	public final Material materialInHand_target;
	public final DamageElement elementInHand_target;
	public final String armorSetString_target;
	public final String name_attacker;
	public final String[] groups_target;

	public final DamageElement damageElement_attacker;
	public final LivingEntity entity_attacker;
	public final String armorSetString_attacker;
	public final Material materialInHand_attacker;
	public final DamageElement elementInHand_attacker;
	public final String name_target;
	public final String[] groups_attacker;
	
//CONSTRUCTORS
	public EventInfo(Player player_target, Player player_attacker, DamageElement rangedElement, int eventDamage) 
	{
		this.eventDamage = eventDamage;
		eventType = EventType.PLAYER_PLAYER;
		this.rangedElement = rangedElement;
		
		this.entity_target = player_target;
		damageElement_target = DamageElement.GENERIC_HUMAN;
		materialInHand_target = player_target.getItemInHand().getType();
		elementInHand_target = DamageElement.matchMeleeElement(materialInHand_target);
		armorSetString_target = new ArmorSet(player_target).toString();
		name_target = player_target.getName();
		groups_target = ModDamage.Permissions.getGroups(player_target.getWorld().getName(), player_target.getName());
		
		this.entity_attacker = player_attacker;
		damageElement_attacker = DamageElement.GENERIC_HUMAN;
		materialInHand_attacker = player_attacker.getItemInHand().getType();
		elementInHand_attacker = DamageElement.matchMeleeElement(materialInHand_attacker);
		armorSetString_attacker = new ArmorSet(player_attacker).toString();
		name_attacker = player_attacker.getName();
		groups_attacker = ModDamage.Permissions.getGroups(player_attacker.getWorld().getName(), player_attacker.getName());
	}
	
	public EventInfo(LivingEntity entity_target, DamageElement mobType_target, Player player_attacker, DamageElement rangedElement, int eventDamage) 
	{
		this.eventDamage = eventDamage;
		eventType = EventType.PLAYER_MOB;
		this.rangedElement = rangedElement;
		
		this.entity_target = entity_target;
		damageElement_target= mobType_target;
		materialInHand_target = null;
		elementInHand_target = null;
		armorSetString_target = null;
		name_target = null;
		groups_target = null;
		
		this.entity_attacker = player_attacker;
		damageElement_attacker = DamageElement.GENERIC_HUMAN;
		materialInHand_attacker = player_attacker.getItemInHand().getType();
		elementInHand_attacker = DamageElement.matchMeleeElement(materialInHand_attacker);
		armorSetString_attacker = new ArmorSet(player_attacker).toString();
		name_attacker = player_attacker.getName();
		groups_attacker = ModDamage.Permissions.getGroups(player_attacker.getWorld().getName(), player_attacker.getName());
	}
	
	public EventInfo(Player player_target, LivingEntity entity_attacker, DamageElement mobType_attacker, int eventDamage) 
	{
		this.eventDamage = eventDamage;
		eventType = EventType.MOB_PLAYER;
		this.rangedElement = null;
		
		this.entity_target = player_target;
		damageElement_target = DamageElement.GENERIC_HUMAN;
		materialInHand_target = player_target.getItemInHand().getType();
		elementInHand_target = DamageElement.matchMeleeElement(materialInHand_target);
		armorSetString_target = new ArmorSet(player_target).toString();
		name_target = player_target.getName();
		groups_target = ModDamage.Permissions.getGroups(player_target.getWorld().getName(), player_target.getName());

		this.entity_attacker = entity_attacker;
		damageElement_attacker = mobType_attacker;
		materialInHand_attacker = null;
		elementInHand_attacker = null;
		armorSetString_attacker = null;
		name_attacker = null;
		groups_attacker = null;
		
	}
	
	public EventInfo(LivingEntity entity_target, DamageElement mobType_target, LivingEntity entity_attacker, DamageElement mobType_attacker, int eventDamage) 
	{
		this.eventDamage = eventDamage;
		eventType = EventType.MOB_MOB;
		rangedElement = null;

		this.entity_target = entity_target;
		damageElement_target = mobType_target;
		materialInHand_target = null;
		elementInHand_target = null;
		armorSetString_target = null;
		name_target = null;
		groups_target = null;

		this.entity_attacker = entity_attacker;
		damageElement_attacker = mobType_target;
		materialInHand_attacker = null;
		elementInHand_attacker = null;
		armorSetString_attacker = null;
		name_attacker = null;
		groups_attacker = null;
	}
	
	public EventInfo(Player player_target, DamageElement damageType, int eventDamage) 
	{
		this.eventDamage = eventDamage;
		eventType = EventType.NONLIVING_PLAYER;
		this.rangedElement = null;
		
		this.entity_target = player_target;
		damageElement_target = DamageElement.GENERIC_HUMAN;
		materialInHand_target = player_target.getItemInHand().getType();
		elementInHand_target = DamageElement.matchMeleeElement(materialInHand_target);
		armorSetString_target = new ArmorSet(player_target).toString();
		name_target = player_target.getName();
		groups_target = ModDamage.Permissions.getGroups(player_target.getWorld().getName(), player_target.getName());
		
		entity_attacker = null;
		damageElement_attacker = damageType;
		materialInHand_attacker = null;
		elementInHand_attacker = null;
		armorSetString_attacker = null;
		name_attacker = null;
		groups_attacker = null;
	}
	
	public EventInfo(LivingEntity entity_target, DamageElement mobType_target, DamageElement damageType, int eventDamage) 
	{
		this.eventDamage = eventDamage;
		eventType = EventType.NONLIVING_MOB;
		this.rangedElement = null;

		this.entity_target = entity_target;
		damageElement_target = mobType_target;
		materialInHand_target = null;
		elementInHand_target = null;
		armorSetString_target = null;
		name_target = null;
		groups_target = null;
		
		entity_attacker = null;
		damageElement_attacker = damageType;
		materialInHand_attacker = null;
		elementInHand_attacker = null;
		armorSetString_attacker = null;
		name_attacker = null;
		groups_attacker = null;
	}
}