package com.KoryuObihiro.bukkit.ModDamage;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ProjectileEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

enum ModDamageEventHandler
{
	Damage, Death, Food, ProjectileHit, Spawn, Tame;
	
	public void runRoutines(TargetEventInfo eventInfo)
	{
		for(Routine routine : ModDamage.routineManager.getRoutines(this))
			routine.run(eventInfo);
	}
	
	static ModDamagePlayerListener playerListener = new ModDamagePlayerListener();
	static ModDamageEntityListener entityListener = new ModDamageEntityListener();
	
	static class ModDamagePlayerListener extends PlayerListener
	{
	//// SPAWN ////
		@Override
		public void onPlayerRespawn(PlayerRespawnEvent event)
		{
			
			if(ModDamage.isEnabled)
			{
				Player player = event.getPlayer();
				TargetEventInfo eventInfo = new TargetEventInfo(player, ModDamageElement.PLAYER, player.getHealth());
				Spawn.runRoutines(eventInfo);
				player.setHealth(eventInfo.eventValue);
			}
		}
	}

	static class ModDamageEntityListener extends EntityListener
	{	
	//// DAMAGE ////
		@Override
		public void onEntityDamage(EntityDamageEvent event)
		{
			if(!event.isCancelled() && (event.getEntity() instanceof LivingEntity)) 
				if(ModDamage.isEnabled && ((LivingEntity)event.getEntity()).getNoDamageTicks() <= 40L)//TODO Does this wor
				{
					AttackerEventInfo eventInfo = getDamageEventInfo(event);
					if(eventInfo != null)
					{
						Damage.runRoutines(eventInfo);
						event.setDamage(eventInfo.eventValue);
						event.setCancelled(event.getDamage() <= 0);
					}
					else  ModDamage.log.severe("[" + Bukkit.getPluginManager().getPlugin("ModDamage").getDescription().getName() + "] Error! Unhandled damage event. Is Bukkit and ModDamage up-to-date?");
				}
		}
		
	////DEATH ////
		@Override
		public void onEntityDeath(EntityDeathEvent event)
		{
			if(ModDamage.isEnabled && event.getEntity() instanceof LivingEntity)
			{
			    AttackerEventInfo eventInfo = getDamageEventInfo(((LivingEntity)event.getEntity()).getLastDamageCause());
				if(eventInfo != null)
					Death.runRoutines(eventInfo);		
			}
		}
	
	////FOOD ////
		@Override
		public void onEntityRegainHealth(EntityRegainHealthEvent event)
		{
			if(ModDamage.isEnabled && !event.isCancelled() && event.getRegainReason().equals(RegainReason.SATIATED))
			{
				LivingEntity entity = (LivingEntity)event.getEntity();
				TargetEventInfo eventInfo = new TargetEventInfo(entity, ModDamageElement.matchMobType(entity), event.getAmount());
				Food.runRoutines(eventInfo);
			}
		}
		
	//// PROJECTILE HIT ////
		@Override
		public void onProjectileHit(ProjectileHitEvent event)
		{
			if(ModDamage.isEnabled)
			{
				Projectile projectile = (Projectile)event.getEntity();
				ModDamageElement rangedElement = ModDamageElement.matchRangedElement(projectile);
				ProjectileEventInfo eventInfo = null;
				if(projectile.getShooter() != null)
					eventInfo = new ProjectileEventInfo(projectile.getShooter(), ModDamageElement.matchMobType(projectile.getShooter()), projectile, rangedElement, 0);
				else eventInfo = new ProjectileEventInfo(projectile.getWorld(), ModDamageElement.DISPENSER, projectile, rangedElement, 0);
				ProjectileHit.runRoutines(eventInfo);
			}
		}
		
	//// SPAWN ////
		@Override
		public void onCreatureSpawn(CreatureSpawnEvent event)
		{ 
			if(ModDamage.isEnabled && !event.isCancelled())
			{
				LivingEntity entity = (LivingEntity)event.getEntity();
				TargetEventInfo eventInfo = new TargetEventInfo(entity, ModDamageElement.matchMobType(entity), entity.getHealth());
				Spawn.runRoutines(eventInfo);
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
				Tame.runRoutines(eventInfo);
			}
		}
		
	//// HELPER FUNCTIONS ////
		private AttackerEventInfo getDamageEventInfo(EntityDamageEvent event)
		{
			if(event != null)
			{
	    		LivingEntity ent_damaged = (LivingEntity)event.getEntity();
				ModDamageElement primaryElement = ModDamageElement.matchEventElement(event.getCause());
			    switch(primaryElement)
			    {
			    	case LIVING:
			    	case PROJECTILE:
						if(event instanceof EntityDamageByEntityEvent)
						{
							EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
							if(event_EE.getDamager() instanceof Projectile)
							{
								Projectile projectile = (Projectile)event_EE.getDamager();
								ModDamageElement rangedElement = ModDamageElement.matchRangedElement(projectile);
								if(projectile.getShooter() != null)
									return new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), projectile.getShooter(), ModDamageElement.matchMobType(projectile.getShooter()), projectile, rangedElement, event.getDamage());
								else if(event_EE.getCause().equals(DamageCause.PROJECTILE))//FIXME Necessary?
					    			return new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), null, ModDamageElement.DISPENSER, projectile, rangedElement, event.getDamage());
							}
							else if(event_EE.getDamager() != null) return new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), (LivingEntity)event_EE.getDamager(), ModDamageElement.matchMobType((LivingEntity)event_EE.getDamager()), null, null, event.getDamage());
						}
			    	case UNKNOWN: break;
					default: return new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), null, primaryElement, null, null, event.getDamage());
			    }
			}
		    return null;
		}
	}
};