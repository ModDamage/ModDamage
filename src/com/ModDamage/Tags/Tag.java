package com.ModDamage.Tags;


import com.ModDamage.EventInfo.IDataProvider;
import org.bukkit.entity.Entity;

public abstract class Tag<T> {
    public final IDataProvider<String> name;
    public final Class<T> type;

    private Tag(IDataProvider<String> name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public static Tag<?> get(IDataProvider<String> name, String typeString) {
        if (typeString.isEmpty())
            return new Tag<Integer>(name, Integer.class) {
                @Override
                public TagsHolder<Integer> getHolder(TagManager tagManager) {
                    return tagManager.intTags;
                }
            };
        else if (typeString.equalsIgnoreCase("s"))
            return new Tag<String>(name, String.class) {
                @Override
                public TagsHolder<String> getHolder(TagManager tagManager) {
                    return tagManager.stringTags;
                }
            };
        else
            throw new IllegalArgumentException("Bad Tag type: "+typeString);
    }

    public abstract TagsHolder<T> getHolder(TagManager tagManager);

    public String toString() {
        return name.toString();
    }
}
