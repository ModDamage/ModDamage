package com.ModDamage.Events.Entity;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Events.Init;
import com.ModDamage.Magic.MagicStuff;

public class Spawn extends MDEvent implements Listener
{
	public Spawn() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class,	"entity",
			World.class,	"world",
			Number.class,	"health", "-default",
			SpawnReason.class, "reason", "spawn_reason",
			Boolean.class,	"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		EventData data = myInfo.makeData(
				player, // entity
				player.getWorld(),
				MagicStuff.getMaxHealth(player),
				null,
				null
				);
		
		runRoutines(data);
		
		MagicStuff.setEntityHealth(player, data.get(Number.class, data.start + 2));
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		if(!ModDamage.isEnabled || event.isCancelled()) return;
		
		LivingEntity entity = event.getEntity();
		EventData data = myInfo.makeData(
				entity,
				entity.getWorld(),
				MagicStuff.getMaxHealth(entity),
				event.getSpawnReason(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
		
		Number health = data.get(Number.class, data.start + 2);
		
		if (health.doubleValue() > 0)
			MagicStuff.setEntityHealth(entity, Math.min(health.doubleValue(), MagicStuff.getMaxHealth(entity).doubleValue()));
		else
			event.setCancelled(true);
		
		if (!event.isCancelled())
			Init.onInit(entity);
	}
}
