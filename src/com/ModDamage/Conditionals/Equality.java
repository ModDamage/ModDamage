package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;

public class Equality extends Conditional<Object>
{
	public static final Pattern operatorPattern = Pattern.compile("\\s*([!=]?)=\\s*");
	protected final IDataProvider<Object> rightDP;
	protected final boolean equalTo;
	
	@SuppressWarnings("unchecked")
	protected Equality(Class<?> leftCls, IDataProvider<Object> left, boolean equalTo, IDataProvider<Object> right)
	{
		super((Class<Object>) leftCls, left);
		this.equalTo = equalTo;
		this.rightDP = right;
	}

	@Override
	public Boolean get(Object left, EventData data) throws BailException
	{
		Object right = rightDP.get(data);
		
		return left.equals(right) == equalTo;
	}

	@Override
	public Class<Boolean> provides() { return Boolean.class; }
	
	@Override
	public String toString()
	{
		return startDP + (equalTo?" == ":" != ") + rightDP;
	}

	
	public static void register()
	{
		DataProvider.register(Boolean.class, Object.class, operatorPattern, new IDataParser<Boolean, Object>()
			{
				@SuppressWarnings("unchecked")
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Object> leftDP, Matcher m, StringMatcher sm)
				{
					boolean equalTo = !m.group(1).equals("!");
					
					IDataProvider<Object> right = DataProvider.parse(info, null, sm.spawn());
					if (right == null) return null;
					
					IDataProvider<? extends Object> transformed = DataProvider.transform(leftDP.provides(), right, info, false);
					if (transformed != null)
						right = (IDataProvider<Object>) transformed;
					else {
						transformed = DataProvider.transform(right.provides(), leftDP, info, false);
						if (transformed != null)
							leftDP = (IDataProvider<Object>) transformed;
						else {
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Cannot compare equality of types " + leftDP.provides().getSimpleName() + " and " + right.provides().getSimpleName());
							return null;
						}
					}
					
					sm.accept();
					return new Equality(leftDP.provides(), leftDP, equalTo, right);
				}
			});
	}
}
