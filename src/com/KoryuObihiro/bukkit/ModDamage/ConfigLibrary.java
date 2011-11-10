package com.KoryuObihiro.bukkit.ModDamage;

import java.util.LinkedHashMap;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;

public class ConfigLibrary
{
	@SuppressWarnings("unchecked")
	public static LinkedHashMap<String, Object> getStringMap(String targetName, Object object)
	{
		if(object != null)
		{
			if(object instanceof LinkedHashMap)
				return (LinkedHashMap<String, Object>)object;
			ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: expected map of values for \"" + targetName + "\"", LoadState.FAILURE);
		}
		else ModDamage.addToLogRecord(DebugSetting.VERBOSE, "Warning: nothing found for \"" + targetName + "\"", LoadState.NOT_LOADED);
		return null;
	}
	
	public static String getCaseInsensitiveKey(LinkedHashMap<String, Object> map, String key)
	{
		for(String someKey : map.keySet())
			if(someKey.equalsIgnoreCase(key))
				return someKey;
		return null;
	}
}
