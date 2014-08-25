package com.moddamage;

import com.moddamage.PluginConfiguration.LoadState;
import com.moddamage.backend.BailException;
import com.moddamage.backend.EventFinishedListener;
import com.moddamage.backend.ScriptLineHandler;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.events.Command;
import com.moddamage.events.Init;
import com.moddamage.events.Repeat;
import com.moddamage.events.block.*;
import com.moddamage.events.chunk.ChunkLoad;
import com.moddamage.events.chunk.ChunkPopulate;
import com.moddamage.events.chunk.ChunkUnload;
import com.moddamage.events.entity.*;
import com.moddamage.events.inventory.*;
import com.moddamage.events.item.*;
import com.moddamage.events.player.*;
import com.moddamage.events.weather.LightingStrike;
import com.moddamage.events.weather.ThunderChange;
import com.moddamage.events.weather.WeatherChange;
import com.moddamage.events.world.StructureGrow;
import com.moddamage.routines.Routines;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.Map.Entry;

public class MDEvent implements Listener
{
	public static Map<String, MDEvent> allEvents = new HashMap<String, MDEvent>();
	public static Map<String, List<MDEvent>> eventCategories = new HashMap<String, List<MDEvent>>();
	
	public static void registerVanillaEvents()
	{
		if (!eventCategories.isEmpty())
		{
			for(Entry<String, List<MDEvent>> entries: eventCategories.entrySet())
				if (entries.getValue() != null && entries.getValue().size() > 0)
					for(MDEvent event: entries.getValue())
						HandlerList.unregisterAll(event);
			
			eventCategories.clear();
		}
		
		addEvents("Block",
                new BlockBurn(),
                new BlockFade(),
                new BlockFlow(),
                new BlockForm(),
                new BlockGrow(),
                new BlockDispense(),
                new BlockIgnite(),
                new BlockSpread(),
                new SignChange(),
				new BreakBlock(),
				new PlaceBlock(),
                new LeavesDecay(),
                new FurnaceExtract(),
                new BlockPhysics()
				);
		
		addEvents("Chunk",
                new ChunkLoad(),
                new ChunkPopulate(),
                new ChunkUnload()
				);
		
		addEvents("Entity",
				new Combust(),
				new Damage(),
				new Death(),
				new Explode(),
				new Heal(),
				new HorseJump(),
				new ProjectileHit(),
				new ProjectileLaunch(),
				new ShootBow(),
				new Spawn(),
				new Tame(),
				new Target(),
                new Teleport()
				);

		addEvents("Inventory",
				new InventoryOpen(),
				new InventoryClose(),
				new InventoryClick(),
				new Craft(),
				new PrepareCraft()
				);

		addEvents("Item",
				new DropItem(),
				new PickupItem(),
				new ItemHeld(),
				new Enchant(),
				new PrepareEnchant()
				);

		addEvents("Player",
				new AsyncChat(),
				new Chat(),
				new Consume(),
				new Interact(),
				new InteractEntity(),
				new Join(),
				new Kick(),
				new LevelChange(),
				new Login(),
				new PickupExp(),
				new Quit(),
				new ToggleFlight(),
				new ToggleSneak(),
				new ToggleSprint(),
                new Fish()
				);

		addEvents("World",
				new StructureGrow()
				);
		
		addEvents("Weather",
				new LightingStrike(),
				new ThunderChange(),
				new WeatherChange());

		addEvents("Misc",
				Init.instance,
				Command.instance,
				Repeat.instance
				);
	}
	
	public static boolean disableDeathMessages = false;
	public static boolean disableJoinMessages = false;
	public static boolean disableQuitMessages = false;
	public static boolean disableKickMessages = false;
	
	public static void addEvents(String category, MDEvent... eventsArray)
	{
		addEvents(category, Arrays.asList(eventsArray));
	}
	
	public static void addEvents(String category, List<MDEvent> newEvents)
	{
		if (eventCategories.containsKey(category) && eventCategories.get(category) != null) 
		{
			List<MDEvent> oldEvents = eventCategories.get(category);
			newEvents.addAll(0, oldEvents);
		}

		eventCategories.put(category, newEvents);
		
		for (MDEvent event : newEvents)
		{
			allEvents.put(event.name(), event);
		}
	}
	
	public static void addEvent(String category, MDEvent event)
	{
		addEvents(category, event);
	}
	
	protected EventInfo myInfo;
	public EventInfo getInfo() { return myInfo; }
	
	protected MDEvent(EventInfo myInfo)
	{
		this.myInfo = myInfo;
	}
	
	public void runRoutines(EventData data)
	{
		try
		{
			if (routines != null) {
                routines.run(data);
                eventFinished(true);
                return;
            }
		}
		catch (BailException e)
		{
			ModDamage.reportBailException(e);
		}
        eventFinished(false);
	}
	protected Routines routines = null;
	protected LoadState loadState = LoadState.NOT_LOADED;
	protected static LoadState combinedLoadState = LoadState.NOT_LOADED;
	
	public LoadState getState(){ return loadState; }
	
	public String name() { return this.getClass().getSimpleName(); }
	

	public ScriptLineHandler getLineHandler()
	{
		if (routines == null)
			routines = new Routines();

		LogUtil.info("on " + name());
		
		loadState = LoadState.SUCCESS;
		combinedLoadState = LoadState.combineStates(combinedLoadState, loadState);
		
		return routines.getLineHandler(myInfo);
	}
	
	
	public static MDEvent getEvent(String name)
	{
		return allEvents.get(name);
	}
	
	public static void registerEvents()
	{
		for (Entry<String, MDEvent> entry : allEvents.entrySet()) {
			if (entry.getValue().routines != null && !entry.getValue().routines.isEmpty())
				Bukkit.getPluginManager().registerEvents(entry.getValue(), ModDamage.configuration.plugin);
		}
	}
	
	public static void unregisterEvents()
	{
		for (Entry<String, MDEvent> entry : allEvents.entrySet()) {
			HandlerList.unregisterAll(entry.getValue());
		}
	}
	
	public static void clearEvents()
	{
		for (Entry<String, MDEvent> entry : allEvents.entrySet()) {
			entry.getValue().routines = null;
		}
	}


    private static List<EventFinishedListener> whenEventFinishesList = new ArrayList<EventFinishedListener>();

    public static void whenEventFinishes(EventFinishedListener task) {
        whenEventFinishesList.add(task);
    }

    private static void eventFinished(boolean success) {
        for (EventFinishedListener task : whenEventFinishesList) {
            try {
                task.eventFinished(success);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        whenEventFinishesList.clear();
    }
};