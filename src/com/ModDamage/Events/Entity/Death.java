package com.ModDamage.Events.Entity;

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

import java.util.List;

public class Death extends MDEvent implements Listener
{
	public Death() { super(myInfo); }
	
	static final EventInfo myInfo = Damage.myInfo.chain(new SimpleEventInfo(
			Integer.class, "experience", "-default",
            List.class, "drops",
			String.class, "message", "msg"));
			
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		String message = null;
		
		if (event instanceof PlayerDeathEvent) {
			message = ((PlayerDeathEvent)event).getDeathMessage();
			if(disableDeathMessages)
				((PlayerDeathEvent)event).setDeathMessage(null);
		}
			
		Entity entity = event.getEntity();
		
	    EventData damageData = Damage.getEventData(((LivingEntity) entity).getLastDamageCause());
	    
		if(damageData == null) // for instance, /butcher often does this
			damageData = Damage.myInfo.makeData(
					null,
					null,
					entity,
					entity.getWorld(),
					DamageType.UNKNOWN,
					0,
					null
					);
		
		EventData data = myInfo.makeChainedData(damageData,
                event.getDroppedExp(),
                event.getDrops(),
                message);
		
		runRoutines(data);
		
		event.setDroppedExp(data.get(Integer.class, data.start));
		
		if (event instanceof PlayerDeathEvent) {
			if(!disableDeathMessages)
				((PlayerDeathEvent)event).setDeathMessage(data.get(String.class, data.start + 2));
		}
	}
}
