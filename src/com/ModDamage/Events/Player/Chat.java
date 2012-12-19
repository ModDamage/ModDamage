package com.ModDamage.Events.Player;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class Chat extends MDEvent implements Listener
{
	public Chat() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	"player",
			World.class,	"world",
			String.class,	"message",
			String.class,	"format",
			Boolean.class,	"cancelled");
	
	// About deprecation: MD does a lot of things that will require synchronous 
	// behavior. The Async event would be way to much work to impliment
	
	@EventHandler(priority=EventPriority.HIGHEST)
	@SuppressWarnings("deprecation") 
	public void onChat(PlayerChatEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		EventData data = myInfo.makeData(
				event.getPlayer(),
				event.getPlayer().getWorld(),
				event.getMessage(),
				event.getFormat(),
				event.isCancelled());
		
		runRoutines(data);

		event.setMessage(data.get(String.class, data.start + 2));
		event.setFormat(data.get(String.class, data.start + 3));
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
