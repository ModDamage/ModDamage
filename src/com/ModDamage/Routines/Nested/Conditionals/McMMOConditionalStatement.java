package com.ModDamage.Routines.Nested.Conditionals;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.Backend.EntityType;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.gmail.nossr50.mcMMO;

public abstract class McMMOConditionalStatement extends Conditional
{
	protected final DataRef<Entity> entityRef;
	protected final DataRef<EntityType> entityElementRef;
	
	protected McMMOConditionalStatement(DataRef<Entity> entityRef, DataRef<EntityType> entityElementRef)
	{
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
	}

	@Override
	public boolean evaluate(EventData data)
	{
		mcMMO mcmmo = ExternalPluginManager.getMcMMOPlugin();
		if (mcmmo != null && entityElementRef.get(data).matches(EntityType.PLAYER))
			return evaluate(data, mcmmo, (Player)entityRef.get(data));
		return false;
	}
	
	protected abstract boolean evaluate(EventData data, mcMMO mcMMOplugin, Player player);
}
