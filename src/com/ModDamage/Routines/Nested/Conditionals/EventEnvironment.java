package com.ModDamage.Routines.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.World.Environment;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class EventEnvironment extends Conditional 
{
	public static final Pattern pattern = Pattern.compile("event\\.environment\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	protected final Environment environment;
	private final DataRef<World> worldRef;
	
	public EventEnvironment(String configString, Environment environment, DataRef<World> worldRef)
	{
		super(configString);
		this.environment = environment;
		this.worldRef = worldRef;
	}
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
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
					return new EventEnvironment(matcher.group(), environment, info.get(World.class, "world"));
			return null;
		}
	}
}
