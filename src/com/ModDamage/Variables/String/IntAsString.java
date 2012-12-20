package com.ModDamage.Variables.String;

import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.StringExp;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class IntAsString extends StringExp<Integer>
{
	public IntAsString(IDataProvider<Integer> intDP)
	{
		super(Integer.class, intDP);
	}
	
	@Override
	public String get(Integer integer, EventData data)
	{
		return integer.toString();
	}
	
	public static void register()
	{
		DataProvider.registerTransformer(String.class, Integer.class, new IDataTransformer<String, Integer>()
			{
				@Override
				public IDataProvider<String> transform(EventInfo info, IDataProvider<Integer> intDP)
				{
					return new IntAsString(intDP);
				}
			});
	}

	@Override
	public String toString()
	{
		return startDP.toString();
	}
}
