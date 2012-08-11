package com.ModDamage.Events;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class Combust extends MDEvent implements Listener
{
	public Combust() { super(myInfo); }

	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class,	"entity",
			World.class,	"world",
			Integer.class,	"duration",
			Entity.class,	"combustor",
			Integer.class,	"block_type",
			Integer.class,	"block_data",
			Boolean.class,  "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onCombust(EntityCombustEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Entity entity = event.getEntity();
		Integer duration = event.getDuration();
		Entity combustor = null;
		Integer block_type = 0;
		Integer block_data = 0;
		
		if (event instanceof EntityCombustByEntityEvent)
			combustor = ((EntityCombustByEntityEvent)event).getCombuster();
		
		if (event instanceof EntityCombustByBlockEvent)
		{
			Block combustorBlock = ((EntityCombustByBlockEvent)event).getCombuster();
			if (combustorBlock != null)
			{
				block_type = combustorBlock.getTypeId();
				block_data = (int) combustorBlock.getData();
			}
		}
		
		EventData data = myInfo.makeData(
				entity,
				entity.getWorld(),
				duration,
				combustor,
				block_type,
				block_data,
				event.isCancelled());
		
		runRoutines(data);
		
		event.setDuration((Integer) data.get(data.start + 2));
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
