package com.moddamage.events.weather;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class WeatherChange extends MDEvent {
	public WeatherChange() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			World.class, "world",
			Boolean.class, "state",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onWeatherChange(WeatherChangeEvent event)
	{
		if(!ModDamage.isEnabled) return;

		EventData data = myInfo.makeData(
				event.getWorld(),
				event.toWeatherState(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
