package com.moddamage.conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.BaseDataParser;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

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
		Boolean b = bool.get(data);
		if (b == null) return true;
		return !b;
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
					return invert(bool);
				}
			});
	}
	
	// This is a hacky way to fix ! precedence
	public static IDataProvider<Boolean> invert(IDataProvider<Boolean> bool) {

		if (bool instanceof CompoundConditional) {
			CompoundConditional cc = (CompoundConditional) bool;
			
			cc.startDP = invert(cc.startDP);
			
			return cc;
		}
		
		return new InvertBoolean(bool);
	}
	
	@Override
	public String toString()
	{
		return "!" + bool;
	}
}
