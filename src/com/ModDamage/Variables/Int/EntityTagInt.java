package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.Expressions.InterpolatedString;
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
		DataProvider.register(Integer.class, Entity.class, Pattern.compile("_tag(?:value)?_", Pattern.CASE_INSENSITIVE),
            new IDataParser<Integer, Entity>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm)
					{
                        IDataProvider<String> tagDP = InterpolatedString.parseWord(InterpolatedString.word, sm.spawn(), info);
                        if (tagDP == null) return null;

						return sm.acceptIf(new EntityTagInt(
								entityDP,
								tagDP));
					}
				});
	}
	
	protected final IDataProvider<String> tagDP;
	
	EntityTagInt(IDataProvider<Entity> entityDP, IDataProvider<String> tagDP)
	{
		super(Entity.class, entityDP);
		this.tagDP = tagDP;
	}
	
	
	@Override
	public Integer myGet(Entity entity, EventData data) throws BailException
	{
        String tag = tagDP.get(data);
        if (tag == null) return null;
        tag = tag.toLowerCase();

        return ModDamage.getTagger().intTags.onEntity.getTagValue(entity, tag);
	}
	
	@Override
	public void mySet(Entity entity, EventData data, Integer value) throws BailException
	{
        String tag = tagDP.get(data);
        if (tag == null) return;
        tag = tag.toLowerCase();

        ModDamage.getTagger().intTags.onEntity.addTag(entity, tag, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return startDP + "_tag_" + tagDP;
	}
}