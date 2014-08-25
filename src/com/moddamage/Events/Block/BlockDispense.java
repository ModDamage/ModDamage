package com.ModDamage.Events.Block;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class BlockDispense extends MDEvent {
	public BlockDispense() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			ItemStack.class, "item",
			Vector.class, "velocity",
			World.class,	"world",
			Block.class,	"block",
			Boolean.class,	"cancelled");
	
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
