package com.ModDamage.Alias;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ModDamage.LogUtil;
import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;

abstract public class Aliaser<Type, StoredInfoClass> implements ScriptLineHandler
{
	private Map<String, StoredInfoClass> thisMap = new HashMap<String, StoredInfoClass>();
	protected final String name;
	protected LoadState loadState = LoadState.NOT_LOADED;
	
	Aliaser(String name){ this.name = name; }
	
	/*public StoredInfoClass matchAlias(String key)
	{
		if(thisMap.containsKey(key))
			return thisMap.get(key);
		return null;
		/*
		Type value = matchNonAlias(key);
		if(value != null) return getNewStorageClass(value);
		LogUtil.error("No matching " + name + " alias or value \"" + key + "\"");
		return getDefaultValue();* /
	}*/

//	abstract public boolean completeAlias(String key, Object nestedContent);

	//abstract protected Type matchNonAlias(String key);
	
	//abstract protected String getObjectName(Type object);
	
	public String getName(){ return name; }

	public LoadState getLoadState(){ return this.loadState; }

	public boolean hasAlias(String key)
	{
		return thisMap.containsKey(key);
	}
	
	public StoredInfoClass getAlias(String key)
	{
		return thisMap.get(key);
	}
	
	public void putAlias(String key, StoredInfoClass obj)
	{
		thisMap.put(key, obj);
	}
	
	public void clear()
	{
		thisMap.clear();
		loadState = LoadState.NOT_LOADED;
	}
	

	@Override
	public void done()
	{
	}
	
//	public void load(LinkedHashMap<String, Object> rawAliases)
//	{
//		clear();
//		LogUtil.console_only("");
//		if(rawAliases != null && !rawAliases.isEmpty())
//		{
//			loadState = LoadState.SUCCESS;
//			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, this.name + " aliases found, parsing...");
//			for(String alias : rawAliases.keySet())
//				thisMap.put("_" + alias, getDefaultValue());
//			for(Entry<String, Object> entry : rawAliases.entrySet())
//			{
//				LogUtil.console_only("");
//				if(entry.getValue() != null)
//				{
//					if(!this.completeAlias("_" + entry.getKey(), entry.getValue()))
//						this.loadState = LoadState.FAILURE;
//				}
//				else ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Found empty " + this.name.toLowerCase() + " alias \"" + entry.getKey() + "\", ignoring...");
//			}
//			for(String alias : thisMap.keySet())
//				if(thisMap.get(alias) == null)
//					thisMap.remove(alias);
//		}
//	}
	
	//abstract protected StoredInfoClass getNewStorageClass(Type value);
	protected StoredInfoClass getDefaultValue(){ return null; }

	abstract public static class SingleValueAliaser<Type> extends Aliaser<Type, Type>
	{
		SingleValueAliaser(String name){ super(name); }
		
		@Override
		public ScriptLineHandler handleLine(final ScriptLine nameLine, boolean hasChildren)
		{
			return new ScriptLineHandler() {
				Type value;
				boolean hasValue;
				
				@Override
				public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren)
				{
					if (hasValue) {
						LogUtil.error(line, name+" alias _"+nameLine.line+" cannot have multiple values.");
						return null;
					}
					value = matchAlias(line.line);
					hasValue = true;
					return null;
				}
				
				@Override
				public void done()
				{
					if (!hasValue) {
						LogUtil.error(nameLine, name+" alias _"+nameLine.line+" has no value.");
						return;
					}
					putAlias("_"+nameLine.line, value);
				}
			};
		}

//		@Override
//		public boolean completeAlias(String key, Object nestedContent)
//		{
//			if(nestedContent instanceof String)
//			{
//				Type matchedItem = matchAlias((String)nestedContent);
//				if(matchedItem != null)
//				{
//					//ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Adding value \"" + getObjectName(matchedItem) + "\"");
//					putAlias(key, matchedItem);
//					return true;
//				}
//			}
//			LogUtil.error("Error adding alias \"" + key + "\" - unrecognized value \"" + nestedContent.toString() + "\"");
//			return false;
//		}
		
		public Type matchAlias(String key)
		{
			if(hasAlias(key))
				return getAlias(key);
			
			
			Type value = matchNonAlias(key);
			LogUtil.error("No matching " + name + " alias or value \"" + key + "\"");
			return value;
		}
		
		abstract Type matchNonAlias(String string);

