package com.ModDamage.Variables.String;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.StringExp;

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
