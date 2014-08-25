package com.ModDamage.Events.Entity;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class Target extends MDEvent implements Listener
{
	public Target() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class,	"entity",
			World.class,	"world",
			Entity.class,	"target",
			EntityTargetEvent.TargetReason.class, "reason",
			Boolean.class,	"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onTarget(EntityTargetEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Entity entity = event.getEntity();
		Entity target = event.getTarget();
		EventData data = myInfo.makeData(
				entity,
				entity.getWorld(),
				target,
				event.getReason(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
