package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.WorldAliaser;

public class EventWorld extends Conditional
{
	public static final Pattern pattern = Pattern.compile("event\\.world\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	protected final Collection<String> worlds;
	public EventWorld(Collection<String> worlds)
	{
		this.worlds = worlds;
	}
	@Override
	public boolean evaluate(TargetEventInfo eventInfo){ return worlds.contains(eventInfo.world.getName());}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EventWorld getNew(Matcher matcher)
		{
			Collection<String> worlds = WorldAliaser.match(matcher.group(1));
			if(!worlds.isEmpty())
				return new EventWorld(worlds);
			return null;
		}
	}
}
