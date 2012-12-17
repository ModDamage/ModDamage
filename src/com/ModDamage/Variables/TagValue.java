package com.ModDamage.Variables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.SettableDataProvider;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Tags.Tag;
import com.ModDamage.Tags.Taggable;

public class TagValue<T, S> extends SettableDataProvider<T, S>
{	
	public static void register()
	{
		DataProvider.register(Object.class, Object.class, Pattern.compile("_(s?)tag(?:value)?_", Pattern.CASE_INSENSITIVE),
            new IDataParser<Object, Object>()
				{
					@Override
                    @SuppressWarnings({ "unchecked", "rawtypes" })
					public IDataProvider<Object> parse(EventInfo info, IDataProvider<Object> objDP, Matcher m, StringMatcher sm)
					{
                        Tag<?> tag = Tag.get(InterpolatedString.parseWord(InterpolatedString.word, sm.spawn(), info), m.group(1));
                        if (tag == null) return null;

                        Taggable<?> taggable = Taggable.get(objDP, info);

						return sm.acceptIf(new TagValue(tag, taggable, tag.defaultValue));
					}
				});
	}

    private final Tag<T> tag;
    private final Taggable<S> taggable;
	
	@SuppressWarnings("unchecked")
	TagValue(Tag<T> tag, Taggable<S> taggable, T defaultValue)
	{
		super((Class<S>)taggable.inner.provides(), taggable.inner);
        this.tag = tag;
        this.taggable = taggable;
        super.defaultValue = defaultValue;
    }
	
	
	@Override
	public T get(S obj, EventData data) throws BailException
	{
        return taggable.get(tag, obj, data);
	}

    @Override
    public void set(S obj, EventData data, T value) throws BailException
    {
        taggable.set(tag, obj, data, value);
    }

    @Override
    public Class<T> provides() {
        return tag.type;
    }
	
	@Override
	public boolean isSettable()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return taggable + "_tag_" + tag;
	}
}