package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.entity.Entity;
import org.yaml.snakeyaml.Yaml;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public class ModDamageTag
{
	private final static LinkedHashMap<String, HashSet<Integer>> tags = new LinkedHashMap<String, HashSet<Integer>>();
	private static Yaml yaml = null;
	private static OutputStream writer = null;
	private static InputStream reader = null;

	public static void reload(File file)
	{
		tags.clear();
		/*
		if(file != null)
		{
			if(!file.exists())
			{
				ModDamage.addToLogRecord(DebugSetting.NORMAL, "No tags file found, generating a new one...", LoadState.NOT_LOADED);
				if(!file.createNewFile())
				{
					ModDamage.addToLogRecord(DebugSetting.NORMAL, "Couldn't make new file! Whoops.", LoadState.FAILURE);
				}
				writer = new FileOutputStream(file);
			}
			if(file.canRead())
			{
				
			}
			else
			{
				
			}
			
			if(file.canWrite())
			{
				
			}
			else
			{
				
			}
		}
		else
		{
			
		}
		*/
	}
	
	public static void generateTag(String tag)
	{
		if(!tags.containsKey(tag))
			tags.put(tag, new HashSet<Integer>());
	}
	
	public static void addTag(String tag, Entity entity)
	{
		if(tags.containsKey(tag))
		{
			if(!tags.get(tag).contains(entity.getEntityId()))
				tags.get(tag).add(entity.getEntityId());
		}
		else ModDamage.log.info("FATAL ERROR: for some reason tag \"" + tag + "\" doesn't already exist. (add)");
	}
	
	public static void addTag(String tag, Entity entity, long tagDuration)
	{
		addTag(tag, entity);
	}
	
	public static void removeTag(String tag, Entity entity)
	{
		if(tags.containsKey(tag))
			tags.get(tag).remove(entity.getEntityId());
		else ModDamage.log.info("FATAL ERROR: for some reason tag \"" + tag + "\" doesn't already exist. (remove)");
	}
	
	public static void cleanUp()
	{
		for(String tag : tags.keySet())
			for(Integer id : tags.get(tag));//FIXME
	}
	
	public static List<String> getTags(Entity entity)
	{
		List<String> entityTags = new ArrayList<String>();
		int id = entity.getEntityId();
		for(String tag : tags.keySet())
			if(tags.get(tag).contains(id))
				entityTags.add(tag);
		return entityTags;
	}
}
