package com.moddamage.routines.nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.LogUtil;
import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.backend.BailException;
import com.moddamage.backend.ScriptLine;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;
import com.moddamage.expressions.LiteralNumber;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.routines.Routines;

public class For extends NestedRoutine
{
	protected final IDataProvider<Number> fromDP;
	protected final IDataProvider<Number> toDP;
	protected final IDataProvider<Number> byDP;
	protected final EventInfo myInfo;
	protected final Routines routines = new Routines();

	private For(ScriptLine scriptLine, IDataProvider<Number> fromDP, IDataProvider<Number> toDP, IDataProvider<Number> byDP, EventInfo myInfo)
	{
		super(scriptLine);
		this.fromDP = fromDP;
		this.toDP = toDP;
		this.byDP = byDP;
		this.myInfo = myInfo;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Number fromNum = fromDP.get(data);
		Number toNum = toDP.get(data);
		Number byNum = byDP.get(data);
		
		if (fromNum == null || toNum == null || byNum == null) return;
		
		if (Utils.isFloating(fromNum) || Utils.isFloating(toNum) || Utils.isFloating(byNum)) {
			double from = fromNum.doubleValue();
			double to = toNum.doubleValue();
			double by = byNum.doubleValue();

			double n = from;
			EventData myData = myInfo.makeChainedData(data, n);
			for (; n < to; n += by) {
				myData.objects[0] = n;

				routines.run(myData);
			}
		}
		else {
			long from = fromNum.longValue();
			long to = toNum.longValue();
			long by = byNum.longValue();
			
			long n = from;
			EventData myData = myInfo.makeChainedData(data, n);
			for (; n < to; n += by) {
				myData.objects[0] = n;

				routines.run(myData);
			}
		}
	}

	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("for\\s+(\\w+)\\s+from\\s+(.+)$", Pattern.CASE_INSENSITIVE), new RoutineFactory());

	}
	
	public static final Pattern toPattern = Pattern.compile("\\s+to\\s+"); 
	public static final Pattern byPattern = Pattern.compile("\\s*$|\\s+by\\s+"); 

	protected static class RoutineFactory extends NestedRoutine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			String name = matcher.group(1);
			
			StringMatcher sm = new StringMatcher(matcher.group(2));
			
			IDataProvider<Number> fromDP = DataProvider.parse(info, Number.class, sm.spawn(), false, true, toPattern);
			if (fromDP == null) return null;
			
			if (!sm.matchesFront(toPattern))
				return null;
			
			IDataProvider<Number> toDP = DataProvider.parse(info, Number.class, sm.spawn(), false, true, byPattern);
			if (toDP == null) return null;
			
			if (!sm.isEmpty() && !sm.matchesFront(toPattern))
				return null;
			
			IDataProvider<Number> byDP;
			if (sm.isEmpty()) {
				byDP = new LiteralNumber(1);
			}
			else {
				byDP = DataProvider.parse(info, Number.class, sm.spawn(), true, true, null);
				if (byDP == null) return null;
			}
			


			EventInfo myInfo = info.chain(new SimpleEventInfo(Number.class, name));

			LogUtil.info("For " + name + " from " + fromDP + " to " + toDP + " by " + byDP);

			For routine = new For(scriptLine, fromDP, toDP, byDP, myInfo);
			return new NestedRoutineBuilder(routine, routine.routines, myInfo);
		}
	}
}
