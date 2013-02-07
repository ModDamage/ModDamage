package com.ModDamage.Tags;

import com.ModDamage.ModDamage;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;

import java.util.*;
import java.util.Map.Entry;

public class PlayerTags<T> implements ITags<T, OfflinePlayer> {
    private final Map<String, Map<String, T>> tags = new HashMap<String, Map<String, T>>();

    public PlayerTags() { }

    public void addTag(OfflinePlayer player, String tag, T tagValue) {
        ModDamage.getTagger().dirty(); // only need to save when dirty
        Map<String, T> tags = this.tags.get(tag);
        if (tags == null) {
            tags = new HashMap<String, T>();
            this.tags.put(tag, tags);
        }
        tags.put(player.getName(), tagValue);
    }

    public boolean isTagged(OfflinePlayer player, String tag) {
        Map<String, T> tags = this.tags.get(tag);
        return tags != null && tags.containsKey(player.getName());
    }

    public List<String> getTags(OfflinePlayer player) {
        List<String> tagsList = new ArrayList<String>();
        for (Map.Entry<String, Map<String, T>> entry : tags.entrySet())
            if (entry.getValue().containsKey(player.getName()))
                tagsList.add(entry.getKey());
        return tagsList;
    }
    
    public Map<OfflinePlayer, T> getAllTagged(String tag) {
    	Map<String, T> players = tags.get(tag);
    	
    	if (players != null) {
    		Map<OfflinePlayer, T> map = new HashMap<OfflinePlayer, T>(players.size());
    		
    		for (Entry<String, T> entry : players.entrySet())
			{
				map.put(Bukkit.getOfflinePlayer(entry.getKey()), entry.getValue());
			}
    		
    		return map;
    	}
    	
    	return null;
    }

    public T getTagValue(OfflinePlayer player, String tag) {
        Map<String, T> tags = this.tags.get(tag);
        if (tags == null) return null;

        return tags.get(player.getName());
    }

    public void removeTag(OfflinePlayer player, String tag) {
        if (tags.containsKey(tag))
            tags.get(tag).remove(player.getName());
    }

    public void clear() {
        tags.clear();
    }

    @SuppressWarnings("rawtypes")
    public void load(Map tagMap, Map<UUID, Entity> entities) {
        @SuppressWarnings("unchecked")
        Map<String, Map<String, T>> playersMap = (Map<String, Map<String, T>>) tagMap;
        if (playersMap != null)
        {
            for (Map.Entry<String, Map<String, T>> tagEntry : playersMap.entrySet())
            {
                Map<String, T> pmap = new HashMap<String, T>(tagEntry.getValue().size());

                for (Map.Entry<String, T> pentry : tagEntry.getValue().entrySet())
                {
                    pmap.put(pentry.getKey(), pentry.getValue());
                }

                tags.put(tagEntry.getKey(), pmap);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public Map save(Set<Entity> entities) {
        Map<String, Map<String, T>> playerMap = new HashMap<String, Map<String, T>>();
        for(Map.Entry<String, Map<String, T>> tagEntry : tags.entrySet())
        {
            HashMap<String, T> savedPlayers = new HashMap<String, T>();

            if (tagEntry.getValue().isEmpty()) continue;

            for(Map.Entry<String, T> entry : tagEntry.getValue().entrySet())
                savedPlayers.put(entry.getKey(), entry.getValue());

            if (!savedPlayers.isEmpty())
                playerMap.put(tagEntry.getKey(), savedPlayers);
        }
        return playerMap;
    }
}