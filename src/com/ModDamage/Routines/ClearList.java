package com.ModDamage.Routines;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.LogUtil;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.ISettableDataProvider;
import com.ModDamage.Routines.Nested.NestedRoutine;

@SuppressWarnings("rawtypes")
public class ClearList extends NestedRoutine
{
	private final ISettableDataProvider<List> listDP;

	protected ClearList(ScriptLine scriptLine, ISettableDataProvider<List> listDP)
	{
		super(scriptLine);
		this.listDP = listDP;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		List list = listDP.get(data);
		if (list == null) return;
		
		list.clear();
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("clear.(.*)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}
	
	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			ISettableDataProvider<List> listDP = info.get(List.class, matcher.group(1));
			if(listDP == null) return null;
			
			LogUtil.info("Clear " + listDP);
			return new RoutineBuilder(new ClearList(scriptLine, listDP));
		}
	}
}
