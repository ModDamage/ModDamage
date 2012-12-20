package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.DataProvider.BaseDataParser;

public class InvertBoolean implements IDataProvider<Boolean>
{
	public static final Pattern pattern = Pattern.compile("!\\s*");
	
	private IDataProvider<Boolean> bool;
	
	public InvertBoolean(IDataProvider<Boolean> bool)
	{
		this.bool = bool;
	}
	
	@Override
	public Boolean get(EventData data) throws BailException
	{
		return !bool.get(data);
	}

	@Override
	public Class<Boolean> provides() { return Boolean.class; }
	
	public static void register()
	{
		DataProvider.register(Boolean.class, pattern, new BaseDataParser<Boolean>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, Matcher m, StringMatcher sm)
				{
					IDataProvider<Boolean> bool = DataProvider.parse(info, Boolean.class, sm.spawn());
					if (bool == null) return null;
					
					sm.accept();
					return new InvertBoolean(bool);
				}
			});
	}
	
	@Override
	public String toString()
	{
		return "!" + bool;
	}
}
