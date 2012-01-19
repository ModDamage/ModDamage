package com.ModDamage.Backend.Aliasing;

import java.util.Collection;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.RoutineObjects.Nested.Parameterized.Message.DynamicMessage;

public class MessageAliaser extends CollectionAliaser<DynamicMessage> 
{
	public MessageAliaser() {super(AliasManager.Message.name());}
	@Override
	protected DynamicMessage matchNonAlias(String key){ return new DynamicMessage(key);}
	@Override
	protected String getObjectName(DynamicMessage object){ return object.toString();}
	
	//TODO Possible to just use Aliaser's matchAlias?
	@Override
	public Collection<DynamicMessage> matchAlias(String key)
	{
		if(thisMap.containsKey(key))
			return thisMap.get(key);
		DynamicMessage value = matchNonAlias(key);
		if(value != null) return getNewStorageClass(value);
		ModDamage.addToLogRecord(OutputPreset.FAILURE, "No matching " + name + " alias or value \"" + key + "\"");
		return getDefaultValue();
	}
}
