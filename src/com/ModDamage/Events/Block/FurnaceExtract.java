package com.ModDamage.Events.Block;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.FurnaceExtractEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class FurnaceExtract extends MDEvent {
	public FurnaceExtract() { super(myInfo);}
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	"player",
			World.class,	"world",
			Block.class, 	"block",
			Integer.class,	"experience", "-default");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onFurnaceExtractEvent(FurnaceExtractEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		EventData data = myInfo.makeData(
				event.getPlayer(),
				event.getBlock().getLocation().getWorld(),
				event.getBlock(),
				event.getExpToDrop()
				);
		
		runRoutines(data);
		
		event.setExpToDrop(data.get(Integer.class, data.objects.length - 1));
		
	}
}
