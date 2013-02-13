package com.ModDamage.Routines.Nested;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EnumHelper;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Expressions.StringExp;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.ISettableDataProvider;
import com.ModDamage.Parsing.SettableDataProvider;
import com.ModDamage.Routines.Routines;

@SuppressWarnings("rawtypes")
public final class SetProperty extends NestedRoutine
{
	protected final ISettableDataProvider propertyDP;
	protected final IDataProvider valueDP;
	private final EventInfo myInfo;

	public SetProperty(String configString, ISettableDataProvider propertyDP, IDataProvider valueDP, EventInfo myInfo)
	{
		super(configString);
		this.propertyDP = propertyDP;
		this.valueDP = valueDP;
		this.myInfo = myInfo;
	}

	//static final EventInfo myInfo = new SimpleEventInfo(Integer.class, "value", "-default");

	@SuppressWarnings("unchecked")
	@Override
	public void run(EventData data) throws BailException
	{
		EventData myData;
		if (myInfo != null)
			myData = myInfo.makeChainedData(data, propertyDP.get(data));
		else
			myData = data;

		propertyDP.set(data, valueDP.get(myData));
	}

	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(?:set|change)\\.(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	private static final Pattern truePattern = Pattern.compile("true|yes", Pattern.CASE_INSENSITIVE);
	private static final Pattern falsePattern = Pattern.compile("false|no", Pattern.CASE_INSENSITIVE);

	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{	
		@SuppressWarnings("unchecked")
		@Override
		public SetProperty getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			final ISettableDataProvider propertyDP = SettableDataProvider.parse(info, null, matcher.group(1));
			if (propertyDP == null) return null;
			if (!propertyDP.isSettable()) {
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: Variable \"" + propertyDP + "\" is read-only.");
				return null;
			}

			IDataProvider valueDP = null;
			EventInfo myInfo;

			if (propertyDP.provides() == Integer.class) {
				myInfo = new SimpleEventInfo(Integer.class, "value", "-default");
				ModDamage.addToLogRecord(OutputPreset.INFO, "Set "+propertyDP+":");

				EventInfo einfo = info.chain(myInfo);
				Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
				if(routines == null) return null;

				valueDP = IntegerExp.getNew(routines, einfo);
			}
			else if (propertyDP.provides() == String.class) {
				List<IDataProvider<String>> messages = StringExp.getStrings(nestedContent, info);
				if (messages == null) return null;
				if (messages.size() != 1) {
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Wrong number of strings, only 1 allowed");
					return null;
				}

				myInfo = null;
				valueDP = messages.get(0);

				ModDamage.addToLogRecord(OutputPreset.INFO, "Set "+propertyDP+" to " + valueDP);
			}
			else {
				if (!(nestedContent instanceof String)) {
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Only string allowed, not "+nestedContent.getClass().getSimpleName());
					return null;
				}
				String v = (String) nestedContent;

				myInfo = null;
				
				if (propertyDP.provides() == Boolean.class) {
					if (truePattern.matcher(v).matches())
						valueDP = new IDataProvider() {
							public Boolean get(EventData data) { return true; }

							public Class provides() { return Boolean.class; }

							public String toString() { return "true"; }
						};
					else if (falsePattern.matcher(v).matches())
						valueDP = new IDataProvider() {
							public Boolean get(EventData data) { return false; }

							public Class provides() { return Boolean.class; }

							public String toString() { return "false"; }
						};
				}
				else if (propertyDP.provides().isEnum()) {
					Map<String, Enum<?>> stringMap = EnumHelper.getTypeMapForEnum(propertyDP.provides());
					
					for (Entry<String, Enum<?>> entry : stringMap.entrySet())
					{
						if (entry.getKey().equalsIgnoreCase(v)) {
							final Enum<?> value = entry.getValue();
							valueDP = new IDataProvider() {
								public Object get(EventData data) { return value; }
		
								public Class provides() { return propertyDP.provides(); }
		
								public String toString() { return value.toString(); }
							};
							break;
						}
					}
				}
				
				
				if (valueDP == null) {
					if (v.equalsIgnoreCase("none"))
						valueDP = new IDataProvider() {
							public Object get(EventData data) { return null; }
	
							public Class provides() { return propertyDP.provides(); }
	
							public String toString() { return "none"; }
						};
					else {
						valueDP = DataProvider.parse(info, propertyDP.provides(), v);
						if (valueDP == null) return null;
					}
				}

				ModDamage.addToLogRecord(OutputPreset.INFO, "Set "+propertyDP+" to " + valueDP);
			}

			return new SetProperty(matcher.group(), propertyDP, valueDP, myInfo);
		}
	}
}
