package com.ModDamage.Events;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.ModDamage.ModDamage;
import com.ModDamage.MDEvent;
import com.ModDamage.PluginConfiguration;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Matchables.DamageType;

public class Damage extends MDEvent implements Listener
{
	public Damage() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class, 		"attacker", "-target-other",
			Projectile.class, 	"projectile",
			Entity.class, 		"target", "-attacker-other",
			World.class,		"world",
			DamageType.class, 	"damage_type", // e.g. damage_type.type.FIRE
			Integer.class, 		"damage", "-default",
			Boolean.class,		"cancelled");
	
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event)
	{
		if(!ModDamage.isEnabled || event.isCancelled()) return;
		
		if(!event.isCancelled() && (event.getEntity() instanceof LivingEntity)) 
		{
			EventData data = getEventData(event);
			if(data != null)
			{
				runRoutines(data);
				event.setDamage(data.get(Integer.class, data.start + 5));
				
				event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
				
				if (event.getDamage() <= 0)
					event.setCancelled(true);
			}
			else PluginConfiguration.log.severe("[" + Bukkit.getPluginManager().getPlugin("ModDamage").getDescription().getName() + 
					"] Error! Unhandled damage event. Is Bukkit and ModDamage up-to-date?");
		}
	}

	public static EventData getEventData(EntityDamageEvent event)
	{
		if (event == null) return null;
		
		DamageType damageElement = DamageType.get(event.getCause());
		
		Entity attacker = null;
		Projectile projectile = null;
		Entity target = event.getEntity();
		World world = target.getWorld();
		
		if(event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
			Entity damager = event_EE.getDamager();
			
			if(damager instanceof Projectile)
			{
				projectile = (Projectile)damager;
				
				attacker = projectile.getShooter();
			}
			else
			{
				attacker = damager;
			}
		}
		
	    return myInfo.makeData(
	    		attacker,
	    		projectile,
	    		target,
	    		world,
	    		damageElement,
	    		event.getDamage(),
	    		event.isCancelled());
	}
}
