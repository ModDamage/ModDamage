package com.ModDamage.Expressions.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.Parsing.BaseDataParser;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.LogUtil;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class IntFunction implements IDataProvider<Integer>
{
	public static final Random random = new Random();
	
	enum FunctionType {
		ROLL(1) {
			@Override public int evaluate(int[] params)
			{
				if (params[0] < 0)
					return -random.nextInt(-params[0]+1);
				return random.nextInt(params[0]+1);
			}
		},
		RANGE(2,3) {
			@Override public int evaluate(int[] params)
			{
				if (params[0] > params[1]) {
					int temp = params[0];
					params[0] = params[1];
					params[1] = temp;
				}
				
				int size = params[1]+1 - params[0];
				
				if (params.length == 3) {
					int interval = params[2];
					return random.nextInt(size / interval)*interval + params[0];
				}
				return random.nextInt(size) + params[0];
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
	
	private IntFunction(FunctionType funcType, List<IDataProvider<Integer>> args)
	{
		this.funcType = funcType;
		this.args = args;
	}

	@Override
	public Integer get(EventData data) throws BailException
	{
		int[] argValues = new int[args.size()];
		
		for (int i = 0; i < argValues.length; i++) {
			Integer value = args.get(i).get(data);
			if (value == null)
				return null;
			
			argValues[i] = value;
		}
		
		return funcType.evaluate(argValues);
	}

	@Override
	public Class<Integer> provides() { return Integer.class; }
	
	static final Pattern commaPattern = Pattern.compile("\\s*,\\s*");
	static final Pattern endPattern = Pattern.compile("\\s*\\)");
	public static void register()
	{
		DataProvider.register(Integer.class, Pattern.compile("("+Utils.joinBy("|", FunctionType.values())+")\\s*\\(", Pattern.CASE_INSENSITIVE), new BaseDataParser<Integer>()
			{
				@Override
				public IDataProvider<Integer> parse(ScriptLine scriptLine, EventInfo info, Matcher m, StringMatcher sm)
				{
					FunctionType ftype = FunctionType.match(m.group(1));
					if (ftype == null)
					{
						LogUtil.error(scriptLine, "Unknown function named: \"" + m.group(1) + "\"");
						return null;
					}
					
					List<IDataProvider<Integer>> args = new ArrayList<IDataProvider<Integer>>();
					while (true)
					{
						IDataProvider<Integer> arg = DataProvider.parse(scriptLine, info, Integer.class, sm.spawn());
						if (arg == null)
						{
							LogUtil.error(scriptLine, "Unable to match expression: \"" + sm.string + "\"");
							return null;
						}
						
						args.add(arg);
						
						if (sm.matchFront(commaPattern) == null)
							break;
					}
					
					
					Matcher endMatcher = sm.matchFront(endPattern);
					if (endMatcher == null)
					{
						LogUtil.error(scriptLine, "Missing end paren: \"" + sm.string + "\"");
						return null;
					}
					
					if (args.size() < ftype.minParams || args.size() > ftype.maxParams)
					{
						LogUtil.error(scriptLine, "Wrong number of parameters for " + m.group(1) + " function");
						return null;
					}
					
					return sm.acceptIf(new IntFunction(ftype, args));
				}
			});
	}

	@Override
	public String toString()
	{
		return funcType.name().toLowerCase() + "(" + Utils.joinBy(", ", args) + ")";
	}
}
