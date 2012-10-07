package com.ModDamage.Variables.String;

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
import com.ModDamage.EventInfo.SettableDataProvider;

public class EntityTagString extends SettableDataProvider<String, Entity>
{	
	public static void register()
	{
		DataProvider.register(String.class, Entity.class, Pattern.compile("_stag_(\\w+)", Pattern.CASE_INSENSITIVE), new IDataParser<String, Entity>()
				{
					@Override
					public IDataProvider<String> parse(EventInfo info, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm)
					{
						return sm.acceptIf(new EntityTagString(
								entityDP,
								m.group(1).toLowerCase()));
					}
				});
	}
	
	protected final String tag;
	
	EntityTagString(IDataProvider<Entity> entityDP, String tag)
	{
		super(Entity.class, entityDP);
		this.tag = tag;
	}
	
	
	@Override
	public String get(Entity entity, EventData data) throws BailException
	{
		return ModDamage.getTagger().stringTags.getTagValue(entity, tag);
	}
	
	@Override
	public void set(Entity entity, EventData data, String value)
	{
		ModDamage.getTagger().stringTags.addTag(entity, tag, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return startDP + "_stag_" + tag;
	}


	@Override
	public Class<String> provides()
	{
		return String.class;
	}
}