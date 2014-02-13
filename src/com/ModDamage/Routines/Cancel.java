package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.LogUtil;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.ISettableDataProvider;
import com.ModDamage.Routines.Nested.NestedRoutine;

public class Cancel extends NestedRoutine
{
	private final ISettableDataProvider<Boolean> cancelDP;
	
	protected Cancel(ScriptLine scriptLine, ISettableDataProvider<Boolean> cancelDP)
	{
		super(scriptLine);
		this.cancelDP = cancelDP;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		cancelDP.set(data, true);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("cancel", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}
	
	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			ISettableDataProvider<Boolean> cancelDP = info.get(Boolean.class, "cancelled", false);
			if(cancelDP == null)
			{
				LogUtil.error("This event cannot be cancelled.");
				return null;
			}
			
			LogUtil.info("Cancel");
			return new RoutineBuilder(new Cancel(scriptLine, cancelDP));
		}
	}
}
