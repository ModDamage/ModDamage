package com.ModDamage.Conditionals;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ExternalPluginManager;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.Matchables.EntityType;
import com.gmail.nossr50.mcMMO;

public abstract class McMMOConditionalStatement extends Conditional
{
	protected final DataRef<Entity> entityRef;
	protected final DataRef<EntityType> entityElementRef;
	
	protected McMMOConditionalStatement(String configString, DataRef<Entity> entityRef, DataRef<EntityType> entityElementRef)
	{
		super(configString);
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
	}

	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		mcMMO mcmmo = ExternalPluginManager.getMcMMOPlugin();
		if (mcmmo != null && entityElementRef.get(data).matches(EntityType.PLAYER))
			return evaluate(data, mcmmo, (Player)entityRef.get(data));
		return false;
	}
	
	protected abstract boolean evaluate(EventData data, mcMMO mcMMOplugin, Player player);
}
