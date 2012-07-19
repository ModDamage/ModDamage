package com.ModDamage.Events;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class Interact extends MDEvent implements Listener
{
	public Interact() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,		"player",
			World.class,		"world",
			ItemStack.class, 	"item",
			Boolean.class,		"interact_left",
			Boolean.class,		"interact_right",
			Boolean.class,		"interact_block",
			Boolean.class,		"interact_air",
			Integer.class,		"interact_block_type",
			Integer.class,		"interact_block_data");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		Action action = event.getAction();
		
		Block clickedBlock = event.getClickedBlock();
		int block_type = 0;
		int block_data = 0;
		if (clickedBlock != null)
		{
			block_type = clickedBlock.getTypeId();
			block_data = clickedBlock.getData();
		}
		
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				event.getItem(),
				action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK,
				action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK,
				action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK,
				action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR,
				block_type,
				block_data);
		
		runRoutines(data);
	}
}
