package com.ModDamage.Events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class PickupExp extends MDEvent implements Listener
{
	public PickupExp() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	"player",
			World.class,	"world",
			Integer.class,	"experience");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPickupExperience(PlayerExpChangeEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				event.getAmount());
		
		runRoutines(data);
		
		int experience = data.get(Integer.class, data.start + 2);
		
		event.setAmount(experience);
	}
}
