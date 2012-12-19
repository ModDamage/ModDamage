package com.ModDamage.Events.Player;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class Fish extends MDEvent implements Listener
{
	public Fish() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	"player",
			World.class,	"world",
			Entity.class,	"caught",
            PlayerFishEvent.State.class, "state",
			Boolean.class,	"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onFish(PlayerFishEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		Entity caught = event.getCaught();
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				caught,
                event.getState(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
