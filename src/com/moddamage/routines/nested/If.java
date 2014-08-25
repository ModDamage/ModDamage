package com.moddamage.routines.nested;

import com.moddamage.LogUtil;
import com.moddamage.backend.BailException;
import com.moddamage.backend.ScriptLine;
import com.moddamage.conditionals.Conditional;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.routines.Routine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class If extends NestedRoutine
{
	public final IDataProvider<Boolean> conditional;
	public final boolean isElse;
	public If elseRoutine = null;
	
	private If(ScriptLine scriptLine, boolean isElse, IDataProvider<Boolean> conditional)
	{
		super(scriptLine);
		this.isElse = isElse;
		this.conditional = conditional;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
		if (conditional == null) { // else
			routines.run(data);
			return;
		}
		
		Boolean result = conditional.get(data);
		if(result != null) {
			if (result) {
				routines.run(data);
				return;
			}
		}
		
		if (elseRoutine != null)
			elseRoutine.run(data);
	}
	
	
	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(?:(else\\s*|el)?if\\s+(.*)|(else))", Pattern.CASE_INSENSITIVE), new RoutineFactory());
		Conditional.register();
	}

	protected static class RoutineFactory extends NestedRoutine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			boolean isElse = matcher.group(1) != null || matcher.group(3) != null;
			
			IDataProvider<Boolean> conditional;
			if (matcher.group(3) == null)
			{
				conditional = DataProvider.parse(info, Boolean.class, matcher.group(2));
				if (conditional == null) return null;
			}
			else
			{
				conditional = null;
			}
			
			LogUtil.info((isElse? "Else " : "") + (matcher.group(3) != null? "" : ("If: " + conditional)));
			
			
			If routine = new If(scriptLine, isElse, conditional);
			return new NestedRoutineBuilder(routine, routine.routines, info);
		}
	}
}
