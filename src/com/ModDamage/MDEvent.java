package com.ModDamage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.ModDamage.PluginConfiguration.LoadState;
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