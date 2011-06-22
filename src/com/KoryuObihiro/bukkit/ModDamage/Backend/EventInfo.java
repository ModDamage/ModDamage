package com.KoryuObihiro.bukkit.ModDamage.Backend;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class EventInfo
{
	
	private EventType eventType;
	public EventType getEventType(){ return eventType;}
	
	private DamageElement mobType_target;
	private LivingEntity entity_target;
	private boolean target_isPlayer;
	private Material inHand_target;
	private ArmorSet armorSet_target;
	public DamageElement getMobType_target(){ return mobType_target;}
	public boolean isPlayer_target(){ return target_isPlayer;}
	public LivingEntity getEntity_target(){ return entity_target;}
	public Material getInHand_target(){ return inHand_target;}
	public ArmorSet getArmorSet_target(){ return armorSet_target;}

	private DamageElement mobType_attacker;
	private LivingEntity entity_attacker;
	private boolean attacker_isPlayer;
	private ArmorSet armorSet_attacker;
	private Material inHand_attacker;
	public DamageElement getMobType_attacker(){ return mobType_attacker;}
	public boolean isPlayer_attacker(){ return attacker_isPlayer;}
	public LivingEntity getEntity_attacker(){ return entity_attacker;}
	public ArmorSet getArmorSet_attacker(){ return armorSet_attacker;}
	public Material getInHand_attacker(){ return inHand_attacker;}
	
	
	public EventInfo(LivingEntity ent_damaged, LivingEntity ent_damager) 
	{
		eventType = EventType.MOB_MOB;
	}
	public EventInfo(Player player_damaged, Player ent_damager, DamageElement rangedElement) 
	{
		eventType = EventType.PLAYER_MOB;
	}
	public EventInfo(LivingEntity ent_damaged, Player player_damager, DamageElement rangedElement) 
	{
		eventType = EventType.MOB_PLAYER;
	}
	public EventInfo(Player ent_damaged, DamageElement damageType) 
	{
		eventType = EventType.NONLIVING_PLAYER;
	}
	public EventInfo(LivingEntity ent_damaged, DamageElement damageType)
	{
		eventType = EventType.NONLIVING_MOB;
	}
}