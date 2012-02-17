package com.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.DebugSetting;
import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.EntityType;

public class TypeNameAliaser extends Aliaser<EntityType, List<String>> 
{
	public static TypeNameAliaser aliaser = new TypeNameAliaser();
	//public static List<String> match(String string) { return aliaser.matchAlias(string); }
	
	protected HashMap<EntityType, List<String>> thisMap = new HashMap<EntityType, List<String>>();
	
	private static TypeNameAliaser staticInstance = new TypeNameAliaser();
	public static TypeNameAliaser getStaticInstance(){ return staticInstance; }
	
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
		
		boolean failFlag = false;
		EntityType element = null;
		for(Entry<String, Object> entry : rawAliases.entrySet())
		{
			this.loadState = LoadState.SUCCESS;
			element = EntityType.getElementNamed(entry.getKey());
			if(element != null)
			{
				if(entry.getValue() instanceof String)
				{
					String name = (String)entry.getValue();
					thisMap.get(element).add(name);
					ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Aliasing type " + element.name() +  " as \"" + name + "\"");
					continue;
				}
				else if(entry.getValue() instanceof List)
				{
					List<?> someList = (List<?>)entry.getValue();
					ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, (someList.size() > 1?"Aliasing multiple strings for type " + element.name():"Aliasing type " + element.name() +  " as \"" + name + "\""));
					
					ModDamage.changeIndentation(true);
					for(Object object : someList)
						if(object instanceof String)
						{
							String name = (String)object;
							thisMap.get(element).add(name);
							ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Adding item \"" + name + "\"");
						}
						else if(object instanceof List)
						{
							for(Object anotherObject : ((List<?>)object))
							{
								if(anotherObject instanceof String)
								{
									thisMap.get(element).add(((String)anotherObject));
									ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Adding item \"" + name + "\"");
								}
								else
								{
									ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unrecognized object " + anotherObject.toString());
									failFlag = true;
								}
							}
						}
					ModDamage.changeIndentation(false);
				}
				else
				{
					failFlag = true;
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid content in alias for type " + element.name() + ": " + entry.getValue().toString());
				}
			}
			else ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, "Unknown type \"" + entry.getKey() + "\"");//Only a warning because some users may preempt updated mob types
		}
		if(failFlag) loadState = LoadState.FAILURE;
		if(loadState == LoadState.SUCCESS && !ModDamage.getDebugSetting().shouldOutput(DebugSetting.VERBOSE))
			ModDamage.addToLogRecord(OutputPreset.INFO, "Aliasing names for one or more Types");
	}

	@Override
	public void clear()
	{
		loadState = LoadState.NOT_LOADED;
		for(List<String> names : thisMap.values())
			names.clear();
	}
	
	public String toString(EntityType element)
	{
		List<String> names = thisMap.get(element);
		return !names.isEmpty()?names.get(random.nextInt(names.size())):element.name();
	}
	
	@Deprecated
	public List<String> matchAlias(String string){ return null; }
	@Deprecated
	public boolean completeAlias(String key, Object nestedContent){ return false; }
	@Deprecated
	protected EntityType matchNonAlias(String key){ return null; }
	@Deprecated
	protected String getObjectName(EntityType object){ return null; }
	@Deprecated
	protected List<String> getNewStorageClass(EntityType value){ return null; }
}