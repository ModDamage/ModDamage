package com.ModDamage.Expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.StringMatcher;

public class InterpolatedString implements IDataProvider<String>
{
    public static final Pattern interpolationStartPattern = Pattern.compile("%\\{");
    public static final Pattern interpolationEndPattern = Pattern.compile("}");
	//private static final Pattern interpolationPattern = Pattern.compile("%\\{([^}]+)\\}", Pattern.CASE_INSENSITIVE);
	private static final Pattern colorReplacePattern = Pattern.compile("&([0-9a-fk-or])", Pattern.CASE_INSENSITIVE);
	
	private final List<InterpolatedPart> parts = new ArrayList<InterpolatedPart>();
	private int minSize;
	
	public InterpolatedString(String message, EventInfo info, boolean colorize)
	{
		StringMatcher sm = new StringMatcher(message);
        String part;
		while((part = sm.skipTo(interpolationStartPattern)) != null)
		{
			if (colorize)
				part = colorReplace(part);
			addPart(part);

            Matcher start = sm.matchFront(interpolationStartPattern); // already know this is true because of the while condition
			
			IDataProvider<String> match = DataProvider.parse(info, String.class, sm.spawn(), false, true, interpolationEndPattern);
			if(match == null) {
                ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, "String expression not matched!");
                addPart(start.group());
                continue;
            }
            if (!sm.matchesFront(interpolationEndPattern)) continue; // Why would this ever not match?

			addPart(match);
		}
		part = sm.string;
		if (colorize)
			part = colorReplace(part);
		addPart(part);
	}

    /**
     * Used by parseWord
     */
    private InterpolatedString() {}



    /**
     * This parses a word that may include %{interpolations}.
     * @param wordPattern The pattern for the text before, after, and inbetween interpolations. It should NOT match empty.
     * @param sm sm.spawn()
     * @param info The current EventInfo
     * @return The new InterpolatedString or null if parsing failed
     */
    public static IDataProvider<String> parseWord(Pattern wordPattern, StringMatcher sm, EventInfo info) {
        InterpolatedString is = new InterpolatedString();

        Matcher m;

        while (true) {
            m = sm.matchFront(interpolationStartPattern);
            if (m != null) {
                IDataProvider<String> stringDP = DataProvider.parse(info, String.class, sm.spawn(), false, true, interpolationEndPattern);
                if (stringDP == null) return null;
                if (!sm.matchesFront(interpolationEndPattern)) return null;
                is.addPart(stringDP);
                continue;
            }

            m = sm.matchFront(wordPattern);
            if (m != null) {
                is.addPart(m.group());
                continue;
            }

            break;
        }

        sm.accept();
        return is;
    }

    public static final Pattern word = Pattern.compile("\\w+");
    public static final Pattern comma = Pattern.compile(",");

    /**
     * This parses a word that may include %{interpolations}.
     * @param wordPattern The pattern for the text before, after, and inbetween interpolations. It should NOT match empty.
     * @param seperatorPattern The pattern to use between words. Often just InterpolatedString.comma
     * @param sm sm.spawn()
     * @param info The current EventInfo
     * @return The new InterpolatedString or null if parsing failed
     */
    public static Collection<IDataProvider<String>> parseWordList(Pattern wordPattern, Pattern seperatorPattern, StringMatcher sm, EventInfo info) {
        List<IDataProvider<String>> iss = new ArrayList<IDataProvider<String>>(1);

        while (true) {
            IDataProvider<String> is = parseWord(wordPattern, sm.spawn(), info);
            if (is == null) return null;
            iss.add(is);

            if (!sm.matchesFront(seperatorPattern)) break;
        }

        sm.accept();
        return iss;
    }
	
	private String colorReplace(String str)
	{
		return colorReplacePattern.matcher(str).replaceAll("§$1");
	}
	
	private void addPart(String str)
	{
		parts.add(new StringPart(str));
		minSize += str.length();
	}
	
	private void addPart(IDataProvider<String> str)
	{
		parts.add(new DynamicStringPart(str));
		minSize += 3; // just a guess, many will be small numbers
	}
	
	public String toString(EventData data) throws BailException
	{
		StringBuilder builder = new StringBuilder(minSize);
		
		for (InterpolatedPart part : parts)
			builder.append(part.toString(data));
		
		return builder.toString();
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		for (InterpolatedPart part : parts)
			builder.append(part.toString());
		
		return builder.toString();
	}
	
	interface InterpolatedPart {
		public String toString(EventData data) throws BailException;
		public String toString();
	}
	
	private static class StringPart implements InterpolatedPart {
		private String string;
		public StringPart(String str) { string = str; }
		public String toString(EventData data) { return string; }
		public String toString() { return string; }
	}
	
	private static class DynamicStringPart implements InterpolatedPart {
		private IDataProvider<String> dstring;
		public DynamicStringPart(IDataProvider<String> str) { dstring = str; }
		public String toString(EventData data) throws BailException {
			String str = dstring.get(data);
			if (str == null) str = "";
			return str;
		}
		public String toString() { return "%{" + dstring + "}"; }
	}

	@Override
	public String get(EventData data) throws BailException
	{
		return toString(data);
	}

	@Override
	public Class<String> provides()
	{
		return String.class;
	}
}