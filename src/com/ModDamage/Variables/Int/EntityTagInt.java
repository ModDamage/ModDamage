package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.SettableIntegerExp;

public class EntityTagInt extends SettableIntegerExp<Entity>
{	
	public static void register()
	{
		DataProvider.register(Integer.class, Entity.class, Pattern.compile("_tag(?:value)?_(\\w+)", Pattern.CASE_INSENSITIVE), new IDataParser<Integer, Entity>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm)
					{
						return sm.acceptIf(new EntityTagInt(
								entityDP,
								m.group(1).toLowerCase()));
					}
				});
	}
	
	protected final String tag;
	
	EntityTagInt(IDataProvider<Entity> entityDP, String tag)
	{
		super(Entity.class, entityDP);
		this.tag = tag;
	}
	
	
	@Override
	public Integer myGet(Entity entity, EventData data) throws BailException
	{
		return ModDamage.getTagger().intTags.getTagValue(entity, tag);
	}
	
	@Override
	public void mySet(Entity entity, EventData data, Integer value)
	{
		ModDamage.getTagger().intTags.addTag(entity, tag, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return startDP + "_tag_" + tag;
	}
}