package com.moddamage.conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.backend.BailException;
import com.moddamage.expressions.InterpolatedString;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.tags.Tag;
import com.moddamage.tags.Taggable;

import com.moddamage.StringMatcher;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;

public class IsTagged<T> extends Conditional<T>
{
	public static final Pattern pattern = Pattern.compile("\\.is(s?)tagged\\.", Pattern.CASE_INSENSITIVE);

    private final Tag<?> tag;
    private final Taggable<T> taggable;
	
	@SuppressWarnings("unchecked")
	public IsTagged(Taggable<T> taggable, Tag<?> tag)
	{
		super((Class<T>) taggable.inner.provides(), taggable.inner);
		this.tag = tag;
        this.taggable = taggable;
	}

	@Override
	public Boolean get(T obj, EventData data) throws BailException
	{
		if (obj == null) return false;
		
		return taggable.has(tag, obj, data);
	}
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Object.class, pattern, new IDataParser<Boolean, Object>()
			{
				@Override
                @SuppressWarnings({ "unchecked", "rawtypes" })
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Object> objDP, Matcher m, StringMatcher sm)
				{
                    Tag<?> tag = Tag.get(InterpolatedString.parseWord(InterpolatedString.word, sm.spawn(), info), m.group(1));
                    Taggable<?> taggable = Taggable.get(objDP, info);
                    if (tag == null || taggable == null) return null;

                    sm.accept();
					return (IDataProvider<Boolean>) new IsTagged(taggable, tag);
				}
			});
	}
	
	@Override
	public String toString()
	{
		return taggable + ".istagged." + tag;
	}
}
