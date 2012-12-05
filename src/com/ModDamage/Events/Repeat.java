package com.ModDamage.Events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;


public class Repeat extends MDEvent
{
	public static final Repeat instance = new Repeat();

	private Repeat() {super(null);}

	Map<String, RepeatInfo> repeatMap = new HashMap<String, RepeatInfo>();


	@SuppressWarnings("unchecked")
	@Override
	public void load(Object repeats)
	{
		specificLoadState = LoadState.FAILURE;
		boolean failed = false;

		for (RepeatInfo info : repeatMap.values())
			info.stopAll();
		repeatMap.clear();

		// LinkedHashMap<String, Object> entries = ModDamage.getPluginConfiguration().getConfigMap();
		// Object commands = PluginConfiguration.getCaseInsensitiveValue(entries, "Repeat");

		if(repeats == null)
			return;

		if (!(repeats instanceof List))
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Expected List, got "+repeats.getClass().getSimpleName()+"for Repeat event");
			return;
		}

		List<LinkedHashMap<String, Object>> repeatConfigMaps = (List<LinkedHashMap<String, Object>>) repeats;
		if(repeatConfigMaps == null || repeatConfigMaps.size() == 0)
			return;

		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Loading repeats...");

		ModDamage.changeIndentation(true);


		for (LinkedHashMap<String, Object> repeatConfigMap : repeatConfigMaps)
		for (Entry<String, Object> entry : repeatConfigMap.entrySet())
		{
			String name = entry.getKey();

			RepeatInfo repeat = new RepeatInfo(name);
			ModDamage.addToLogRecord(OutputPreset.INFO, "Repeat ["+repeat.name+"]");
			repeat.routines = RoutineAliaser.parseRoutines(entry.getValue(), myInfo);
			if (repeat.routines == null)
			{
				failed = true;
				continue;
			}

			repeatMap.put(repeat.name, repeat);
		}

		ModDamage.changeIndentation(false);

		if (!failed) specificLoadState = LoadState.SUCCESS;
	}


	private static EventInfo myInfo = new SimpleEventInfo(
			Entity.class, "entity",
			World.class, "world",
			Integer.class, "repeat_delay",
			Integer.class, "repeat_count");

	static class RepeatInfo
	{
		String name;

		Routines routines;

		Map<Entity, RepeatData> datas = new HashMap<Entity, RepeatData>();

		public RepeatInfo(String name)
		{
			this.name = name;
		}

		public void stopAll() {
			List<RepeatData> datasCopy = new ArrayList<RepeatData>(datas.values());
			for (RepeatData data : datasCopy)
				data.stop();
		}

		public class RepeatData implements Runnable
		{
			final Entity entity;
			int delay;
			int count;

			int taskId = -1;

			public RepeatData(Entity entity, int delay, int count)
			{
				this.entity = entity;
				this.delay = delay;
				this.count = count;
			}

			public void run()
			{
				taskId = -1;
				if (!entity.isValid()) { stop(); return; }

				if (count > 0) count --;


				EventData data = myInfo.makeData(
						entity,
						entity.getWorld(),
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

				if (taskId != -1) datas.put(entity, this);
				else ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, "Unable to start repeat task!");
			}

			private void stop()
			{
				Bukkit.getScheduler().cancelTask(taskId);
				taskId = -1;

				datas.remove(this);
			}
		}
	}


	public static void start(String name, Entity entity, int delay, int count) {
		RepeatInfo info = instance.repeatMap.get(name);
		if (info == null) 
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "No Repeat named "+name);
			return;
		}

		RepeatInfo.RepeatData data = info.datas.get(entity);
		if (data == null)
			data = info.new RepeatData(entity, delay, count);
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

	public static void stop(String name, Entity entity) {
		RepeatInfo info = instance.repeatMap.get(name);
		if (info == null) return;

		RepeatInfo.RepeatData data = info.datas.get(entity);
		if (data == null) return;

		data.stop();
	}
}
