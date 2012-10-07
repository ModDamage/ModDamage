package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class EntityTagged extends Conditional<Entity>
{
	public static final Pattern pattern = Pattern.compile("\\.istagged\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	private final String tag;
	
	public EntityTagged(IDataProvider<Entity> entityDP, String tag)
	{
		super(Entity.class, entityDP);
		this.tag = tag;
	}

	@Override
	public Boolean get(Entity entity, EventData data)
	{
		return entity != null && ModDamage.getTagger().intTags.isTagged(entity, tag);
	}
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Entity.class, pattern, new IDataParser<Boolean, Entity>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm)
				{
					return new EntityTagged(entityDP, m.group(1).toLowerCase());
				}
			});
	}
	
	@Override
	public String toString()
	{
		return startDP + ".istagged." + tag;
	}
}
