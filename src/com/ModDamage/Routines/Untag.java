package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.MDLogger.OutputPreset;
import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Tags.Tag;
import com.ModDamage.Tags.Taggable;

@SuppressWarnings("rawtypes")
public class Untag<T, D> extends Routine
{
	private final Tag<D> tag;
	private final Taggable<T> taggable;
	
	protected Untag(ScriptLine scriptLine, Tag<D> tag, Taggable<T> taggable)
	{
		super(scriptLine);
        this.tag = tag;
        this.taggable = taggable;
	}
	@Override
	public void run(EventData data) throws BailException
	{
        taggable.remove(tag, data);
	}
	
	private static final Pattern dotPattern = Pattern.compile("\\s*\\.\\s*");

	public static void registerRoutine()
	{
		Routine.registerRoutine(Pattern.compile("un(s?)tag\\.(.*)", Pattern.CASE_INSENSITIVE), new UntagRoutineBuilder());
	}
	
	protected static class UntagRoutineBuilder extends Routine.RoutineFactory
	{
		@SuppressWarnings("unchecked")
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
            StringMatcher sm = new StringMatcher(matcher.group(2));
            
            Taggable<?> taggable = Taggable.get(DataProvider.parse(info, null, sm.spawn()), info);
            if (taggable == null) return null;
            
            if (!sm.matchesFront(dotPattern)) return null;
            
            IDataProvider<String> tagNameDP = InterpolatedString.parseWord(InterpolatedString.word, sm.spawn(), info);
            if (tagNameDP == null) return null;
            
            if (!sm.isEmpty()) return null;

            Tag<?> tag = Tag.get(tagNameDP, matcher.group(1));
            if (tag == null) return null;

			ModDamage.addToLogRecord(OutputPreset.INFO, "Un"+matcher.group(1)+"tag: \"" + tag + "\" on " + taggable);
			return new RoutineBuilder(new Untag(scriptLine, tag, taggable));
		}
	}
}
