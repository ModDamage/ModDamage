package com.ModDamage.Events.Entity;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class ShootBow extends MDEvent implements Listener
{
	public ShootBow() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class, 		"shooter", "entity",
			Entity.class,		"projectile",
			World.class,		"world",
			Boolean.class, 		"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onShootBow(EntityShootBowEvent event)
	{
		if(!ModDamage.isEnabled) return;

		LivingEntity shooter = event.getEntity();
		Entity projectile = event.getProjectile();
		
		EventData data = myInfo.makeData(
				shooter,
				projectile,
				projectile.getWorld(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
