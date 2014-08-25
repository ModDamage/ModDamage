package com.ModDamage.Events.Item;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class PrepareEnchant extends MDEvent implements Listener
{
	public PrepareEnchant() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,		"player",
			World.class,		"world",
			ItemHolder.class, 	"item",
			Integer.class,		"bonus",
			Integer.class,		"level_1",
			Integer.class,		"level_2",
			Integer.class,		"level_3",
			Boolean.class,		"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPrepareItemEnchant(final PrepareItemEnchantEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getEnchanter();
		Integer bonus = event.getEnchantmentBonus();
		int[] levels = event.getExpLevelCostsOffered();
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				new ItemHolder(event.getItem()),
				bonus,
				levels[0], levels[1], levels[2],
				event.isCancelled()
				);
		
		runRoutines(data);
		
		levels[0] = data.get(Integer.class, data.start + 4);
		levels[1] = data.get(Integer.class, data.start + 5);
		levels[2] = data.get(Integer.class, data.start + 6);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
