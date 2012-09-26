package com.ModDamage.Events.Entity;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class Tame extends MDEvent implements Listener
{
	public Tame() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class,	"entity",
			Entity.class,	"tamer",
			World.class,	"world",
			Boolean.class,	"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityTame(EntityTameEvent event)
	{
		if(!ModDamage.isEnabled || event.isCancelled()) return;
		
		LivingEntity entity = (LivingEntity)event.getEntity();
		LivingEntity owner = (LivingEntity)event.getOwner();
		EventData data = myInfo.makeData(
				entity,
				owner,
				entity.getWorld(),
				event.isCancelled());
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
