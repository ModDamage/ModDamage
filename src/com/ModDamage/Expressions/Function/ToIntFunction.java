package com.ModDamage.Expressions.Function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.LogUtil;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.BaseDataParser;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class ToIntFunction implements IDataProvider<Integer>
{
	private final IDataProvider<?> valDP;

	private ToIntFunction(IDataProvider<?> stringDP)
	{
		this.valDP = stringDP;
	}

	@Override
	public Integer get(EventData data) throws BailException
	{
		Object val = valDP.get(data);
		if (val == null) return null;
		
		if (val instanceof String) {
			try {
				return Integer.parseInt((String) val);
			}
			catch (NumberFormatException e) {
				return null;
			}
		}
		else if (val instanceof Number) {
			return ((Number) val).intValue();
		}
		else
			return null; // shouldn't happen
	}

	@Override
	public Class<Integer> provides() { return Integer.class; }

	static final Pattern endPattern = Pattern.compile("\\s*\\)");
	public static void register()
	{
		DataProvider.register(Integer.class, Pattern.compile("(?:to|as)int(?:eger)?\\(", Pattern.CASE_INSENSITIVE), new BaseDataParser<Integer>()
			{
				@Override
				public IDataProvider<Integer> parse(ScriptLine scriptLine, EventInfo info, Matcher m, StringMatcher sm)
				{
					IDataProvider<?> valDP;
					
					IDataProvider<Number> numberDP = DataProvider.parse(scriptLine, info, Number.class, sm.spawn(), false, false, null);
					if (numberDP != null)
						valDP = numberDP;
					else
					{
						IDataProvider<String> strDP = DataProvider.parse(scriptLine, info, String.class, sm.spawn(), false, false, null);
						if (strDP != null)
							valDP = strDP;
						else
						{
							IDataProvider<Object> objDP = DataProvider.parse(scriptLine, info, null, sm.spawn());
							if (objDP != null)
								LogUtil.error(scriptLine, "Wanted String or Number for toint(), not " + objDP.provides().getSimpleName());
							return null;
						}
					}
					
					Matcher endMatcher = sm.matchFront(endPattern);
					if (endMatcher == null)
					{
						LogUtil.error(scriptLine, "Missing end paren: \"" + sm.string + "\"");
						return null;
					}

					return sm.acceptIf(new ToIntFunction(valDP));
				}
			});
	}

	@Override
	public String toString()
	{
		return "toInt(" + valDP + ")";
	}
}
