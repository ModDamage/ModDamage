package com.ModDamage.Tags;


import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.Parsing.IDataProvider;

public abstract class Tag<T> {
    private final IDataProvider<String> name;
    public final Class<T> type;
    public final T defaultValue;

    private Tag(IDataProvider<String> name, Class<T> type, T defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public static Tag<?> get(IDataProvider<String> name, String typeString) {
        if (typeString.isEmpty())
            return new Tag<Number>(name, Number.class, 0) {
                @Override
                public TagsHolder<Number> getHolder(TagManager tagManager) {
                    return tagManager.numTags;
                }
            };
        else if (typeString.equalsIgnoreCase("s"))
            return new Tag<String>(name, String.class, null) {
                @Override
                public TagsHolder<String> getHolder(TagManager tagManager) {
                    return tagManager.stringTags;
                }
            };
        else
            throw new IllegalArgumentException("Bad Tag type: "+typeString);
    }

    public String getName(EventData data) throws BailException {
    	String n = name.get(data);
    	if (n == null) return null;
        return n.toLowerCase();
    }

    public abstract TagsHolder<T> getHolder(TagManager tagManager);

    public String toString() {
        return name.toString();
    }
}
