package com.moddamage.events.item;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class ItemHeld extends MDEvent implements Listener
{
	public ItemHeld() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	"player",
			World.class,	"world",
			Integer.class,	"prevslot",
			Integer.class,	"newslot");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onItemHeld(PlayerItemHeldEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				event.getPreviousSlot(),
				event.getNewSlot());
		
		runRoutines(data);
	}
}
