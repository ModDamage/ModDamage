package com.moddamage.expressions.function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.LogUtil;
import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

public class IndexOfFunction extends DataProvider<Integer, String>
{
	private final boolean caseInsensitive;
	private final IDataProvider<String> otherDP;

	private IndexOfFunction(IDataProvider<String> stringDP, boolean caseInsensitive, IDataProvider<String> otherDP)
	{
		super(String.class, stringDP);
		this.caseInsensitive = caseInsensitive;
		this.otherDP = otherDP;
	}

	@Override
	public Integer get(String str, EventData data) throws BailException
	{
		String other = otherDP.get(data);
		if (other == null) return null;
		
		if (caseInsensitive) {
			str = str.toLowerCase();
			other = other.toLowerCase();
		}
		
		return str.indexOf(other);
	}

	@Override
	public Class<Integer> provides() { return Integer.class; }

	static final Pattern endPattern = Pattern.compile("\\s*\\)");
	public static void register()
	{
		DataProvider.register(Integer.class, String.class, Pattern.compile("_(i)?indexOf\\("), new IDataParser<Integer, String>()
			{
				@Override
				public IDataProvider<Integer> parse(EventInfo info, IDataProvider<String> stringDP, Matcher m, StringMatcher sm)
				{
					boolean caseInsensitive = (m.group(1) != null);
					
					IDataProvider<String> otherDP = DataProvider.parse(info, String.class, sm.spawn());
					if (otherDP == null) return null;

					Matcher endMatcher = sm.matchFront(endPattern);
					if (endMatcher == null)
					{
						LogUtil.error("Missing end paren: \"" + sm.string + "\"");
						return null;
					}

					return sm.acceptIf(new IndexOfFunction(stringDP, caseInsensitive, otherDP));
				}
			});
	}

	@Override
	public String toString()
	{
		return startDP + "_" + (caseInsensitive?"i":"") + "indexOf(" + otherDP + ")";
	}
}
