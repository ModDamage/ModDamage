package com.ModDamage.Expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.BaseDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class Function implements IDataProvider<Integer>
{
	public static final Random random = new Random();
	
	enum FunctionType {
		ROLL(1) {
			@Override public int evaluate(int[] params)
			{
				return random.nextInt(params[0]+1);
			}
		},
		RANGE(2,3) {
			@Override public int evaluate(int[] params)
			{
				int interval = params.length == 3? params[2] : 1;
				return random.nextInt(params[1]+1-params[0])*interval + params[0];
			}
		},
		ABS(1) {
			@Override public int evaluate(int[] params)
			{
				return Math.abs(params[0]);
			}
		},
		SQRT(1) {
			@Override public int evaluate(int[] params)
			{
				return (int)Math.sqrt(params[0]);
			}
		},
		MIN(2, 100) {
			@Override public int evaluate(int[] params)
			{
				int v = params[0];
				for (int i = 1; i < params.length; i++)
					if (params[i] < v) v = params[i];
				return v;
			}
		},
		MAX(2, 100) {
			@Override public int evaluate(int[] params)
			{
				int v = params[0];
				for (int i = 1; i < params.length; i++)
					if (params[i] > v) v = params[i];
				return v;
			}
		};
		
		int minParams, maxParams;
		private FunctionType(int params) { minParams = params; maxParams = params; }
		private FunctionType(int min, int max) { minParams = min; maxParams = max; }
		
		public abstract int evaluate(int[] params);
		
		public static FunctionType match(String string)
		{
			try
			{
				return FunctionType.valueOf(string.toUpperCase());
			}
			catch (IllegalArgumentException e)
			{
				return null;
			}
		}
	}
	
	private final FunctionType funcType;
	private final List<IDataProvider<Integer>> args;
	
	private Function(FunctionType funcType, List<IDataProvider<Integer>> args)
	{
		this.funcType = funcType;
		this.args = args;
	}

	@Override
	public Integer get(EventData data) throws BailException
	{
		int[] argValues = new int[args.size()];
		
		for (int i = 0; i < argValues.length; i++)
			argValues[i] = args.get(i).get(data);
		
		return funcType.evaluate(argValues);
	}

	@Override
	public Class<Integer> provides() { return Integer.class; }
	
	
	public static void register()
	{
		final Pattern commaPattern = Pattern.compile("\\s*,\\s*");
		final Pattern endPattern = Pattern.compile("\\s*\\)");
		DataProvider.register(Integer.class, Pattern.compile("(\\w+)\\s*\\("), new BaseDataParser<Integer>()
			{
				@Override
				public IDataProvider<Integer> parse(EventInfo info, Class<?> want, Matcher m, StringMatcher sm)
				{
					FunctionType ftype = FunctionType.match(m.group(1));
					if (ftype == null)
					{
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown function named: \"" + m.group(1) + "\"");
						return null;
					}
					
					List<IDataProvider<Integer>> args = new ArrayList<IDataProvider<Integer>>();
					while (true)
					{
						IDataProvider<Integer> arg = DataProvider.parse(info, Integer.class, sm.spawn());
						if (arg == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \"" + sm.string + "\"");
							return null;
						}
						
						args.add(arg);
						
						if (sm.matchFront(commaPattern) == null)
							break;
					}
					
					
					Matcher endMatcher = sm.matchFront(endPattern);
					if (endMatcher == null)
					{
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Missing end paren: \"" + sm.string + "\"");
						return null;
					}
					
					if (args.size() < ftype.minParams || args.size() > ftype.maxParams)
					{
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Wrong number of parameters for " + m.group(1) + " function");
						return null;
					}
					
					return sm.acceptIf(new Function(ftype, args));
				}
			});
	}

	@Override
	public String toString()
	{
		return funcType.name().toLowerCase() + "(" + Utils.joinBy(", ", args) + ")";
	}
}
