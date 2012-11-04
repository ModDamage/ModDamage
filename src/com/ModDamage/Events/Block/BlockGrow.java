package com.ModDamage.Events.Block;

import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.LeavesDecayEvent;

public class BlockGrow extends MDEvent implements Listener
{
	public BlockGrow() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			World.class,	"world",
			Block.class,	"block",
            Material.class, "newtype",
            Integer.class,  "newtypeid",
            Integer.class,  "newdata",
			Boolean.class,	"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockGrow(BlockGrowEvent event)
	{
		if(!ModDamage.isEnabled) return;

		EventData data = myInfo.makeData(
                event.getBlock().getWorld(),
				event.getBlock(),
                event.getNewState().getType(),
                event.getNewState().getTypeId(),
                event.getNewState().getData().getData(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
