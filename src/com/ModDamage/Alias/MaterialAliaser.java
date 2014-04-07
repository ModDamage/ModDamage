package com.ModDamage.Alias;

import java.util.Collection;

import org.bukkit.Material;

import com.ModDamage.Alias.Aliaser.CollectionAliaser;
import com.ModDamage.Backend.ScriptLine;

public class MaterialAliaser extends CollectionAliaser<Material> 
{
	public static MaterialAliaser aliaser = new MaterialAliaser();
	public static Collection<Material> match(ScriptLine scriptLine) { return aliaser.matchAlias(scriptLine); }
	public static Collection<Material> match(ScriptLine scriptLine, String string) { return aliaser.matchAlias(scriptLine, string); }
	
	public MaterialAliaser() { super(AliasManager.Material.name()); }

	@Override
	public Material matchNonAlias(String key)
	{
		if (key.toLowerCase().startsWith("id_"))
		{
			try
			{
				key = key.substring(3);
				return Material.getMaterial(Integer.parseInt(key));
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}
		return Material.matchMaterial(key);
	}

	//@Override
	//protected String getObjectName(Material object){ return object.name(); }
}
