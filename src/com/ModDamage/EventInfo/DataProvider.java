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
		IDataProvider<T> parse(EventInfo info, Class<?> want, IDataProvider<S> startDP, Matcher m, StringMatcher sm);
	}
	
	public abstract static class BaseDataParser<T> implements IDataParser<T, Object>
	{
		@Override
		public final IDataProvider<T> parse(EventInfo info, Class<?> want, IDataProvider<Object> nullDP, Matcher m, StringMatcher sm)
		{
			assert(nullDP == null);
			return parse(info, want, m, sm);
		}
		
		public abstract IDataProvider<T> parse(EventInfo info, Class<?> want, Matcher m, StringMatcher sm);
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
		IDataProvider<T> parse(EventInfo info, Class<?> want, IDataProvider<?> dp, Matcher m, StringMatcher sm)
		{
			return parser.parse(info, want, (IDataProvider<S>) dp, m, sm);
		}
	}
	
	
	
	@SuppressWarnings("serial")
	static class Parsers extends ArrayList<ParserData<?, ?>>
	{
		Pattern compiledPattern;
	}
	
	
	
	static Map<Class<?>, Parsers> parsersByStart = new HashMap<Class<?>, Parsers>();
	static Map<Class<?>, ArrayList<ParserData<?, ?>>> transformersByStart = new HashMap<Class<?>, ArrayList<ParserData<?, ?>>>();
	
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
	
	public static <T, S> void registerTransformer(Class<T> provides, Class<S> wants, IDataParser<T, S> parser)
	{
		ArrayList<ParserData<?, ?>> transformersList = transformersByStart.get(wants);
		if (transformersList == null)
		{
			transformersList = new Parsers();
			transformersByStart.put(wants, transformersList);
		}
		
		transformersList.add(new ParserData<T, S>(provides, wants, null, parser));
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
	

	public static <T> IDataProvider<T> parse(EventInfo info, Class<T> cls, String s)
	{
		return parse(info, cls, new StringMatcher(s), true, true);
	}

	public static <T> IDataProvider<T> parse(EventInfo info, Class<T> cls, String s, boolean finish, boolean complain)
	{
		return parse(info, cls, new StringMatcher(s), finish, complain);
	}

	public static <T> IDataProvider<T> parse(EventInfo info, Class<T> cls, StringMatcher sm)
	{
		return parse(info, cls, sm, false, true);
	}
	@SuppressWarnings("unchecked")
	public static <T> IDataProvider<T> parse(EventInfo info, Class<T> cls, StringMatcher sm, boolean finish, boolean complain)
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
				dp = parseHelper(info, cls, info.get(entry.getValue(), substr), sm2.spawn());
				tdp = maybeTransform(info, cls, dp, sm2.spawn(), finish);
				if (tdp != null)
				{
					sm2.accept();
					sm.accept();
					return tdp;
				}
				
				soFar = sm2.string;
			}
		}
		
		IDataProvider<?> dp2 = parseHelper(info, cls, null, sm.spawn());
		tdp = maybeTransform(info, cls, dp2, sm.spawn(), finish);
		if (tdp != null)
		{
			sm.accept();
			return (IDataProvider<T>) dp2;
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
			
			String error = "Unable to parse \""+startString+"\"";
			if (sm.isEmpty())
				error += ": wanted "+cls.getSimpleName()+", got "+provName;
			else
				error += " at "+provName+" \""+soFar+"\" for \""+cls.getSimpleName()+"\"";
			
			ModDamage.addToLogRecord(OutputPreset.FAILURE, error);
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> IDataProvider<T> maybeTransform(EventInfo info, Class<T> want, IDataProvider<?> dp, StringMatcher sm, boolean finish)
	{
		if (dp == null) return null;
		if (finish && !sm.isEmpty()) return null;
		
		if (want == null) return (IDataProvider<T>) dp;
		
		Class<?> dpProvides = dp.provides();
		
		if (want.equals(dpProvides)) return (IDataProvider<T>) dp;
		
		if (want.isAssignableFrom(dpProvides) || dpProvides.isAssignableFrom(want))
			return new CastDataProvider<T>(dp, want);
		
		// dp doesn't match the required cls, look for any transformers that may convert it to the correct class
		
		for (Entry<Class<?>, ArrayList<ParserData<?, ?>>> entry : transformersByStart.entrySet())
		{
			Class<?> ecls = entry.getKey();
			if (classesMatch(ecls, dpProvides))
			{
				for (ParserData<?, ?> parserData : entry.getValue())
				{
					if (parserData.provides != want) continue;
					
					IDataProvider<T> transDP = (IDataProvider<T>) parserData.parse(info, want, dp, null, sm.spawn());
					if (transDP != null) {
						sm.accept();
						return transDP;
					}
				}
			}
		}
		
		return null;
	}
	
	private static boolean classesMatch(Class<?> cls1, Class<?> cls2)
	{
		if (cls1 == cls2) return true;
		if (cls1 == null || cls2 == null) return false;
		
		return cls1.isAssignableFrom(cls2) || cls2.isAssignableFrom(cls1); // basically is castable
	}
	
	
	private static IDataProvider<?> parseHelper(EventInfo info, Class<?> want, IDataProvider<?> dp, StringMatcher sm)
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
				
				if (classesMatch(pcls, dpProvides))
				{
					IDataProvider<?> dp2 = tryParsers(info, want, dp, m, sm.spawn(), parserEntry.getValue());
					if (dp2 != null)
					{
						dp = dp2;
						continue outerLoop;
					}
				}
				else // look for a transformer that can transform dp to the correct type
				{
					for (Entry<Class<?>, ArrayList<ParserData<?, ?>>> transEntry : transformersByStart.entrySet())
					{
						Class<?> tcls = transEntry.getKey();
						if (!classesMatch(tcls, dpProvides))
							continue;
						
						for (ParserData<?, ?> parserData : transEntry.getValue())
						{
							if (!classesMatch(parserData.provides, pcls))
								continue;
							
							StringMatcher sm2 = sm.spawn();
							
							IDataProvider<?> transDP = parserData.parse(info, want, dp, null, sm2.spawn());
							if (transDP == null) 
								continue;
							
							IDataProvider<?> dp2 = tryParsers(info, want, transDP, m, sm2.spawn(), parsers);
							if (dp2 != null)
							{
								sm2.accept();
								dp = dp2;
								continue outerLoop;
							}
						}
					}
				}
			}
			
			break;
		}
		
		sm.accept();
		return dp;
	}
	
	private static IDataProvider<?> tryParsers(EventInfo info, Class<?> want, IDataProvider<?> dp, Matcher m, StringMatcher sm, Parsers parserList)
	{
		for (ParserData<?, ?> parserData : parserList)
		{
			if (m.group(parserData.compiledGroup) == null) 
				continue;
			
			StringMatcher sm2 = sm.spawn();
			Matcher m2 = sm2.matchFront(parserData.pattern);
			if (m2 == null) {
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Matched group failed to match??");
				continue;
			}
			
			IDataProvider<?> provider = parserData.parse(info, want, dp, m2, sm2.spawn());
			if (provider != null) {
				sm2.accept();
				sm.accept();
				return provider;
			}
		}
		return null;
	}
}