		//@Override
		//protected Type getNewStorageClass(Type value){ return null; }
	}
	
	abstract public static class CollectionAliaser<InfoType> extends Aliaser<InfoType, Collection<InfoType>>
	{
		CollectionAliaser(String name){ super(name); }
		
		public void putAllAliases(String key, Collection<InfoType> items)
		{
			Collection<InfoType> aliases = getAlias(key);
			if (aliases == null)
			{
				putAlias(key, items);
				return;
			}
			
			aliases.addAll(items);
		}
		

		@Override
		public ScriptLineHandler handleLine(final ScriptLine nameLine, boolean hasChildren)
		{
			return new ScriptLineHandler() {
				Collection<InfoType> values = new ArrayList<InfoType>();
				boolean hasValue;
				
				@Override
				public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren)
				{
					Collection<InfoType> subvalues = matchAlias(line);
					if (subvalues != null)
						values.addAll(subvalues);
					else {
						InfoType value = matchNonAlias(line.line);
						values.add(value);
					}
					hasValue = true;
					return null;
				}
				
				@Override
				public void done()
				{
					if (!hasValue) {
						LogUtil.error(nameLine, name+" alias _"+nameLine.line+" has no value.");
						return;
					}
					putAlias("_"+nameLine.line, values);
				}
			};
		}
		

//		@Override
//		public boolean completeAlias(String key, Object nestedContent)
//		{
//			boolean failFlag = false;
//			HashSet<InfoType> matchedItems = new HashSet<InfoType>();
//			List<String> foundValues = new ArrayList<String>();
//			LogUtil.info("Adding " + name + " alias \"" + key + "\"");
//			ModDamage.changeIndentation(true);
//			if(nestedContent instanceof String)
//			{
//				foundValues.add(((String)nestedContent));
//			}
//			else if(nestedContent instanceof List)
//			{
//				for(Object object : ((List<?>)nestedContent))
//				{
//					if(object instanceof String)
//						foundValues.add(((String)object));
//					else
//					{
//						LogUtil.error("Unrecognized object " + nestedContent.toString());
//						failFlag = true;
//					}
//				}
//			}
//			else
//			{
//				LogUtil.error("Unrecognized object " + nestedContent.toString());
//				failFlag = true;
//			}
//			
//			for(Object listedValue : foundValues)
//			{
//				Collection<InfoType> matchedList = matchAlias((String)listedValue);
//				if(!matchedList.isEmpty())
//				{
//					for(InfoType value : matchedList)
//					{
//						if(!matchedItems.contains(value))
//						{
//							//ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Adding value \"" + getObjectName(value) + "\"");
//							matchedItems.add(value);
//						}
//						//else
//						//	ModDamage.addToLogRecord(OutputPreset.WARNING, "Duplicate value \"" + getObjectName(value) + "\" - ignoring.");
//					}
//				}
//				else if(key.equalsIgnoreCase((String)listedValue))
//					ModDamage.addToLogRecord(OutputPreset.WARNING, "Self-referential value \"" + key + "\" - ignoring.");
//				else
//				{
//					LogUtil.error("Error: invalid value \"" + (String)listedValue + "\" - ignoring.");
//					failFlag = true;//debug output already handled in failed matchAlias
//				}
//			}
//			ModDamage.changeIndentation(false);
//			if(!failFlag) putAllAliases(key, matchedItems);
//			return !failFlag;
//		}
		
		//@Override
		public Collection<InfoType> matchAlias(ScriptLine scriptLine) {
			return matchAlias(scriptLine, scriptLine.line);
		}
		
		public Collection<InfoType> matchAlias(ScriptLine scriptLine, String key)
		{
			if(hasAlias(key))
				return getAlias(key);
			
			
			boolean failFlag = false;
			List<InfoType> values = new ArrayList<InfoType>();
			if (key != null)
			{
				for(String valueString : key.split(","))
				{
					InfoType value = matchNonAlias(valueString);
					if(value != null) values.add(value);
					else failFlag = true;
				}
			}
			if(!failFlag && !values.isEmpty()) return values;
			LogUtil.error(scriptLine, "No matching " + name + " alias or value \"" + key + "\"");
			return getDefaultValue();
		}

		abstract protected InfoType matchNonAlias(String valueString);

		@Override
		protected Collection<InfoType> getDefaultValue(){ return new ArrayList<InfoType>(); }
	}
}