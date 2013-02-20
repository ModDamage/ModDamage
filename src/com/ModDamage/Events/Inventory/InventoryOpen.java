package com.ModDamage.Events.Inventory;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class InventoryOpen extends MDEvent implements Listener
{
	public InventoryOpen() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Inventory.class,	"inv", "inventory",
			InventoryView.class, "view",
			Player.class,		"player",
			World.class,		"world",
			Boolean.class,		"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteract(InventoryOpenEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = (Player) event.getPlayer();
		
		EventData data = myInfo.makeData(
				event.getInventory(),
				event.getView(),
				player,
				player.getWorld(),
				event.isCancelled()
				);
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
