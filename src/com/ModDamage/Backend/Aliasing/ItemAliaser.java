package com.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.EventInfo.EventInfo;

public class ItemAliaser extends Aliaser<Object, Collection<String>> 
{
	public static ItemAliaser aliaser = new ItemAliaser();
	public static Collection<ModDamageItemStack> match(String string, EventInfo info) { return aliaser.matchAlias(string, info); }
	
	public ItemAliaser() { super(AliasManager.Item.name()); }
	
	////////// Copied from CollectionAliaser and RoutineAliaser. Needs to be revamped eventually. /////////////
	
	static final Pattern commaPattern = Pattern.compile("\\s*,\\s*");
	
	public Collection<ModDamageItemStack> matchAlias(String key, EventInfo info)
	{
		Collection<String> values = thisMap.get(key);
		if (values == null)
		{
			if (key.startsWith("_")) {
				if (thisMap.containsKey(key))
					return Arrays.<ModDamageItemStack>asList(); // hmm, not ready yet?
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown alias: \"" + key + "\"");
				return null;
			}
			values = new ArrayList<String>();
			values.add(key);
		}
		
		List<ModDamageItemStack> items = new ArrayList<ModDamageItemStack>();
		
		for (String itemStr : values)
		{
			StringMatcher sm = new StringMatcher(itemStr);
			while(true) {
				ModDamageItemStack item = ModDamageItemStack.getNewFromFront(info, sm.spawn());
				if (item == null) return null;
				items.add(item);
				
				if (sm.matchesFront(commaPattern))
					continue;
				if (sm.isEmpty())
					break;
				
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unidentified Yucky Stuff: \""+ sm.string +"\"");
				return null;
			}
		}
		
		return items;
	}
	
	@Override
	public boolean completeAlias(String key, Object nestedContent)
	{
		boolean failFlag = false;
		HashSet<String> matchedItems = new HashSet<String>();
		List<String> foundValues = new ArrayList<String>();
		ModDamage.addToLogRecord(OutputPreset.INFO, "Adding " + name + " alias \"" + key + "\"");
		ModDamage.changeIndentation(true);
		if(nestedContent instanceof String)
		{
			foundValues.add(((String)nestedContent));
		}
		else if(nestedContent instanceof List)
		{
			for(Object object : ((List<?>)nestedContent))
			{
				if(object instanceof String)
					foundValues.add(((String)object));
				else
				{
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unrecognized object " + nestedContent.toString());
					failFlag = true;
				}
			}
		}
		else
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unrecognized object " + nestedContent.toString());
			failFlag = true;
		}
		
		for(Object listedValue : foundValues)
		{
			Collection<String> matchedList = matchAlias((String)listedValue);
			if(!matchedList.isEmpty())
			{
				for(String value : matchedList)
				{
					if(!matchedItems.contains(value))
					{
						//ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Adding value \"" + getObjectName(value) + "\"");
						matchedItems.add(value);
					}
					//else
					//	ModDamage.addToLogRecord(OutputPreset.WARNING, "Duplicate value \"" + getObjectName(value) + "\" - ignoring.");
				}
			}
			else if(key.equalsIgnoreCase((String)listedValue))
				ModDamage.addToLogRecord(OutputPreset.WARNING, "Self-referential value \"" + key + "\" - ignoring.");
			else
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: invalid value \"" + (String)listedValue + "\" - ignoring.");
				failFlag = true;//debug output already handled in failed matchAlias
			}
		}
		ModDamage.changeIndentation(false);
		if(!failFlag) thisMap.get(key).addAll(matchedItems);
		return !failFlag;
	}
	
	//@Override
	public Collection<String> matchAlias(String key)
	{
		if(thisMap.containsKey(key))
			return thisMap.get(key);
		
		
		List<String> values = new ArrayList<String>();
		if (key != null)
		{
			values.add(key);
			/*for(String valueString : key.split(","))
			{
				values.add(valueString);
			}*/
		}
		if(!values.isEmpty()) return values;
		ModDamage.addToLogRecord(OutputPreset.FAILURE, "No matching " + name + " alias or value \"" + key + "\"");
		return getDefaultValue();
	}
	
	//@Override
	//protected String getObjectName(ModDamageItemStack object){ return object.toString(); }
}