package com.ModDamage.Events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class ToggleFlight extends MDEvent implements Listener
{
	public ToggleFlight() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	"player",
			World.class,	"world",
			Boolean.class, 	"isFlying",
			Boolean.class,	"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onToggleFlight(PlayerToggleFlightEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				event.isFlying(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
