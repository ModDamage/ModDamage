package com.ModDamage.Routines.Nested;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.ListExp;
import com.ModDamage.Routines.Routines;

@SuppressWarnings("rawtypes")
public class Foreach extends NestedRoutine
{
	protected final IDataProvider<List> listDP;
	protected final EventInfo myInfo;
	protected final Routines routines;

	private Foreach(String configString, IDataProvider<List> listDP, EventInfo myInfo, Routines routines)
	{
		super(configString);
		this.listDP = listDP;
		this.myInfo = myInfo;
		this.routines = routines;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		List list = listDP.get(data);
		if (list == null) return;

		for (Object obj : list) {
			EventData myData = myInfo.makeChainedData(data, obj);

			routines.run(myData);
		}
	}

	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("for(?:\\s*each)?\\s+(.+?)\\s+as\\s+(\\w+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());

	}

	protected final static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Foreach getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			if(matcher == null || nestedContent == null)
				return null;

			String name = matcher.group(2);
			IDataProvider<List> listDP = DataProvider.parse(info, List.class, matcher.group(1));
			if (listDP == null) return null;

			ListExp alistDP = (ListExp) listDP;

			EventInfo myInfo = info.chain(new SimpleEventInfo(alistDP.providesElement(), name));

			NestedRoutine.paddedLogRecord(OutputPreset.INFO, "Foreach " + listDP + " as " + name);

			Routines routines = RoutineAliaser.parseRoutines(nestedContent, myInfo);
			if(routines == null)
			{
				NestedRoutine.paddedLogRecord(OutputPreset.FAILURE, "Invalid content in Foreach");
				return null;
			}

			NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, "End Foreach");
			return new Foreach(matcher.group(), listDP, myInfo, routines);
		}
	}
}
