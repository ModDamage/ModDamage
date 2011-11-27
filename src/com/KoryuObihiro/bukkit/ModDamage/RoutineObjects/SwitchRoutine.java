package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.ArmorSetSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.BiomeSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.ConditionSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.EntityTypeSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.EnvironmentSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.PlayerGroupSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.WeightedSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.WieldSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.WorldSwitch;

public abstract class SwitchRoutine<EventInfoClass, CaseInfoClass> extends NestedRoutine 
{
	private static LinkedHashMap<Pattern, SwitchBuilder> registeredSwitchRoutines = new LinkedHashMap<Pattern, SwitchBuilder>();
	
	protected final List<CaseInfoClass> switchCases = new ArrayList<CaseInfoClass>();
	protected final List<List<Routine>> switchRoutines = new ArrayList<List<Routine>>();
	public final boolean isLoaded;
	public final List<String> failedCases = new ArrayList<String>();
	
	//TODO Definitely not as efficient as it could be. Refactor?
	protected SwitchRoutine(String configString, List<String> switchCases, List<Object> nestedContents)
	{
		super(configString);
		boolean caseFailed = false;
		for(int i = 0; i < switchCases.size(); i++)
		{
			//get the case first, see if it refers to anything valid
			String switchCase = switchCases.get(i);
			CaseInfoClass matchedCase = matchCase(switchCases.get(i));
			if(caseIsSane(matchedCase))
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
	
	protected boolean caseIsSane(CaseInfoClass someCase){ return someCase != null;}
	
	@Override
	public void run(TargetEventInfo eventInfo) 
	{
		EventInfoClass info = getRelevantInfo(eventInfo);
		if(info != null)
			for(int i = 0; i < switchCases.size(); i++)
				if(compare(info, switchCases.get(i)))
				{
					for(Routine routine : switchRoutines.get(i))
						routine.run(eventInfo);
					break;
				}
	}
	
	protected boolean compare(EventInfoClass info_event, CaseInfoClass info_case){ return info_event.equals(info_case);}
	
	abstract protected EventInfoClass getRelevantInfo(TargetEventInfo eventInfo);

	abstract protected CaseInfoClass matchCase(String switchCase);
	
	public static void register()
	{
		registeredSwitchRoutines.clear();
		NestedRoutine.registerRoutine(Pattern.compile("switch\\.(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
		registeredSwitchRoutines.clear();
		ArmorSetSwitch.register();
		BiomeSwitch.register();
		ConditionSwitch.register();
		EntityTypeSwitch.register();
		EnvironmentSwitch.register();
		PlayerGroupSwitch.register();
		WieldSwitch.register();
		WorldSwitch.register();
		WeightedSwitch.register();
	}

	protected static void registerSwitch(Pattern syntax, SwitchBuilder builder)
	{
		Routine.registerRoutine(registeredSwitchRoutines, syntax, builder);
	}
	
	protected final static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@SuppressWarnings("unchecked")
		@Override
		public SwitchRoutine<?, ?> getNew(Matcher switchMatcher, Object nestedContent)
		{
			if(switchMatcher != null && nestedContent != null)
			{
				if(switchMatcher.matches())
				{
					NestedRoutine.paddedLogRecord(OutputPreset.INFO, "Switch: \"" + switchMatcher.group() + "\"");
					for(Entry<Pattern, SwitchBuilder> entry : registeredSwitchRoutines.entrySet())
					{
						Matcher matcher = entry.getKey().matcher(switchMatcher.group(1));
						if(matcher.matches())
						{
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
									SwitchRoutine<?, ?> routine = entry.getValue().getNew(matcher, switchCases, nestedContents);
									if(routine != null)
									{
										if(routine.isLoaded)
										{
											NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, "End Switch \"" + switchMatcher.group() + "\"");
											return routine;
										}
										else 
										{
											ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
											ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid contents of Switch \"" + switchMatcher.group() + "\"");
											for(String caseName : routine.failedCases)
												ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: invalid case \"" + caseName + "\"");
											ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
										}
									}
								}
							}
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: unexpected nested content " + nestedContent.toString() + " in Switch routine \"" + switchMatcher + "\"");
							ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
							break;
						}
					}
				}
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: invalid Switch \"" + switchMatcher.group() + "\"" + (ModDamage.getDebugSetting().equals(DebugSetting.VERBOSE)?"\n":""));
			}
			return null;
		}
	}
	
	abstract protected static class SwitchBuilder
	{
		abstract protected SwitchRoutine<?, ?> getNew(Matcher matcher, List<String> switchCases, List<Object> nesteContents);
	}
	
	abstract public static class SingleValueSwitchRoutine<Type> extends SwitchRoutine<Type, Collection<Type>>
	{

		protected SingleValueSwitchRoutine(String configString, List<String> switchCases, List<Object> nestedContents)
		{
			super(configString, switchCases, nestedContents);
		}

		@Override
		protected boolean compare(Type info_event, Collection<Type> info_case)
		{
			return info_case.contains(info_event);
		}
		
		@Override
		protected boolean caseIsSane(Collection<Type> someCase){ return super.caseIsSane(someCase) && !someCase.isEmpty();}
	}
	
	abstract public static class EntitySingleTraitSwitchRoutine<InfoType> extends SingleValueSwitchRoutine<InfoType>
	{
		protected final ModDamageElement necessaryElement;
		protected final EntityReference entityReference;
		protected EntitySingleTraitSwitchRoutine(String configString, List<String> switchCases, List<Object> nestedContents, ModDamageElement necessaryElement, EntityReference entityReference) 
		{
			super(configString, switchCases, nestedContents);
			this.necessaryElement = necessaryElement;
			this.entityReference = entityReference;
		}
		
		protected Entity getRelevantEntity(TargetEventInfo eventInfo)
		{
			if(entityReference.getElement(eventInfo).matchesType(necessaryElement))
				return entityReference.getEntity(eventInfo);
			return null;
		}
	}
	
	abstract public static class EntityMultipleTraitSwitchRoutine<InfoType> extends SwitchRoutine<Collection<InfoType>, Collection<InfoType>>
	{
		protected final ModDamageElement necessaryElement;
		protected final EntityReference entityReference;
		protected EntityMultipleTraitSwitchRoutine(String configString, List<String> switchCases, List<Object> nestedContents, ModDamageElement necessaryElement, EntityReference entityReference) 
		{
			super(configString, switchCases, nestedContents);
			this.necessaryElement = necessaryElement;
			this.entityReference = entityReference;
		}
		
		protected Entity getRelevantEntity(TargetEventInfo eventInfo)
		{
			if(entityReference.getElement(eventInfo).matchesType(necessaryElement))
				return entityReference.getEntity(eventInfo);
			return null;
		}
		
		@Override
		protected boolean compare(Collection<InfoType> info_event, Collection<InfoType> info_case)
		{
			for(Object object : info_event)
				if(info_case.contains(object))
					return true;
			return false;
		}
		
		@Override
		protected boolean caseIsSane(Collection<InfoType> someCase){ return super.caseIsSane(someCase) && !someCase.isEmpty();}
	}
}
