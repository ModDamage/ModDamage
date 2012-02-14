package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Routines.Nested.Conditionals.Conditional;
import com.ModDamage.Routines.Nested.Conditionals.InvertConditional;

public class ConditionalRoutine extends NestedRoutine
{
	protected final Conditional conditional;
	protected final Routines routines;
	private ConditionalRoutine(String configString, Conditional conditional, Routines routines)
	{
		super(configString);
		this.conditional = conditional;
		this.routines = routines;
	}
	
	@Override
	public void run(EventData data)
	{
		if(conditional.evaluate(data))
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
		public ConditionalRoutine getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			if(matcher == null || nestedContent == null)
				return null;
			
			NestedRoutine.paddedLogRecord(OutputPreset.INFO, "Conditional: \"" + matcher.group() + "\"");
			
			String conditionalStr = matcher.group(2);
			
			Conditional conditional = Conditional.getNew(conditionalStr, info);
			
			if (conditional == null)
				return null;
			
			if (matcher.group(1) != null)
				conditional = new InvertConditional(conditional);
			
			//try
			//{
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, info);
			if(routines != null)
			{
				NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, "End conditional \"" + matcher.group() + "\"");
				return new ConditionalRoutine(matcher.group(), conditional, routines);
			}
			else NestedRoutine.paddedLogRecord(OutputPreset.FAILURE, "Invalid content in conditional \"" + matcher.group() + "\"");
			//}
			//catch (Exception e){ e.printStackTrace(); }
			
			return null;
		}
	}
}
