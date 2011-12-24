package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class TypeAliaser extends CollectionAliaser<ModDamageElement> 
{
	public TypeAliaser() {super(AliasManager.Type.name());}

	@Override
	public Collection<ModDamageElement> matchAlias(String key)
	{
		if(thisMap.containsKey(key))
			return thisMap.get(key);
		ModDamageElement value = matchNonAlias(key);
		if(!value.equals(ModDamageElement.UNKNOWN)) return Arrays.asList(value);
		{
			ModDamage.changeIndentation(true);
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "No matching " + name + " alias or value \"" + key + "\"");
			ModDamage.changeIndentation(false);
		}
		return new ArrayList<ModDamageElement>();
	}
	
	@Override
	protected ModDamageElement matchNonAlias(String key){ return ModDamageElement.matchElement(key);}

	@Override
	protected String getObjectName(ModDamageElement object){ return object.name();}

}