package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Switch;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World.Environment;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.SwitchRoutine.SingleValueSwitchRoutine;

public class EnvironmentSwitch extends SingleValueSwitchRoutine<Environment>
{	
	public EnvironmentSwitch(String configString, List<String> switchCases, List<Object> nestedContents){ super(configString, switchCases, nestedContents);}
	@Override
	protected Environment getRelevantInfo(TargetEventInfo eventInfo){ return eventInfo.world.getEnvironment();}
	@Override
	protected Collection<Environment> matchCase(String switchCase)
	{
		for(Environment environment : Environment.values())
			if(switchCase.equalsIgnoreCase(environment.name()))
				return Arrays.asList(environment);
		return null;
	}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("event\\.environment", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public EnvironmentSwitch getNew(Matcher matcher, List<String> switchCases, List<Object> nestedContents)
		{
			return new EnvironmentSwitch(matcher.group(), switchCases, nestedContents);
		}
	}
}