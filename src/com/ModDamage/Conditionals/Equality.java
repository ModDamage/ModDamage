package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

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
		return left.equals(rightDP.get(data)) == equalTo;
	}

	@Override
	public Class<Boolean> provides() { return Boolean.class; }
	
	@Override
	public String toString()
	{
		return startDP + (equalTo?"==":"!=") + rightDP;
	}

	
	public static void register()
	{
		DataProvider.register(Boolean.class, Object.class, operatorPattern, new IDataParser<Boolean, Object>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, Class<?> want, IDataProvider<Object> leftDP, Matcher m, StringMatcher sm)
				{
					boolean equalTo = !m.group(1).equals("!");
					
					IDataProvider<Object> right = DataProvider.parse(info, leftDP.provides(), sm.spawn());
					
					sm.accept();
					return new Equality(leftDP.provides(), leftDP, equalTo, right);
				}
			});
	}
}
