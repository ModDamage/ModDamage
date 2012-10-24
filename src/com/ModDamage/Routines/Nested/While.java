package com.ModDamage.Routines.Nested;

import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Conditionals.Conditional;
import com.ModDamage.Conditionals.InvertBoolean;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Routines.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class While extends NestedRoutine
{
	protected final IDataProvider<Boolean> conditional;
	protected final Routines routines;
	private While(String configString, IDataProvider<Boolean> conditional, Routines routines)
	{
		super(configString);
		this.conditional = conditional;
		this.routines = routines;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
		while(conditional.get(data))
			routines.run(data);
	}
	
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("while\\s+(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
		Conditional.register();
	}

	protected final static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public While getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			if(matcher == null || nestedContent == null)
				return null;
			
			
			IDataProvider<Boolean> conditional = DataProvider.parse(info, Boolean.class, matcher.group(1));
			if (conditional == null) return null;
			
			NestedRoutine.paddedLogRecord(OutputPreset.INFO, "While: " + conditional);
			
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, info);
			if(routines == null)
			{
				NestedRoutine.paddedLogRecord(OutputPreset.FAILURE, "Invalid content in While");
				return null;
			}
			
			NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, "End While");
			return new While(matcher.group(), conditional, routines);
		}
	}
}
