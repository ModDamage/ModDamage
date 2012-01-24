package com.ModDamage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;

public class ModDamageTagger
{
	public static final String configString_save = "interval-save";
	public static final String configString_clean = "interval-clean";
	public static final int defaultInterval = 10 * 20;
	
	private final Map<String, Map<Entity, Integer>> entityTags = Collections.synchronizedMap(new HashMap<String, Map<Entity, Integer>>());
	private final Map<String, Map<OfflinePlayer, Integer>> playerTags = Collections.synchronizedMap(new HashMap<String, Map<OfflinePlayer, Integer>>());
	
	private long saveInterval;
	private long cleanInterval;
	private Integer saveTaskID;
	private Integer cleanTaskID;

	final File file;
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
					ModDamage.addToLogRecord(OutputPreset.INFO, "No tags file found at " + file.getAbsolutePath() + ", generating a new one...");
					if(!file.getParentFile().mkdirs() && !file.createNewFile())
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Couldn't make new tags file! Tags will not have persistence between reloads.");
				}
				reader = new FileInputStream(file);
				Object tagFileObject = yaml.load(reader);
				reader.close();
				if(tagFileObject != null)
				{
					if(tagFileObject instanceof Map)
					{
						Map<UUID, Entity> entities = new HashMap<UUID, Entity>();
						for(World world : Bukkit.getWorlds())
						{
							for (Entity entity : world.getEntities())
								if (!(entity instanceof OfflinePlayer))
									entities.put(entity.getUniqueId(), entity);
						}
						
						
						@SuppressWarnings("unchecked")
						Map<String, Object> tagMap = (Map<String, Object>)tagFileObject;
						for(Entry<String, Object> entry : tagMap.entrySet())
						{
							if(entry.getValue() instanceof Map)
							{
								HashMap<Entity, Integer> entityMap = new HashMap<Entity, Integer>();
								HashMap<OfflinePlayer, Integer> playerMap = new HashMap<OfflinePlayer, Integer>();
								
								@SuppressWarnings("unchecked")
								Map<String, Object> rawUuidMap = (Map<String, Object>)entry.getValue();
								for(Entry<String, Object> tagEntry : rawUuidMap.entrySet())
								{
									Integer integer = tagEntry.getValue() != null && tagEntry.getValue() instanceof Integer? 
											(Integer)tagEntry.getValue() : null;
									if (integer == null) 
									{
										ModDamage.addToLogRecord(OutputPreset.FAILURE, "Could not read value for entity UUID " + tagEntry.getKey() + " under tag \"" + tagEntry + "\".");
										continue;
									}
									
									if (tagEntry.getKey().startsWith("player:"))
										playerMap.put(Bukkit.getOfflinePlayer(tagEntry.getKey().substring(7)), integer);
									else
									{
										try
										{
											Entity entity = entities.get(UUID.fromString(tagEntry.getKey()));
											if (entity != null) entityMap.put(entity, integer);
										}
										catch (IllegalArgumentException e)
										{
											ModDamage.addToLogRecord(OutputPreset.FAILURE, "Could not read entity UUID " + tagEntry.getKey() + " under tag \"" + tagEntry + "\".");
										}
									}
								}
								if(!entityMap.isEmpty())
									entityTags.put(entry.getKey(), entityMap);
								if(!playerMap.isEmpty())
									playerTags.put(entry.getKey(), playerMap);
							}
							else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Could not read nested content under tag \"" + entry.getKey() + "\".");
						}
					}
					else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Incorrectly formatted tags.yml. Starting with an empty tag list.");
				}
			}
			catch(Exception e){ ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error loading tags.yml: "+e.toString());}
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
		
		saveTaskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(modDamage, new Runnable(){
			@Override public void run()
			{
				save();
			}
		}, saveInterval, saveInterval);
		
		cleanTaskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(modDamage, new Runnable(){
			@Override public void run()
			{
				cleanUp();
			}
		}, cleanInterval, cleanInterval);	
	}
	
	private boolean dirty = false;
	
	/**
	 * Saves all tags to a file.
	 */
	public synchronized void save()
	{
		if(file != null && dirty)
		{
			Map<String, HashMap<String, Integer>> tempMap = new HashMap<String, HashMap<String, Integer>>();
			for(Entry<String, Map<Entity, Integer>> tagEntry : entityTags.entrySet())
			{
				HashMap<String, Integer> savedEntities = new HashMap<String, Integer>();
				for(Entry<Entity, Integer> entry : tagEntry.getValue().entrySet())
					savedEntities.put(entry.getKey().getUniqueId().toString(), entry.getValue());
				tempMap.put(tagEntry.getKey(), savedEntities);
			}
			for(Entry<String, Map<OfflinePlayer, Integer>> tagEntry : playerTags.entrySet())
			{
				HashMap<String, Integer> savedPlayers = new HashMap<String, Integer>();
				for(Entry<OfflinePlayer, Integer> entry : tagEntry.getValue().entrySet())
					savedPlayers.put("player:"+entry.getKey().getName(), entry.getValue());
				tempMap.put(tagEntry.getKey(), savedPlayers);
			}
			try
			{
				writer = new FileWriter(file);
				writer.write(yaml.dump(tempMap));
				writer.close();
			}
			catch (IOException e){ PluginConfiguration.log.warning("Error writing to " + file.getAbsolutePath() + "!");}
		}
	}
	
