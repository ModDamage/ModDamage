package com.moddamage.events.block;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.FurnaceExtractEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class FurnaceExtract extends MDEvent {
	public FurnaceExtract() { super(myInfo);}
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class, "player",
			World.class, "world",
			Block.class, "block",
			Integer.class, "experience", "-default");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onFurnaceExtractEvent(FurnaceExtractEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		EventData data = myInfo.makeData(
				event.getPlayer(),
				event.getBlock().getLocation().getWorld(),
				event.getBlock(),
				event.getExpToDrop()
				);
		
		runRoutines(data);
		
		event.setExpToDrop(data.get(Integer.class, data.objects.length - 1));
		
	}
}
