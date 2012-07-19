package com.ModDamage.Events;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class InteractEntity extends MDEvent implements Listener
{
	public InteractEntity() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	"player",
			World.class,	"world",
			Entity.class,	"target");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteractEntity(PlayerInteractEntityEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		Entity target = event.getRightClicked();
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				target);
		
		runRoutines(data);
	}
}
