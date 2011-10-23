package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;

public class ElementAliaser extends Aliaser<List<ModDamageElement>, ModDamageElement> 
{
	private static final long serialVersionUID = -557230493957602224L;

	public ElementAliaser() {super("Element");}

	@Override
	public List<ModDamageElement> matchAlias(String key)
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

	@Override
	protected List<ModDamageElement> getNewStorageClass(ModDamageElement value){ return Arrays.asList(value);}

	@Override
	protected List<ModDamageElement> getNewStorageClass(){ return new ArrayList<ModDamageElement>();}

}