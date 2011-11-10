package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class ElementAliaser extends CollectionAliaser<ModDamageElement> 
{
	private static final long serialVersionUID = -557230493957602224L;

	public ElementAliaser() {super("Element");}

	@Override
	public Collection<ModDamageElement> matchAlias(String key)
	{
		if(this.containsKey(key))
			return this.get(key);
		ModDamageElement value = matchNonAlias(key);
		if(!value.equals(ModDamageElement.UNKNOWN)) return Arrays.asList(value);
		{
			ModDamage.indentation++;
			ModDamage.addToLogRecord(DebugSetting.QUIET, "No matching " + name + " alias or value \"" + key + "\"", LoadState.FAILURE);
			ModDamage.indentation--;
		}
		return new ArrayList<ModDamageElement>();
	}
	
	@Override
	protected ModDamageElement matchNonAlias(String key){ return ModDamageElement.matchElement(key);}

	@Override
	protected String getObjectName(ModDamageElement object){ return object.name();}

}