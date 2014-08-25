package com.moddamage.expressions.list;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.moddamage.ModDamage;
import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.expressions.InterpolatedString;
import com.moddamage.expressions.ListExp;
import com.moddamage.parsing.BaseDataParser;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.tags.ITags;
import com.moddamage.tags.TagManager;
import com.moddamage.tags.TagsHolder;

@SuppressWarnings("rawtypes")
public class ThingsTagged extends ListExp {
	public final boolean isString;
	public final Class<?> tagClass;
	public final IDataProvider<String> tagName;

    public ThingsTagged(boolean isString, Class<?> tagClass, IDataProvider<String> tagName)
	{
    	this.isString = isString;
    	this.tagClass = tagClass;
    	this.tagName = tagName;
	}

    @SuppressWarnings("unchecked")
	public List get(EventData data) throws BailException {
        TagManager manager = ModDamage.getTagger();
        
        TagsHolder<?> holder;
        if (isString)
        	holder = manager.stringTags;
        else
        	holder = manager.numTags;
    	
		ITags<?, ?> tags;
		if (tagClass == OfflinePlayer.class)
			tags = holder.onPlayer;
		else if (tagClass == Entity.class)
			tags = holder.onEntity;
		else if (tagClass == World.class)
			tags = holder.onWorld;
		else if (tagClass == Chunk.class)
			tags = holder.onChunk;
		else if (tagClass == Location.class)
			tags = holder.onLocation;
		else return null;
        
		String tag = tagName.get(data);
		if (tag == null) return null;
		
        Map<?, ?> things = tags.getAllTagged(tag.toLowerCase());
        if (things == null) return null;


        return new ArrayList(things.keySet());
    }

    public Class<?> providesElement() {
        return tagClass;
    }

    public String toString() {
        return tagClass.getSimpleName().toLowerCase() + " " + (isString?"s":"") + "tagged " + tagName;
    }

    public static void register()
    {
        DataProvider.register(List.class, Pattern.compile("(players?|entit(?:y|ies)|worlds?|chunks?|loc(?:ation)?s?|blocks?) (s)?tagged ", Pattern.CASE_INSENSITIVE), new BaseDataParser<List>() {
            public ThingsTagged parse(EventInfo info, Matcher m, StringMatcher sm) {
                String taggableType = m.group(1).toLowerCase();
                String tagType = m.group(2);
                IDataProvider<String> tagName = InterpolatedString.parseWord(InterpolatedString.word, sm.spawn(), info);
                
                
                boolean isString;
                if (tagType != null)
                	isString = true;
                else
                	isString = false;
                
                Class<?> tagClass;
                if (taggableType.startsWith("player"))
                	tagClass = OfflinePlayer.class;
                else if (taggableType.startsWith("entity"))
                	tagClass = Entity.class;
                else if (taggableType.startsWith("world"))
                	tagClass = World.class;
                else if (taggableType.startsWith("chunk"))
                	tagClass = Chunk.class;
                else if (taggableType.startsWith("loc") || taggableType.startsWith("block"))
                	tagClass = Location.class;
                else return null;

                sm.accept();
                return new ThingsTagged(isString, tagClass, tagName);
            }
        });
    }
}
