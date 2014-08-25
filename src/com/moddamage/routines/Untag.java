package com.moddamage.routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.LogUtil;
import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.backend.ScriptLine;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.expressions.InterpolatedString;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.tags.Tag;
import com.moddamage.tags.Taggable;

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

			LogUtil.info("Un"+matcher.group(1)+"tag: \"" + tag + "\" on " + taggable);
			return new RoutineBuilder(new Untag(scriptLine, tag, taggable));
		}
	}
}
