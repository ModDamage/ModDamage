package com.ModDamage.Routines;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Utils;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.ISettableDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class ValueChange extends Routine 
{
	private static final LinkedHashMap<Pattern, ValueBuilder> builders = new LinkedHashMap<Pattern, ValueBuilder>();
	protected enum ValueChangeType
	{
		Add
		{
			@Override
			double changeValue(double current, double value){ return current + value; }
		},
		Set
		{
			@Override
			double changeValue(double current, double value){ return value; }
		},
		Subtract
		{
			@Override
			double changeValue(double current, double value){ return current - value; }
		};
		abstract double changeValue(double current, double value);

		public String getStringAppend(){ return " (" + this.name().toLowerCase() + ")"; }

	}
	
	private final ValueChangeType changeType;
	protected final IDataProvider<? extends Number> newValueDP;
	protected final ISettableDataProvider<Number> defaultDP;
	protected final boolean isFloating;
	protected ValueChange(String configString, ISettableDataProvider<Number> defaultDP, ValueChangeType changeType, IDataProvider<? extends Number> newValueDP)
	{
		super(configString);
		this.defaultDP = defaultDP;
		this.changeType = changeType;
		this.newValueDP = newValueDP;
		this.isFloating = (Utils.isFloating(defaultDP.provides()) || defaultDP.provides() == Number.class);
	}
	
	@Override
	public final void run(final EventData data) throws BailException{
		Number defN = defaultDP.get(data);
		if (defN == null)
			defN = isFloating? (Number) 0.0 : (Number) 0;
        
		Number value = getNewValue(defN, data);
		if (value == null) return;

		double newValue = changeType.changeValue(
				defN.doubleValue(), value.doubleValue());
		
		Class<?> provides = defaultDP.provides();
		
		if (provides == Double.class)
			defaultDP.set(data, (double) newValue);
		else if (provides == Float.class)
			defaultDP.set(data, (float) newValue);
		else if (provides == Long.class)
			defaultDP.set(data, (long) newValue);
		else if (provides == Integer.class)
			defaultDP.set(data, (int) newValue);
		else if (provides == Short.class)
			defaultDP.set(data, (short) newValue);
		else if (provides == Byte.class)
			defaultDP.set(data, (byte) newValue);
		else
			throw new Error("Unknown Number type: "+provides.getName());
	}
	
	protected Number getNewValue(Number def, EventData data) throws BailException
	{
		return newValueDP.get(data);
	}
	
	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(?:(\\+|add\\.)|(\\-|sub\\.)|(set\\.|=|))(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
		
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
			if (changeType == null) throw new Error("changeType == null $VC86");
			
			for(Entry<Pattern, ValueBuilder> entry : builders.entrySet())
			{
				Matcher anotherMatcher = entry.getKey().matcher(matcher.group(4));
				if(anotherMatcher.matches())
					return entry.getValue().getNew(anotherMatcher, changeType, info);
			}
			
			ISettableDataProvider<Number> defaultDP = info.get(Number.class, "-default");
			if (defaultDP == null) return null;
			IDataProvider<? extends Number> newValueDP = DataProvider.parse(info, defaultDP.provides(), matcher.group(4));
			if (newValueDP == null) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, changeType.name() + ": " + matcher.group(4));
			return new ValueChange(matcher.group(), defaultDP, changeType, newValueDP);
		}
	}
	
	public static void registerRoutine(Pattern pattern, ValueBuilder builder){ builders.put(pattern, builder); }
	public static abstract class ValueBuilder
	{
		public abstract ValueChange getNew(final Matcher matcher, final ValueChangeType changeType, EventInfo info);
	}
}
