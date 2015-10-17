package com.moddamage.events.inventory;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class InventoryClose extends MDEvent implements Listener
{
	public InventoryClose() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Inventory.class, "inv", "inventory",
			InventoryView.class, "view",
			Player.class, "player",
			World.class, "world");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteract(InventoryCloseEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = (Player) event.getPlayer();
		
		EventData data = myInfo.makeData(
				event.getInventory(),
				event.getView(),
				player,
				player.getWorld()
				);
		
		runRoutines(data);
	}
}
