package com.moddamage.events.weather;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.LightningStrikeEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;
import com.moddamage.routines.Lightning;

public class LightingStrike extends MDEvent {
	public LightingStrike() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			World.class, "world",
			Lightning.class, "bolt",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onStrike(LightningStrikeEvent event)
	{
		if(!ModDamage.isEnabled) return;

		EventData data = myInfo.makeData(
				event.getWorld(),
				event.getLightning(),
				event.isCancelled());
		
		runRoutines(data);
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
