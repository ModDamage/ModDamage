package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.Alias.MessageAliaser;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.StringMatcher;
import com.ModDamage.Tags.Tag;
import com.ModDamage.Tags.Taggable;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Routines.Nested.NestedRoutine;

@SuppressWarnings("rawtypes")
public class TagAction<T, D> extends NestedRoutine
{
	private final Tag<D> tag;
	private final Taggable<T> taggable;
	private final IDataProvider<D> valueDP;
	
	protected TagAction(String configString, Tag<D> tag, Taggable<T> taggable, IDataProvider<D> valueDP, EventInfo myInfo)
	{
		super(configString);
        this.tag = tag;
        this.taggable = taggable;
		this.valueDP = valueDP;

        this.myInfo = myInfo;
	}
	@Override
	public void run(EventData data) throws BailException
	{
        if (valueDP == null)
            taggable.remove(tag, data);
        else
            taggable.set(tag, data, valueDP.get(myInfo.makeChainedData(data, taggable.get(tag, data))));
	}

	public static void registerRoutine()
	{
		Routine.registerRoutine(Pattern.compile("un(s?)tag\\.(\\w+)\\.(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	public static void registerNested()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(s?)tag\\.(\\w+)\\.(.+)", Pattern.CASE_INSENSITIVE), new NestedRoutineBuilder());
	}
	
	public final EventInfo myInfo;
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@SuppressWarnings("unchecked")
		@Override
		public TagAction getNew(Matcher matcher, EventInfo info)
		{
            StringMatcher sm = new StringMatcher(matcher.group(3));
            IDataProvider<String> tagNameDP = InterpolatedString.parseWord(InterpolatedString.word, sm.spawn(), info);
            if (tagNameDP == null) return null;

            Tag<?> tag = Tag.get(tagNameDP, matcher.group(1));
            Taggable<?> taggable = Taggable.get(DataProvider.parse(info, null, matcher.group(2)), info);
            if (tag == null || taggable == null) return null;

			ModDamage.addToLogRecord(OutputPreset.INFO, "un"+matcher.group(1)+"tag: \"" + tag + "\" on " + taggable);
			return new TagAction(matcher.group(), tag, taggable, null, null);
		}
	}
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
        @SuppressWarnings("unchecked")
		public TagAction getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
            StringMatcher sm = new StringMatcher(matcher.group(3));
            IDataProvider<String> tagNameDP = InterpolatedString.parseWord(InterpolatedString.word, sm.spawn(), info);
            if (tagNameDP == null) return null;

            Tag<?> tag = Tag.get(tagNameDP, matcher.group(1));
            Taggable<?> taggable = Taggable.get(DataProvider.parse(info, null, matcher.group(2)), info);
            if (tag == null || taggable == null) return null;

			ModDamage.addToLogRecord(OutputPreset.INFO, ""+matcher.group(1)+"tag: \"" + tag + "\" on " + taggable);
			
			ModDamage.changeIndentation(true);

            IDataProvider<?> valueDP;


            EventInfo myInfo = new SimpleEventInfo(tag.type, "value", "-default");

            if (tag.type == Integer.class)
            {
                EventInfo einfo = info.chain(myInfo);
                Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
                if(routines == null) return null;

                IDataProvider<Integer> value = IntegerExp.getNew(routines, einfo);
                if(value == null) return null;

                valueDP = value;
            }
            else if (tag.type == String.class)
            {
                EventInfo einfo = info.chain(myInfo);

                String string;

                if (nestedContent instanceof String)
                    string = (String) nestedContent;
                else {
                    ModDamage.addToLogRecord(OutputPreset.FAILURE, "Only one string allowed for stag");
                    return null;
                }

                IDataProvider<String> value = MessageAliaser.match(string, einfo).iterator().next();
                if(value == null) return null;

                valueDP = value;
            }
            else
                return null;
			
			ModDamage.changeIndentation(false);

			return new TagAction(matcher.group(), tag, taggable, valueDP, myInfo);
		}
	}
}
