package com.ModDamage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

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
	
	private final Map<String, Map<Entity, Integer>> entityTags = new HashMap<String, Map<Entity, Integer>>();
	private final Map<String, Map<String, Integer>> playerTags = new HashMap<String, Map<String, Integer>>();
	
	/**
	 * World tags are stored differently because there are usually very few worlds
	 * and potentially many tags.
	 */
	private final Map<World, Map<String, Integer>> worldTags = new HashMap<World, Map<String, Integer>>();
	
	
	private long saveInterval;
	//private long cleanInterval;
	private Integer saveTaskID;
	private Integer cleanTaskID;

	public final File file;
	public final File newFile;
	public final File oldFile;
	private InputStream reader = null;
	private FileWriter writer = null;
	private Yaml yaml = new Yaml();

	public ModDamageTagger(File file, long saveInterval, long cleanInterval)
	{
		this.saveInterval = saveInterval;
		//this.cleanInterval = cleanInterval;
		this.file = file;
		newFile = new File(file.getParent(), file.getName()+".new");
		oldFile = new File(file.getParent(), file.getName()+".old");
		
		
		load();

		reload(false);
	}
	
	public void reload(){ reload(true); }
	
	private void reload(boolean initialized)
	{
		//cleanUp();
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
	}
	
	private boolean dirty = false;
	
	public void load()
	{
		try
		{
			if(!file.exists())
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "No tags file found at " + file.getAbsolutePath() + ", generating a new one...");
				if(!file.createNewFile())
				{
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Couldn't make new tags file! Tags will not have persistence between reloads.");
					return;
				}
			}
			reader = new FileInputStream(file);
			Object tagFileObject = yaml.load(reader);
			reader.close();
			if(tagFileObject == null || !(tagFileObject instanceof Map)) return;
			
			Map<UUID, Entity> entities = new HashMap<UUID, Entity>();
			for(World world : Bukkit.getWorlds())
			{
				for (Entity entity : world.getEntities())
					if (!(entity instanceof OfflinePlayer))
						entities.put(entity.getUniqueId(), entity);
			}
			
			@SuppressWarnings("unchecked")
			Map<String, Object> tagMap = (Map<String, Object>)tagFileObject;
			
			
			// Backwards compatibility - remove eventually
			if (!tagMap.containsKey("tagsVersion"))
			{
				oldLoad(entities, tagMap);
				return;
			}
			// End Backwards compatibility - remove eventually
			
			
			@SuppressWarnings("unchecked")
			Map<String, Map<String, Integer>> entitiesMap = (Map<String, Map<String, Integer>>) tagMap.get("entity");
			if (entitiesMap != null)
			{
				for (Entry<String, Map<String, Integer>> tagEntry : entitiesMap.entrySet())
				{
					Map<Entity, Integer> taggedEntities = new HashMap<Entity, Integer>(tagEntry.getValue().size());
					entityTags.put(tagEntry.getKey(), taggedEntities);
						
					for (Entry<String, Integer> entry : tagEntry.getValue().entrySet())
					{
						Entity entity = entities.get(UUID.fromString(entry.getKey()));
						if (entity != null)
							taggedEntities.put(entity, entry.getValue());
					}
				}
			}
			
			@SuppressWarnings("unchecked")
			Map<String, Map<String, Integer>> playersMap = (Map<String, Map<String, Integer>>) tagMap.get("player");
			if (playersMap != null)
			{
				for (Entry<String, Map<String, Integer>> tagEntry : playersMap.entrySet())
				{
					playerTags.put(tagEntry.getKey(), tagEntry.getValue());
				}
			}
			
			@SuppressWarnings("unchecked")
			Map<String, Map<String, Integer>> worldsMap = (Map<String, Map<String, Integer>>) tagMap.get("world");
			if (worldsMap != null)
			{
				for (Entry<String, Map<String, Integer>> tagEntry : worldsMap.entrySet())
				{
					World world = Bukkit.getWorld(tagEntry.getKey());
					if (world != null)
						worldTags.put(world, new HashMap<String, Integer>(tagEntry.getValue()));
				}
			}
		}
		catch(Exception e){ ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error loading tags: "+e.toString()); }
	}
	
	// Backwards compatibility - remove eventually
	public void oldLoad(Map<UUID, Entity> entities, Map<String, Object> tagMap)
	{
		for(Entry<String, Object> entry : tagMap.entrySet())
		{
			if(!(entry.getValue() instanceof Map))
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Could not read nested content under tag \"" + entry.getKey() + "\".");
				continue;
			}
			
			Map<Entity, Integer> entityMap = new WeakHashMap<Entity, Integer>();
			Map<String, Integer> playerMap = new HashMap<String, Integer>();
			
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
					playerMap.put(tagEntry.getKey().substring(7), integer);
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
	}
	// End Backwards compatibility - remove eventually
	
	/**
	 * Saves all tags to a file.
	 */
	public void save()
	{
		if(file != null && dirty)
		{
			Set<Entity> entities = new HashSet<Entity>();
			for (World world : Bukkit.getWorlds())
				entities.addAll(world.getEntities());

			Map<String, Map<String, Integer>> entityMap = new HashMap<String, Map<String, Integer>>();
			for(Entry<String, Map<Entity, Integer>> tagEntry : entityTags.entrySet())
			{
				HashMap<String, Integer> savedEntities = new HashMap<String, Integer>();
				
				tagEntry.getValue().keySet().retainAll(entities); // simple cleanup operation, it might not even be necessary
				
				if (tagEntry.getValue().isEmpty()) continue;
				
				for(Entry<Entity, Integer> entry : tagEntry.getValue().entrySet())
					savedEntities.put(entry.getKey().getUniqueId().toString(), entry.getValue());
				
				if (!savedEntities.isEmpty())
					entityMap.put(tagEntry.getKey(), savedEntities);
			}
			
			Map<String, Map<String, Integer>> worldMap = new HashMap<String, Map<String, Integer>>();
			for (Entry<World, Map<String, Integer>> worldEntry : worldTags.entrySet())
			{
				if (worldEntry.getValue().isEmpty()) continue;
				
				HashMap<String, Integer> savedTags = new HashMap<String, Integer>();
				for(Entry<String, Integer> entry : worldEntry.getValue().entrySet())
					savedTags.put(entry.getKey(), entry.getValue());
				worldMap.put(worldEntry.getKey().getName(), savedTags);
			}
			
			
			Map<String, Object> saveMap = new HashMap<String, Object>();
			
			saveMap.put("tagsVersion", 1);
			saveMap.put("entity", entityMap);
			saveMap.put("player", playerTags);
			saveMap.put("world", worldMap);
			
			try
			{
				writer = new FileWriter(newFile);
				writer.write(yaml.dump(saveMap));
				writer.close();
			}
			catch (IOException e){
				PluginConfiguration.log.warning("Error saving tags at " + newFile.getAbsolutePath() + "!");
				return;
			}
			
			oldFile.delete();
			file.renameTo(oldFile);
			newFile.renameTo(file);
		}
	}
	
