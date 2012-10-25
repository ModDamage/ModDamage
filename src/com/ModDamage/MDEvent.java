package com.ModDamage;

import java.util.HashMap;
import java.util.Map;

import com.ModDamage.Events.Player.*;
import org.bukkit.event.Listener;

import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Events.Command;
import com.ModDamage.Events.Init;
import com.ModDamage.Events.Repeat;
import com.ModDamage.Events.Block.BreakBlock;
import com.ModDamage.Events.Block.PlaceBlock;
import com.ModDamage.Events.Entity.Combust;
import com.ModDamage.Events.Entity.Damage;
import com.ModDamage.Events.Entity.Death;
import com.ModDamage.Events.Entity.Explode;
import com.ModDamage.Events.Entity.Heal;
import com.ModDamage.Events.Entity.ProjectileHit;
import com.ModDamage.Events.Entity.ProjectileLaunch;
import com.ModDamage.Events.Entity.ShootBow;
import com.ModDamage.Events.Entity.Spawn;
import com.ModDamage.Events.Entity.Tame;
import com.ModDamage.Events.Entity.Target;
import com.ModDamage.Events.Item.DropItem;
import com.ModDamage.Events.Item.Enchant;
import com.ModDamage.Events.Item.ItemHeld;
import com.ModDamage.Events.Item.PickupItem;
import com.ModDamage.Events.Item.PrepareEnchant;
import com.ModDamage.Routines.Routines;

public class MDEvent implements Listener
{
	public static Map<String, MDEvent[]> eventCategories = new HashMap<String, MDEvent[]>();
	static {
		eventCategories.put("Block", new MDEvent[] {
				new BreakBlock(),
				new PlaceBlock(),
				});
		
		eventCategories.put("Entity", new MDEvent[] {
				new Combust(),
				new Damage(),
				new Death(),
				new Explode(),
				new Heal(),
				new ProjectileHit(),
				new ProjectileLaunch(),
				new ShootBow(),
				new Spawn(),
				new Tame(),
				new Target(),
				});

		eventCategories.put("Item", new MDEvent[] {
				new DropItem(),
				new PickupItem(),
				new ItemHeld(),
				new Enchant(),
				new PrepareEnchant(),
				});

		eventCategories.put("Player", new MDEvent[] {
				new Interact(),
				new InteractEntity(),
				new Join(),
				new LevelChange(),
				new PickupExp(),
				new Quit(),
				new Teleport(),
				new ToggleFlight(),
				new ToggleSneak(),
				new ToggleSprint(),
                new Fish(),
				});

		eventCategories.put("Misc", new MDEvent[] {
				Init.instance,
				Command.instance,
				Repeat.instance,
				});
	}

	public static boolean disableDeathMessages = false;
	public static boolean disableJoinMessages = false;
	public static boolean disableQuitMessages = false;
	
	
	protected EventInfo myInfo;
	protected MDEvent(EventInfo myInfo)
	{
		this.myInfo = myInfo;
	}
	
	public void runRoutines(EventData data)
	{
		try
		{
			if (routines != null)
				routines.run(data);
		}
		catch (BailException e)
		{
			ModDamage.reportBailException(e);
		}
	}
	protected Routines routines = null;
	protected LoadState specificLoadState = LoadState.NOT_LOADED;
	protected static LoadState state = LoadState.NOT_LOADED;
	
	protected LoadState getState(){ return specificLoadState; }
	
	protected String name() { return this.getClass().getSimpleName(); }
	
	protected void load(Object nestedContent)
	{
		routines = RoutineAliaser.parseRoutines(nestedContent, myInfo);
		specificLoadState = routines != null? LoadState.SUCCESS : LoadState.FAILURE;
		if (specificLoadState != LoadState.SUCCESS)
			this.routines = null;
	}
	
	protected static void reload()
	{
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Loading routines...");
		state = LoadState.NOT_LOADED;
		ModDamage.changeIndentation(true);
		for (MDEvent[] events : eventCategories.values())
		{
			for (MDEvent event : events)
			{
				Object nestedContent = PluginConfiguration.getCaseInsensitiveValue(ModDamage.getPluginConfiguration().getConfigMap(), event.name());
				if(nestedContent != null)
				{
					ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
					ModDamage.addToLogRecord(OutputPreset.INFO, event.name() + " configuration:");
					event.load(nestedContent);
				}
				else
				{
					event.specificLoadState = LoadState.NOT_LOADED;
					event.routines = new Routines();
				}
				ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
				switch(event.specificLoadState)
				{
					case NOT_LOADED:
						ModDamage.addToLogRecord(OutputPreset.WARNING, event.name() + " configuration not found.");
						break;
					case FAILURE:
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error in " + event.name() + " configuration.");
						break;
					case SUCCESS:
						ModDamage.addToLogRecord(OutputPreset.INFO, "End " + event.name() + " configuration.");
						break;
						
					default: throw new Error("Unknown state: "+event.specificLoadState+" $MDE125");
				}
				state = LoadState.combineStates(state, event.specificLoadState);
			}
		}
		ModDamage.changeIndentation(false);
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		switch(state)
		{
			case NOT_LOADED:
				ModDamage.addToLogRecord(OutputPreset.WARNING, "No routines loaded! Are any routines defined?");
				break;
			case FAILURE:
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "One or more errors occurred while loading routines.");
				break;
			case SUCCESS:
				ModDamage.addToLogRecord(OutputPreset.INFO, "Routines loaded!");
				break;
				
			default: throw new Error("Unknown state: "+state+" $MDE143");
		}
	}
};