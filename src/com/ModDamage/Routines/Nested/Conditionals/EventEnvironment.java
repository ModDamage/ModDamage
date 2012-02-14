package com.ModDamage.Routines.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.World.Environment;

import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class EventEnvironment extends Conditional 
{
	public static final Pattern pattern = Pattern.compile("event\\.environment\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	protected final Environment environment;
	private final DataRef<World> worldRef;
	public EventEnvironment(Environment environment, DataRef<World> worldRef)
	{
		this.environment = environment;
		this.worldRef = worldRef;
	}
	@Override
	public boolean evaluate(EventData data)
	{
		return worldRef.get(data).getEnvironment().equals(environment);
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EventEnvironment getNew(Matcher matcher, EventInfo info)
		{
			for(Environment environment : Environment.values())
				if(matcher.group(1).equalsIgnoreCase(environment.name()))
					return new EventEnvironment(environment, info.get(World.class, "world"));
			return null;
		}
	}
}
