package com.ModDamage.Routines.Nested;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.LogUtil;
import com.ModDamage.MDLogger.OutputPreset;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routine;
import com.ModDamage.Routines.Routines;

public class SwitchRoutine extends NestedRoutine
{
	protected final boolean all;
	protected final List<IDataProvider<Boolean>> switchCases; // = new ArrayList<IDataProvider<Boolean>>();
	protected final List<Routines> switchRoutines; // = new ArrayList<Routines>();
//	public final boolean isLoaded;
	public final List<String> failedCases = new ArrayList<String>();
	
	protected SwitchRoutine(ScriptLine scriptLine, boolean all, EventInfo info, List<IDataProvider<Boolean>> switchCases, List<Routines> switchRoutines)
	{
		super(scriptLine);
		this.all = all;
		
		assert(switchCases.size() == switchRoutines.size());
		this.switchCases = switchCases;
		this.switchRoutines = switchRoutines;
//		boolean caseFailed = false;
//		for(int i = 0; i < switchCases.size(); i++)
//		{
//			//get the case first, see if it refers to anything valid
//			String switchCase = switchCases.get(i);
//			IDataProvider<Boolean> matchedCase = DataProvider.parse(info, Boolean.class, switchType + "." + switchCases.get(i));
//			if(matchedCase != null)
//				NestedRoutine.paddedLogRecord(OutputPreset.INFO, " case: \"" + switchCase + "\"");
//			else
//			{
//				NestedRoutine.paddedLogRecord(OutputPreset.INFO, " case (failed): \"" + switchCase + "\"");
//				caseFailed = true;
//			}
//			//then grab the routines
//			Routines routines = RoutineAliaser.parseRoutines(nestedContents.get(i), info);
//			if(routines != null)
//			{
//				this.switchCases.add(matchedCase);
//				this.switchRoutines.add(routines);
//				NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, " End case \"" + switchCase + "\"");
//			}	
//			else
//			{
//				NestedRoutine.paddedLogRecord(OutputPreset.FAILURE, " Invalid content in case \"" + switchCase + "\"");
//				caseFailed = true;
//			}
//		}
//		isLoaded = !caseFailed;
	}
	
	@Override
	public void run(EventData data) throws BailException 
	{
		for(int i = 0; i < switchCases.size(); i++)
		{
			IDataProvider<Boolean> condition = switchCases.get(i);
			Boolean result = condition.get(data);
			if (result == null) continue;
			
			if(result)
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
		NestedRoutine.registerRoutine(Pattern.compile("switch(all)?[\\. ](.*)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}
	

	public static class SwitchRoutineBuilder implements IRoutineBuilder, ScriptLineHandler
	{
		final ScriptLine scriptLine;
		final String switchType;
		final boolean all;
		final EventInfo info;
		
		final List<IDataProvider<Boolean>> switchCases = new ArrayList<IDataProvider<Boolean>>();
		final List<Routines> switchRoutines = new ArrayList<Routines>();
		
		public SwitchRoutineBuilder(ScriptLine scriptLine, String switchType, boolean all, EventInfo info)
		{
			this.scriptLine = scriptLine;
			this.switchType = switchType;
			this.all = all;
			this.info = info;
		}

		@Override
		public ScriptLineHandler getScriptLineHandler()
		{
			return this;
		}

		@Override
		public Routine buildRoutine()
		{
			return new SwitchRoutine(scriptLine, all, info, switchCases, switchRoutines);
		}

		@Override
		public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren)
		{
			IDataProvider<Boolean> matchedCase = DataProvider.parse(info, Boolean.class, switchType + "." + line.line);
			if(matchedCase == null) return null;
			
			Routines routines = new Routines(scriptLine.origin);
			
			switchCases.add(matchedCase);
			switchRoutines.add(routines);
			
			LogUtil.info(" case " + matchedCase + ":");
			
			return routines.getLineHandler(info);
		}

		@Override
		public void done()
		{
		}

	}
	
	protected static class RoutineFactory extends NestedRoutine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			NestedRoutine.paddedLogRecord(OutputPreset.INFO, "Switch: \"" + matcher.group(2) + "\"");
//			SwitchRoutine routine = new SwitchRoutine(scriptLine, matcher.group(2), matcher.group(1) != null, info);
			return new SwitchRoutineBuilder(scriptLine, matcher.group(2), matcher.group(1) != null, info);
			
//			if(nestedContent != null)
//			{
//				NestedRoutine.paddedLogRecord(OutputPreset.INFO, "Switch: \"" + matcher.group() + "\"");
//				
//				if(nestedContent != null && nestedContent instanceof List)
//				{
//					List<?> rawSwitchCases = (List<?>)nestedContent;
//					List<String> switchCases = new ArrayList<String>();
//					List<Object> nestedContents = new ArrayList<Object>();
//					boolean finished = true;
//					for(Object object : rawSwitchCases)
//					{
//						if((object instanceof LinkedHashMap) && ((LinkedHashMap<?, ?>)object).size() == 1)
//						{
//							LinkedHashMap<String, Object> tempMap = ((LinkedHashMap<String, Object>)object);
//							switchCases.addAll(tempMap.keySet());
//							nestedContents.addAll(tempMap.values());
//						}
//						else 
//						{
//							finished = false;
//							break;
//						}
//					}
//					if(finished)
//					{
//						SwitchRoutine routine = new SwitchRoutine(scriptLine, matcher.group(2), matcher.group(1) != null, info, switchCases, nestedContents);
//						if(routine.isLoaded)
//						{
//							NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, "End switch \"" + matcher.group() + "\"");
//							return routine;
//						}
//						else 
//						{
//							LogUtil.console_only("");
//							LogUtil.error("Invalid content in switch \"" + matcher.group() + "\"");
//							for(String caseName : routine.failedCases)
//								LogUtil.error("Error: invalid case \"" + caseName + "\"");
//							LogUtil.console_only("");
//						}
//					}
//				}
//				else
//				{
//					LogUtil.error("Error: unexpected content " + nestedContent.toString() + " nested in switch \"" + matcher + "\"");
//					LogUtil.console_only("");
//				}
//			}
//			
//			LogUtil.error("Error: invalid switch \"" + matcher.group() + "\"" + (ModDamage.getDebugSetting().equals(DebugSetting.VERBOSE)?"\n":""));
//			
//			return null;
		}
	}
}
