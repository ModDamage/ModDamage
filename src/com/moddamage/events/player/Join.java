package com.moddamage.events.player;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;
import com.moddamage.events.Init;

public class Join extends MDEvent implements Listener
{
	public Join() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	"player",
			World.class,	"world");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		if (disableJoinMessages)
			event.setJoinMessage(null);
		
		Player player = event.getPlayer();
		EventData data = myInfo.makeData(
				player,
				player.getWorld());
		
		runRoutines(data);
		
		Init.onInit(player);
	}
}
