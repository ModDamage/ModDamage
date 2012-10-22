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
	public static final Pattern pattern = Pattern.compile("\\.is(s?)tagged\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	private final String tag;
	private final boolean isString;
	
	public EntityTagged(IDataProvider<Entity> entityDP, String tag, boolean isString)
	{
		super(Entity.class, entityDP);
		this.tag = tag;
		this.isString = isString;
	}

	@Override
	public Boolean get(Entity entity, EventData data)
	{
		if (entity == null) return false;
		
		if (isString)
			return ModDamage.getTagger().stringTags.onEntity.isTagged(entity, tag);
		else
			return ModDamage.getTagger().intTags.onEntity.isTagged(entity, tag);
	}
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Entity.class, pattern, new IDataParser<Boolean, Entity>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm)
				{
					return new EntityTagged(entityDP, m.group(2).toLowerCase(), m.group(1).equalsIgnoreCase("s"));
				}
			});
	}
	
	@Override
	public String toString()
	{
		return startDP + ".istagged." + tag;
	}
}
