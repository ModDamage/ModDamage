package com.ModDamage.Events.Player;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

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
