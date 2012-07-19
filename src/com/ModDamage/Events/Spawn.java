package com.ModDamage.Events;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class Spawn extends MDEvent implements Listener
{
	public Spawn() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class,	"entity",
			World.class,	"world",
			Integer.class,	"health", "-default");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		EventData data = myInfo.makeData(
				player, // entity
				player.getWorld(),
				player.getMaxHealth()
				);
		
		runRoutines(data);
		
		player.setHealth(data.get(Integer.class, data.start + 2));
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		if(!ModDamage.isEnabled || event.isCancelled()) return;
		
		LivingEntity entity = (LivingEntity)event.getEntity();
		EventData data = myInfo.makeData(
				entity,
				entity.getWorld(),
				entity.getHealth());
		
		runRoutines(data);
		
		int health = data.get(Integer.class, data.start + 2);
		
		if (health > 0)
			entity.setHealth(health);
		else
			event.setCancelled(true);
	}
}