//public API
	/**
	 * Add the entity's UUID to a tag. A new tag is made if it doesn't already exist.
	 */
	public void addTag(Entity entity, String tag, int tagValue)
	{
		if (entity instanceof OfflinePlayer) 
		{
			addTag((OfflinePlayer)entity, tag, tagValue);
			return;
		}
		dirty = true; // only need to save when dirty
		if(!entityTags.containsKey(tag))
			entityTags.put(tag, new WeakHashMap<Entity, Integer>());
		entityTags.get(tag).put(entity, tagValue);
	}
	
	public void addTag(OfflinePlayer player, String tag, int tagValue)
	{
		dirty = true; // only need to save when dirty
		if(!playerTags.containsKey(tag))
			playerTags.put(tag, new HashMap<String, Integer>());
		playerTags.get(tag).put(player.getName(), tagValue);
	}
	
	public void addTag(World world, String tag, int tagValue)
	{
		dirty = true; // only need to save when dirty
		if(!worldTags.containsKey(world))
			worldTags.put(world, new HashMap<String, Integer>());
		worldTags.get(world).put(tag, tagValue);
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
	public boolean isTagged(Entity entity, String tag)
	{
		if (entity instanceof OfflinePlayer) return isTagged((OfflinePlayer)entity, tag);
		return entityTags.containsKey(tag) && entityTags.get(tag).containsKey(entity);
	}
	
	public boolean isTagged(OfflinePlayer player, String tag)
	{
		return playerTags.containsKey(tag) && playerTags.get(tag).containsKey(player);
	}
	
	public boolean isTagged(World world, String tag)
	{
		return worldTags.containsKey(world) && worldTags.get(world).containsKey(tag);
	}
	
	/**
	 * @param entity - The entity whose UUID will be checked for tags.
	 * @return List of found tags.
	 */
	public List<String> getTags(Entity entity)
	{
		if (entity instanceof OfflinePlayer) return getTags((OfflinePlayer)entity);
		List<String> tagsList = new ArrayList<String>();
		for(Entry<String, Map<Entity, Integer>> entry : entityTags.entrySet())
			if(entry.getValue().containsKey(entity))
				tagsList.add(entry.getKey());
		return tagsList;
	}
	
	private List<String> getTags(OfflinePlayer player)
	{
		List<String> tagsList = new ArrayList<String>();
		for(Entry<String, Map<String, Integer>> entry : playerTags.entrySet())
			if(entry.getValue().containsKey(player.getName()))
				tagsList.add(entry.getKey());
		return tagsList;
	}
	
	public List<String> getTags(World world)
	{
		Map<String, Integer> map = worldTags.get(world);
		if (map != null)
			return new ArrayList<String>(map.keySet());
		return null;
	}
	
	public Integer getTagValue(Entity entity, String tag)
	{
		if (entity instanceof OfflinePlayer) return getTagValue((OfflinePlayer)entity, tag);
		if(isTagged(entity, tag))
			return entityTags.get(tag).get(entity);
		return null;
	}
	
	public Integer getTagValue(OfflinePlayer player, String tag)
	{
		if(isTagged(player, tag))
			return playerTags.get(tag).get(player);
		return null;
	}
	
	public Integer getTagValue(World world, String tag)
	{
		if(isTagged(world, tag))
			return worldTags.get(world).get(tag);
		return null;
	}
	
	/**
	 * Removes the entity's UUID from a tag, if {@link void generateTag(String tag) [generateTag]} was called correctly.	 * 
	 */
	public void removeTag(Entity entity, String tag)
	{
		if (entity instanceof OfflinePlayer) 
		{
			removeTag((OfflinePlayer)entity, tag);
			return;
		}
		if(entityTags.containsKey(tag))
			entityTags.get(tag).remove(entity);
	}
	
	public void removeTag(OfflinePlayer player, String tag)
	{
		if(playerTags.containsKey(tag))
			playerTags.get(tag).remove(player);
	}
	
	public void removeTag(World world, String tag)
	{
		if(worldTags.containsKey(world))
			worldTags.get(world).remove(tag);
	}
	
	/**
	 * This method checks whether or not the number of tagged entities exceeds the number of entities in the server,
	 * and if so removes entities that no longer exist.
	 */
	/*public synchronized void cleanUp()
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
	}*/
	
	/**
	 * Only the ModDamage main should use this method.
	 */
	public void clear(){
		entityTags.clear();
		playerTags.clear();
		worldTags.clear();
	}
	
	/**
	 * This is used in the ModDamage main to finish any file IO.
	 */
	public void close()
	{
		//cleanUp();
		save();
	}
	
	/**
	 * @return LoadState reflecting the file's load state.
	 */
	public LoadState getLoadState(){ return file != null? LoadState.SUCCESS : LoadState.NOT_LOADED; }
}