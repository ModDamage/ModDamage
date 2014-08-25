package com.moddamage.conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.LogUtil;
import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;

public class CompoundConditional extends Conditional<Boolean>
{
	public enum LogicalOperator
	{
		AND
		{
			public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException
			{
				if (!left) return false;
				Boolean b = right.get(data);
				if (b == null) return false;
				return b;
			}
		},
		OR
		{
			public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException
			{
				if (left) return true;
				Boolean b = right.get(data);
				if (b == null) return false;
				return b;
			}
		},
		XOR
		{
			public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException
			{
				Boolean b = right.get(data);
				if (b == null) return false;
				return left ^ b;
			}
		},
		NAND
		{
			public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException
			{
				if (!left) return true;
				Boolean b = right.get(data);
				if (b == null) return true;
				return !b;
			}
		},
		NOR
		{
			public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException
			{
				if (left) return false;
				Boolean b = right.get(data);
				if (b == null) return true;
				return b;
			}
		},
		XNOR
		{
			public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException
			{
				Boolean b = right.get(data);
				if (b == null) return true;
				return !(left ^ b);
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
			
			LogUtil.error("Invalid comparison operator \"" + key + "\"");
			return null;
		}
		
		abstract public boolean operate(EventData data, boolean left, IDataProvider<Boolean> right) throws BailException;
	}
	
	
	public final LogicalOperator operator;
	public final IDataProvider<Boolean> rightDP;
	public CompoundConditional(IDataProvider<Boolean> left, LogicalOperator operator, IDataProvider<Boolean> right)
	{
		super(Boolean.class, left);
		this.operator = operator;
		this.rightDP = right;
	}

	@Override
	public Boolean get(Boolean left, EventData data) throws BailException
	{
		return operator.operate(data, left, rightDP);
	}

	@Override
	public String toString()
	{
		return startDP + " " + operator.name().toLowerCase() + " " + rightDP;
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
					
					sm.accept();
					return new CompoundConditional(leftDP, operator, rightDP);
				}
			});
	}
}
