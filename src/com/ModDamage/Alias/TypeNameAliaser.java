package com.ModDamage.Alias;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Matchables.EntityType;

public class TypeNameAliaser extends Aliaser<EntityType, List<String>> 
{
	public static TypeNameAliaser aliaser = new TypeNameAliaser();
	//public static List<String> match(String string) { return aliaser.matchAlias(string); }
	
	protected HashMap<EntityType, List<String>> thisMap = new HashMap<EntityType, List<String>>();
	
	private static final Random random = new Random();

	TypeNameAliaser()
	{
		super(AliasManager.TypeName.name());
		for(EntityType element : EntityType.values())
			thisMap.put(element, new ArrayList<String>());
	}
	
	@Override
	public void load(LinkedHashMap<String, Object> rawAliases)
	{
		clear();
		
		for(Entry<String, Object> entry : rawAliases.entrySet())
		{
			loadState = LoadState.SUCCESS;
			
			EntityType type = EntityType.getElementNamed(entry.getKey());
			if(type == null)
			{
				ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, "Unknown type \"" + entry.getKey() + "\"");//Only a warning because some users may preempt updated mob types
				continue;
			}
			
			List<String> names = matchAlias(type);
			
			Object value = entry.getValue();
			
			if(value instanceof List)
			{
				List<?> someList = (List<?>)value;
				if (someList.size() == 1)
				{
					value = someList.get(0);
				}
				else
				{
					ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Aliasing type " + type.name());
					
					ModDamage.changeIndentation(true);
					for(Object object : someList)
					{
						if(object instanceof String)
						{
							names.add((String)object);
							ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "as \"" + value + "\"");
						}
						else 
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown item: "+object.toString());
					}
					ModDamage.changeIndentation(false);
					continue;
				}
			}
			
			if(value instanceof String)
			{
				names.add((String)value);
				ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Aliasing type " + type.name() +  " as \"" + value + "\"");
				continue;
			}
			
			loadState = LoadState.FAILURE;
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid content in alias for type " + type.name() + ": " + entry.getValue().toString());
		}
		if(loadState == LoadState.SUCCESS)
			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Aliasing names for one or more Types");
	}

	@Override
	public void clear()
	{
		loadState = LoadState.NOT_LOADED;
		thisMap.clear();
	}
	
	public String toString(EntityType element)
	{
		List<String> names = thisMap.get(element);
		return names != null && !names.isEmpty()? names.get(random.nextInt(names.size())) : element.name();
	}
	
	public List<String> matchAlias(EntityType type){
		List<String> list = thisMap.get(type);
		if (list == null)
		{
			list = new ArrayList<String>();
			thisMap.put(type, list);
		}
		return list;
	}
	
	public List<String> matchAlias(String string){
		EntityType type = EntityType.getElementNamed(string);
		if (type == null) return null;
		return matchAlias(type);
	}
	
	@Deprecated
	public boolean completeAlias(String key, Object nestedContent){ return false; }
	@Deprecated
	protected EntityType matchNonAlias(String key){ return null; }
	@Deprecated
	protected String getObjectName(EntityType object){ return null; }
	@Deprecated
	protected List<String> getNewStorageClass(EntityType value){ return null; }
}