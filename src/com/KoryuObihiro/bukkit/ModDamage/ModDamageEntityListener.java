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

import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.RangedElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

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
					RangedElement rangedElement = RangedElement.matchElement(event_EE.getDamager());
					LivingEntity ent_damager = (rangedElement != null?((Projectile)event_EE.getDamager()).getShooter():(LivingEntity)event_EE.getDamager());
					if(ent_damager != null) eventInfo = new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), ent_damager, ModDamageElement.matchMobType(ent_damager), rangedElement, event.getDamage());
					else if(rangedElement != null && event.getCause().equals(DamageCause.ENTITY_ATTACK))
						eventInfo = new AttackerEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), null, ModDamageElement.TRAP_DISPENSER, rangedElement, event.getDamage());
				}
				else{ ModDamage.log.severe("[" + plugin.getDescription().getName() + "] Error! Unhandled damage event. Is Bukkit and ModDamage up-to-date?");}
				
				for(Routine routine : ModDamage.damageRoutines)
					routine.run(eventInfo);
					
				if(eventInfo.eventValue < 0 && !ModDamage.negative_Heal) 
					eventInfo.eventValue = 0;
				event.setDamage(eventInfo.eventValue);
			}
		}
	}
	
////DEATH ////
	@Override
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(ModDamage.isEnabled && event.getEntity() instanceof LivingEntity)
		{
			LivingEntity entity = (LivingEntity)event.getEntity();
			TargetEventInfo eventInfo = new TargetEventInfo(entity, ModDamageElement.matchMobType(entity), 0, null);
			
			for(Routine routine : ModDamage.deathRoutines)
				routine.run(eventInfo);
		}
	}
	
////FOOD ////
	@Override
	public void onEntityRegainHealth(EntityRegainHealthEvent event)
	{
		if(ModDamage.isEnabled && !event.isCancelled() && event.getRegainReason().equals(RegainReason.EATING))
		{
			LivingEntity entity = (LivingEntity)event.getEntity();
			TargetEventInfo eventInfo = new TargetEventInfo(entity, ModDamageElement.matchMobType(entity), event.getAmount(), null);
				
			for(Routine routine : ModDamage.foodRoutines)
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
			TargetEventInfo eventInfo = new TargetEventInfo(entity, ModDamageElement.matchMobType(entity), entity.getHealth(), null);

			for(Routine routine : ModDamage.spawnRoutines)
				routine.run(eventInfo);
			
			entity.setHealth(eventInfo.eventValue);
			event.setCancelled(entity.getHealth() <= 0);
		}
	}
	
	//TODO onProjectileHit when next RB comes out (currently 1000)
}
