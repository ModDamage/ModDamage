package com.moddamage.events.player;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class InteractEntity extends MDEvent implements Listener
{
	public InteractEntity() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class, "player",
			World.class, "world",
			Entity.class, "target",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteractEntity(PlayerInteractEntityEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		Entity target = event.getRightClicked();
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				target,
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
