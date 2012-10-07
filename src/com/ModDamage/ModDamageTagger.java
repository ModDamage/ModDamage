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
	
	public final class TagsHolder<T>
	{

		private final Map<String, Map<Entity, T>> entityTags = new HashMap<String, Map<Entity, T>>();
		private final Map<String, Map<String, T>> playerTags = new HashMap<String, Map<String, T>>();
		
		/**
		 * World tags are stored differently because there are usually very few worlds
		 * and potentially many tags.
		 */
		private final Map<World, Map<String, T>> worldTags = new HashMap<World, Map<String, T>>();
		
		
		
		
		//public API
		/**
		 * Add the entity's UUID to a tag. A new tag is made if it doesn't already exist.
		 */
		public void addTag(Entity entity, String tag, T tagValue)
		{
			if (entity instanceof OfflinePlayer) 
			{
				addTag((OfflinePlayer)entity, tag, tagValue);
				return;
			}
			dirty = true; // only need to save when dirty
			if(!entityTags.containsKey(tag))
				entityTags.put(tag, new WeakHashMap<Entity, T>());
			entityTags.get(tag).put(entity, tagValue);
		}
		
		public void addTag(OfflinePlayer player, String tag, T tagValue)
		{
			dirty = true; // only need to save when dirty
			if(!playerTags.containsKey(tag))
				playerTags.put(tag, new HashMap<String, T>());
			playerTags.get(tag).put(player.getName(), tagValue);
		}
		
		public void addTag(World world, String tag, T tagValue)
		{
			dirty = true; // only need to save when dirty
			if(!worldTags.containsKey(world))
				worldTags.put(world, new HashMap<String, T>());
			worldTags.get(world).put(tag, tagValue);
		}
		

		/**
		 * Checks if entity has been tagged with the specified tag.
		 * @return Boolean indicating whether or not the entity was tagged.
		 */
		public boolean isTagged(Entity entity, String tag)
		{
			if (entity instanceof OfflinePlayer) return isTagged((OfflinePlayer)entity, tag);
			return entityTags.containsKey(tag) && entityTags.get(tag).get(entity) != null;
		}
		
		public boolean isTagged(OfflinePlayer player, String tag)
		{
			return playerTags.containsKey(tag) && playerTags.get(tag).get(player.getName()) != null;
		}
		
		public boolean isTagged(World world, String tag)
		{
			return worldTags.containsKey(world) && worldTags.get(world).get(tag) != null;
		}
		
		/**
		 * @param entity - The entity whose UUID will be checked for tags.
		 * @return List of found tags.
		 */
		public List<String> getTags(Entity entity)
		{
			if (entity instanceof OfflinePlayer) return getTags((OfflinePlayer)entity);
			List<String> tagsList = new ArrayList<String>();
			for(Entry<String, Map<Entity, T>> entry : entityTags.entrySet())
				if(entry.getValue().containsKey(entity))
					tagsList.add(entry.getKey());
			return tagsList;
		}
		
		private List<String> getTags(OfflinePlayer player)
		{
			List<String> tagsList = new ArrayList<String>();
			for(Entry<String, Map<String, T>> entry : playerTags.entrySet())
				if(entry.getValue().containsKey(player.getName()))
					tagsList.add(entry.getKey());
			return tagsList;
		}
		
		public List<String> getTags(World world)
		{
			Map<String, T> map = worldTags.get(world);
			if (map != null)
				return new ArrayList<String>(map.keySet());
			return null;
		}
		
		public T getTagValue(Entity entity, String tag)
		{
			if (entity instanceof OfflinePlayer) return getTagValue((OfflinePlayer)entity, tag);
			Map<Entity, T> tags = entityTags.get(tag);
			if (tags == null) return null;
			
			return tags.get(entity);
		}
		
		public T getTagValue(OfflinePlayer player, String tag)
		{
			Map<String, T> tags = playerTags.get(tag);
			if (tags == null) return null;
			
			return tags.get(player.getName());
		}
		
		public T getTagValue(World world, String tag)
		{
			Map<String, T> tags = worldTags.get(world);
			if (tags == null) return null;
			
			return tags.get(tag);
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
				playerTags.get(tag).remove(player.getName());
		}
		
		public void removeTag(World world, String tag)
		{
			if(worldTags.containsKey(world))
				worldTags.get(world).remove(tag);
		}
		
		/*
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
		}*/
		
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
		
		
		
		public void loadTags(Map<String, Object> tagMap, Map<UUID, Entity> entities) {
			@SuppressWarnings("unchecked")
			Map<String, Map<String, T>> entitiesMap = (Map<String, Map<String, T>>) tagMap.get("entity");
			if (entitiesMap != null)
			{
				for (Entry<String, Map<String, T>> tagEntry : entitiesMap.entrySet())
				{
					Map<Entity, T> taggedEntities = new HashMap<Entity, T>(tagEntry.getValue().size());
					entityTags.put(tagEntry.getKey(), taggedEntities);
						
					for (Entry<String, T> entry : tagEntry.getValue().entrySet())
					{
						Entity entity = entities.get(UUID.fromString(entry.getKey()));
						if (entity != null)
							taggedEntities.put(entity, entry.getValue());
					}
				}
			}
			
			@SuppressWarnings("unchecked")
			Map<String, Map<String, T>> playersMap = (Map<String, Map<String, T>>) tagMap.get("player");
			if (playersMap != null)
			{
				for (Entry<String, Map<String, T>> tagEntry : playersMap.entrySet())
				{
					Map<String, T> pmap = new HashMap<String, T>(tagEntry.getValue().size());
					
					for (Entry<String, T> pentry : tagEntry.getValue().entrySet())
					{
						pmap.put(pentry.getKey(), pentry.getValue());
					}
					
					playerTags.put(tagEntry.getKey(), pmap);
				}
			}
			
			@SuppressWarnings("unchecked")
			Map<String, Map<String, T>> worldsMap = (Map<String, Map<String, T>>) tagMap.get("world");
			if (worldsMap != null)
			{
				for (Entry<String, Map<String, T>> tagEntry : worldsMap.entrySet())
				{
					World world = Bukkit.getWorld(tagEntry.getKey());
					if (world != null)
						worldTags.put(world, new HashMap<String, T>(tagEntry.getValue()));
				}
			}
		}
		
		
		public Map<String, Object> saveTags(Set<Entity> entities) {
			Map<String, Map<String, T>> entityMap = new HashMap<String, Map<String, T>>();
			for(Entry<String, Map<Entity, T>> tagEntry : entityTags.entrySet())
			{
				HashMap<String, T> savedEntities = new HashMap<String, T>();
				
				tagEntry.getValue().keySet().retainAll(entities); // simple cleanup operation, it might not even be necessary
				
				if (tagEntry.getValue().isEmpty()) continue;
				
				for(Entry<Entity, T> entry : tagEntry.getValue().entrySet())
					savedEntities.put(entry.getKey().getUniqueId().toString(), entry.getValue());
				
				if (!savedEntities.isEmpty())
					entityMap.put(tagEntry.getKey(), savedEntities);
			}
			
			Map<String, Map<String, T>> playerMap = new HashMap<String, Map<String, T>>();
			for(Entry<String, Map<String, T>> tagEntry : playerTags.entrySet())
			{
				HashMap<String, T> savedPlayers = new HashMap<String, T>();
				
				if (tagEntry.getValue().isEmpty()) continue;
				
				for(Entry<String, T> entry : tagEntry.getValue().entrySet())
					savedPlayers.put(entry.getKey(), entry.getValue());
				
				if (!savedPlayers.isEmpty())
					playerMap.put(tagEntry.getKey(), savedPlayers);
			}
			
			Map<String, Map<String, T>> worldMap = new HashMap<String, Map<String, T>>();
			for (Entry<World, Map<String, T>> worldEntry : worldTags.entrySet())
			{
				if (worldEntry.getValue().isEmpty()) continue;
				
				HashMap<String, T> savedTags = new HashMap<String, T>();
				for(Entry<String, T> entry : worldEntry.getValue().entrySet())
					savedTags.put(entry.getKey(), entry.getValue());
				worldMap.put(worldEntry.getKey().getName(), savedTags);
			}
			
			
			Map<String, Object> saveMap = new HashMap<String, Object>();
			
			saveMap.put("entity", entityMap);
			saveMap.put("player", playerMap);
			saveMap.put("world", worldMap);
			
			return saveMap;
		}
	}
	
	public final TagsHolder<Integer> intTags = new TagsHolder<Integer>();
	public final TagsHolder<String> stringTags = new TagsHolder<String>();
	
	
	private long saveInterval;
	//private long cleanInterval;
	private int saveTaskID;
	private int cleanTaskID;

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
				if(saveTaskID != 0) Bukkit.getScheduler().cancelTask(saveTaskID);
				if(cleanTaskID != 0) Bukkit.getScheduler().cancelTask(cleanTaskID);
			}
		}
		Plugin modDamage = Bukkit.getPluginManager().getPlugin("ModDamage");
		
		saveTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(modDamage, new Runnable(){
			@Override public void run()
			{
				save();
			}
		}, saveInterval, saveInterval);
	}
	
	private boolean dirty = false;
	
	@SuppressWarnings("unchecked")
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
			
			Map<String, Object> tagMap = (Map<String, Object>)tagFileObject;
			
			if (!tagMap.containsKey("int")) // Old style tags.yml
			{
				intTags.loadTags(tagMap, entities);
				save(); // upgrade the file
			}
			else // New way
			{
				intTags.loadTags((Map<String, Object>) tagMap.get("int"), entities);
				stringTags.loadTags((Map<String, Object>) tagMap.get("string"), entities);
			}
		}
		catch(Exception e){ ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error loading tags: "+e.toString()); }
	}
	
	
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

			
			Map<String, Object> saveMap = new HashMap<String, Object>();
			
			saveMap.put("tagsVersion", 2);
			saveMap.put("int", intTags.saveTags(entities));
			saveMap.put("string", stringTags.saveTags(entities));
			
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

	public void clear()
	{
		intTags.clear();
		stringTags.clear();
	}
}