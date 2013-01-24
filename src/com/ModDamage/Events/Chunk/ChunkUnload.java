package com.ModDamage.Events.Chunk;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class ChunkUnload extends MDEvent implements Listener
{
	public ChunkUnload() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			World.class,	"world",
			Chunk.class,	"chunk");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onChunkUnload(ChunkUnloadEvent event)
	{
		if(!ModDamage.isEnabled) return;

		EventData data = myInfo.makeData(
                event.getChunk().getWorld(),
				event.getChunk());
		
		runRoutines(data);
	}
}
