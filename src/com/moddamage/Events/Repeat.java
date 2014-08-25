package com.ModDamage.Events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import com.ModDamage.LogUtil;
import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Events.Repeat.RepeatInfo.RepeatData;
import com.ModDamage.Routines.Routines;


public class Repeat extends MDEvent implements ScriptLineHandler
{
	public static final Repeat instance = new Repeat();

	private Repeat() {
		super(null);
		
		repeatMap.put(Entity.class, new HashMap<String, Repeat.RepeatInfo<?>>());
		repeatMap.put(Location.class, new HashMap<String, Repeat.RepeatInfo<?>>());
		repeatMap.put(World.class, new HashMap<String, Repeat.RepeatInfo<?>>());
		repeatMap.put(Chunk.class, new HashMap<String, Repeat.RepeatInfo<?>>());
	}

	Map<Class<?>, Map<String, RepeatInfo<?>>> repeatMap = new HashMap<Class<?>, Map<String, RepeatInfo<?>>>();

	
	public void reset()
	{
		for (Map<String, RepeatInfo<?>> specificMap : repeatMap.values())
		{
			for (RepeatInfo<?> info : specificMap.values())
				info.stopAll();
			
			specificMap.clear();
		}
	}

	@Override
	public ScriptLineHandler getLineHandler()
	{
		LogUtil.info("on " + name());
		
		return this;
	}
	
