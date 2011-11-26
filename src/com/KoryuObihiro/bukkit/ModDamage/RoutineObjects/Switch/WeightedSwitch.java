package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class WeightedSwitch extends SwitchRoutine<TargetEventInfo, Integer>
{
	protected static final Random random = new Random();
	protected int totalWeight;
	public WeightedSwitch(String configString, List<String> switchCases, List<Object> nestedContents)
	{
		super(configString, switchCases, nestedContents);
	}

	@Override
	protected TargetEventInfo getRelevantInfo(TargetEventInfo eventInfo){ return eventInfo;}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		int someNumber = random.nextInt()% totalWeight - 1;
		int highestNumberChecked = 0;
		for(int i = 0; i < switchCases.size(); i++)
		{
			highestNumberChecked += switchCases.get(i);
			if(someNumber <= highestNumberChecked)
			{
				for(Routine routine : switchRoutines.get(i))
					routine.run(eventInfo);
				break;
			}
		}
	}
	
	@Override
	protected Integer matchCase(String switchCase)
	{
		try
		{
			Integer integer = Integer.parseInt(switchCase);
			if(integer > 0)
			{
				totalWeight += integer;
				return integer;
			}
		}
		catch(NumberFormatException e){}
		return null;
	}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("weight(?:ed)?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public WeightedSwitch getNew(Matcher matcher, List<String> switchCases, List<Object> nestedContents)
		{
			return new WeightedSwitch(matcher.group(), switchCases, nestedContents);
		}
	}
}
