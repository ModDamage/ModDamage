package com.ModDamage.Expressions.Function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;

public class ReplaceFunction extends DataProvider<String, String>
{
	private final boolean first;
	private final IDataProvider<String> findDP;
	private final IDataProvider<String> replacementDP;

	private ReplaceFunction(IDataProvider<String> stringDP, boolean first, IDataProvider<String> findDP, IDataProvider<String> replacementDP)
	{
		super(String.class, stringDP);
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
		
		if (first) {
			int index = str.indexOf(find);
			if (index != -1)
				return str.substring(0,  index) + str.substring(index+find.length(), str.length());
			else
				return str;
		}
		else
			return str.replace(find, replacement);
	}

	@Override
	public Class<String> provides() { return String.class; }

	static final Pattern commaPattern = Pattern.compile("\\s*,\\s*");
	static final Pattern endPattern = Pattern.compile("\\s*\\)");
	public static void register()
	{
		DataProvider.register(String.class, String.class, Pattern.compile("_replace(one|first)?\\("), new IDataParser<String, String>()
			{
				@Override
				public IDataProvider<String> parse(EventInfo info, IDataProvider<String> worldDP, Matcher m, StringMatcher sm)
				{
					@SuppressWarnings("unchecked")
					IDataProvider<String>[] args = new IDataProvider[2];

					for (int i = 0; i < 2; i++)
					{
						IDataProvider<String> arg = DataProvider.parse(info, String.class, sm.spawn());
						if (arg == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \"" + sm.string + "\"");
							return null;
						}

						args[i] = arg;

						if (sm.matchesFront(commaPattern) != (i != 1))
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Wrong number of parameters for replace" + (m.group(1) != null?m.group(1):"") + " function: "+i);
							return null;
						}
					}


					Matcher endMatcher = sm.matchFront(endPattern);
					if (endMatcher == null)
					{
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Missing end paren: \"" + sm.string + "\"");
						return null;
					}

					return sm.acceptIf(new ReplaceFunction(worldDP, m.group(1) != null, args[0], args[1]));
				}
			});
	}

	@Override
	public String toString()
	{
		return startDP + "_replace"+(first?"first":"")+"(" + findDP + ", " + replacementDP + ")";
	}
}
