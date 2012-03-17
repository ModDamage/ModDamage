package com.ModDamage.Routines;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;

public class ValueChange extends Routine 
{
	private static final LinkedHashMap<Pattern, ValueBuilder> builders = new LinkedHashMap<Pattern, ValueBuilder>();
	protected enum ValueChangeType
	{
		Add
		{
			@Override
			int changeValue(int current, int value){ return current + value; }
		},
		Set
		{
			@Override
			int changeValue(int current, int value){ return value; }
		},
		Subtract
		{
			@Override
			int changeValue(int current, int value){ return current - value; }
		};
		abstract int changeValue(int current, int value);

		public String getStringAppend(){ return " (" + this.name().toLowerCase() + ")"; }

	}
	
	private final ValueChangeType changeType;
	protected final IntegerExp number;
	protected final DataRef<IntRef> defaultRef;
	protected ValueChange(String configString, DataRef<IntRef> defaultRef, ValueChangeType changeType, IntegerExp number)
	{
		super(configString);
		this.defaultRef = defaultRef;
		this.changeType = changeType;
		this.number = number;
	}
	
	@Override
	public final void run(final EventData data) throws BailException{
		IntRef def = defaultRef.get(data);
		def.value = changeType.changeValue(
				def.value, getValue(data));
	}
	
	protected int getValue(EventData data) throws BailException
	{
		return number.getValue(data);
	}
	
	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(?:(\\+|add\\.)|(\\-|sub\\.)|(set\\.|=|))(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
		
		DiceRoll.register();
		Division.register();
		Multiplication.register();
	}
	
	private static final class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public ValueChange getNew(final Matcher matcher, EventInfo info)
		{
			ValueChangeType changeType = null;
			if(matcher.group(1) != null)
				changeType = ValueChangeType.Add;
			else if(matcher.group(2) != null)
				changeType = ValueChangeType.Subtract;
			else if(matcher.group(3) != null)
				changeType = ValueChangeType.Set;
			assert(changeType != null);
			
			for(Entry<Pattern, ValueBuilder> entry : builders.entrySet())
			{
				Matcher anotherMatcher = entry.getKey().matcher(matcher.group(4));
				if(anotherMatcher.matches())
					return entry.getValue().getNew(anotherMatcher, changeType, info);
			}
			IntegerExp integer = IntegerExp.getNew(matcher.group(4), info, false);
			DataRef<IntRef> defaultRef = info.get(IntRef.class, "-default");
			if(integer != null && defaultRef != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, changeType.name() + ": " + matcher.group(4));
				return new ValueChange(matcher.group(), defaultRef, changeType, integer);
			}
			return null;
		}
	}
	
	public static void registerRoutine(Pattern pattern, ValueBuilder builder){ builders.put(pattern, builder); }
	public static abstract class ValueBuilder
	{
		public abstract ValueChange getNew(final Matcher matcher, final ValueChangeType changeType, EventInfo info);
	}
}
