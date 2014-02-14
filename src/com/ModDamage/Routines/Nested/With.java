package com.ModDamage.Routines.Nested;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.LogUtil;
import com.ModDamage.MDLogger.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.ISettableDataProvider;

public class With extends NestedRoutine
{
	protected final List<IDataProvider<?>> dps;
	protected final EventInfo myInfo;

	private With(ScriptLine scriptLine, List<IDataProvider<?>> dps, EventInfo myInfo)
	{
		super(scriptLine);
		this.dps = dps;
		this.myInfo = myInfo;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void run(EventData data) throws BailException
	{
		Object[] myDataObjs = new Object[dps.size()];

		for (int i = 0; i < myDataObjs.length; i++) {
			myDataObjs[i] = dps.get(i).get(data);
		}

		EventData myData = myInfo.makeChainedData(data, myDataObjs, true);

		routines.run(myData);

		for (int i = 0; i < myDataObjs.length; i++) {
			IDataProvider dp = dps.get(i);
			if (dp instanceof ISettableDataProvider) {
				ISettableDataProvider sdp = (ISettableDataProvider) dp;

				if (sdp.isSettable())
					sdp.set(data, myData.objects[i]);
			}
		}
	}

	private static Pattern asPattern = Pattern.compile("\\s+as\\s+(\\w+)");
	private static Pattern commaPattern = Pattern.compile("\\s*,\\s*");
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("with\\s+(.*)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}

	protected static class RoutineFactory extends NestedRoutine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			List<Object> infos = new ArrayList<Object>();
			List<IDataProvider<?>> dps = new ArrayList<IDataProvider<?>>();
			StringBuilder logSb = new StringBuilder();

			StringMatcher sm = new StringMatcher(matcher.group(1));

			do {

				IDataProvider<?> dp = DataProvider.parse(info, null, sm.spawn(), false, true, asPattern);
				if (dp == null) return null;

				Matcher m = sm.matchFront(asPattern);
				if (m == null) {
					NestedRoutine.paddedLogRecord(OutputPreset.FAILURE, "Expected \"as\" found \"" + sm.string + "\"");
					return null;
				}

				infos.add(dp.provides()); // type of object
				infos.add(m.group(1)); // name of object

				if (!dps.isEmpty()) logSb.append(", ");
				logSb.append(dp).append(" as ").append(m.group(1));

				dps.add(dp);

			} while (sm.matchesFront(commaPattern));

			EventInfo myInfo = info.chain(new SimpleEventInfo(infos.toArray(), true));

			LogUtil.info("With: " + logSb.toString());

			With routine = new With(scriptLine, dps, myInfo);
			return new NestedRoutineBuilder(routine, routine.routines, myInfo);
		}
	}
}
