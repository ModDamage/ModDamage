package com.moddamage.events.player;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class LevelChange extends MDEvent implements Listener
{
	public LevelChange() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	"player",
			World.class,	"world",
			Integer.class,	"old", "oldlevel",
			Integer.class,	"new", "newlevel", "level");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPickupExperience(PlayerLevelChangeEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				event.getOldLevel(),
				event.getNewLevel());
		
		runRoutines(data);
	}
}
