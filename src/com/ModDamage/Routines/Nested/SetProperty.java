package com.ModDamage.Routines.Nested;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.EventInfo.*;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Expressions.StringExp;
import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Routines.Routines;

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

	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{	
		@Override
		public SetProperty getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			final ISettableDataProvider propertyDP = SettableDataProvider.parse(info, null, matcher.group(1));
			if (propertyDP == null) return null;
			if (!propertyDP.isSettable()) {
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: Variable \"" + propertyDP + "\" is read-only.");
				return null;
			}

			IDataProvider valueDP;
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

				ModDamage.addToLogRecord(OutputPreset.INFO, "Set "+propertyDP+" to " + valueDP);
			}

			return new SetProperty(matcher.group(), propertyDP, valueDP, myInfo);
		}
	}
}
