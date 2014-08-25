package com.ModDamage.Events.Inventory;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class InventoryClick extends MDEvent implements Listener
{
	public InventoryClick() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Inventory.class,	"inv", "inventory",
			InventoryView.class, "view",
			ItemHolder.class,	"item", "current",
			ItemHolder.class,	"cursor", "cursoritem",
			
			InventoryAction.class, "action",
			
			ClickType.class,	"clicktype",
			Boolean.class,		"leftclick",
			Boolean.class,		"rightclick",
			Boolean.class,		"shiftclick",
			
			Integer.class,		"hotbarkey",
			
			SlotType.class,		"slottype",
			Integer.class,		"slot",
			Integer.class,		"rawslot",
			
			Player.class,		"player",
			World.class,		"world",
			Boolean.class,		"cancelled");
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteract(InventoryClickEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = (Player) event.getWhoClicked();
		
		ItemStack current = event.getCurrentItem();
		ItemStack cursor = event.getCursor();
		
		ItemHolder currenth = new ItemHolder(current);
		ItemHolder cursorh = new ItemHolder(cursor);
		
		EventData data = myInfo.makeData(
				event.getInventory(),
				event.getView(),
				currenth,
				cursorh,
				
				event.getAction(),
				
				event.getClick(),
				event.isLeftClick(),
				event.isRightClick(),
				event.isShiftClick(),
				
				event.getHotbarButton(),
				
				event.getSlotType(),
				event.getSlot(),
				event.getRawSlot(),
				
				player,
				player.getWorld(),
				event.isCancelled()
				);
		
		runRoutines(data);
		
		if (currenth.getItem() != current)
			event.setCurrentItem(currenth.getItem());

		if (cursorh.getItem() != cursor)
			event.setCursor(cursorh.getItem());
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
