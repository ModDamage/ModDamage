package com.ModDamage;

import org.bukkit.event.Listener;

import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Events.Combust;
import com.ModDamage.Events.Damage;
import com.ModDamage.Events.Death;
import com.ModDamage.Events.DropItem;
import com.ModDamage.Events.Enchant;
import com.ModDamage.Events.Explode;
import com.ModDamage.Events.Heal;
import com.ModDamage.Events.Interact;
import com.ModDamage.Events.InteractEntity;
import com.ModDamage.Events.ItemHeld;
import com.ModDamage.Events.Join;
import com.ModDamage.Events.PickupExp;
import com.ModDamage.Events.PickupItem;
import com.ModDamage.Events.PrepareEnchant;
import com.ModDamage.Events.ProjectileHit;
import com.ModDamage.Events.Quit;
import com.ModDamage.Events.Spawn;
import com.ModDamage.Events.Tame;
import com.ModDamage.Events.Target;
import com.ModDamage.Events.Teleport;
import com.ModDamage.Routines.Routines;

public class MDEvent implements Listener
{
	public static MDEvent[] events = new MDEvent[] {
			new Combust(),
			new Damage(),
			new Death(),
			new DropItem(),
			new Enchant(),
			new Explode(),
			new Heal(),
			new Interact(),
			new InteractEntity(),
			new ItemHeld(),
			new Join(),
			new PickupExp(),
			new PickupItem(),
			new PrepareEnchant(),
			new ProjectileHit(),
			new Quit(),
			new Spawn(),
			new Tame(),
			new Target(),
			new Teleport(),
		};

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
	
	protected static void reload()
	{
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Loading routines...");
		state = LoadState.NOT_LOADED;
		ModDamage.changeIndentation(true);
		for(MDEvent event : events)
		{
			Object nestedContent = PluginConfiguration.getCaseInsensitiveValue(ModDamage.getPluginConfiguration().getConfigMap(), event.name());
			if(nestedContent != null)
			{
				ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
				ModDamage.addToLogRecord(OutputPreset.INFO, event.name() + " configuration:");
				Routines routines = RoutineAliaser.parseRoutines(nestedContent, event.myInfo);
				event.specificLoadState = routines != null? LoadState.SUCCESS : LoadState.FAILURE;
				if(event.specificLoadState.equals(LoadState.SUCCESS))
					event.routines = routines;
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
					
				default: assert(false);
			}
			state = LoadState.combineStates(state, event.specificLoadState);
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
				
			default: assert(false);
		}
	}
};