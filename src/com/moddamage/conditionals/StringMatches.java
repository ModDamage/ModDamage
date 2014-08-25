package com.moddamage.conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.moddamage.LogUtil;
import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

public class StringMatches extends Conditional<String> 
{
	public static final Pattern pattern = Pattern.compile("\\.(i)?matches\\.(?:\"([^\"]+)\"|'([^']+)')", Pattern.CASE_INSENSITIVE);
	
	private final Pattern matchPattern;
	
	public StringMatches(IDataProvider<String> stringDP, Pattern matchPattern)
	{
		super(String.class, stringDP);
		this.matchPattern = matchPattern;
	}
	@Override
	public Boolean get(String str, EventData data) throws BailException
	{
		return matchPattern.matcher(str).matches();
	}
	
	@Override
	public String toString()
	{
		return startDP + ".matches." + matchPattern.pattern();
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, String.class, pattern, new IDataParser<Boolean, String>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<String> stringDP, Matcher m, StringMatcher sm)
				{
					String p = m.group(2);
					if (p == null)
						p = m.group(3);
					
					Pattern matchPattern;
					try {
						if (m.group(1) != null)
							matchPattern = Pattern.compile(p, Pattern.CASE_INSENSITIVE);
						else
							matchPattern = Pattern.compile(p);
					}
					catch (PatternSyntaxException ps)
					{
						LogUtil.error("Invalid regex: " + ps.getDescription());
						return null;
					}
					
					return new StringMatches(stringDP, matchPattern);
				}
			});
	}
}
