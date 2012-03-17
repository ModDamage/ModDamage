package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;

import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.Aliasing.WorldAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class EventWorld extends Conditional
{
	public static final Pattern pattern = Pattern.compile("event\\.world\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	protected final Collection<String> worlds;
	private final DataRef<World> worldRef;

	public EventWorld(String configString, Collection<String> worlds, DataRef<World> worldRef)
	{
		super(configString);
		this.worlds = worlds;
		this.worldRef = worldRef;
	}

	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		return worlds.contains(worldRef.get(data).getName());
	}

	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}

	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EventWorld getNew(Matcher matcher, EventInfo info)
		{
			Collection<String> worlds = WorldAliaser.match(matcher.group(1));
			DataRef<World> worldRef = info.get(World.class, "world");
			if (!worlds.isEmpty() && worldRef != null)
				return new EventWorld(matcher.group(), worlds, worldRef);
			return null;
		}
	}
}
