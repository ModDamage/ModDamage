package com.moddamage.events.player;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.backend.ItemHolder;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class Interact extends MDEvent implements Listener
{
	public Interact() { super(myInfo); }

	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class, "player",
			World.class, "world",
			ItemHolder.class, "item",
			Action.class, "action",
			Boolean.class, "interact_left",
			Boolean.class, "interact_right",
			Boolean.class, "interact_with_block",
			Boolean.class, "interact_air", "interact_with_air",
			Block.class, "interact_block",
			Result.class, "use_interact_block",
			Result.class, "use_item",
			Boolean.class, "cancelled");

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
				event.useInteractedBlock(),
				event.useItemInHand(),
				event.isCancelled()
				);

		runRoutines(data);

		event.setCancelled(data.get(Boolean.class, data.start + data.objects.length - 1));
		event.setUseItemInHand(data.get(Result.class, data.start + data.objects.length - 2));
		event.setUseInteractedBlock(data.get(Result.class, data.start + data.objects.length - 3));
	}
}
