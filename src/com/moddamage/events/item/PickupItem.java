package com.moddamage.events.item;

import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class PickupItem extends MDEvent implements Listener
{
	public PickupItem() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class, "player",
			World.class, "world",
			Item.class, "item",
			Integer.class, "remaining",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPickupItem(PlayerPickupItemEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				event.getItem(),
				event.getRemaining(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
