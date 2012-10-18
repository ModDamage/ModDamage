package com.ModDamage.Variables.String;

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
import com.ModDamage.EventInfo.SettableDataProvider;

public class EntityTagString extends SettableDataProvider<String, Entity>
{	
	public static void register()
	{
		DataProvider.register(String.class, Entity.class, Pattern.compile("_stag_", Pattern.CASE_INSENSITIVE), new IDataParser<String, Entity>()
				{
					@Override
					public IDataProvider<String> parse(EventInfo info, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm)
					{
                        IDataProvider<String> tagDP = InterpolatedString.parseWord(InterpolatedString.word, sm.spawn(), info);
                        if (tagDP == null) return null;

                        return sm.acceptIf(new EntityTagString(
								entityDP,
								tagDP));
					}
				});
	}

    protected final IDataProvider<String> tagDP;
	
	EntityTagString(IDataProvider<Entity> entityDP, IDataProvider<String> tagDP)
	{
		super(Entity.class, entityDP);
        this.tagDP = tagDP;
	}
	
	
	@Override
	public String get(Entity entity, EventData data) throws BailException
	{
        String tag = tagDP.get(data);
        if (tag == null) return null;
        tag = tag.toLowerCase();

        return ModDamage.getTagger().stringTags.getTagValue(entity, tag);
	}
	
	@Override
	public void set(Entity entity, EventData data, String value) throws BailException
	{
        String tag = tagDP.get(data);
        if (tag == null) return;
        tag = tag.toLowerCase();

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
		return startDP + "_stag_" + tagDP;
	}


	@Override
	public Class<String> provides()
	{
		return String.class;
	}
}