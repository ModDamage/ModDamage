package com.ModDamage.Tags;

import com.ModDamage.ModDamage;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockVector;

import java.util.*;

public class LocationTags<T> implements ITags<T, Location> {

    private final Map<String, Map<String, Map<BlockVector, T>>> tags = new HashMap<String, Map<String, Map<BlockVector, T>>>();

    LocationTags() {}

    private Map<String, Map<BlockVector, T>> getTags(String worldName, boolean create) {
        Map<String, Map<BlockVector, T>> tags1 = tags.get(worldName);
        if (tags1 == null && create) {
            tags1 = new HashMap<String, Map<BlockVector, T>>();
            tags.put(worldName, tags1);
        }
        return tags1;
    }

    private Map<BlockVector, T> getWorldTags(String tag, String worldName, boolean create) {
        Map<String, Map<BlockVector, T>> tags1 = getTags(worldName, create);
        if (tags1 == null) return null;

        Map<BlockVector, T> tags2 = tags1.get(tag);
        if (tags2 == null && create) {
            tags2 = new HashMap<BlockVector, T>();
            tags1.put(tag, tags2);
        }
        return tags2;
    }

    private BlockVector toBlockVector(Location loc) {
        return new BlockVector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    /**
     * Tag the thing. A new tag is made if it doesn't already exist.
     */
    public void addTag(Location loc, String tag, T tagValue) {
        ModDamage.getTagger().dirty(); // only need to save when dirty
        getWorldTags(tag, loc.getWorld().getName(), true).put(toBlockVector(loc), tagValue);
    }

    /**
     * Checks if thing has been tagged with the specified tag.
     *
     * @return Boolean indicating whether or not the thing was tagged.
     */
    public boolean isTagged(Location loc, String tag) {
        Map<BlockVector, T> tags2 = getWorldTags(tag, loc.getWorld().getName(), false);
        if (tags2 == null) return false;

        return tags2.containsKey(toBlockVector(loc));
    }

    public List<String> getTags(Location loc) {
        String worldName = loc.getWorld().getName();

        List<String> tagsList = new ArrayList<String>();
        for (Map.Entry<String, Map<String, Map<BlockVector, T>>> entry : tags.entrySet()) {
            Map<BlockVector, T> worldBlockTags = entry.getValue().get(worldName);
            if (worldBlockTags == null) continue;

            if (worldBlockTags.containsKey(loc))
                tagsList.add(entry.getKey());
        }
        return tagsList;
    }

    public T getTagValue(Location loc, String tag) {
        Map<BlockVector, T> tags2 = getWorldTags(tag, loc.getWorld().getName(), false);
        if (tags2 == null) return null;

        return tags2.get(toBlockVector(loc));
    }

    /**
     * Deletes the thing's tag
     */

    public void removeTag(Location loc, String tag) {
        Map<BlockVector, T> tags2 = getWorldTags(tag, loc.getWorld().getName(), false);
        if (tags2 == null) return;

        tags2.remove(toBlockVector(loc));
    }

    public void clear() {
        tags.clear();
    }

    public void load(Map tagMap, Map<UUID, Entity> entities) {
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Map<String, T>>> locationMap = (Map<String, Map<String, Map<String, T>>>) tagMap;
        if (locationMap != null)
        {
            for (Map.Entry<String, Map<String, Map<String, T>>> tagEntry : locationMap.entrySet())
            {
                Map<String, Map<BlockVector, T>> worldMap = new HashMap<String, Map<BlockVector, T>>(tagEntry.getValue().size());

                for (Map.Entry<String, Map<String, T>> worldEntry : tagEntry.getValue().entrySet())
                {
                    Map<BlockVector, T> locMap = new HashMap<BlockVector, T>(worldEntry.getValue().size());

                    for (Map.Entry<BlockVector, T> locEntry : locMap.entrySet())
                        locMap.put(locEntry.getKey(), locEntry.getValue());

                    worldMap.put(worldEntry.getKey(), locMap);
                }

                tags.put(tagEntry.getKey(), worldMap);
            }
        }
    }

    public Map save(Set<Entity> entities) {
        Map<String, Map<String, Map<String, T>>> locationMap = new HashMap<String, Map<String, Map<String, T>>>();
        for(Map.Entry<String, Map<String, Map<BlockVector, T>>> tagEntry : tags.entrySet())
        {
            if (tagEntry.getValue().isEmpty()) continue;

            HashMap<String, Map<String, T>> savedWorlds = new HashMap<String, Map<String, T>>(tagEntry.getValue().size());

            for(Map.Entry<String, Map<BlockVector, T>> worldEntry : tagEntry.getValue().entrySet()) {
                if (worldEntry.getValue().isEmpty()) continue;

                String world = worldEntry.getKey();

                HashMap<String, T> savedLocations = new HashMap<String, T>(worldEntry.getValue().size());

                for (Map.Entry<BlockVector, T> entry : worldEntry.getValue().entrySet()) {
                    BlockVector bv = entry.getKey();
                    savedLocations.put(
                            bv.getBlockX()+","+bv.getBlockY()+","+bv.getBlockZ(),
                            entry.getValue());
                }

                savedWorlds.put(worldEntry.getKey(), savedLocations);
            }

            locationMap.put(tagEntry.getKey(), savedWorlds);
        }
        return locationMap;
    }
}