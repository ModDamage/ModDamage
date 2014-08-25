package com.moddamage.routines;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.LogUtil;
import com.moddamage.Utils;
import com.moddamage.backend.BailException;
import com.moddamage.backend.ScriptLine;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.parsing.ISettableDataProvider;
import com.moddamage.parsing.SettableDataProvider;
import com.moddamage.variables.number.NumberOp;
import com.moddamage.variables.number.NumberOp.Operator;

@SuppressWarnings("rawtypes")
public class SetProperty extends Routine
{
	protected final ISettableDataProvider propertyDP;
	protected final IDataProvider valueDP;

	public SetProperty(ScriptLine scriptLine, ISettableDataProvider propertyDP, IDataProvider valueDP)
	{
		super(scriptLine);
		this.propertyDP = propertyDP;
		this.valueDP = valueDP;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(EventData data) throws BailException
	{
		propertyDP.set(data, valueDP.get(data));
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(?:set|change)(?:\\.|\\s+)(.*?)(?::|\\s*([-+*/^%])?=|\\s+to\\s)\\s*(.+)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}
	
	private static final Pattern truePattern = Pattern.compile("true|yes", Pattern.CASE_INSENSITIVE);
	private static final Pattern falsePattern = Pattern.compile("false|no", Pattern.CASE_INSENSITIVE);

	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			final ISettableDataProvider propertyDP = SettableDataProvider.parse(info, null, matcher.group(1));
			if (propertyDP == null) return null;
			if (!propertyDP.isSettable()) {
				LogUtil.error("Error: Variable \"" + propertyDP + "\" is read-only.");
				return null;
			}

			String value = matcher.group(3);
			IDataProvider valueDP = null;
			
			
			if (propertyDP.provides() == Boolean.class) {
				if (truePattern.matcher(value).matches())
					valueDP = new IDataProvider() {
						public Boolean get(EventData data) { return true; }
						public Class provides() { return Boolean.class; }
						public String toString() { return "true"; }
					};
				else if (falsePattern.matcher(value).matches())
					valueDP = new IDataProvider() {
						public Boolean get(EventData data) { return false; }
						public Class provides() { return Boolean.class; }
						public String toString() { return "false"; }
					};
			}
			else if (propertyDP.provides().isEnum()) {
				@SuppressWarnings("unchecked")
				Map<String, Enum<?>> stringMap = Utils.getTypeMapForEnum(propertyDP.provides(), true);
				
				final Enum<?> enumValue = stringMap.get(value.toUpperCase());
				if (enumValue != null) {
					valueDP = new IDataProvider() {
						public Object get(EventData data) { return enumValue; }
						public Class provides() { return propertyDP.provides(); }
						public String toString() { return enumValue.toString(); }
					};
				}
			}
			
			
			if (valueDP == null) {
				if (value.equalsIgnoreCase("none"))
					valueDP = new IDataProvider() {
						public Object get(EventData data) { return null; }

						public Class provides() { return propertyDP.provides(); }

						public String toString() { return "none"; }
					};
				else {
					@SuppressWarnings("unchecked")
					IDataProvider<Number> newValueDP = DataProvider.parse(info, propertyDP.provides(), value);
					if (newValueDP == null) return null;
					valueDP = newValueDP;
				}
			}
			

			String operation = matcher.group(2);
			if (operation != null) {
				if (Number.class.isAssignableFrom(propertyDP.provides())) {
					Operator op = Operator.operatorMap.get(operation);
					@SuppressWarnings("unchecked")
					IDataProvider<Number> newValueDP = new NumberOp(propertyDP, op, valueDP);
					valueDP = newValueDP;
				}
			}

			LogUtil.info("Set "+propertyDP+" to " + valueDP);
			

			return new RoutineBuilder(new SetProperty(scriptLine, propertyDP, valueDP));
		}
	}
}
