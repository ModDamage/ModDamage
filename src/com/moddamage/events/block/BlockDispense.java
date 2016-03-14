package com.moddamage.events.block;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class BlockDispense extends MDEvent {
	public BlockDispense() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			ItemStack.class, "item",
			Vector.class, "velocity",
			World.class, "world",
			Block.class, "block",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockDispense(BlockDispenseEvent event)
	{
		if(!ModDamage.isEnabled) return;

		EventData data = myInfo.makeData(
				event.getItem(),
				event.getVelocity(),
                event.getBlock().getWorld(),
				event.getBlock(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setItem(data.get(ItemStack.class, data.start));
		event.setVelocity(data.get(Vector.class, data.start + 1));
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
