package com.ModDamage.Routines.Nested;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.DebugSetting;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Utils;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Routines.Routines;

public class SwitchRoutine extends NestedRoutine 
{
	protected final List<IDataProvider<Boolean>> switchCases = new ArrayList<IDataProvider<Boolean>>();
	protected final List<Routines> switchRoutines = new ArrayList<Routines>();
	protected final boolean all;
	public final boolean isLoaded;
	public final List<String> failedCases = new ArrayList<String>();
	
	protected SwitchRoutine(String configString, String switchType, boolean all, EventInfo info, List<String> switchCases, List<Object> nestedContents)
	{
		super(configString);
		this.all = all;
		boolean caseFailed = false;
		for(int i = 0; i < switchCases.size(); i++)
		{
			//get the case first, see if it refers to anything valid
			String switchCase = switchCases.get(i);
			IDataProvider<Boolean> matchedCase = DataProvider.parse(info, Boolean.class, switchType + "." + switchCases.get(i));
			if(matchedCase != null)
				NestedRoutine.paddedLogRecord(OutputPreset.INFO, " case: \"" + switchCase + "\"");
			else
			{
				NestedRoutine.paddedLogRecord(OutputPreset.INFO, " case (failed): \"" + switchCase + "\"");
				caseFailed = true;
			}
			//then grab the routines
			Routines routines = RoutineAliaser.parseRoutines(nestedContents.get(i), info);
			if(routines != null)
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
	public void run(EventData data) throws BailException 
	{
		for(int i = 0; i < switchCases.size(); i++)
		{
			IDataProvider<Boolean> condition = switchCases.get(i);
			if(condition.get(data))
			{
				try
				{
					switchRoutines.get(i).run(data);
				}
				catch (BailException e)
				{
					throw new BailException("In case "+ condition.getClass().getSimpleName()
							+" "+ Utils.safeToString(condition), e);
				}
				if (!all) return;
			}
		}
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("switch(all)?\\.(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected final static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@SuppressWarnings("unchecked")
		@Override
		public SwitchRoutine getNew(Matcher switchMatcher, Object nestedContent, EventInfo info)
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
						SwitchRoutine routine = new SwitchRoutine(switchMatcher.group(), switchMatcher.group(2), switchMatcher.group(1) != null, info, switchCases, nestedContents);
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
