package com.moddamage.events.entity;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class Combust extends MDEvent implements Listener
{
	public Combust() { super(myInfo); }

	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class, "entity",
			World.class, "world",
			Integer.class, "duration",
			Entity.class, "combustor",
			Block.class, "block",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onCombust(EntityCombustEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Entity entity = event.getEntity();
		Integer duration = event.getDuration();
		Entity combustor = null;
		
		if (event instanceof EntityCombustByEntityEvent)
			combustor = ((EntityCombustByEntityEvent)event).getCombuster();
		
		Block combustorBlock = null;
		
		if (event instanceof EntityCombustByBlockEvent)
			combustorBlock = ((EntityCombustByBlockEvent)event).getCombuster();
		
		EventData data = myInfo.makeData(
				entity,
				entity.getWorld(),
				duration,
				combustor,
				combustorBlock,
				event.isCancelled());
		
		runRoutines(data);
		
		event.setDuration((Integer) data.get(data.start + 2));
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
