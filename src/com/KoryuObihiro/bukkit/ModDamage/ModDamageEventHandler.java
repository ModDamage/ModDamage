package com.KoryuObihiro.bukkit.ModDamage;

import java.util.ArrayList;
import java.util.List;

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

import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ProjectileEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

enum ModDamageEventHandler
{
	Damage, Death, Food, ProjectileHit, Spawn, Tame;
	
	public void runRoutines(TargetEventInfo eventInfo)
	{
		for(Routine routine : routines)
			routine.run(eventInfo);
	}
	protected final List<Routine> routines = new ArrayList<Routine>();
	protected LoadState specificLoadState = LoadState.NOT_LOADED;
	protected static LoadState state = LoadState.NOT_LOADED;
	
	protected LoadState getState(){ return specificLoadState;}
	
	protected static void reload()
	{
		ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Loading routines...");
		ModDamage.changeIndentation(true);
		for(ModDamageEventHandler eventType : ModDamageEventHandler.values())
		{
			eventType.routines.clear();
			Object nestedContent = ModDamage.getPluginConfiguration().getConfigMap().get(PluginConfiguration.getCaseInsensitiveKey(ModDamage.getPluginConfiguration().getConfigMap(), eventType.name()));//XXX This is nasty. Change it.
			if(nestedContent != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, eventType.name() + " configuration:");
				List<Routine> routines = new ArrayList<Routine>();
				eventType.specificLoadState = RoutineAliaser.parseRoutines(routines, nestedContent)?LoadState.SUCCESS:LoadState.FAILURE;
				if(eventType.specificLoadState.equals(LoadState.SUCCESS))
					eventType.routines.addAll(routines);
			}
			else eventType.specificLoadState = LoadState.NOT_LOADED;
			switch(eventType.specificLoadState)
			{
				case NOT_LOADED:
				ModDamage.addToLogRecord(OutputPreset.WARNING, eventType.name() + " configuration not found.");
					break;
				case FAILURE:
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error in " + eventType.name() + " configuration.");
					break;
				case SUCCESS:
				ModDamage.addToLogRecord(OutputPreset.INFO, "End " + eventType.name() + " configuration.");
					break;
			}
			state = LoadState.combineStates(state, eventType.specificLoadState);
		}
		ModDamage.changeIndentation(false);
		switch(state)
		{
			case NOT_LOADED:
				ModDamage.addToLogRecord(OutputPreset.WARNING, "No routines loaded! Are any routines defined?");
				break;
			case FAILURE:
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "One or more errors occurred while loading routines.");
				break;
			case SUCCESS:
				ModDamage.addToLogRecord(OutputPreset.INFO, "Routines loaded!");
				break;
		}
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
					else  PluginConfiguration.log.severe("[" + Bukkit.getPluginManager().getPlugin("ModDamage").getDescription().getName() + "] Error! Unhandled damage event. Is Bukkit and ModDamage up-to-date?");
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
	
	//// FOOD ////
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