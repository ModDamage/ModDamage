package com.ModDamage.Events.Block;

import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;

public class LeavesDecay extends MDEvent implements Listener
{
	public LeavesDecay() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			World.class,	"world",
			Block.class,	"block",
			Boolean.class,	"cancelled");
	
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
