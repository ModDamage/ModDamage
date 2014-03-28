package com.ModDamage;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.ModDamage.BaseConfig.LoadState;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EventFinishedListener;
import com.ModDamage.Backend.ScriptLineHandler;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Events.Command;
import com.ModDamage.Events.Init;
import com.ModDamage.Events.Repeat;
import com.ModDamage.Events.Block.BlockBurn;
import com.ModDamage.Events.Block.BlockFade;
import com.ModDamage.Events.Block.BlockFlow;
import com.ModDamage.Events.Block.BlockForm;
import com.ModDamage.Events.Block.BlockGrow;
import com.ModDamage.Events.Block.BlockIgnite;
import com.ModDamage.Events.Block.BlockPhysics;
import com.ModDamage.Events.Block.BlockSpread;
import com.ModDamage.Events.Block.BreakBlock;
import com.ModDamage.Events.Block.FurnaceExtract;
import com.ModDamage.Events.Block.LeavesDecay;
import com.ModDamage.Events.Block.PlaceBlock;
import com.ModDamage.Events.Chunk.ChunkLoad;
import com.ModDamage.Events.Chunk.ChunkPopulate;
import com.ModDamage.Events.Chunk.ChunkUnload;
import com.ModDamage.Events.Entity.Combust;
import com.ModDamage.Events.Entity.Damage;
import com.ModDamage.Events.Entity.Death;
import com.ModDamage.Events.Entity.Explode;
import com.ModDamage.Events.Entity.Heal;
import com.ModDamage.Events.Entity.HorseJump;
import com.ModDamage.Events.Entity.ProjectileHit;
import com.ModDamage.Events.Entity.ProjectileLaunch;
import com.ModDamage.Events.Entity.ShootBow;
import com.ModDamage.Events.Entity.Spawn;
import com.ModDamage.Events.Entity.Tame;
import com.ModDamage.Events.Entity.Target;
import com.ModDamage.Events.Entity.Teleport;
import com.ModDamage.Events.Inventory.Craft;
import com.ModDamage.Events.Inventory.InventoryClick;
import com.ModDamage.Events.Inventory.InventoryClose;
import com.ModDamage.Events.Inventory.InventoryOpen;
import com.ModDamage.Events.Inventory.PrepareCraft;
import com.ModDamage.Events.Item.DropItem;
import com.ModDamage.Events.Item.Enchant;
import com.ModDamage.Events.Item.ItemHeld;
import com.ModDamage.Events.Item.PickupItem;
import com.ModDamage.Events.Item.PrepareEnchant;
import com.ModDamage.Events.Player.AsyncChat;
import com.ModDamage.Events.Player.Chat;
import com.ModDamage.Events.Player.Consume;
import com.ModDamage.Events.Player.Fish;
import com.ModDamage.Events.Player.Interact;
import com.ModDamage.Events.Player.InteractEntity;
import com.ModDamage.Events.Player.Join;
import com.ModDamage.Events.Player.Kick;
import com.ModDamage.Events.Player.LevelChange;
import com.ModDamage.Events.Player.Login;
import com.ModDamage.Events.Player.PickupExp;
import com.ModDamage.Events.Player.Quit;
import com.ModDamage.Events.Player.ToggleFlight;
import com.ModDamage.Events.Player.ToggleSneak;
import com.ModDamage.Events.Player.ToggleSprint;
import com.ModDamage.Events.World.StructureGrow;
import com.ModDamage.Routines.Routines;
import com.google.common.collect.MapMaker;

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
                new BlockIgnite(),
                new BlockSpread(),
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
	
	public static void addEvents(String category, Collection<MDEvent> events)
	{
		List<MDEvent> newEvents = new ArrayList<MDEvent>(events);
		if (eventCategories.containsKey(category) && eventCategories.get(category) != null) 
		{
			List<MDEvent> oldEvents = eventCategories.get(category);
			newEvents.addAll(0, oldEvents);
		}

		eventCategories.put(category, newEvents);
		
		for (MDEvent event : newEvents)
			allEvents.put(event.name(), event);
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
			for (Reference<Routines> routines : routines_cached)
				if (routines != null && routines.get() != null) {
	                routines.get().run(data); //FIXME: Variable bleeds through scripts...
	                return;
	            }
			eventFinished(true);
		}
		catch (BailException e)
		{
			ModDamage.reportBailException(e);
		}
        eventFinished(false);
	}
	protected Map<String, Routines> routines = new MapMaker().weakValues().makeMap();
	protected List<Reference<Routines>> routines_cached = new LinkedList<Reference<Routines>>();
	
	protected Map<String, LoadState> loadStates = new HashMap<String, LoadState>();
	private static Map<String, LoadState> combinedLoadStates = new HashMap<String, LoadState>();
	
	public static LoadState getCombinedLoadStates(BaseConfig config) {
		return combinedLoadStates.containsKey(config.getName())? combinedLoadStates.get(config.getName()) : LoadState.NOT_LOADED;
	}
	
	protected static void setCombinedLoadState(BaseConfig config, LoadState state) {
		combinedLoadStates.put(config.getName(), state);
	}
	
	public LoadState getState(BaseConfig config){ 
			return (loadStates.containsKey(config.getName())) ? loadStates.get(config.getName()) : LoadState.NOT_LOADED;
	}
	
	public String name() { return this.getClass().getSimpleName(); }
	

	public ScriptLineHandler getLineHandler(BaseConfig config)
	{
		if (routines == null)
			routines = new MapMaker().weakValues().makeMap();
		
		String name = config.getName();
		
		if (!routines.containsKey(name))
			routines.put(name, new Routines(config));
			
		
		LogUtil.info("on " + name());
		
		loadStates.put(name, LoadState.SUCCESS);
		if (combinedLoadStates.containsKey(name))
			combinedLoadStates.put(name, LoadState.combineStates(combinedLoadStates.get(name), loadStates.get(name)));
		else
			combinedLoadStates.put(name, loadStates.get(name));	
		
		return routines.get(name).getLineHandler(myInfo);
	}
	
	
	public static MDEvent getEvent(String name)
	{
		return allEvents.get(name);
	}
	
	//FIXME: Not really working with multiconfig (MUST IMPLEMENT More iterators)
	public static void registerEvents()
	{
		master: 
			for (Entry<String, MDEvent> entry : allEvents.entrySet()) {
				if (entry.getValue().routines != null && !entry.getValue().routines.isEmpty()) {
					boolean found = false;
					for (Routines r : entry.getValue().routines.values()) //Must iterate to make sure routines exist.
						if (!r.isEmpty()) {
							found = true;
							break master;
						}
					if (found)
						Bukkit.getPluginManager().registerEvents(entry.getValue(), ModDamage.getInstance());
					else //Remove unused stuff
						HandlerList.unregisterAll(entry.getValue());
				}
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