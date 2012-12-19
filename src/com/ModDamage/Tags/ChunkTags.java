package com.ModDamage.Tags;

import com.ModDamage.ModDamage;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.*;

public class ChunkTags<T> implements ITags<T, Chunk> {
    private final Map<World, Map<Chunk, Map<String, T>>> tags = new HashMap<World, Map<Chunk, Map<String,T>>>();

    public ChunkTags() { }

    public Map<Chunk, Map<String, T>> getWorldChunkTags(World world, boolean create)
    {
        Map<Chunk, Map<String, T>> worldTags = this.tags.get(world);

        if (create && worldTags == null) {
            worldTags = new HashMap<Chunk, Map<String, T>>();
            this.tags.put(world, worldTags);
        }

        return worldTags;
    }

    public Map<String, T> getChunkTags(Chunk chunk, boolean create)
    {
        Map<Chunk, Map<String, T>> worldTags = getWorldChunkTags(chunk.getWorld(), create);
        if (worldTags == null) return null;

        Map<String, T> tags = worldTags.get(chunk);

        if (create && tags == null) {
            tags = new HashMap<String, T>();
            worldTags.put(chunk, tags);
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
        for (Map.Entry<World, Map<Chunk, Map<String, T>>> worldEntry : tags.entrySet())
        {
            if (worldEntry.getValue().isEmpty()) continue;

            HashMap<String, Map<String, T>> savedWorldTags = new HashMap<String, Map<String, T>>();
            for(Map.Entry<Chunk, Map<String, T>> chunkEntry : worldEntry.getValue().entrySet())
            {
                if (chunkEntry.getValue().isEmpty()) continue;

                HashMap<String, T> savedChunkTags = new HashMap<String, T>();
                for(Map.Entry<String, T> entry : chunkEntry.getValue().entrySet())
                    savedChunkTags.put(entry.getKey(), entry.getValue());

                Chunk chunk = chunkEntry.getKey();
                savedWorldTags.put(chunk.getX() + ":" + chunk.getZ(), savedChunkTags);
            }
            chunkMap.put(worldEntry.getKey().getName(), savedWorldTags);
        }
        return chunkMap;
    }
}