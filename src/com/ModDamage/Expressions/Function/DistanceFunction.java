package com.ModDamage.Expressions.Function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.DataProvider.BaseDataParser;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class DistanceFunction implements IDataProvider<Integer>
{
	private final IDataProvider<Location> first, second;
	
	private DistanceFunction(IDataProvider<Location> first, IDataProvider<Location> second)
	{
		this.first = first;
		this.second = second;
	}

	@Override
	public Integer get(EventData data) throws BailException
	{
		Location f = first.get(data);
		Location s = second.get(data);
		if (f == null || s == null) return 0;
		
		return (int) f.distance(s);
	}

	@Override
	public Class<Integer> provides()
	{
		return Integer.class;
	}
	
	static final Pattern commaPattern = Pattern.compile("\\s*,\\s*");
	static final Pattern endPattern = Pattern.compile("\\s*\\)");
	public static void register()
	{
		DataProvider.register(Integer.class, Pattern.compile("(dist(?:ance)?)\\s*\\("), new BaseDataParser<Integer>()
			{
				@Override
				public IDataProvider<Integer> parse(EventInfo info, Matcher m, StringMatcher sm)
				{
					@SuppressWarnings("unchecked")
					IDataProvider<Location>[] args = new IDataProvider[2];
					
					for (int i = 0; i < 2; i++)
					{
						IDataProvider<Location> arg = DataProvider.parse(info, Location.class, sm.spawn());
						if (arg == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \"" + sm.string + "\"");
							return null;
						}
						
						args[i] = arg;
						
						if ((sm.matchFront(commaPattern) == null) != (i == 1))
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Wrong number of parameters for " + m.group(1) + " function: "+i);
							return null;
						}
					}
					
					
					Matcher endMatcher = sm.matchFront(endPattern);
					if (endMatcher == null)
					{
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Missing end paren: \"" + sm.string + "\"");
						return null;
					}
					
					return sm.acceptIf(new DistanceFunction(args[0], args[1]));
				}
			});
	}

	@Override
	public String toString()
	{
		return "distance(" + first + ", " + second + ")";
	}
}
