package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class CompoundConditional extends Conditional<Boolean>
{
	public enum LogicalOperator
	{
		AND
		{
			public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException
			{
				return left && right.get(data);
			}
		},
		OR
		{
			public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException
			{
				return left || right.get(data);
			}
		},
		XOR
		{
			public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException
			{
				return left ^ right.get(data);
			}
		},
		NAND
		{
			public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException
			{
				return !(left && right.get(data));
			}
		},
		NOR
		{
			public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException
			{
				return !(left && right.get(data));
			}
		},
		XNOR
		{
			public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException
			{
				return !(left ^ right.get(data));
			}
		};

		public static final Pattern pattern = Pattern.compile("\\s*(" + Utils.joinBy("|", LogicalOperator.values()) + ")\\s*", Pattern.CASE_INSENSITIVE);
		
		
		public static LogicalOperator match(String key)
		{
			try
			{
				return LogicalOperator.valueOf(key.toUpperCase());	
			}
			catch (IllegalArgumentException e) {}
			catch (NullPointerException e) {}
			
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid comparison operator \"" + key + "\"");
			return null;
		}
		
		abstract public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException;
	}
	
	
	private final LogicalOperator operator;
	private final IDataProvider<Boolean> right;
	public CompoundConditional(IDataProvider<Boolean> left, LogicalOperator operator, IDataProvider<Boolean> right)
	{
		super(Boolean.class, left);
		this.operator = operator;
		this.right = right;
	}

	@Override
	public Boolean get(Boolean left, EventData data) throws BailException
	{
		return operator.operate(data, left, right);
	}
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Boolean.class, LogicalOperator.pattern, new IDataParser<Boolean, Boolean>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Boolean> leftDP, Matcher m, StringMatcher sm)
				{
					LogicalOperator operator = LogicalOperator.match(m.group(1));
					
					IDataProvider<Boolean> rightDP = DataProvider.parse(info, Boolean.class, sm.spawn());
					if (rightDP == null) return null;
					
					return new CompoundConditional(leftDP, operator, rightDP);
				}
			});
	}
}
