package com.moddamage.events.chunk;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class ChunkPopulate extends MDEvent implements Listener
{
	public ChunkPopulate() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			World.class, "world",
			Chunk.class, "chunk");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onChunkPopulate(ChunkPopulateEvent event)
	{
		if(!ModDamage.isEnabled) return;

		EventData data = myInfo.makeData(
                event.getChunk().getWorld(),
				event.getChunk());
		
		runRoutines(data);
	}
}
