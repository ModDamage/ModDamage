package com.ModDamage.EventInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.misc.Multimap;

public abstract class DataProvider<T, S> implements IDataProvider<T>
{
	protected final Class<S> wantStart;
	protected IDataProvider<S> startDP;
	protected T defaultValue = null;
	
	protected DataProvider(Class<S> wantStart, IDataProvider<S> startDP)
	{
		this.wantStart = wantStart;
		this.startDP = startDP;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final T get(EventData data) throws BailException
	{
		Object ostart = startDP.get(data);
		if (ostart != null && wantStart.isInstance(ostart))
		{
			T value = get((S) ostart, data);
			if (value == null) value = defaultValue;
			return value;
		}
		
		return defaultValue;
	}
	public abstract T get(S start, EventData data) throws BailException;

	public abstract Class<T> provides();
	
	
	public static class CastDataProvider<T> implements IDataProvider<T>
	{
		private final IDataProvider<?> inner;
		private final Class<T> want;
		
		public CastDataProvider(IDataProvider<?> inner, Class<T> want)
		{
			this.inner = inner;
			this.want = want;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public T get(EventData data) throws BailException
		{
			Object obj = inner.get(data);
			if (want.isInstance(obj)) return (T) obj;
			return null;
		}

		@Override
		public Class<T> provides()
		{
			return want;
		}
		
		@Override
		public String toString()
		{
			return inner.toString();
		}
	}
	
	

	public interface IDataParser<T, S>
	{
		IDataProvider<T> parse(EventInfo info, IDataProvider<S> startDP, Matcher m, StringMatcher sm);
	}
	
	public abstract static class BaseDataParser<T> implements IDataParser<T, Object>
	{
		@Override
		public final IDataProvider<T> parse(EventInfo info, IDataProvider<Object> nullDP, Matcher m, StringMatcher sm)
		{
			assert(nullDP == null);
			return parse(info, m, sm);
		}
		
		public abstract IDataProvider<T> parse(EventInfo info, Matcher m, StringMatcher sm);
	}
	
	static class ParserData<T, S>
	{
		final Class<T> provides;
		final Class<S> wants;
		final Pattern pattern;
		final IDataParser<T, S> parser;
		
		final int numGroups;
		int compiledGroup;
		
		public ParserData(Class<T> provides, Class<S> wants, Pattern pattern, IDataParser<T, S> parser)
		{
			this.provides = provides;
			this.wants = wants;
			this.pattern = pattern;
			this.parser = parser;
			
			numGroups = pattern == null? 0 : pattern.matcher("").groupCount();
		}

		@SuppressWarnings("unchecked")
		IDataProvider<T> parse(EventInfo info, IDataProvider<?> dp, Matcher m, StringMatcher sm)
		{
			return parser.parse(info, (IDataProvider<S>) dp, m, sm);
		}
	}
	
	
	@SuppressWarnings("serial")
	static class Parsers extends ArrayList<ParserData<?, ?>>
	{
		Pattern compiledPattern;
	}
	
	
	
	static Map<Class<?>, Parsers> parsersByStart = new HashMap<Class<?>, Parsers>();
	
	
	public interface IDataTransformer<T, S>
	{
		IDataProvider<T> transform(EventInfo info, IDataProvider<S> dp);
	}
	static class TransformerData<T, S>
	{
		final Class<T> provides;
		final Class<S> wants;
		final IDataTransformer<T, S> transformer;
		
		public TransformerData(Class<T> provides, Class<S> wants, IDataTransformer<T, S> transformer)
		{
			this.provides = provides;
			this.wants = wants;
			this.transformer = transformer;
		}

		@SuppressWarnings("unchecked")
		IDataProvider<T> transform(EventInfo info, IDataProvider<?> dp)
		{
			return transformer.transform(info, (IDataProvider<S>) dp);
		}
	}
	
	
	static Map<Class<?>, ArrayList<TransformerData<?, ?>>> transformersByStart = new HashMap<Class<?>, ArrayList<TransformerData<?, ?>>>();
	
	public static <T, S> void register(Class<T> provides, Class<S> wants, Pattern pattern, IDataParser<T, S> parser)
	{
		Parsers parserList = parsersByStart.get(wants);
		if (parserList == null)
		{
			parserList = new Parsers();
			parsersByStart.put(wants, parserList);
		}
		
		parserList.add(new ParserData<T, S>(provides, wants, pattern, parser));
	}
	
	public static <T> void register(Class<T> provides, Pattern pattern, BaseDataParser<T> parser)
	{
		register(provides, null, pattern, parser);
	}
	
	public static <T, S> void registerTransformer(Class<T> provides, Class<S> wants, IDataTransformer<T, S> transformer)
	{
		ArrayList<TransformerData<?, ?>> transformersList = transformersByStart.get(wants);
		if (transformersList == null)
		{
			transformersList = new ArrayList<TransformerData<?, ?>>();
			transformersByStart.put(wants, transformersList);
		}
		
		transformersList.add(new TransformerData<T, S>(provides, wants, transformer));
	}
	
	public static void clear()
	{
		parsersByStart.clear();
	}
	
	public static void compile()
	{
		for (Entry<Class<?>, Parsers> parsersEntry : parsersByStart.entrySet())
		{
			Parsers parsers = parsersEntry.getValue();
			StringBuilder sb = new StringBuilder("^(?:");
			
			int currentGroup = 1;
			
			boolean first = true;
			for (ParserData<?, ?> parserData : parsers)
			{
				if (first) first = false;
				else sb.append("|");
				
				sb.append("(");
				
				sb.append(parserData.pattern.pattern());
				
				sb.append(")");
				
				parserData.compiledGroup = currentGroup;
				currentGroup += 1 + parserData.numGroups;
			}
			
			sb.append(")");
			parsers.compiledPattern = Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
			
			int groupCount = parsers.compiledPattern.matcher("").groupCount();
			assert (groupCount == currentGroup - 1);
		}
	}
	
	public static <T> IDataProvider<T> parse(EventInfo info, Class<T> want, String s)
	{
		return parse(info, want, s, true, true);
	}
	
	public static <T> IDataProvider<T> parse(EventInfo info, Class<T> want, String s, boolean finish, boolean complain)
	{
		return parse(info, want, new StringMatcher(s), finish, complain, null);
	}

	public static <T> IDataProvider<T> parse(EventInfo info, Class<T> want, StringMatcher sm)
	{
		return parse(info, want, sm, false, true, null);
	}
	
	public static <T> IDataProvider<T> parse(EventInfo info, Class<T> want, StringMatcher sm, boolean finish, boolean complain, Pattern endPattern)
	{
		String startString = sm.string;
		String soFar = null;
		
		IDataProvider<?> dp = null;
		IDataProvider<T> tdp = null;
		
		// Match EventInfo names first, since they are the most common start
		Multimap<String, Class<?>> infoMap = info.getAllNames();
		for (Multimap.Entry<String, Class<?>> entry : infoMap)
		{
			if (sm.string.length() < entry.getKey().length()) continue;
			
			String substr = sm.string.substring(0, entry.getKey().length());
			if (substr.equalsIgnoreCase(entry.getKey()))
			{
				StringMatcher sm2 = sm.spawn();
				sm2.matchFront(substr);
				dp = parseHelper(info, info.get(entry.getValue(), substr), sm2.spawn());
				
				if ((!finish || sm2.isEmpty()) && (endPattern == null || endPattern.matcher(sm2.string).lookingAt())) {
					tdp = transform(want, dp, info, false);
					if (tdp != null)
					{
						sm2.accept();
						sm.accept();
						return tdp;
					}
				}
				
				soFar = sm2.string;
			}
		}
		
		IDataProvider<?> dp2 = parseHelper(info, null, sm.spawn());
		

		if ((!finish || sm.isEmpty()) && (endPattern == null || endPattern.matcher(sm.string).lookingAt())) {
			tdp = transform(want, dp2, info, false);
			if (tdp != null)
			{
				sm.accept();
				return tdp;
			}
		}
		
		if (dp == null)
		{
			dp = dp2;
			soFar = sm.string;
		}
		
		if (complain)
		{
			Class<?> dpProvides = dp == null? null : dp.provides();
			String provName = dpProvides == null? "null" : dpProvides.getSimpleName();
			
			String simpleName = want == null? "null" : want.getSimpleName();
			
			String error = "Unable to parse \""+startString+"\"";
			if (sm.isEmpty())
				error += ": wanted "+simpleName+", got "+provName;
			else
				error += " at "+provName+" \""+soFar+"\" for \""+simpleName+"\"";
			
			ModDamage.addToLogRecord(OutputPreset.FAILURE, error);
		}
		
		return null;
	}
	
	public static <T> IDataProvider<T> transform(Class<T> want, IDataProvider<?> dp, EventInfo info)
	{
		return transform(want, dp, info, true);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> IDataProvider<T> transform(Class<T> want, IDataProvider<?> dp, EventInfo info, boolean complain)
	{
		if (dp == null) return null;
		
		if (want == null) return (IDataProvider<T>) dp;
		
		Class<?> dpProvides = dp.provides();
		
		if (want.equals(dpProvides)) return (IDataProvider<T>) dp;
		
		if (want.isAssignableFrom(dpProvides) || dpProvides.isAssignableFrom(want))
			return new CastDataProvider<T>(dp, want);
		
		// dp doesn't match the required cls, look for any transformers that may convert it to the correct class
		for (Entry<Class<?>, ArrayList<TransformerData<?, ?>>> entry : transformersByStart.entrySet())
		{
			Class<?> ecls = entry.getKey();
			if (classesMatch(ecls, dpProvides))
			{
				for (TransformerData<?, ?> transformer : entry.getValue())
				{
					if (transformer.provides != want) continue;
					
					IDataProvider<T> transDP = (IDataProvider<T>) transformer.transform(info, dp);
					if (transDP != null)
						return transDP;
				}
			}
		}
		
		return null;
	}
	
	static boolean classesMatch(Class<?> cls1, Class<?> cls2)
	{
		if (cls1 == cls2) return true;
		if (cls1 == null || cls2 == null) return false;
		
		return cls1.isAssignableFrom(cls2) || cls2.isAssignableFrom(cls1); // basically is castable
	}
	
	
	private static IDataProvider<?> parseHelper(EventInfo info, IDataProvider<?> dp, StringMatcher sm)
	{
		outerLoop: while (!sm.isEmpty())
		{
			Class<?> dpProvides = dp == null? null : dp.provides();
			
			for (Entry<Class<?>, Parsers> parserEntry : parsersByStart.entrySet())
			{
				Class<?> pcls = parserEntry.getKey();
				Parsers parsers = parserEntry.getValue();

				Matcher m = parsers.compiledPattern.matcher(sm.string);
				if (!m.lookingAt())
					continue;
				
				IDataProvider<?> tryDP = dp;
				
				if (!classesMatch(pcls, dpProvides)) // look for a transformer that can transform dp to the correct type
				{
					tryDP = transform(pcls, dp, info, true);
					if (tryDP == null)
						continue;
				}
				
				IDataProvider<?> dp2 = tryParsers(info, tryDP, m, sm.spawn(), parserEntry.getValue());
				if (dp2 != null)
				{
					dp = dp2;
					continue outerLoop;
				}
				
			}
			
			break;
		}
		
		sm.accept();
		return dp;
	}
	
	private static IDataProvider<?> tryParsers(EventInfo info, IDataProvider<?> dp, Matcher m, StringMatcher sm, Parsers parserList)
	{
		for (ParserData<?, ?> parserData : parserList)
		{
			if (m.group(parserData.compiledGroup) == null) 
				continue;
			
			StringMatcher sm2 = sm.spawn();
			Matcher m2 = sm2.matchFront(parserData.pattern);
			if (m2 == null) {
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Matched group failed to match?? "+parserData.parser.getClass().getName()+" \""+parserData.pattern.pattern()+"\"");
				continue;
			}
			
			IDataProvider<?> provider = parserData.parse(info, dp, m2, sm2.spawn());
			if (provider != null) {
				sm2.accept();
				sm.accept();
				return provider;
			}
		}
		return null;
	}
}
