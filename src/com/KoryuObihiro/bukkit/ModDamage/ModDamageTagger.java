package com.KoryuObihiro.bukkit.ModDamage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.yaml.snakeyaml.Yaml;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;


public class ModDamageTagger
{
	public static final int defaultInterval = 120 * 20;
	private final Map<String, HashSet<UUID>> tags = Collections.synchronizedMap(new LinkedHashMap<String, HashSet<UUID>>());
	
	private final HashSet<Integer> pendingTaskIDs = new HashSet<Integer>();
	private long saveInterval;
	private long cleanInterval;
	private Integer saveTaskID;
	private Integer cleanTaskID;

	private final File file;
	private InputStream reader = null;
	private FileWriter writer = null;
	private Yaml yaml = new Yaml();

	public ModDamageTagger(File file, long saveInterval, long cleanInterval)
	{
		this.file = file;
		if(file != null)
		{
			try
			{
				if(!file.exists())
				{
					ModDamage.addToLogRecord(DebugSetting.NORMAL, "No tags file found at " + file.getAbsolutePath() + ", generating a new one...", LoadState.NOT_LOADED);
					if(!file.getParentFile().mkdirs() && !file.createNewFile())
						ModDamage.addToLogRecord(DebugSetting.QUIET, "Couldn't make new tags file! Tags will not have persistence between reloads.", LoadState.FAILURE);
				}
				reader = new FileInputStream(file);
				Object tagFileObject = yaml.load(reader);
				reader.close();
				if(tagFileObject != null)
				{
					if(tagFileObject instanceof LinkedHashMap)
					{
						@SuppressWarnings("unchecked")
						LinkedHashMap<String, Object> tagMap = (LinkedHashMap<String, Object>)tagFileObject;
						for(String tag : tagMap.keySet())
						{
							if(tagMap.get(tag) instanceof List)
							{
								HashSet<UUID> uuids = new HashSet<UUID>();
								
								@SuppressWarnings("unchecked")
								List<String> uuidStrings = (List<String>)tagMap.get(tag);
								for(String uuidString : uuidStrings)
								{
									UUID uuid = UUID.fromString(uuidString);
									if(uuid != null)
										uuids.add(uuid);
									else ModDamage.addToLogRecord(DebugSetting.QUIET, "Could not read entity ID " + uuidString + " for tag \"" + tag + "\".", LoadState.NOT_LOADED);
								}
								if(!uuids.isEmpty()) tags.put(tag, uuids);
								else ModDamage.addToLogRecord(DebugSetting.VERBOSE, "No entity IDs added for tag \"" + tag + "\" in tags.yml.", LoadState.NOT_LOADED);
							}
							else ModDamage.addToLogRecord(DebugSetting.QUIET, "Could not read tag list for tag \"" + tag + "\".", LoadState.NOT_LOADED);
						}
					}
					else ModDamage.addToLogRecord(DebugSetting.QUIET, "Incorrectly formatted tags.yml. Starting with an empty tag list.", LoadState.NOT_LOADED);
				}
			}
			catch(Exception e){ ModDamage.addToLogRecord(DebugSetting.NORMAL, "Error loading tags.yml.", LoadState.FAILURE);}
		}
		else
		{
			saveTaskID = null;
			cleanTaskID = null;
		}
		this.saveInterval = saveInterval;
		this.cleanInterval = cleanInterval;
		reload(false);
	}
	
	public void reload(){ reload(true);}
	
	private synchronized void reload(boolean initialized)
	{
		cleanUp();
		save();
		if(initialized)
		{
			if(file != null)
			{
				if(saveTaskID != null) Bukkit.getScheduler().cancelTask(saveTaskID);
				if(cleanTaskID != null) Bukkit.getScheduler().cancelTask(cleanTaskID);
			}
		}
		Plugin modDamage = Bukkit.getPluginManager().getPlugin("ModDamage");
		saveTaskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(modDamage, new ModDamageTagSaveTask(), saveInterval, saveInterval);
		cleanTaskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(modDamage, new ModDamageTagCleanTask(), cleanInterval, cleanInterval);	
	}
	
	private abstract class ModDamageTagTask implements Runnable//FIXME This shouldn't be necessary, if we're handling syncing right. The problem is...we're not. :(
	{
		@Override public void run()
		{
			try
			{
				doSomething();
			}
			catch(ConcurrentModificationException e)
			{
				ModDamage.log.warning("Encountered a threading error with tags.");
			}
		}
		protected abstract void doSomething();
	}
	private class ModDamageTagSaveTask extends ModDamageTagTask{ @Override protected void doSomething(){ save();}}
	private class ModDamageTagCleanTask extends ModDamageTagTask{ @Override protected void doSomething(){ cleanUp();}}
	
