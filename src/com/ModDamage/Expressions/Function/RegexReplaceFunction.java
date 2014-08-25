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

public class RegexReplaceFunction extends DataProvider<String, String>
{
	private final boolean case_insensitive;
	private final boolean first;
	private final IDataProvider<String> findDP;
	private final IDataProvider<String> replacementDP;

	private RegexReplaceFunction(IDataProvider<String> stringDP, boolean case_insensitive, boolean first, IDataProvider<String> findDP, IDataProvider<String> replacementDP)
	{
		super(String.class, stringDP);
		this.case_insensitive = case_insensitive;
		this.first = first;
		this.findDP = findDP;
		this.replacementDP = replacementDP;
	}

	@Override
	public String get(String str, EventData data) throws BailException
	{
		String find = findDP.get(data);
		String replacement = replacementDP.get(data);
		if (find == null || replacement == null) return null;
		
		if (case_insensitive)
			find = "(?i)" + find;
		
		if (first) 
			return str.replaceFirst(find, replacement);
		else
			return str.replaceAll(find, replacement);
	}

	@Override
	public Class<String> provides() { return String.class; }

	static final Pattern commaPattern = Pattern.compile("\\s*,\\s*");
	static final Pattern endPattern = Pattern.compile("\\s*\\)");
	public static void register()
	{
		DataProvider.register(String.class, String.class, Pattern.compile("_(i)?r(?:e(?:gex)?)?replace(one|first)?\\("), new IDataParser<String, String>()
			{
				@Override
				public IDataProvider<String> parse(ScriptLine scriptLine, EventInfo info, IDataProvider<String> worldDP, Matcher m, StringMatcher sm)
				{
					@SuppressWarnings("unchecked")
					IDataProvider<String>[] args = new IDataProvider[2];

					for (int i = 0; i < 2; i++)
					{
						IDataProvider<String> arg = DataProvider.parse(scriptLine, info, String.class, sm.spawn());
						if (arg == null)
						{
							LogUtil.error(scriptLine, "Unable to match expression: \"" + sm.string + "\"");
							return null;
						}

						args[i] = arg;

						if (sm.matchesFront(commaPattern) != (i != 1))
						{
							LogUtil.error(scriptLine, "Wrong number of parameters for replace" + (m.group(1) != null?m.group(1):"") + " function: "+i);
							return null;
						}
					}


					Matcher endMatcher = sm.matchFront(endPattern);
					if (endMatcher == null)
					{
						LogUtil.error(scriptLine, "Missing end paren: \"" + sm.string + "\"");
						return null;
					}

					return sm.acceptIf(new RegexReplaceFunction(worldDP, m.group(1) != null, m.group(2) != null, args[0], args[1]));
				}
			});
	}

	@Override
	public String toString()
	{
		return startDP + "_"+(case_insensitive?"i":"")+"RegexReplace"+(first?"First":"")+"(" + findDP + ", " + replacementDP + ")";
	}
}
