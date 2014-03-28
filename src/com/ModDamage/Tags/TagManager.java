package com.ModDamage.Tags;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;

import com.ModDamage.ModDamage;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import com.ModDamage.LogUtil;
import com.ModDamage.BaseConfig.LoadState;

public class TagManager
{
//	public static final String configString_save = "interval-save";
	public static final int defaultInterval = 10 * 20;

    public final TagsHolder<Number> numTags = new TagsHolder<Number>();
	public final TagsHolder<String> stringTags = new TagsHolder<String>();
	
	
	private long saveInterval;
	//private long cleanInterval;
	private int saveTaskID;
//	private int cleanTaskID;

	public final File file;
	public final File newFile;
	public final File oldFile;
	private InputStream reader = null;
	private FileWriter writer = null;
	private Yaml yaml = new Yaml();

	public TagManager(File file, long saveInterval)
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
		save();
		if(initialized)
		{
			if(file != null)
			{
				if(saveTaskID != 0) Bukkit.getScheduler().cancelTask(saveTaskID);
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
    public void dirty() { dirty = true; }
	
	@SuppressWarnings("unchecked")
	public void load()
	{
		try
		{
			if(!file.exists())
			{
				LogUtil.info("No tags file found at " + file.getAbsolutePath() + ", generating a new one...");
				if(!file.createNewFile())
				{
					LogUtil.error("Couldn't make new tags file! Tags will not have persistence between reloads.");
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
			
			if (!tagMap.containsKey("string")) // Old style tags.yml
			{
				numTags.loadTags(tagMap, entities);
				save(); // upgrade the file
			}
			else // New way
			{
				if (tagMap.containsKey("int"))
					numTags.loadTags((Map<String, Object>) tagMap.get("int"), entities);
				else
					numTags.loadTags((Map<String, Object>) tagMap.get("num"), entities);
				stringTags.loadTags((Map<String, Object>) tagMap.get("string"), entities);
			}
		}
		catch(Exception e){ LogUtil.error("Error loading tags: "+e.toString()); }
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
			saveMap.put("num", numTags.saveTags(entities));
			saveMap.put("string", stringTags.saveTags(entities));
			
			try
			{
				writer = new FileWriter(newFile);
				writer.write(yaml.dump(saveMap));
				writer.close();
			}
			catch (IOException e){
				ModDamage.printToLog(Level.WARNING, "Error saving tags at " + newFile.getAbsolutePath() + "!");
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
		numTags.clear();
		stringTags.clear();
	}
}