package com.ModDamage.Events.Entity;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class Explode extends MDEvent implements Listener
{
	public Explode() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class,	"entity",
			World.class,	"world",
			Integer.class,	"yield",
			Boolean.class,	"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onExplode(EntityExplodeEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Entity entity = event.getEntity();
		Integer yield = (int) (event.getYield() * 10);
		
		EventData data = myInfo.makeData(
				entity,
				entity != null? entity.getWorld() : null,
				yield,
				event.isCancelled());
		
		runRoutines(data);
		
		event.setYield(((Integer) data.get(data.start + 2)) / 10.0f);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
