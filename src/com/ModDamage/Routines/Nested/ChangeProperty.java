package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Routines.Routines;

public final class ChangeProperty extends NestedRoutine
{	
	private final Routines routines;
	protected final IntegerExp targetPropertyMatch;
	public ChangeProperty(String configString, Routines routines, IntegerExp targetPropertyMatch)
	{
		super(configString);
		this.routines = routines;
		this.targetPropertyMatch = targetPropertyMatch;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(IntRef.class, "value", "-default");

	@Override
	public void run(EventData data) throws BailException
	{
		IntRef value = new IntRef(targetPropertyMatch.getValue(data));
		EventData myData = myInfo.makeChainedData(data, value);
		
		routines.run(myData);
		targetPropertyMatch.setValue(data, value.value);
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(?:set|change)\\.(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{	
		@Override
		public ChangeProperty getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			IntegerExp targetPropertyMatch = IntegerExp.getNew(matcher.group(1), info);
			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if(targetPropertyMatch != null && routines != null)
			{
				if(targetPropertyMatch.isSettable())
					return new ChangeProperty(matcher.group(), routines, targetPropertyMatch);
				else
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: Variable \"" + matcher.group(1) + "\" is read-only.");
			}
			return null;
		}
	}
}
