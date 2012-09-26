package com.ModDamage.Events.Item;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.Backend.EnchantmentsRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class Enchant extends MDEvent implements Listener
{
	public Enchant() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class, 			"player",
			World.class,			"world",
			ItemStack.class, 		"item",
			EnchantmentsRef.class,	"enchantments",
			Integer.class,			"level",
			Boolean.class,			"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEnchantItem(EnchantItemEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getEnchanter();
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				event.getItem(),
				new EnchantmentsRef(event.getEnchantsToAdd()),
				event.getExpLevelCost(),
				event.isCancelled()
				);
		
		runRoutines(data);
		
		int level = data.get(Integer.class, data.start + 4);
		
		event.setExpLevelCost(level);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
