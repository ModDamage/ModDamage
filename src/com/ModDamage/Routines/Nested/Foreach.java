package com.ModDamage.Routines.Nested;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.MDLogger.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.ListExp;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routines;

@SuppressWarnings("rawtypes")
public class Foreach extends NestedRoutine
{
	protected final IDataProvider<List> listDP;
	protected final EventInfo myInfo;
	protected final Routines routines = new Routines();

	private Foreach(ScriptLine scriptLine, IDataProvider<List> listDP, EventInfo myInfo)
	{
		super(scriptLine);
		this.listDP = listDP;
		this.myInfo = myInfo;
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
		NestedRoutine.registerRoutine(Pattern.compile("for(?:\\s*each)?\\s+(.+?)\\s+as\\s+(\\w+)", Pattern.CASE_INSENSITIVE), new RoutineFactory());

	}

	protected static class RoutineFactory extends NestedRoutine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			String name = matcher.group(2);
			IDataProvider<List> listDP = DataProvider.parse(info, List.class, matcher.group(1));
			if (listDP == null) return null;

			ListExp alistDP = (ListExp) listDP;

			EventInfo myInfo = info.chain(new SimpleEventInfo(alistDP.providesElement(), name));

			ModDamage.addToLogRecord(OutputPreset.INFO, "Foreach " + listDP + " as " + name);

			Foreach routine = new Foreach(scriptLine, listDP, myInfo);
			return new NestedRoutineBuilder(routine, routine.routines, myInfo);
		}
	}
}
