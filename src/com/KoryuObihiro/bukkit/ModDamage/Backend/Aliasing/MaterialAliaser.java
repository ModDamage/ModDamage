package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import org.bukkit.Material;

public class MaterialAliaser extends Aliaser<Material> 
{
	private static final long serialVersionUID = 7539931612104625797L;

	public MaterialAliaser() {super("Group");}

	@Override
	protected Material matchNonAlias(String key)
	{
		for(Material material : Material.values())
			if(material.name().equalsIgnoreCase(key))
				return material;
		return null;
	}

	@Override
	protected String getObjectName(Material material){ return material.name();}
}
