package com.moddamage.events.block;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class LeavesDecay extends MDEvent implements Listener
{
	public LeavesDecay() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			World.class, "world",
			Block.class, "block",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onLeavesDecay(LeavesDecayEvent event)
	{
		if(!ModDamage.isEnabled) return;

		EventData data = myInfo.makeData(
                event.getBlock().getWorld(),
				event.getBlock(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
