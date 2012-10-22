package com.ModDamage.Tags;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.*;

public final class TagsHolder<T>
{
    public final ITags<T, Location> onLocation = new LocationTags<T>();
    public final ITags<T, Entity> onEntity = new EntityTags<T>(this);
    public final ITags<T, OfflinePlayer> onPlayer = new PlayerTags<T>();
    public final ITags<T, World> onWorld = new WorldTags<T>();

    /**
     * Only the ModDamage main should use this method.
     */
    public void clear(){
        onLocation.clear();
        onEntity.clear();
        onPlayer.clear();
        onWorld.clear();
    }


    public void loadTags(Map<String, Object> tagMap, Map<UUID, Entity> entities) {
        onLocation.load((Map) tagMap.get("location"), entities);
        onEntity.load((Map) tagMap.get("entity"), entities);
        onPlayer.load((Map) tagMap.get("player"), entities);
        onWorld.load((Map) tagMap.get("world"), entities);
    }


    public Map<String, Object> saveTags(Set<Entity> entities) {
        Map<String, Object> saveMap = new HashMap<String, Object>();

        saveMap.put("location", onLocation.save(entities));
        saveMap.put("entity", onEntity.save(entities));
        saveMap.put("player", onPlayer.save(entities));
        saveMap.put("world", onWorld.save(entities));

        return saveMap;
    }
}
