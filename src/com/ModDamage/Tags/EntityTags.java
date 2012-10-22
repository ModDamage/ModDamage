package com.ModDamage.Tags;

import com.ModDamage.ModDamage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;

import java.util.*;

public class EntityTags<T> implements ITags<T, Entity> {
    private final Map<String, Map<Entity, T>> tags = new HashMap<String, Map<Entity, T>>();
    private final TagsHolder<T> tagsHolder;

    public EntityTags(TagsHolder<T> tagsHolder) {
        this.tagsHolder = tagsHolder;
    }

    public void addTag(Entity entity, String tag, T tagValue) {
        if (entity instanceof OfflinePlayer) {
            tagsHolder.onPlayer.addTag((OfflinePlayer) entity, tag, tagValue);
            return;
        }
        ModDamage.getTagger().dirty(); // only need to save when dirty
        Map<Entity, T> tags = this.tags.get(tag);
        if (tags == null) {
            tags = new WeakHashMap<Entity, T>();
            this.tags.put(tag, tags);
        }
        tags.put(entity, tagValue);
    }

    public boolean isTagged(Entity entity, String tag) {
        if (entity instanceof OfflinePlayer) return tagsHolder.onPlayer.isTagged((OfflinePlayer) entity, tag);
        Map<Entity, T> tags = this.tags.get(tag);
        return tags != null && tags.containsKey(entity);
    }

    public List<String> getTags(Entity entity) {
        if (entity instanceof OfflinePlayer) return tagsHolder.onPlayer.getTags((OfflinePlayer) entity);
        List<String> tagsList = new ArrayList<String>();
        for (Map.Entry<String, Map<Entity, T>> entry : tags.entrySet())
            if (entry.getValue().containsKey(entity))
                tagsList.add(entry.getKey());
        return tagsList;
    }

    public T getTagValue(Entity entity, String tag) {
        if (entity instanceof OfflinePlayer) return tagsHolder.onPlayer.getTagValue((OfflinePlayer) entity, tag);
        Map<Entity, T> tags = this.tags.get(tag);
        if (tags == null) return null;

        return tags.get(entity);
    }

    public void removeTag(Entity entity, String tag) {
        if (entity instanceof OfflinePlayer) {
            tagsHolder.onPlayer.removeTag((OfflinePlayer) entity, tag);
            return;
        }
        if (tags.containsKey(tag))
            tags.get(tag).remove(entity);
    }

    public void clear() {
        tags.clear();
    }

    public void load(Map tagMap, Map<UUID, Entity> entities) {
        @SuppressWarnings("unchecked")
        Map<String, Map<String, T>> entitiesMap = (Map<String, Map<String, T>>) tagMap;
        if (entitiesMap != null)
        {
            for (Map.Entry<String, Map<String, T>> tagEntry : entitiesMap.entrySet())
            {
                Map<Entity, T> taggedEntities = new HashMap<Entity, T>(tagEntry.getValue().size());
                tags.put(tagEntry.getKey(), taggedEntities);

                for (Map.Entry<String, T> entry : tagEntry.getValue().entrySet())
                {
                    Entity entity = entities.get(UUID.fromString(entry.getKey()));
                    if (entity != null)
                        taggedEntities.put(entity, entry.getValue());
                }
            }
        }
    }

    public Map save(Set<Entity> entities) {
        Map<String, Map<String, T>> entityMap = new HashMap<String, Map<String, T>>();
        for(Map.Entry<String, Map<Entity, T>> tagEntry : tags.entrySet())
        {
            HashMap<String, T> savedEntities = new HashMap<String, T>();

            tagEntry.getValue().keySet().retainAll(entities); // simple cleanup operation, it might not even be necessary

            if (tagEntry.getValue().isEmpty()) continue;

            for(Map.Entry<Entity, T> entry : tagEntry.getValue().entrySet())
                savedEntities.put(entry.getKey().getUniqueId().toString(), entry.getValue());

            if (!savedEntities.isEmpty())
                entityMap.put(tagEntry.getKey(), savedEntities);
        }
        return entityMap;
    }
}