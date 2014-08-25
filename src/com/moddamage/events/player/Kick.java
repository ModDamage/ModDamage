package com.moddamage.events.player;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class Kick extends MDEvent implements Listener
{
	public Kick() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	"player",
			String.class,	"message",
			String.class,	"reason",
			World.class,	"world",
			Boolean.class,	"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onQuit(PlayerKickEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		if (disableQuitMessages)
			event.setLeaveMessage(null);
		
		Player player = event.getPlayer();
		EventData data = myInfo.makeData(
				player,
				event.getLeaveMessage(),
				event.getReason(),
				player.getWorld(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setLeaveMessage(data.get(String.class, data.start + 1));
		event.setReason(data.get(String.class, data.start + 2));
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