	@Override
	public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren)
	{
		String[] parts = line.line.split("\\s+");
		String name = parts[0];
		Class<?> type;
		if (parts.length == 1 || parts[1].equalsIgnoreCase("entity"))
			type = Entity.class;
		else if (parts[1].equalsIgnoreCase("loc") || parts[1].equalsIgnoreCase("location") || parts[1].equalsIgnoreCase("block"))
			type = Location.class;
		else if (parts[1].equalsIgnoreCase("world"))
			type = World.class;
		else if (parts[1].equalsIgnoreCase("chunk"))
			type = Chunk.class;
		else {
			LogUtil.error("Illegal repeat type: "+parts[1]);
			return null;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		RepeatInfo<?> repeat = new RepeatInfo(name, type);
		repeatMap.get(repeat.repeatType).put(repeat.name, repeat);
		
		LogUtil.info("Repeat ["+repeat.name+" "+repeat.repeatType.getSimpleName()+"]");
//		repeat.routines = RoutineAliaser.parseRoutines(entry.getValue(), repeat.myInfo);
//		if (repeat.routines == null)
//		{
//			failed = true;
//			continue;
//		}

		return repeat.routines.getLineHandler(repeat.myInfo);
	}
	@Override
	public void done()
	{
	}
	
//	@SuppressWarnings("unchecked")
//	@Override
//	public void load(Object repeats)
//	{
//		loadState = LoadState.FAILURE;
//		boolean failed = false;
//
//		for (Map<String, RepeatInfo<?>> specificMap : repeatMap.values())
//		{
//			for (RepeatInfo<?> info : specificMap.values())
//				info.stopAll();
//			
//			specificMap.clear();
//		}
//
//		// LinkedHashMap<String, Object> entries = ModDamage.getPluginConfiguration().getConfigMap();
//		// Object commands = PluginConfiguration.getCaseInsensitiveValue(entries, "Repeat");
//
//		if(repeats == null)
//			return;
//
//		if (!(repeats instanceof List))
//		{
//			LogUtil.error("Expected List, got "+repeats.getClass().getSimpleName()+"for Repeat event");
//			return;
//		}
//
//		List<LinkedHashMap<String, Object>> repeatConfigMaps = (List<LinkedHashMap<String, Object>>) repeats;
//		if(repeatConfigMaps == null || repeatConfigMaps.size() == 0)
//			return;
//
//		LogUtil.console_only("");
//		ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Loading repeats...");
//
//		ModDamage.changeIndentation(true);
//
//
//		for (LinkedHashMap<String, Object> repeatConfigMap : repeatConfigMaps)
//		for (Entry<String, Object> entry : repeatConfigMap.entrySet())
//		{
//			String[] parts = entry.getKey().split("\\s+");
//			String name = parts[0];
//			Class<?> type;
//			if (parts.length == 1 || parts[1].equalsIgnoreCase("entity"))
//				type = Entity.class;
//			else if (parts[1].equalsIgnoreCase("loc") || parts[1].equalsIgnoreCase("location") || parts[1].equalsIgnoreCase("block"))
//				type = Location.class;
//			else if (parts[1].equalsIgnoreCase("world"))
//				type = World.class;
//			else if (parts[1].equalsIgnoreCase("chunk"))
//				type = Chunk.class;
//			else {
//				LogUtil.error("Illegal repeat type: "+parts[1]);
//				continue;
//			}
//
//			@SuppressWarnings("rawtypes")
//			RepeatInfo<?> repeat = new RepeatInfo(name, type);
//			LogUtil.info("Repeat ["+repeat.name+" "+repeat.repeatType.getSimpleName()+"]");
//			repeat.routines = RoutineAliaser.parseRoutines(entry.getValue(), repeat.myInfo);
//			if (repeat.routines == null)
//			{
//				failed = true;
//				continue;
//			}
//
//			repeatMap.get(repeat.repeatType).put(repeat.name, repeat);
//		}
//
//		ModDamage.changeIndentation(false);
//
//		if (!failed) loadState = LoadState.SUCCESS;
//	}


	static class RepeatInfo<T>
	{
		String name;
		Class<T> repeatType;
		EventInfo myInfo;

		Routines routines = new Routines();
		

		Map<T, RepeatData> datas = new HashMap<T, RepeatData>();

		public RepeatInfo(String name, Class<T> repeatType)
		{
			this.name = name;
			this.repeatType = repeatType;
			
			String word = repeatType.getSimpleName().toLowerCase();
			if (repeatType == Location.class) word = "loc";
			
			myInfo = new SimpleEventInfo(
					repeatType, "it", word,
					World.class, "world",
					Integer.class, "repeat_delay",
					Integer.class, "repeat_count");
		}

		public void stopAll() {
			List<RepeatData> datasCopy = new ArrayList<RepeatData>(datas.values());
			for (RepeatData data : datasCopy)
				data.stop();
		}

		public class RepeatData implements Runnable
		{
			final T it;
			int delay;
			int count;

			int taskId = -1;

			public RepeatData(T it, int delay, int count)
			{
				this.it = it;
				this.delay = delay;
				this.count = count;
			}

			public void run()
			{
				taskId = -1;

				if (count > 0) count --;
				
				World world;
				
				if (it instanceof World) {
					world = (World) it;
				}
				else if (it instanceof Entity) {
					world = ((Entity) it).getWorld();
					if (!((Entity) it).isValid()) { stop(); return; }
				}
				else if (it instanceof Location) {
					world = ((Location) it).getWorld();
				}
				else if (it instanceof Chunk) {
					world = ((Chunk) it).getWorld();
				}
				else
					throw new IllegalArgumentException("BAD it!");


				EventData data = myInfo.makeData(
						it,
						world,
						delay,
						count
						);

				try
				{
					routines.run(data);
				}
				catch (BailException e)
				{
					ModDamage.reportBailException(e);
					{ stop(); return; }
				}

				delay = data.get(Integer.class, 2);
				count = data.get(Integer.class, 3);

				start(); // start the next task
			}

			private void start()
			{
				if (taskId != -1) return; // already going

				if (delay <= 0 || count == 0) { stop(); return; } // can't start like this

				taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(ModDamage.getPluginConfiguration().plugin, this, delay);

				if (taskId != -1) datas.put(it, this);
				else LogUtil.warning_strong("Unable to start repeat task!");
			}

			private void stop()
			{
				Bukkit.getScheduler().cancelTask(taskId);
				taskId = -1;

				datas.remove(this);
			}
		}
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void start(String name, Object it, int delay, int count) {
		Class<?> type;
		if (it instanceof Entity)
			type = Entity.class;
		else if (it instanceof Block) {
			type = Location.class;
			it = ((Block) it).getLocation();
		}
		else if (it instanceof Location)
			type = Location.class;
		else if (it instanceof World)
			type = World.class;
		else if (it instanceof Chunk)
			type = Chunk.class;
		else
			throw new IllegalArgumentException("Invalid it type: "+it.getClass());
			
		RepeatInfo info = instance.repeatMap.get(type).get(name);
		if (info == null) 
		{
			LogUtil.error("No Repeat named "+name);
			return;
		}

		RepeatInfo.RepeatData data = (RepeatData) info.datas.get(it);
		if (data == null)
			data = info.new RepeatData(it, delay, count);
		else
		{
			data.delay = delay;
			if (data.count < 0 || count < 0)
				data.count = count;
			else
				data.count += count;
		}
		data.start();
	}

	@SuppressWarnings("rawtypes")
	public static void stop(String name, Object it) {
		Class<?> type;
		if (it instanceof Entity)
			type = Entity.class;
		else if (it instanceof Block) {
			type = Location.class;
			it = ((Block) it).getLocation();
		}
		else if (it instanceof Location)
			type = Location.class;
		else if (it instanceof World)
			type = World.class;
		else if (it instanceof Chunk)
			type = Chunk.class;
		else
			throw new IllegalArgumentException("Invalid it type: "+it.getClass());
		
		RepeatInfo info = instance.repeatMap.get(type).get(name);
		if (info == null) return;

		RepeatInfo.RepeatData data = (RepeatData) info.datas.get(it);
		if (data == null) return;

		data.stop();
	}
}
