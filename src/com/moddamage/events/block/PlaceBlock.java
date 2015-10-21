package com.moddamage.events.block;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class PlaceBlock extends MDEvent implements Listener
{
	public PlaceBlock() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class, "player",
			World.class, "world",
			Block.class, "block",
			Block.class, "againstblock",
			ItemStack.class, "item",
			Boolean.class, "canbuild",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if(!ModDamage.isEnabled) return;

		EventData data = myInfo.makeData(
                event.getPlayer(),
				event.getBlockPlaced().getWorld(),
				event.getBlockPlaced(),
				event.getBlockAgainst(),
				event.getItemInHand(),
				event.canBuild(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setBuild(data.get(Boolean.class, data.start + 5));
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
