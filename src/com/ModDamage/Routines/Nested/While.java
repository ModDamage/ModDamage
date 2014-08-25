package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.MDLogger.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

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
			IDataProvider<Boolean> conditional = DataProvider.parse(scriptLine, info, Boolean.class, matcher.group(1));
			if (conditional == null) return null;

			NestedRoutine.paddedLogRecord(OutputPreset.INFO, "While: " + conditional);

			While routine = new While(scriptLine, conditional);
			return new NestedRoutineBuilder(routine, routine.routines, info);
		}
	}
}
