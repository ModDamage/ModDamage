package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.ISettableDataProvider;
import com.ModDamage.EventInfo.SettableDataProvider;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;

public final class ChangeProperty extends NestedRoutine
{	
	private final Routines routines;
	protected final ISettableDataProvider<Integer> targetPropertyMatch;
	public ChangeProperty(String configString, Routines routines, ISettableDataProvider<Integer> targetPropertyMatch)
	{
		super(configString);
		this.routines = routines;
		this.targetPropertyMatch = targetPropertyMatch;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(Integer.class, "value", "-default");

	@Override
	public void run(EventData data) throws BailException
	{
		EventData myData = myInfo.makeChainedData(data, targetPropertyMatch.get(data));
		
		routines.run(myData);
		targetPropertyMatch.set(data, myData.get(Integer.class, myData.start));
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
			ISettableDataProvider<Integer> targetPropertyMatch = SettableDataProvider.parse(info, Integer.class, matcher.group(1));
            if (targetPropertyMatch == null) return null;
            if (!targetPropertyMatch.isSettable()) {
                ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: Variable \"" + matcher.group(1) + "\" is read-only.");
                return null;
            }

            ModDamage.addToLogRecord(OutputPreset.INFO, "Set "+targetPropertyMatch+":");

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if(routines == null) return null;

            return new ChangeProperty(matcher.group(), routines, targetPropertyMatch);
		}
	}
}
