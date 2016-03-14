package com.moddamage.events.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class SignChange extends MDEvent {
	public SignChange() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			String.class, "line_1",
			String.class, "line_2",
			String.class, "line_3",
			String.class, "line_4",
			Player.class, "player",
			Block.class, "block",
			Boolean.class, "cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event)
	{
		if(!ModDamage.isEnabled) return;

		EventData data = myInfo.makeData(
				event.getLine(0),
				event.getLine(1),
				event.getLine(2),
				event.getLine(3),
				event.getPlayer(),
				event.getBlock(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setLine(0, data.get(String.class, data.start));
		event.setLine(1, data.get(String.class, data.start + 1));
		event.setLine(2, data.get(String.class, data.start + 2));
		event.setLine(3, data.get(String.class, data.start + 3));
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
