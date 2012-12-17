package com.ModDamage.Events.Inventory;

import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class InventoryOpen extends MDEvent implements Listener
{
	public InventoryOpen() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	        "player",
			World.class,	        "world",
			InventoryView.class,	"view",
			Inventory.class,	    "inv", "Inventory");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryOpen(InventoryOpenEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		HumanEntity entity = event.getPlayer();
		EventData data = myInfo.makeData(
				entity,
				entity.getLocation().getWorld(),
				event.getView(),
                event.getInventory());
		
		runRoutines(data);
	}
}
