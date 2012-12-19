package com.ModDamage.Events.Entity;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.MagicStuff;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class ProjectileHit extends MDEvent implements Listener
{
	public ProjectileHit() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class, 		"shooter", "entity",
			Block.class, 		"hitblock",
			Projectile.class,	"projectile",
			World.class,		"world");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		final Projectile projectile = event.getEntity();
		final LivingEntity shooter = projectile.getShooter();
		
		EventData data = myInfo.makeData(
				shooter,
				(projectile instanceof Arrow)? MagicStuff.getGroundBlock((Arrow) projectile) : null,
				projectile,
				projectile.getWorld());
		
		runRoutines(data);
		
	}
}
