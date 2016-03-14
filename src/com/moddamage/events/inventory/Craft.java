package com.moddamage.events.inventory;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class Craft extends MDEvent implements Listener
{
	public Craft() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Inventory.class, "inv", "inventory",
			InventoryView.class, "view",
			Player.class, "player",
			World.class, "world",
			Result.class, "result",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteract(CraftItemEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = (Player) event.getView().getPlayer();
		
		EventData data = myInfo.makeData(
				event.getInventory(),
				event.getView(),
				player,
				player.getWorld(),
				event.getResult(),
				event.isCancelled()
				);
		
		runRoutines(data);
		event.setResult(data.get(Result.class, data.start + data.objects.length -2));
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