//public API
	/**
	 * Add the entity's UUID to a tag. A new tag is made if it doesn't already exist.
	 */
	public synchronized void addTag(Entity entity, String tag, int tagValue)
	{
		if (entity instanceof OfflinePlayer) 
		{
			addTag((OfflinePlayer)entity, tag, tagValue);
			return;
		}
		dirty = true; // only need to save when dirty
		if(!entityTags.containsKey(tag))
			entityTags.put(tag, new HashMap<Entity, Integer>());
		entityTags.get(tag).put(entity, tagValue);
	}
	
	public synchronized void addTag(OfflinePlayer player, String tag, int tagValue)
	{
		dirty = true; // only need to save when dirty
		if(!playerTags.containsKey(tag))
			playerTags.put(tag, new HashMap<OfflinePlayer, Integer>());
		playerTags.get(tag).put(player, tagValue);
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
				removeTag(entity, tag);
		}
	}

	/**
	 * Checks if entity has been tagged with the specified tag.
	 * @return Boolean indicating whether or not the entity was tagged.
	 */
	public synchronized boolean isTagged(Entity entity, String tag)
	{
		if (entity instanceof OfflinePlayer) return isTagged((OfflinePlayer)entity, tag);
		return entityTags.containsKey(tag) && entityTags.get(tag).containsKey(entity.getUniqueId());
	}
	
	public synchronized boolean isTagged(OfflinePlayer player, String tag)
	{
		return playerTags.containsKey(tag) && playerTags.get(tag).containsKey(player);
	}
	
	/**
	 * @param entity - The entity whose UUID will be checked for tags.
	 * @return List of found tags.
	 */
	public synchronized List<String> getTags(Entity entity)
	{
		if (entity instanceof OfflinePlayer) return getTags((OfflinePlayer)entity);
		List<String> tagsList = new ArrayList<String>();
		for(Entry<String, Map<Entity, Integer>> entry : entityTags.entrySet())
			if(entry.getValue().containsKey(entity))
				tagsList.add(entry.getKey());
		return tagsList;
	}
	
	private synchronized List<String> getTags(OfflinePlayer player)
	{
		List<String> tagsList = new ArrayList<String>();
		for(Entry<String, Map<OfflinePlayer, Integer>> entry : playerTags.entrySet())
			if(entry.getValue().containsKey(player))
				tagsList.add(entry.getKey());
		return tagsList;
	}
	
	public synchronized Integer getTagValue(Entity entity, String tag)
	{
		if (entity instanceof OfflinePlayer) return getTagValue((OfflinePlayer)entity, tag);
		if(isTagged(entity, tag))
			return entityTags.get(tag).get(entity);
		return null;
	}
	
	public synchronized Integer getTagValue(OfflinePlayer player, String tag)
	{
		if(isTagged(player, tag))
			return playerTags.get(tag).get(player);
		return null;
	}
	
	/**
	 * Removes the entity's UUID from a tag, if {@link void generateTag(String tag) [generateTag]} was called correctly.	 * 
	 */
	public synchronized void removeTag(Entity entity, String tag)
	{
		if (entity instanceof OfflinePlayer) 
		{
			removeTag((OfflinePlayer)entity, tag);
			return;
		}
		if(entityTags.containsKey(tag))
			entityTags.get(tag).remove(entity.getEntityId());
	}
	
	public synchronized void removeTag(OfflinePlayer player, String tag)
	{
		if(playerTags.containsKey(tag))
			playerTags.get(tag).remove(player);
	}
	
	/**
	 * This method checks whether or not the number of tagged entities exceeds the number of entities in the server,
	 * and if so removes entities that no longer exist.
	 */
	public synchronized void cleanUp()
	{
		//clean up the entities
		Set<Entity> entities = new HashSet<Entity>();
		for(World world : Bukkit.getWorlds())
			entities.addAll(world.getEntities());
		for(Map<Entity, Integer> tagList : entityTags.values())
		{
			int oldSize = tagList.size();
			tagList.keySet().retainAll(entities);
			if (tagList.size() != oldSize) dirty = true;
		}
		// Don't clean up offline player tags
	}
	
	/**
	 * Only the ModDamage main should use this method.
	 */
	public synchronized void clear(){ entityTags.clear(); playerTags.clear(); }
	
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
	public LoadState getLoadState(){ return file != null? LoadState.SUCCESS : LoadState.NOT_LOADED;}
}