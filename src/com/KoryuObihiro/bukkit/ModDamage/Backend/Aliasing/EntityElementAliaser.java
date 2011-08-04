package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;

public class EntityElementAliaser extends Aliaser<ModDamageElement> 
{
	private static final long serialVersionUID = -557230493957602224L;

	public EntityElementAliaser() {super("Element");}

	@Override
	protected ModDamageElement matchNonAlias(String key){ return ModDamageElement.matchElement(key);}

	@Override
	protected String getName(ModDamageElement object){ return object.getReference();}

}
