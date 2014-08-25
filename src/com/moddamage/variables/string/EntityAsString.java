package com.moddamage.variables.string;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.expressions.StringExp;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

public class EntityAsString extends StringExp<Entity>
{
	public EntityAsString(IDataProvider<Entity> entityDP)
	{
		super(Entity.class, entityDP);
	}
	
	@Override
	public String get(Entity entity, EventData data)
	{
		if (entity instanceof Player)
			return ((Player) entity).getName();
		return entity.getType().getName();
	}
	
	public static void register()
	{
		DataProvider.registerTransformer(String.class, Entity.class, new IDataTransformer<String, Entity>()
			{
				@Override
				public IDataProvider<String> transform(EventInfo info, IDataProvider<Entity> entityDP)
				{
					return new EntityAsString(entityDP);
				}
			});
	}

	@Override
	public String toString()
	{
		return startDP.toString();
	}
}
