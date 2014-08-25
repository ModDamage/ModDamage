package com.ModDamage.Events.Player;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class Quit extends MDEvent implements Listener
{
	public Quit() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	"player",
			World.class,	"world");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		if (disableQuitMessages)
			event.setQuitMessage(null);
		
		Player player = event.getPlayer();
		EventData data = myInfo.makeData(
				player,
				player.getWorld());
		
		runRoutines(data);
	}
}
