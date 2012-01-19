package com.ModDamage.RoutineObjects.Nested;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.PluginConfiguration.DebugSetting;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.RoutineObjects.Routine;

public class SwitchRoutine extends NestedRoutine 
{
	protected final List<Conditional> switchCases = new ArrayList<Conditional>();
	protected final List<List<Routine>> switchRoutines = new ArrayList<List<Routine>>();
	public final boolean isLoaded;
	public final List<String> failedCases = new ArrayList<String>();
	
	protected SwitchRoutine(String configString, String switchType, List<String> switchCases, List<Object> nestedContents)
	{
		super(configString);
		boolean caseFailed = false;
		for(int i = 0; i < switchCases.size(); i++)
		{
			//get the case first, see if it refers to anything valid
			String switchCase = switchCases.get(i);
			Conditional matchedCase = Conditional.getNew(switchType + "." + switchCases.get(i));
			if(matchedCase != null)
				NestedRoutine.paddedLogRecord(OutputPreset.INFO, " case: \"" + switchCase + "\"");
			else
			{
				NestedRoutine.paddedLogRecord(OutputPreset.INFO, " case (failed): \"" + switchCase + "\"");
				caseFailed = true;
			}
			//then grab the routines
			List<Routine> routines = new ArrayList<Routine>();
			if(RoutineAliaser.parseRoutines(routines, nestedContents.get(i)))
			{
				this.switchCases.add(matchedCase);
				this.switchRoutines.add(routines);
				NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, " End case \"" + switchCase + "\"");
			}	
			else
			{
				NestedRoutine.paddedLogRecord(OutputPreset.FAILURE, " Invalid content in case \"" + switchCase + "\"");
				caseFailed = true;
			}
		}
		isLoaded = !caseFailed;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo) 
	{
		for(int i = 0; i < switchCases.size(); i++)
			if(switchCases.get(i).evaluate(eventInfo))
			{
				for(Routine routine : switchRoutines.get(i))
					routine.run(eventInfo);
				return;
			}
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("switch\\.(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected final static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@SuppressWarnings("unchecked")
		@Override
		public SwitchRoutine getNew(Matcher switchMatcher, Object nestedContent)
		{
			if(switchMatcher != null && nestedContent != null)
			{
				NestedRoutine.paddedLogRecord(OutputPreset.INFO, "Switch: \"" + switchMatcher.group() + "\"");
				
				if(nestedContent != null && nestedContent instanceof List)
				{
					List<?> rawSwitchCases = (List<?>)nestedContent;
					List<String> switchCases = new ArrayList<String>();
					List<Object> nestedContents = new ArrayList<Object>();
					boolean finished = true;
					for(Object object : rawSwitchCases)
					{
						if((object instanceof LinkedHashMap) && ((LinkedHashMap<?, ?>)object).size() == 1)
						{
							LinkedHashMap<String, Object> tempMap = ((LinkedHashMap<String, Object>)object);
							switchCases.addAll(tempMap.keySet());
							nestedContents.addAll(tempMap.values());
						}
						else 
						{
							finished = false;
							break;
						}
					}
					if(finished)
					{
						SwitchRoutine routine = new SwitchRoutine(switchMatcher.group(), switchMatcher.group(1), switchCases, nestedContents);
						if(routine.isLoaded)
						{
							NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, "End switch \"" + switchMatcher.group() + "\"");
							return routine;
						}
						else 
						{
							ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid content in switch \"" + switchMatcher.group() + "\"");
							for(String caseName : routine.failedCases)
								ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: invalid case \"" + caseName + "\"");
							ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
						}
					}
				}
				else
				{
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: unexpected content " + nestedContent.toString() + " nested in switch \"" + switchMatcher + "\"");
					ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
				}
			}
				
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: invalid switch \"" + switchMatcher.group() + "\"" + (ModDamage.getDebugSetting().equals(DebugSetting.VERBOSE)?"\n":""));
			
			return null;
		}
	}
}
