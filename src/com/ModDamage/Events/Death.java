package com.ModDamage.Events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Matchables.DamageType;

public class Death extends MDEvent implements Listener
{
	public Death() { super(myInfo); }
	
	static final EventInfo myInfo = Damage.myInfo.chain(new SimpleEventInfo(
			Integer.class, "experience", "-default"));
			
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		if(disableDeathMessages && event instanceof PlayerDeathEvent)
			((PlayerDeathEvent)event).setDeathMessage(null);
			
		Entity entity = event.getEntity();
		
	    EventData damageData = Damage.getEventData(((LivingEntity) entity).getLastDamageCause());
	    
		if(damageData == null) // for instance, /butcher often does this
			damageData = Damage.myInfo.makeData(
					null,
					null,
					entity,
					entity.getWorld(),
					DamageType.UNKNOWN,
					0
					);
		
		EventData data = myInfo.makeChainedData(damageData, event.getDroppedExp());
		
		runRoutines(data);
		
		event.setDroppedExp(data.get(Integer.class, data.start));
	}
}
