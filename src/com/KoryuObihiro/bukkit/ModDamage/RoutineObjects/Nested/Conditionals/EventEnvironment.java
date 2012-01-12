package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World.Environment;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

public class EventEnvironment extends Conditional 
{
	public static final Pattern pattern = Pattern.compile("event\\.environment\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	protected final Environment environment;
	public EventEnvironment(Environment environment)
	{
		this.environment = environment;
	}
	@Override
	public boolean evaluate(TargetEventInfo eventInfo){ return eventInfo.world.getEnvironment().equals(environment);}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EventEnvironment getNew(Matcher matcher)
		{
			for(Environment environment : Environment.values())
				if(matcher.group(1).equalsIgnoreCase(environment.name()))
					return new EventEnvironment(environment);
			return null;
		}
	}
}
