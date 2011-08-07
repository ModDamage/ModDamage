package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import org.bukkit.Material;

public class ItemAliaser extends Aliaser<Material> 
{
	private static final long serialVersionUID = -557230493957602224L;

	public ItemAliaser() {super("Item");}

	@Override
	protected Material matchNonAlias(String key){ return Material.matchMaterial(key);}

	@Override
	protected String getObjectName(Material object){ return object.name();}
}
