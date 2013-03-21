package com.ModDamage.Parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import krum.automaton.TokenAutomaton;
import krum.automaton.TokenResult;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.Property.Property;
import com.ModDamage.Parsing.Property.PropertyTransformer;
import com.ModDamage.Parsing.Property.ReflectedProperty;
import com.ModDamage.misc.Multimap;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;

public abstract class DataProvider<T, S> implements IDataProvider<T>
{
	public final Class<S> wantStart;
	public IDataProvider<S> startDP;
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

	public abstract Class<? extends T> provides();
	
	
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

	static class ParserData<T, S>
	{
		final Class<T> provides;
		final Class<S> wants;
		final Pattern pattern;
        final Automaton automaton;
		final IDataParser<T, S> parser;
//        final String regex;

        
		
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
                    .replace("(?:", "(")
                    .replace("\"", "\\\"");

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
		
		public String toString() {
			return ""+parser;
		}
	}
	
	
	@SuppressWarnings("serial")
	static class Parsers extends ArrayList<ParserData<?, ?>>
	{
        TokenAutomaton automaton;
	}
	
	
	
	static Parsers parsers = new Parsers();
	
	
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
		parsers.add(new ParserData<T, S>(provides, wants, pattern, parser));
	}
	
	public static <T> void register(Class<T> provides, Pattern pattern, BaseDataParser<T> parser)
	{
		register(provides, null, pattern, parser);
	}
	
	public static <T, S> void registerTransformer(Class<S> wants, String getterMethodName) {
		Property<T, S> property = ReflectedProperty.get("@transformer", wants, getterMethodName);
		registerTransformer(property.provides, property.startsWith, new PropertyTransformer<T, S>(property));
	}

	/**
	 * Casting transformer
	 */
	public static <T, S> void registerTransformer(Class<T> provides, Class<S> wants)
	{
		registerTransformer(provides, wants, new CastTransformer<T, S>(provides));
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
		parsers.clear();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void compile()
	{
		Automaton a = null;

		for (ParserData<?, ?> parserData : parsers)
		{
			if (a == null) {
				a = parserData.automaton;
				continue;
			}
			a = a.union(parserData.automaton);
		}

		if (a != null)
		{
			a.determinize();

			for (State s : a.getAcceptStates()) {
				if (s.getInfo() instanceof ParserData){
					List list = new ArrayList();
					list.add(s.getInfo());
					s.setInfo(list);
				}
			}
		}

		parsers.automaton = new TokenAutomaton(a);
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
		

		if (want.isAssignableFrom(dpProvides))
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
		
		if (dpProvides.isAssignableFrom(want))
			return new CastDataProvider<T>(dp, want);
		
		return null;
	}
	
	static boolean classesMatch(Class<?> cls1, Class<?> cls2)
	{
		if (cls1 == cls2) return true;
		if (cls1 == null || cls2 == null) return false;
		
		return cls1.isAssignableFrom(cls2) || cls2.isAssignableFrom(cls1); // basically is castable
	}
	

	@SuppressWarnings("unchecked")
	private static IDataProvider<?> parseHelper(EventInfo info, IDataProvider<?> dp, StringMatcher sm)
	{
		outerLoop: while (!sm.isEmpty())
		{
			Class<?> dpProvides = dp == null? null : dp.provides();

			TokenResult tr = new TokenResult();
			if (!(parsers.automaton.find(sm.string, 0, true, tr)))
				break;//continue;


			if (tr.info == null || !(tr.info instanceof List))
				System.err.println("FIWHEOGIHWQ#! " + tr.info);

			for (ParserData<?, ?> parserData : (List<ParserData<?, ?>>)tr.info) {
				Class<?> pcls = parserData.wants;

				IDataProvider<?> tryDP = dp;

				if (!classesMatch(pcls, dpProvides)) // look for a transformer that can transform dp to the correct type
				{
					tryDP = transform(pcls, dp, info, true);
					if (tryDP == null)
						continue;
				}


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
//            ModDamage.addToLogRecord(OutputPreset.FAILURE, "Matched group failed to match?? "+parserData.parser.getClass().getName()+" \""+parserData.pattern.pattern()+"\"");
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
