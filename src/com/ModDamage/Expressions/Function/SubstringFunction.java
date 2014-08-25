package com.ModDamage.Expressions.Function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.LogUtil;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;

public class SubstringFunction extends DataProvider<String, String>
{
	private final IDataProvider<Integer> firstDP;
	private final IDataProvider<Integer> lastDP;

	private SubstringFunction(IDataProvider<String> stringDP, IDataProvider<Integer> firstDP, IDataProvider<Integer> lastDP)
	{
		super(String.class, stringDP);
		this.firstDP = firstDP;
		this.lastDP = lastDP;
	}

	@Override
	public String get(String str, EventData data) throws BailException
	{
		Integer first = firstDP.get(data);
		Integer last = lastDP.get(data);
		if (first == null || last == null) return null;
		
		while (first < 0)
			first += str.length();
		while (last < 0)
			last += str.length();
		
		if (last > str.length())
			last = str.length();
		
		return str.substring(first, last);
	}

	@Override
	public Class<String> provides() { return String.class; }

	static final Pattern commaPattern = Pattern.compile("\\s*,\\s*");
	static final Pattern endPattern = Pattern.compile("\\s*\\)");
	public static void register()
	{
		DataProvider.register(String.class, String.class, Pattern.compile("_(substr(?:ing))?\\("), new IDataParser<String, String>()
			{
				@Override
				public IDataProvider<String> parse(ScriptLine scriptLine, EventInfo info, IDataProvider<String> stringDP, Matcher m, StringMatcher sm)
				{
					@SuppressWarnings("unchecked")
					IDataProvider<Integer>[] args = new IDataProvider[2];

					for (int i = 0; i < 2; i++)
					{
						IDataProvider<Integer> arg = DataProvider.parse(scriptLine, info, Integer.class, sm.spawn());
						if (arg == null)
						{
							LogUtil.error(scriptLine, "Unable to match expression: \"" + sm.string + "\"");
							return null;
						}

						args[i] = arg;

						if (sm.matchesFront(commaPattern) != (i != 1))
						{
							LogUtil.error(scriptLine, "Wrong number of parameters for " + m.group(1) + " function: "+i);
							return null;
						}
					}


					Matcher endMatcher = sm.matchFront(endPattern);
					if (endMatcher == null)
					{
						LogUtil.error(scriptLine, "Missing end paren: \"" + sm.string + "\"");
						return null;
					}

					return sm.acceptIf(new SubstringFunction(stringDP, args[0], args[1]));
				}
			});
	}

	@Override
	public String toString()
	{
		return startDP + "_substr(" + firstDP + ", " + lastDP + ")";
	}
}
