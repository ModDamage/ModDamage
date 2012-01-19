package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.PluginConfiguration.OutputPreset;

public class Function extends DynamicInteger
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
	private final List<DynamicInteger> args;
	
	private Function(FunctionType funcType, List<DynamicInteger> args)
	{
		this.funcType = funcType;
		this.args = args;
	}

	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		int[] argValues = new int[args.size()];
		
		for (int i = 0; i < argValues.length; i++)
			argValues[i] = args.get(i).getValue(eventInfo);
		
		return funcType.evaluate(argValues);
	}
	
	
	public static void register()
	{
		final Pattern commaPattern = Pattern.compile("\\s*,\\s*");
		final Pattern endPattern = Pattern.compile("\\s*\\)");
		DynamicInteger.register(
				Pattern.compile("(\\w+)\\s*\\("),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher m, StringMatcher sm)
					{
						FunctionType ftype = FunctionType.match(m.group(1));
						if (ftype == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown function named: \"" + m.group(1) + "\"");
							return null;
						}
						
						List<DynamicInteger> args = new ArrayList<DynamicInteger>();
						while (true)
						{
							DynamicInteger arg = DynamicInteger.getIntegerFromFront(sm.spawn());
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
						
						sm.accept();
						return new Function(ftype, args);
					}
				});
	}
}
