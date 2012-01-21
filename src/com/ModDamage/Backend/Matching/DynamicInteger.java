package com.ModDamage.Backend.Matching;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.AliasManager;
import com.ModDamage.Backend.Matching.DynamicIntegers.ConstantInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicCalculatedInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicEnchantmentInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicEntityInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicEntityTagInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicEventInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicMcMMOInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicPlayerInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicPlayerItemInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicRoutineInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicServerInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.DynamicWorldInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.Function;
import com.ModDamage.Backend.Matching.DynamicIntegers.NegativeInteger;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Routines.Routine;

public abstract class DynamicInteger extends DynamicString
{
	public static final Pattern whitespace = Pattern.compile("\\s*");
	
	public static final String dynamicIntegerPart;
	public static final String dynamicIntegerPart_silent;
	static
	{
		String tempString = "(?:"+Utils.joinBy("|", EntityReference.values()) + "event|world|server)";
		dynamicIntegerPart = "(-?(?:[0-9]+|(?:" + tempString + "_\\w+)|(?:_\\w+)|(?:\\(.*\\))))";//FIXME The greediness at the end blocks all 
		dynamicIntegerPart_silent = "(?:" + dynamicIntegerPart.substring(1); 
	}
	//private static final Pattern dynamicIntegerPattern = Pattern.compile(dynamicIntegerPart, Pattern.CASE_INSENSITIVE);
	
	
	public abstract int getValue(TargetEventInfo eventInfo);
	
	public boolean isSettable(){ return false; }
	public void setValue(TargetEventInfo eventInfo, int value) { }
	
	public static DynamicInteger getNew(Collection<Routine> routines) 
	{
		if(routines != null && !routines.isEmpty())
			return new DynamicRoutineInteger(routines);
		return null;
	}
	
	public static DynamicInteger getIntegerFromFront(StringMatcher sm)
	{
		sm.matchFront(whitespace);
		
		for(Entry<Pattern, DynamicIntegerBuilder> entry : registeredIntegers.entrySet())
		{
			Matcher matcher = sm.matchFront(entry.getKey());
			if(matcher != null)
			{
				DynamicInteger dir = entry.getValue().getNewFromFront(matcher, sm.spawn());
				if(dir != null)
				{
					sm.accept();
					return dir;
				}
			}
		}
		return null;
	}
	
	public static DynamicInteger getNew(String string){ return getNew(string, true);}
	public static DynamicInteger getNew(String string, boolean outputError)
	{
		StringMatcher sm = new StringMatcher(string);
		DynamicInteger integer = getIntegerFromFront(sm.spawn());
		if (outputError)
		{
			if (integer == null)
				ModDamage.addToLogRecord(OutputPreset.FAILURE, " No match found for dynamic integer \"" + string + "\"");
			else if (!sm.string.isEmpty() && !whitespace.matcher(sm.string).matches())
				ModDamage.addToLogRecord(OutputPreset.WARNING, " Extra junk found after dynamic integer \"" + sm.string + "\"");
		}
		return integer;
	}
	
	public static void registerAllIntegers()
	{
		registeredIntegers.clear();
		
		ConstantInteger.register();
		DynamicCalculatedInteger.register();
		DynamicEnchantmentInteger.register();
		DynamicEntityInteger.register();
		DynamicEntityTagInteger.register();
		DynamicEventInteger.register();
		DynamicMcMMOInteger.register();
		DynamicPlayerInteger.register();
		DynamicPlayerItemInteger.register();
		DynamicServerInteger.register();
		DynamicWorldInteger.register();
		NegativeInteger.register();
		Function.register();
		
		DynamicInteger.register(Pattern.compile("_\\w+"),
				new DynamicIntegerBuilder()
					{
						@Override
						public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm)
						{
							sm.accept();
							return DynamicInteger.getNew(AliasManager.matchRoutineAlias(matcher.group()));
						}
					});
	}
	
	private static Map<Pattern, DynamicIntegerBuilder> registeredIntegers = new HashMap<Pattern, DynamicIntegerBuilder>();
	
	public static void register(Pattern pattern, DynamicIntegerBuilder dib)
	{
		registeredIntegers.put(pattern, dib);
	}
	
	abstract public static class DynamicIntegerBuilder
	{
		abstract public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm);
	}

	@Override
	public String getString(TargetEventInfo eventInfo){ return ""+getValue(eventInfo);}
}