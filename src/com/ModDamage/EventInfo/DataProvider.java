package com.ModDamage.EventInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.misc.Multimap;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import krum.automaton.TokenAutomaton;
import krum.automaton.TokenResult;

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

			//return "("+inner.provides().getSimpleName() + "->" + want.getSimpleName() + ")" + inner.toString();
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
			if (nullDP != null) return null;
			return parse(info, m, sm);
		}
		
		public abstract IDataProvider<T> parse(EventInfo info, Matcher m, StringMatcher sm);
	}
	
	static class ParserData<T, S>
	{
		final Class<T> provides;
		final Class<S> wants;
		final Pattern pattern;
        final Automaton automaton;
		final IDataParser<T, S> parser;
//        final String regex;

        final static Map<Character,Set<Character>> casemap;

        static {
            Map<Character,Set<Character>> map = new HashMap<Character,Set<Character>>();
            for (char c1 = 'a', c2 = 'A'; c1 <= 'z'; c1++, c2++) {
                Set<Character> chars = new HashSet<Character>();
                chars.add(c1);
                chars.add(c2);
                map.put(c1, chars);
                map.put(c2, chars);
            }
            casemap = map;
        }
		
		public ParserData(Class<T> provides, Class<S> wants, Pattern pattern, IDataParser<T, S> parser)
		{
			this.provides = provides;
			this.wants = wants;
			this.pattern = pattern;
			this.parser = parser;

            String regex = pattern.pattern()
                    .replace("[\\w", "[a-z0-9_")
                    .replace("\\w", "[a-z0-9_]")
                    .replace("\\s", "[ \t]")
                    .replace("(?:", "(");

            automaton = new RegExp(regex, RegExp.NONE).toAutomaton().subst(casemap);
            automaton.minimize();

            for (State s : automaton.getAcceptStates())
                s.setInfo(this);
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
//		Pattern compiledPattern;
        TokenAutomaton automaton;
	}
	
	
	
	static Map<Class<?>, Parsers> parsersByStart = new LinkedHashMap<Class<?>, Parsers>();
	
	
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
	
	
	static Map<Class<?>, ArrayList<TransformerData<?, ?>>> transformersByStart = new LinkedHashMap<Class<?>, ArrayList<TransformerData<?, ?>>>();
	
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
//        File dotFolder = new File(ModDamage.getPluginConfiguration().plugin.getDataFolder(), "dot");


//        try {

            for (Entry<Class<?>, Parsers> parsersEntry : parsersByStart.entrySet())
            {
                Parsers parsers = parsersEntry.getValue();

                // this is a cheap attempt at fixing some parsing issues: always put longer matches first
                Collections.sort(parsers, new Comparator<ParserData<?, ?>>() {
                    @Override
                    public int compare(ParserData<?, ?> o1, ParserData<?, ?> o2) {
                        return o2.pattern.pattern().length() - o1.pattern.pattern().length();
                    }
                });

                Automaton a = null;

//                String start = parsersEntry.getKey() == null? "null" : parsersEntry.getKey().getSimpleName();
//                FileWriter fstream = new FileWriter(new File(dotFolder, start+"-patterns.txt"));

                for (ParserData<?, ?> parserData : parsers)
                {
//                    String wants = parserData.wants == null? "null" : parserData.wants.getSimpleName();
//                    String provides = parserData.provides == null? "null" : parserData.provides.getSimpleName();
//                    fstream.write(wants + " " + provides + ": " + parserData.regex + "\n");

                    if (a == null) {
                        a = parserData.automaton;
                        continue;
                    }
                    a = a.union(parserData.automaton);
                }

//                fstream.close();


                assert a != null;
                a.determinize();


//                fstream = new FileWriter(new File(dotFolder, start+".dot"));
//                fstream.write(a.toDot());
//                fstream.close();

                parsers.automaton = new TokenAutomaton(a);

    //			StringBuilder sb = new StringBuilder("^(?:");
    //
    //			int currentGroup = 1;
    //
    //			boolean first = true;
    //			for (ParserData<?, ?> parserData : parsers)
    //			{
    //				if (first) first = false;
    //				else sb.append("|");
    //
    //				sb.append("(");
    //
    //				sb.append(parserData.pattern.pattern());
    //
    //				sb.append(")");
    //
    //				parserData.compiledGroup = currentGroup;
    //				currentGroup += 1 + parserData.numGroups;
    //			}
    //
    //			sb.append(")");
    //			parsers.compiledPattern = Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
    //
    //			int groupCount = parsers.compiledPattern.matcher("").groupCount();
    //			if (groupCount != currentGroup - 1)
    //				throw new Error("BAD! $DP228");
            }

//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
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
					if (!want.isAssignableFrom(transformer.provides) && !transformer.provides.isAssignableFrom(want))
						continue;
					
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

//				Matcher m = parsers.compiledPattern.matcher(sm.string);
//				if (!m.lookingAt())
//					continue;
                TokenResult tr = new TokenResult();
                if (!(parsers.automaton.find(sm.string, 0, true, tr)))
                    continue;

                if (tr.info == null || !(tr.info instanceof ParserData))
                    System.err.println("FIWHEOGIHWQ#!");
				
				IDataProvider<?> tryDP = dp;
				
				if (!classesMatch(pcls, dpProvides)) // look for a transformer that can transform dp to the correct type
				{
					tryDP = transform(pcls, dp, info, true);
					if (tryDP == null)
						continue;
				}

                ParserData<?, ?> parserData = (ParserData<?, ?>) tr.info;

				IDataProvider<?> dp2 = tryParser(info, tryDP, sm.spawn(), parserData);
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
	
	private static IDataProvider<?> tryParser(EventInfo info, IDataProvider<?> dp, StringMatcher sm, ParserData<?, ?> parserData)
	{
        StringMatcher sm2 = sm.spawn();
        Matcher m2 = sm2.matchFront(parserData.pattern);
        if (m2 == null) {
            ModDamage.addToLogRecord(OutputPreset.FAILURE, "Matched group failed to match?? "+parserData.parser.getClass().getName()+" \""+parserData.pattern.pattern()+"\"");
            return null;
        }

        IDataProvider<?> provider = parserData.parse(info, dp, m2, sm2.spawn());
        if (provider != null) {
            sm2.accept();
            sm.accept();
            return provider;
        }

        return null;
	}
}
