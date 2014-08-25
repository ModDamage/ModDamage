package com.ModDamage.Events.Block;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class BreakBlock extends MDEvent implements Listener
{
	public BreakBlock() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	"player",
			World.class,	"world",
			Block.class,	"block",
			Integer.class,	"experience", "-default",
			Boolean.class,	"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		if(!ModDamage.isEnabled) return;

		EventData data = myInfo.makeData(
                event.getPlayer(),
                event.getBlock().getWorld(),
				event.getBlock(),
				event.getExpToDrop(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setExpToDrop(data.get(Integer.class, data.start + data.objects.length - 2));
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
