package com.ModDamage.Events.Entity;

import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class Heal extends MDEvent implements Listener
{
	public Heal() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class,	"entity",
			World.class,	"world",
            EntityRegainHealthEvent.RegainReason.class,	"heal", "reason", "heal_reason", // e.g. heal.type.EATING
			Double.class, 	"heal_amount", "-default",
			Boolean.class,	"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityRegainHealth(EntityRegainHealthEvent event)
	{
		if(!ModDamage.isEnabled || event.isCancelled()) return;
		
		Entity entity = event.getEntity();
		EventData data = myInfo.makeData(
				entity,
				entity.getWorld(),
				event.getRegainReason(),
				event.getAmount(),
				event.isCancelled());
		
		runRoutines(data);
		
		double newHealAmount = data.get(Double.class, data.start + 3);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
		
		if (newHealAmount <= 0)
			event.setCancelled(true);
		else
			event.setAmount(newHealAmount);
	}
}
