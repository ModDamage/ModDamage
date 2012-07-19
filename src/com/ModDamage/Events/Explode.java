package com.ModDamage.Events;

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
			Integer.class,	"yield");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onExplode(EntityExplodeEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Entity entity = event.getEntity();
		Integer yield = (int) (event.getYield() * 10);
		
		EventData data = myInfo.makeData(
				entity,
				entity.getWorld(),
				yield);
		
		runRoutines(data);
		
		event.setYield(((Integer) data.get(data.start + 2)) / 10.0f);
	}
}
