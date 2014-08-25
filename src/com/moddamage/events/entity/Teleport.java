package com.moddamage.events.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class Teleport extends MDEvent implements Listener
{
	public Teleport() { super(myInfo); }

	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class,	"entity",
			World.class,	"world",
			TeleportCause.class,	"cause",
			Location.class,	"from",
			Location.class,	"to",
			Boolean.class,  "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onTeleport(PlayerTeleportEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		EventData data = myInfo.makeData(
				event.getPlayer(),
				event.getPlayer().getWorld(),
				event.getCause(),
				event.getFrom(),
				event.getTo(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onTeleport(EntityTeleportEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		EventData data = myInfo.makeData(
				event.getEntity(),
				event.getEntity().getWorld(),
				null,
				event.getFrom(),
				event.getTo(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
