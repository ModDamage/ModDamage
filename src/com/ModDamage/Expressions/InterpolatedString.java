package com.ModDamage.Expressions;

import java.util.ArrayList;
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

public class InterpolatedString
{
	private static final Pattern interpolationPattern = Pattern.compile("%\\{([^}]+)\\}", Pattern.CASE_INSENSITIVE);
	private static final Pattern colorReplacePattern = Pattern.compile("&([0-9a-fk-o])", Pattern.CASE_INSENSITIVE);
	
	private final List<InterpolatedPart> parts = new ArrayList<InterpolatedPart>();
	private int minSize;
	
	public InterpolatedString(String message, EventInfo info, boolean colorize)
	{
		Matcher interpolationMatcher = interpolationPattern.matcher(message);
		int start = 0;
		while(interpolationMatcher.find(start))
		{
			String part = message.substring(start, interpolationMatcher.start());
			if (colorize)
				part = colorReplace(part);
			addPart(part);
			
			start = interpolationMatcher.end();
			
			IDataProvider<String> match = DataProvider.parse(info, String.class, interpolationMatcher.group(1));
			if(match != null)
			{
				addPart(match);
			}
			else
			{
				ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, "String expression not matched!");
				addPart(message.substring(interpolationMatcher.start(1), interpolationMatcher.end(1)));
			}
		}
		String part = message.substring(start);
		if (colorize)
			part = colorReplace(part);
		addPart(part);
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
		minSize += 4; // just a guess, many will be small numbers
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
}