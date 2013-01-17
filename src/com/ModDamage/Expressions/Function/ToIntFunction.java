package com.ModDamage.Expressions.Function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.BaseDataParser;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class ToIntFunction implements IDataProvider<Integer>
{
	private final IDataProvider<String> stringDP;

	private ToIntFunction(IDataProvider<String> stringDP)
	{
		this.stringDP = stringDP;
	}

	@Override
	public Integer get(EventData data) throws BailException
	{
		String str = stringDP.get(data);
		if (str == null) return null;
		
		try {
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public Class<Integer> provides() { return Integer.class; }

	static final Pattern endPattern = Pattern.compile("\\s*\\)");
	public static void register()
	{
		DataProvider.register(Integer.class, Pattern.compile("(?:to|as)int(?:eger)?\\(", Pattern.CASE_INSENSITIVE), new BaseDataParser<Integer>()
			{
				@Override
				public IDataProvider<Integer> parse(EventInfo info, Matcher m, StringMatcher sm)
				{
					IDataProvider<String> stringDP = DataProvider.parse(info, String.class, sm.spawn());
					if (stringDP == null) return null;

					Matcher endMatcher = sm.matchFront(endPattern);
					if (endMatcher == null)
					{
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Missing end paren: \"" + sm.string + "\"");
						return null;
					}

					return sm.acceptIf(new ToIntFunction(stringDP));
				}
			});
	}

	@Override
	public String toString()
	{
		return "toInt(" + stringDP + ")";
	}
}
