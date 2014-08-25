package com.moddamage.routines.nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.MDLogger.OutputPreset;
import com.moddamage.backend.BailException;
import com.moddamage.backend.ScriptLine;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

public class While extends NestedRoutine
{
	protected final IDataProvider<Boolean> conditional;
	private While(ScriptLine scriptLine, IDataProvider<Boolean> conditional)
	{
		super(scriptLine);
		this.conditional = conditional;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		do {
			Boolean result = conditional.get(data);
			if (result == null) return;
			if (!result) return;
		
			routines.run(data);
		} while (true);
	}


	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("while\\s+(.*)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}

	protected static class RoutineFactory extends NestedRoutine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			IDataProvider<Boolean> conditional = DataProvider.parse(info, Boolean.class, matcher.group(1));
			if (conditional == null) return null;

			NestedRoutine.paddedLogRecord(OutputPreset.INFO, "While: " + conditional);

			While routine = new While(scriptLine, conditional);
			return new NestedRoutineBuilder(routine, routine.routines, info);
		}
	}
}
