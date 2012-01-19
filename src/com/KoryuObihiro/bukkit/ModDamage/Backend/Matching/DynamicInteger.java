package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.StringMatcher;
import com.KoryuObihiro.bukkit.ModDamage.Utils;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.ConstantInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.DynamicCalculatedInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.DynamicEnchantmentInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.DynamicEntityInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.DynamicEntityTagInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.DynamicEventInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.DynamicMcMMOInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.DynamicPlayerInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.DynamicPlayerItemInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.DynamicRoutineInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.DynamicServerInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.DynamicWorldInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.Function;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers.NegativeInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

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
		ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: attempted to use invalid routine list for a dynamic integer reference.");//shouldn't happen
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