package com.ModDamage.Tags;

import com.ModDamage.ModDamage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.*;

public class WorldTags<T> implements ITags<T, World> {
    private final Map<World, Map<String, T>> tags = new HashMap<World, Map<String, T>>();

    public WorldTags() { }

    public void addTag(World world, String tag, T tagValue) {
        ModDamage.getTagger().dirty(); // only need to save when dirty
        Map<String, T> tags = this.tags.get(world);
        if (tags == null) {
            tags = new HashMap<String, T>();
            this.tags.put(world, tags);
        }
        tags.put(tag, tagValue);
    }

    public boolean isTagged(World world, String tag) {
        Map<String, T> tags = this.tags.get(world);
        return tags != null && tags.containsKey(tag);
    }

    public List<String> getTags(World world) {
        Map<String, T> map = tags.get(world);
        if (map != null)
            return new ArrayList<String>(map.keySet());
        return new ArrayList<String>();
    }

    public T getTagValue(World world, String tag) {
        Map<String, T> tags = this.tags.get(world);
        if (tags == null) return null;

        return tags.get(tag);
    }

    public void removeTag(World world, String tag) {
        if (tags.containsKey(world))
            tags.get(world).remove(tag);
    }

    public void clear() {
        tags.clear();
    }

    public void load(Map tagMap, Map<UUID, Entity> entities) {
        @SuppressWarnings("unchecked")
        Map<String, Map<String, T>> worldsMap = (Map<String, Map<String, T>>) tagMap.get("world");
        if (worldsMap != null)
        {
            for (Map.Entry<String, Map<String, T>> tagEntry : worldsMap.entrySet())
            {
                World world = Bukkit.getWorld(tagEntry.getKey());
                if (world != null)
                    tags.put(world, new HashMap<String, T>(tagEntry.getValue()));
            }
        }
    }

    public Map save(Set<Entity> entities) {
        Map<String, Map<String, T>> worldMap = new HashMap<String, Map<String, T>>();
        for (Map.Entry<World, Map<String, T>> worldEntry : tags.entrySet())
        {
            if (worldEntry.getValue().isEmpty()) continue;

            HashMap<String, T> savedTags = new HashMap<String, T>();
            for(Map.Entry<String, T> entry : worldEntry.getValue().entrySet())
                savedTags.put(entry.getKey(), entry.getValue());
            worldMap.put(worldEntry.getKey().getName(), savedTags);
        }
        return worldMap;
    }
}