package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class DiceRoll extends RandomRoutine 
{
	protected final boolean isAdditive;
	protected final DynamicInteger rollValue;
	protected DiceRoll(String configString)
	{
		super(configString);
		this.rollValue = DynamicInteger.getNew("event.value");
		this.isAdditive = false;
	}
	protected DiceRoll(String configString, DynamicInteger rollValue) 
	{
		super(configString);
		this.rollValue = rollValue;
		this.isAdditive = true;
	}

	@Override
	public void run(TargetEventInfo eventInfo)
	{
		eventInfo.eventValue = (isAdditive?eventInfo.eventValue:0) +  Math.abs(random.nextInt()%(rollValue.getValue(eventInfo) + 1));
	}
	
	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("roll(?:\\.(" + DynamicInteger.dynamicIntegerPart + "))?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public DiceRoll getNew(Matcher matcher)
		{ 
			if(!matcher.group(1).equalsIgnoreCase(""))
			{
				DynamicInteger match = DynamicInteger.getNew(matcher.group(2));
				if(match != null)
				{
					ModDamage.addToLogRecord(DebugSetting.NORMAL, "Dice Roll: " + matcher.group(1), LoadState.SUCCESS);
					return new DiceRoll(matcher.group(), match);
				}
			}
			else
			{
				ModDamage.addToLogRecord(DebugSetting.NORMAL, "Dice Roll: roll existing", LoadState.SUCCESS);
				return new DiceRoll(matcher.group());
			}
			return null;
		}
	}
}