package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Switch;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.SwitchRoutine.SingleValueSwitchRoutine;

public class WorldSwitch extends SingleValueSwitchRoutine<String>
{	
	public WorldSwitch(String configString, List<String> switchCases, List<Object> nestedContents)
	{ 
		super(configString, switchCases, nestedContents);
	}
	@Override
	protected String getRelevantInfo(TargetEventInfo eventInfo){ return eventInfo.world.getName();}
	@Override
	protected Collection<String> matchCase(String switchCase){ return AliasManager.matchWorldAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("event\\.world", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public WorldSwitch getNew(Matcher matcher, List<String> switchCases, List<Object> nestedContents)
		{
			return new WorldSwitch(matcher.group(), switchCases, nestedContents);
		}
	}
}
