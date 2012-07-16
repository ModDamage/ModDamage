package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Conditionals.Conditional;
import com.ModDamage.Conditionals.InvertBoolean;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Routines.Routines;

public class If extends NestedRoutine
{
	protected final IDataProvider<Boolean> conditional;
	protected final Routines routines;
	private If(String configString, IDataProvider<Boolean> conditional, Routines routines)
	{
		super(configString);
		this.conditional = conditional;
		this.routines = routines;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
		if(conditional.get(data))
			routines.run(data);
	}
	
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("if(_not)?\\s+(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
		Conditional.register();
	}

	protected final static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public If getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			if(matcher == null || nestedContent == null)
				return null;
			
			NestedRoutine.paddedLogRecord(OutputPreset.INFO, "If: \"" + matcher.group() + "\"");
			
			String conditionalStr = matcher.group(2);
			
			IDataProvider<Boolean> conditional = DataProvider.parse(info, Boolean.class, conditionalStr);
			
			if (conditional == null)
				return null;
			
			if (matcher.group(1) != null)
				conditional = new InvertBoolean(conditional);
			
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, info);
			if(routines != null)
			{
				NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, "End If");
				return new If(matcher.group(), conditional, routines);
			}
			else NestedRoutine.paddedLogRecord(OutputPreset.FAILURE, "Invalid content in If");
			
			return null;
		}
	}
}
