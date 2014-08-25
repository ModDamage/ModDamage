package com.ModDamage.Events.Weather;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.LightningStrikeEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Lightning;

public class LightingStrike extends MDEvent {
	public LightingStrike() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			World.class,	"world",
			Lightning.class,	"bolt",
			Boolean.class,	"cancelled");
	
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
