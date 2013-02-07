package com.ModDamage.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;

public class ChunkTags<T> implements ITags<T, Chunk> {
    private final Map<World, Map<String, Map<String, T>>> tags = new HashMap<World, Map<String, Map<String,T>>>();

    public ChunkTags() { }

    public Map<String, Map<String, T>> getWorldChunkTags(World world, boolean create)
    {
        Map<String, Map<String, T>> worldTags = this.tags.get(world);

        if (create && worldTags == null) {
            worldTags = new HashMap<String, Map<String, T>>();
            this.tags.put(world, worldTags);
        }

        return worldTags;
    }

    public Map<String, T> getChunkTags(Chunk chunk, boolean create)
    {
        Map<String, Map<String, T>> worldTags = getWorldChunkTags(chunk.getWorld(), create);
        if (worldTags == null) return null;
        
        String chunkKey = chunk.getX() + ":" + chunk.getZ();

        Map<String, T> tags = worldTags.get(chunkKey);

        if (create && tags == null) {
            tags = new HashMap<String, T>();
            worldTags.put(chunkKey, tags);
        }

        return tags;
    }

    public void addTag(Chunk chunk, String tag, T tagValue) {
        ModDamage.getTagger().dirty(); // only need to save when dirty
        Map<String, T> tags = getChunkTags(chunk, true);
        tags.put(tag, tagValue);
    }

    public boolean isTagged(Chunk chunk, String tag) {
        Map<String, T> tags = getChunkTags(chunk, false);
        return tags != null && tags.containsKey(tag);
    }

    public List<String> getTags(Chunk chunk) {
        Map<String, T> tags = getChunkTags(chunk, false);
        if (tags != null)
            return new ArrayList<String>(tags.keySet());
        return new ArrayList<String>();
    }
    
    public Map<Chunk, T> getAllTagged(String tag) {
    	Map<Chunk, T> tagged = new HashMap<Chunk, T>();
    	
    	for (Entry<World, Map<String, Map<String, T>>> worldEntry : tags.entrySet())
		{
    		World world = worldEntry.getKey();
    		
			for (Entry<String, Map<String, T>> chunkEntry : worldEntry.getValue().entrySet())
			{
				T t = chunkEntry.getValue().get(tag);
				
				if (t != null) {
	                String[] parts = chunkEntry.getKey().split(":");
	                if (parts.length != 2) continue;

	                int x = Integer.parseInt(parts[0]);
	                int z = Integer.parseInt(parts[1]);
	                
					tagged.put(world.getChunkAt(x, z), t);
				}
			}
		}
    	
    	return tagged;
    }

    public T getTagValue(Chunk chunk, String tag) {
        Map<String, T> tags = getChunkTags(chunk, false);
        if (tags == null) return null;

        return tags.get(tag);
    }

    public void removeTag(Chunk chunk, String tag) {
        Map<String, T> tags = getChunkTags(chunk, false);

        if (tags != null)
            tags.remove(tag);
    }

    public void clear() {
        tags.clear();
    }

    @SuppressWarnings("rawtypes")
	public void load(Map tagMap, Map<UUID, Entity> entities) {
        if (tagMap == null) return;

        @SuppressWarnings("unchecked")
        Map<String, Map<String, Map<String, T>>> chunksMap = (Map<String, Map<String, Map<String, T>>>) tagMap;
        for (Map.Entry<String, Map<String, Map<String, T>>> worldEntry : chunksMap.entrySet())
        {
            World world = Bukkit.getWorld(worldEntry.getKey());
            if (world == null) continue;

            //Map<Chunk, Map<String, T>> worldTags = getWorldChunkTags(world, true);

            for (Map.Entry<String, Map<String, T>> chunkEntry : worldEntry.getValue().entrySet())
            {
                String[] parts = chunkEntry.getKey().split(":");
                if (parts.length != 2) continue;

                int x = Integer.parseInt(parts[0]);
                int z = Integer.parseInt(parts[1]);

                Chunk chunk = world.getChunkAt(x, z);
                if (chunk == null) continue;

                Map<String, T> chunkTags = getChunkTags(chunk, true);

                for (Map.Entry<String, T> tagEntry : chunkEntry.getValue().entrySet())
                    chunkTags.put(tagEntry.getKey(), tagEntry.getValue());
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public Map save(Set<Entity> entities) {
        Map<String, Map<String, Map<String, T>>> chunkMap = new HashMap<String, Map<String, Map<String, T>>>();
        for (Map.Entry<World, Map<String, Map<String, T>>> worldEntry : tags.entrySet())
        {
            if (worldEntry.getValue().isEmpty()) continue;

            HashMap<String, Map<String, T>> savedWorldTags = new HashMap<String, Map<String, T>>();
            for(Map.Entry<String, Map<String, T>> chunkEntry : worldEntry.getValue().entrySet())
            {
                if (chunkEntry.getValue().isEmpty()) continue;

                HashMap<String, T> savedChunkTags = new HashMap<String, T>();
                for(Map.Entry<String, T> entry : chunkEntry.getValue().entrySet())
                    savedChunkTags.put(entry.getKey(), entry.getValue());

                String chunkKey = chunkEntry.getKey();
                savedWorldTags.put(chunkKey, savedChunkTags);
            }
            chunkMap.put(worldEntry.getKey().getName(), savedWorldTags);
        }
        return chunkMap;
    }
}