	/**
	 * Saves all tags to a file.
	 */
	public synchronized void save()
	{
		if(file != null)
		{
			LinkedHashMap<String, List<String>> tempMap = new LinkedHashMap<String, List<String>>();
			Set<String> keys = tags.keySet();
			synchronized(tags)
			{
				for(String tag : keys)
				{
					tempMap.put(tag, new ArrayList<String>());
					for(UUID entityID : tags.get(tag))
						tempMap.get(tag).add(entityID.toString());
				}
			}
			try
			{
				writer = new FileWriter(file);
				writer.write(yaml.dump(tempMap));
				writer.close();
			}
			catch (IOException e){ ModDamage.log.warning("Error writing to " + file.getAbsolutePath() + "!");}
		}
	}
	
//public API
	/**
	 * Add the entity's UUID to a tag. A new tag is made if it doesn't already exist.
	 */
	public synchronized void addTag(String tag, Entity entity)
	{
		if(!tags.containsKey(tag))
			tags.put(tag, new HashSet<UUID>());
		if(!tags.get(tag).contains(entity.getUniqueId()))
			tags.get(tag).add(entity.getUniqueId());
	}
	
	/**
	 * Identical to {@link void addTag(String tag, Entity entity) [addTag]}, except
	 *  a delayed task is initiated to remove the UUID from this tag after the specified duration.
	 * 
	 * @param tagDuration the delay for removing the tag, in Minecraft ticks.
	 */
	public synchronized void addTag(String tag, Entity entity, long tagDuration)
	{
		addTag(tag, entity);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("ModDamage"), new ModDamageTagRemoveTask(tag, entity), tagDuration);
	}
	
	public class ModDamageTagRemoveTask implements Runnable
	{
		final String tag;
		final Entity entity;
		
		ModDamageTagRemoveTask(String tag, Entity entity)
		{
			this.tag = tag;
			this.entity = entity;
		}
		
		@Override
		public void run()
		{
			if(entity != null)
				removeTag(tag, entity);
		}
	}

	/**
	 * Checks if entity has been tagged with the specified tag.
	 * @return Boolean indicating whether or not the entity was tagged.
	 */
	public synchronized boolean isTagged(Entity entity, String tag)
	{
		return tags.containsKey(tag)?tags.get(tag).contains(entity.getUniqueId()):false;
	}
	
	/**
	 * @param entity - The entity whose UUID will be checked for tags.
	 * @return List of found tags.
	 */
	public synchronized List<String> getTags(Entity entity)
	{
		List<String> entityTags = new ArrayList<String>();
		int id = entity.getEntityId();
		Set<String> keys = tags.keySet();
		synchronized(tags)
		{	
			for(String tag : keys)
				if(tags.get(tag).contains(id))
					entityTags.add(tag);
		}
		return entityTags;
	}
	
	/**
	 * Removes the entity's UUID from a tag, if {@link void generateTag(String tag) [generateTag]} was called correctly.	 * 
	 */
	public synchronized void removeTag(String tag, Entity entity)
	{
		if(tags.containsKey(tag))
			tags.get(tag).remove(entity.getEntityId());
	}
	
	/**
	 * This method checks whether or not the number of tagged entities exceeds the number of entities in the server,
	 * and if so removes entities that no longer exist.
	 */
	public synchronized void cleanUp()
	{
		//clean up the entities
		HashSet<UUID> ids = new HashSet<UUID>();
		for(World world : Bukkit.getWorlds())
			for(Entity entity : world.getEntities())
				ids.add(entity.getUniqueId());
		Set<String> keys = tags.keySet();
		synchronized(tags)
		{	
			for(String tag : keys)
				for(UUID id : tags.get(tag))
					if(!ids.contains(id))
						tags.get(tag).remove(id);
		}
		//clean up the tasks
		HashSet<Integer> bukkitTaskIDs = new HashSet<Integer>();
		List<BukkitTask> bukkitTasks = Bukkit.getScheduler().getPendingTasks();
		for(BukkitTask task : bukkitTasks)
			bukkitTaskIDs.add(task.getTaskId());
		for(Integer taskID : pendingTaskIDs)
			if(!bukkitTaskIDs.contains(taskID))
				pendingTaskIDs.remove(taskID);
	}
	
	/**
	 * Only the ModDamage main should use this method.
	 */
	public synchronized void clear(){ tags.clear();}
	
	/**
	 * This is used in the ModDamage main to finish any file IO.
	 */
	public synchronized void close()
	{
		cleanUp();
		save();
	}
	
	/**
	 * @return LoadState reflecting the file's load state.
	 */
	public LoadState getLoadState(){ return file != null?LoadState.SUCCESS:LoadState.NOT_LOADED;}
	
	//TODO Design - Make removal tasks keep track of themselves, or serialize?
}