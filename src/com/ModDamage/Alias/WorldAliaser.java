package com.ModDamage.Alias;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Bukkit;

import com.ModDamage.LogUtil;
import com.ModDamage.Alias.Aliaser.CollectionAliaser;
import com.ModDamage.Backend.ScriptLine;

public class WorldAliaser extends CollectionAliaser<String> 
{
	public static WorldAliaser aliaser = new WorldAliaser();
	public static Collection<String> match(ScriptLine scriptLine) { return aliaser.matchAlias(scriptLine); }
	public static Collection<String> match(ScriptLine scriptLine, String string) { return aliaser.matchAlias(scriptLine, string); }
	
	public WorldAliaser() {super(AliasManager.World.name()); }

	@Override
	protected String matchNonAlias(String key)
	{
		if(Bukkit.getWorld(key) == null)
		{
			LogUtil.warning_strong("Warning: world \"" + key + "\" does not currently exist.");
			return key;
		}
		return Bukkit.getWorld(key).getName();
	}

	//@Override
	//protected String getObjectName(String object){ return object; }

	//@Override
	//protected HashSet<String> getNewStorageClass(String value){ return new HashSet<String>(Arrays.asList(value)); }

	@Override
	protected HashSet<String> getDefaultValue(){ return new HashSet<String>(); }
}
