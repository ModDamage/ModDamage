package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Conditionals.Conditional;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routines;

public class If extends NestedRoutine
{
	protected final IDataProvider<Boolean> conditional;
	protected final Routines routines;
	public final boolean isElse;
	public If elseRoutine = null;
	
	private If(String configString, boolean isElse, IDataProvider<Boolean> conditional, Routines routines)
	{
		super(configString);
		this.isElse = isElse;
		this.conditional = conditional;
		this.routines = routines;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
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
		NestedRoutine.registerRoutine(Pattern.compile("(?:(else\\s*|el)?if\\s+(.*)|(else))", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
		Conditional.register();
	}

	protected final static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public If getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			if(matcher == null || nestedContent == null)
				return null;
			
			boolean isElse = matcher.group(1) != null || matcher.group(3) != null;
			
			IDataProvider<Boolean> conditional;
			if (matcher.group(3) == null)
			{
				conditional = DataProvider.parse(info, Boolean.class, matcher.group(2));
				if (conditional == null) return null;
			}
			else
			{
				conditional = new IDataProvider<Boolean>() {
						public Class<? extends Boolean> provides() { return Boolean.class; }
						public Boolean get(EventData data) { return true; }
					};
			}
			
			NestedRoutine.paddedLogRecord(OutputPreset.INFO, (isElse? "Else " : "") + (matcher.group(3) != null? "" : ("If: " + conditional)));
			
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, info);
			if(routines == null)
			{
				NestedRoutine.paddedLogRecord(OutputPreset.FAILURE, "Invalid content in If");
				return null;
			}
			
			NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, "End If");
			return new If(matcher.group(), isElse, conditional, routines);
		}
	}
}
