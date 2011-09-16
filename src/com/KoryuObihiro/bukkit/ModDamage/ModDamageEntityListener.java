package com.KoryuObihiro.bukkit.ModDamage;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ProjectileEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.RangedElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class ModDamageEntityListener extends EntityListener
{
	public static enum EventType
	{
		Damage, Death, Food, ProjectileHit, Spawn, Tame;
	}
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
					eventInfo = new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), null, ModDamageElement.matchNonlivingElement(event.getCause()), null, null, event.getDamage());
				else if(event instanceof EntityDamageByEntityEvent)
				{
					EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
		    		RangedElement rangedElement = RangedElement.matchElement(event_EE.getDamager());
		    		if(rangedElement != null)
		    		{
		    			Projectile projectile = (Projectile)event_EE.getDamager();
		    			if(projectile.getShooter() != null)
		    				eventInfo = new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), projectile.getShooter(), ModDamageElement.matchMobType(projectile.getShooter()), projectile, rangedElement, event.getDamage());
		    			else if(event_EE.getCause().equals(DamageCause.ENTITY_ATTACK))
			    			eventInfo = new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), null, ModDamageElement.DISPENSER, projectile, rangedElement, event.getDamage());
		    		}
		    		else if(event_EE.getDamager() != null) eventInfo = new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), (LivingEntity)event_EE.getDamager(), ModDamageElement.matchMobType((LivingEntity)event_EE.getDamager()), null, null, event.getDamage());
		    		}
				if(eventInfo != null)
				{
					for(Routine routine : ModDamage.routineManager.getRoutines(EventType.Damage))
						routine.run(eventInfo);
						
					if(eventInfo.eventValue < 0 && !ModDamage.negative_Heal) 
						eventInfo.eventValue = 0;
					event.setDamage(eventInfo.eventValue);
				}
				else  ModDamage.log.severe("[" + plugin.getDescription().getName() + "] Error! Unhandled damage event. Is Bukkit and ModDamage up-to-date?");
			}
		}
	}
	
////DEATH ////
	@Override
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(ModDamage.isEnabled && event.getEntity() instanceof LivingEntity)
		{
			LivingEntity ent_damaged = (LivingEntity)event.getEntity();
		    EntityDamageEvent nEvent = ent_damaged.getLastDamageCause();
		    AttackerEventInfo eventInfo = null;
	    	if(ModDamageElement.matchNonlivingElement(nEvent.getCause()) != null)
	    		eventInfo = new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), null, ModDamageElement.matchNonlivingElement(nEvent.getCause()), null, null, 0);
	    	else if(nEvent instanceof EntityDamageByEntityEvent)
	    	{
	    		EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)nEvent;
	    		RangedElement rangedElement = RangedElement.matchElement(event_EE.getDamager());
	    		if(rangedElement != null)
	    		{
	    			Projectile projectile = (Projectile)event_EE.getDamager();
	    			if(projectile.getShooter() != null)
	    				eventInfo = new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), projectile.getShooter(), ModDamageElement.matchMobType(projectile.getShooter()), projectile, rangedElement, nEvent.getDamage());
	    			else if(nEvent.getCause().equals(DamageCause.ENTITY_ATTACK))
		    			eventInfo = new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), null, ModDamageElement.DISPENSER, projectile, rangedElement, nEvent.getDamage());
	    		}
	    		else if(event_EE.getDamager() != null) eventInfo = new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), (LivingEntity)event_EE.getDamager(), ModDamageElement.matchMobType((LivingEntity)event_EE.getDamager()), null, null, nEvent.getDamage());
	    	}
	    	if(eventInfo != null)
				for(Routine routine : ModDamage.routineManager.getRoutines(EventType.Death))
					routine.run(eventInfo);
			else ModDamage.log.severe("[" + plugin.getDescription().getName() + "] Error! Unhandled death event. Is Bukkit and ModDamage up-to-date?");			
		}
	}
	
////FOOD ////
	@Override
	public void onEntityRegainHealth(EntityRegainHealthEvent event)
	{
		if(ModDamage.isEnabled && !event.isCancelled() && event.getRegainReason().equals(RegainReason.EATING))
		{
			LivingEntity entity = (LivingEntity)event.getEntity();
			TargetEventInfo eventInfo = new TargetEventInfo(entity, ModDamageElement.matchMobType(entity), event.getAmount());

			for(Routine routine : ModDamage.routineManager.getRoutines(EventType.Death))
				routine.run(eventInfo);
		}
	}
	
//// PROJECTILE HIT ////
	@Override
	public void onProjectileHit(ProjectileHitEvent event)
	{
		if(ModDamage.isEnabled)
		{
			Projectile projectile = (Projectile)event.getEntity();
			RangedElement rangedElement = RangedElement.matchElement(projectile);
			ProjectileEventInfo eventInfo = null;
			if(projectile.getShooter() != null)
				eventInfo = new ProjectileEventInfo(projectile.getShooter(), ModDamageElement.matchMobType(projectile.getShooter()), projectile, rangedElement, 0);
			else eventInfo = new ProjectileEventInfo(null, ModDamageElement.DISPENSER, projectile, rangedElement, 0);

			for(Routine routine : ModDamage.routineManager.getRoutines(EventType.ProjectileHit))
				routine.run(eventInfo);
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

			for(Routine routine : ModDamage.routineManager.getRoutines(EventType.Spawn))
				routine.run(eventInfo);
			
			entity.setHealth(eventInfo.eventValue);
			event.setCancelled(entity.getHealth() <= 0);
		}
	}
	
////TAME ////
	@Override
	public void onEntityTame(EntityTameEvent event)
	{
		if(ModDamage.isEnabled)
		{
			AttackerEventInfo eventInfo = new AttackerEventInfo((LivingEntity)event.getEntity(), ModDamageElement.WOLF_TAME, (LivingEntity)event.getOwner(), ModDamageElement.matchMobType((LivingEntity)event.getOwner()), null, null, 0);

			for(Routine routine : ModDamage.routineManager.getRoutines(EventType.Spawn))
				routine.run(eventInfo);
		}
	}
}
