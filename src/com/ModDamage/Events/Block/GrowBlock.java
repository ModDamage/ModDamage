package com.ModDamage.Events.Block;

import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

public class GrowBlock extends MDEvent implements Listener
{
	public GrowBlock() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Block.class,	"block",
			World.class,	"world",
			Boolean.class,	"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockPlace(BlockGrowEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Block block = event.getBlock();
		
		EventData data = myInfo.makeData(
				block,
				block.getWorld(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
