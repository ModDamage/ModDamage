package com.moddamage.events.entity;

import org.bukkit.World;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class ProjectileLaunch extends MDEvent implements Listener
{
	public ProjectileLaunch() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			ProjectileSource.class, "shooter", "entity",
			Projectile.class, "projectile",
			World.class, "world",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onProjectileLaunch(ProjectileLaunchEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Projectile projectile = (Projectile)event.getEntity();
		ProjectileSource shooter = projectile.getShooter();
		
		EventData data = myInfo.makeData(
				shooter,
				projectile,
				projectile.getWorld(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
