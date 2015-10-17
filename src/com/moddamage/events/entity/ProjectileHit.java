package com.moddamage.events.entity;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;
import com.moddamage.magic.MagicStuff;

public class ProjectileHit extends MDEvent implements Listener
{
	public ProjectileHit() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			ProjectileSource.class, "shooter", "entity",
			Block.class, "hitblock",
			Projectile.class, "projectile",
			World.class, "world");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		final Projectile projectile = event.getEntity();
		final ProjectileSource shooter = projectile.getShooter();
		
		EventData data = myInfo.makeData(
				shooter,
				(projectile instanceof Arrow)? MagicStuff.getGroundBlock((Arrow) projectile) : null,
				projectile,
				projectile.getWorld());
		
		runRoutines(data);
		
	}
}
