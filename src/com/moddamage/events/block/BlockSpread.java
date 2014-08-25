package com.ModDamage.Events.Block;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class BlockSpread extends MDEvent implements Listener
{
	public BlockSpread() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			World.class,	"world",
			Block.class,	"source",
			Block.class,	"block",
            Material.class, "newtype",
            Integer.class,  "newtypeid",
            Integer.class,  "newdata",
			Boolean.class,	"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockSpread(BlockSpreadEvent event)
	{
		if(!ModDamage.isEnabled) return;

		EventData data = myInfo.makeData(
                event.getBlock().getWorld(),
                event.getSource(),
				event.getBlock(),
                event.getNewState().getType(),
                event.getNewState().getTypeId(),
                (int) event.getNewState().getData().getData(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
