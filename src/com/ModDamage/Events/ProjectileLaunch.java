package com.ModDamage.Events;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class ProjectileLaunch extends MDEvent implements Listener
{
	public ProjectileLaunch() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class, 		"shooter", "entity",
			Projectile.class,	"projectile",
			World.class,		"world",
			Boolean.class, 		"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onProjectileLaunch(ProjectileLaunchEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Projectile projectile = (Projectile)event.getEntity();
		LivingEntity shooter = projectile.getShooter();
		
		EventData data = myInfo.makeData(
				shooter,
				projectile,
				projectile.getWorld());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
