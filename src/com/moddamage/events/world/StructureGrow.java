package com.moddamage.events.world;

import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class StructureGrow extends MDEvent implements Listener
{
	public StructureGrow() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			World.class, "world",
			Player.class, "player",
			Location.class, "loc", "location",
			TreeType.class, "species",
			Boolean.class, "isFromBonemeal",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onStructureGrow(StructureGrowEvent event)
	{
		if(!ModDamage.isEnabled) return;

		EventData data = myInfo.makeData(
                event.getWorld(),
                event.getPlayer(),
                event.getLocation(),
                event.getSpecies(),
                event.isFromBonemeal(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
