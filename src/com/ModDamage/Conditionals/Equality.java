package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.LogUtil;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;

public class Equality extends Conditional<Object>
{
	public static final Pattern operatorPattern = Pattern.compile("\\s*([!=]?)=\\s*");
	protected final IDataProvider<Object> rightDP;
	protected final boolean equalTo, number;
	
	@SuppressWarnings("unchecked")
	protected Equality(Class<?> leftCls, IDataProvider<Object> left, boolean equalTo, IDataProvider<Object> right)
	{
		super((Class<Object>) leftCls, left);
		this.equalTo = equalTo;
		this.rightDP = right;
		
		this.number = Number.class.isAssignableFrom(left.provides()) || Number.class.isAssignableFrom(right.provides());
	}

	@Override
	public Boolean get(Object left, EventData data) throws BailException
	{
		Object right = rightDP.get(data);
		
		if (number && right != null && left instanceof Number && right instanceof Number) {
			if (Utils.isFloating((Number) left) || Utils.isFloating((Number) right)) {
				left = ((Number) left).doubleValue();
				right = ((Number) right).doubleValue();
			}
			else
			{
				left = ((Number) left).intValue();
				right = ((Number) right).intValue();
			}
		}
		
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
				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public IDataProvider<Boolean> parse(ScriptLine scriptLine, EventInfo info, IDataProvider<Object> leftDP, Matcher m, StringMatcher sm)
				{
					if (leftDP == null) return null;
					
					boolean equalTo = !m.group(1).equals("!");
					
					IDataProvider<Object> right = DataProvider.parse(scriptLine, info, null, sm.spawn());
					if (right == null) return null;
					
					if (Number.class.isAssignableFrom(leftDP.provides()) || Number.class.isAssignableFrom(right.provides())) {
						leftDP = (IDataProvider<Object>) (IDataProvider) DataProvider.transform(Number.class, leftDP, info, false);
						right = (IDataProvider<Object>) (IDataProvider) DataProvider.transform(Number.class, right, info, false);
					}
					else {
						IDataProvider<? extends Object> transformed = DataProvider.transform(leftDP.provides(), right, info, false);
						if (transformed != null)
							right = (IDataProvider<Object>) transformed;
						else {
							transformed = DataProvider.transform(right.provides(), leftDP, info, false);
							if (transformed != null)
								leftDP = (IDataProvider<Object>) transformed;
							else {
								LogUtil.error(scriptLine, "Cannot compare equality of types " + leftDP.provides().getSimpleName() + " and " + right.provides().getSimpleName());
								return null;
							}
						}
					}
					
					sm.accept();
					return new Equality(leftDP.provides(), leftDP, equalTo, right);
				}
			});
	}
}
