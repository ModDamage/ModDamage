package com.KoryuObihiro.bukkit.ModDamage;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTargetEvent;

import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.RangedElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class ModDamageEntityListener extends EntityListener
{
	ModDamage plugin;
	public ModDamageEntityListener(ModDamage plugin){ this.plugin = plugin;}

//// DAMAGE ////
	@Override
	public void onEntityDamage(EntityDamageEvent event)
	{
		if(!event.isCancelled() && (event.getEntity() instanceof LivingEntity)) 
		{
			LivingEntity ent_damaged = (LivingEntity)event.getEntity();
			if(ModDamage.isEnabled && ent_damaged.getNoDamageTicks() <= 40)
			{
				AttackerEventInfo eventInfo = null;
				if(ModDamageElement.matchNonlivingElement(event.getCause()) != null)
					eventInfo = new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), null, ModDamageElement.matchNonlivingElement(event.getCause()), null, event.getDamage());
				else if(event instanceof EntityDamageByEntityEvent)
				{
					EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
					//TODO Make this compatible with dispensers!
					LivingEntity ent_damager = (LivingEntity)event_EE.getDamager();
					RangedElement rangedElement = (event instanceof EntityDamageByProjectileEvent?RangedElement.matchElement(((EntityDamageByProjectileEvent)event).getProjectile()):null);
					eventInfo = new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), ent_damager, ModDamageElement.matchMobType(ent_damager), rangedElement, event.getDamage());
				}
				else{ ModDamage.log.severe("[" + plugin.getDescription().getName() + "] Error! Unhandled damage event. Is this plugin up-to-date?");}
				
				plugin.executeRoutines_Damage(eventInfo);
					
				if(eventInfo.eventValue < 0 && !ModDamage.negative_Heal) 
					eventInfo.eventValue = 0;
				event.setDamage(eventInfo.eventValue);
			}		
		}
	}
	
//// SPAWN ////
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{ 
		if(ModDamage.isEnabled && !event.isCancelled() && event.getEntity() != null)
		{
			LivingEntity entity = (LivingEntity)event.getEntity();
			TargetEventInfo eventInfo = new TargetEventInfo(entity, ModDamageElement.matchMobType(entity), entity.getHealth());

			plugin.executeRoutines_Spawn(eventInfo);
			
			entity.setHealth(eventInfo.eventValue);
			event.setCancelled(entity.getHealth() <= 0);
		}
	}
	
	//TODO
	//----
//// DEATH ////
	@Override
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(ModDamage.isEnabled && event.getEntity() instanceof LivingEntity)
		{
			LivingEntity entity = (LivingEntity)event.getEntity();
			TargetEventInfo eventInfo = new TargetEventInfo(entity, ModDamageElement.matchMobType(entity), 0);
			
			plugin.executeRoutines_Death(eventInfo);
		}
	}
	
//// FOOD ////
	@Override
	public void onEntityRegainHealth(EntityRegainHealthEvent event)
	{
		if(ModDamage.isEnabled && !event.isCancelled() && event.getRegainReason().equals(RegainReason.EATING))
		{
			LivingEntity entity = (LivingEntity)event.getEntity();
			TargetEventInfo eventInfo = new TargetEventInfo(entity, ModDamageElement.matchMobType(entity), entity.getHealth());
				
			plugin.executeRoutines_Food(eventInfo);
		}
	}
}
