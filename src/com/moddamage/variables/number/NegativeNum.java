package com.moddamage.variables.number;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.BaseDataParser;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

public class NegativeNum implements IDataProvider<Number>
{
	public static void register()
	{
		DataProvider.register(Number.class, Pattern.compile("-"), new BaseDataParser<Number>()
				{
					@Override
					public IDataProvider<Number> parse(EventInfo info, Matcher m, StringMatcher sm)
					{
						IDataProvider<Number> number = DataProvider.parse(info, Number.class, sm.spawn());
						if (number == null) return null;
						
						return sm.acceptIf(new NegativeNum(number));
					}
				});
	}
	
	IDataProvider<Number> number;
	
	public NegativeNum(IDataProvider<Number> number)
	{
		this.number = number;
	}
	
	@Override
	public Number get(EventData data) throws BailException
	{
		Number num = number.get(data);
		if (num == null) return null;
		
		if (Utils.isFloating(num))
			return -num.doubleValue();
		else
			return -num.intValue();
	}

	@Override
	public Class<Number> provides() { return Number.class; }

	@Override
	public String toString()
	{
		return "-"+number;
	}
}
