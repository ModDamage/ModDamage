package com.ModDamage.Events.Player;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class Interact extends MDEvent implements Listener
{
	public Interact() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,		"player",
			World.class,		"world",
			ItemHolder.class, 	"item",
			Action.class, 		"action",
			Boolean.class,		"interact_left",
			Boolean.class,		"interact_right",
			Boolean.class,		"interact_with_block",
			Boolean.class,		"interact_air", "interact_with_air",
			Block.class,		"interact_block",
			Boolean.class,		"cancelled");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		Action action = event.getAction();
		
		Block clickedBlock = event.getClickedBlock();
		
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				new ItemHolder(event.getItem()),
				action,
				action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK,
				action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK,
				action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK,
				action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR,
				clickedBlock,
				event.useInteractedBlock() == Result.DENY && event.useItemInHand() == Result.DENY //event.isCancelled()
				);
		
		runRoutines(data);
		
		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
	}
}
