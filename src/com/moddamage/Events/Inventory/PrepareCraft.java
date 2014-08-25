package com.ModDamage.Events.Inventory;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class PrepareCraft extends MDEvent implements Listener
{
	public PrepareCraft() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Inventory.class,	"inv", "inventory",
			InventoryView.class, "view",
			Player.class,		"player",
			World.class,		"world",
			Boolean.class,		"isRepair");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteract(PrepareItemCraftEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = (Player) event.getView().getPlayer();
		
		EventData data = myInfo.makeData(
				event.getInventory(),
				event.getView(),
				player,
				player.getWorld(),
				event.isRepair()
				);
		
		runRoutines(data);
	}
}
