package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Switch;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.SwitchRoutine;
//FIXME Do I work?
public class ConditionSwitch extends SwitchRoutine<TargetEventInfo, Conditional>
{
	public ConditionSwitch(String configString, List<String> switchCases, List<Object> nestedContents)
	{
		super(configString, switchCases, nestedContents);
	}

	@Override
	protected TargetEventInfo getRelevantInfo(TargetEventInfo eventInfo){ return eventInfo;}
	
	@Override
	protected boolean compare(TargetEventInfo eventInfo, Conditional caseStatement)
	{
		return caseStatement.evaluate(eventInfo);
	}
	
	@Override
	protected Conditional matchCase(String switchCase){ return AliasManager.matchConditionAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("(?:condition|if)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public ConditionSwitch getNew(Matcher matcher, List<String> switchCases, List<Object> nestedContents)
		{
			return new ConditionSwitch(matcher.group(), switchCases, nestedContents);
		}
	}
}
