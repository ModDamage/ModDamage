package com.ModDamage.Events;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Matchables.HealType;

public class Heal extends MDEvent implements Listener
{
	public Heal() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class,	"entity",
			World.class,	"world",
			HealType.class,	"heal", // e.g. heal.type.EATING
			Integer.class, 	"heal_amount", "-default");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityRegainHealth(EntityRegainHealthEvent event)
	{
		if(!ModDamage.isEnabled || event.isCancelled()) return;
		
		Entity entity = event.getEntity();
		EventData data = myInfo.makeData(
				entity,
				entity.getWorld(),
				HealType.get(event.getRegainReason()),
				event.getAmount());
		
		runRoutines(data);
		
		int newHealAmount = data.get(Integer.class, data.start + 3);
		
		if (newHealAmount <= 0)
			event.setCancelled(true);
		else
			event.setAmount(newHealAmount);
	}
}
