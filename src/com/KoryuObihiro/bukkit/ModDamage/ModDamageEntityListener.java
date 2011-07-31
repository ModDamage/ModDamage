package com.KoryuObihiro.bukkit.ModDamage;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.RangedElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

public class ModDamageEntityListener extends EntityListener
{
	ModDamage plugin;
	public ModDamageEntityListener(ModDamage plugin){ this.plugin = plugin;}
	
	@Override
	public void onEntityDamage(EntityDamageEvent event)
	{
		if(!event.isCancelled() && (event.getEntity() instanceof LivingEntity)) 
		{
			LivingEntity ent_damaged = (LivingEntity)event.getEntity();
			if(ModDamage.damageRoutinesLoaded && ent_damaged.getNoDamageTicks() <= 40)
			{
				DamageEventInfo eventInfo = null;
				if(ModDamageElement.matchNonlivingElement(event.getCause()) != null)
					eventInfo = new DamageEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), null, ModDamageElement.matchNonlivingElement(event.getCause()), null, event.getDamage());
				else if(event instanceof EntityDamageByEntityEvent)
				{
					EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
					//TODO Make this compatible with dispensers!
					LivingEntity ent_damager = (LivingEntity)event_EE.getDamager();
					RangedElement rangedElement = (event instanceof EntityDamageByProjectileEvent?RangedElement.matchElement(((EntityDamageByProjectileEvent)event).getProjectile()):null);
					eventInfo = new DamageEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), ent_damager, ModDamageElement.matchMobType(ent_damager), rangedElement, event.getDamage());
				}
				else{ ModDamage.log.severe("[" + plugin.getDescription().getName() + "] Error! Unhandled damage event. Is this plugin up-to-date?");}
				
				plugin.executeRoutines_Damage(eventInfo);
					
				if(eventInfo.eventDamage < 0 && !ModDamage.negative_Heal) 
					eventInfo.eventDamage = 0;
				event.setDamage(eventInfo.eventDamage);
			}		
		}
	}
	
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{ 
		if(!event.isCancelled() && event.getEntity() != null && ModDamage.spawnRoutinesLoaded)
		{
			LivingEntity entity = (LivingEntity)event.getEntity();
			SpawnEventInfo eventInfo = new SpawnEventInfo(entity);

			if(eventInfo.element != null)
				plugin.executeRoutines_Spawn(eventInfo);
			
			entity.setHealth(eventInfo.eventHealth);
			event.setCancelled(entity.getHealth() <= 0);
		}
	}
}
