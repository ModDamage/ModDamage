package com.ModDamage.Expressions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Variables.Ints.Constant;
import com.ModDamage.Variables.Ints.EnchantmentInt;
import com.ModDamage.Variables.Ints.EntityInt;
import com.ModDamage.Variables.Ints.EventInt;
import com.ModDamage.Variables.Ints.ItemInt;
import com.ModDamage.Variables.Ints.McMMOInt;
import com.ModDamage.Variables.Ints.NegativeInt;
import com.ModDamage.Variables.Ints.PlayerInt;
import com.ModDamage.Variables.Ints.PotionEffectInt;
import com.ModDamage.Variables.Ints.RoutinesInt;
import com.ModDamage.Variables.Ints.ServerInt;
import com.ModDamage.Variables.Ints.TagInt;
import com.ModDamage.Variables.Ints.WorldInt;

public abstract class IntegerExp extends StringExp
{
	public static final Pattern whitespace = Pattern.compile("\\s*");
	
	public final int getValue(EventData data) throws BailException
	{
		try
		{
			return myGetValue(data);
		}
		catch (Throwable t)
		{
			throw new BailException(this, t);
		}
	}
	protected abstract int myGetValue(EventData data) throws BailException;
	
	public boolean isSettable(){ return false; }
	public void setValue(EventData data, int value) { }
	
	public static IntegerExp getNew(Routines routines, EventInfo info) 
	{
		if(routines != null && !routines.isEmpty())
			return new RoutinesInt(routines, info);
		return null;
	}
	
	public static IntegerExp getIntegerFromFront(StringMatcher sm, EventInfo info)
	{
		sm.matchFront(whitespace);
		
		for(Entry<Pattern, DynamicIntegerBuilder> entry : registeredIntegers.entrySet())
		{
			StringMatcher sm2 = sm.spawn();
			Matcher matcher = sm2.matchFront(entry.getKey());
			if(matcher != null)
			{
				IntegerExp dir = entry.getValue().getNewFromFront(matcher, sm2, info);
				if(dir != null)
				{
					sm.accept();
					return dir;
				}
			}
		}
		return null;
	}
	
	public static IntegerExp getNew(String string, EventInfo info){ return getNew(string, info, true); }
	public static IntegerExp getNew(String string, EventInfo info, boolean outputError)
	{
		StringMatcher sm = new StringMatcher(string);
		IntegerExp integer = getIntegerFromFront(sm.spawn(), info);
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
		
		Constant.register();
		IntegerOpExp.register();
		EnchantmentInt.register();
		EntityInt.register();
		TagInt.register();
		McMMOInt.register();
		PlayerInt.register();
		ItemInt.register();
		ServerInt.register();
		WorldInt.register();
		NegativeInt.register();
		Function.register();
		EventInt.register();
		PotionEffectInt.register();
	}
	
	private static Map<Pattern, DynamicIntegerBuilder> registeredIntegers = new LinkedHashMap<Pattern, DynamicIntegerBuilder>();
	
	public static void register(Pattern pattern, DynamicIntegerBuilder dib)
	{
		registeredIntegers.put(pattern, dib);
	}
	
	abstract public static class DynamicIntegerBuilder
	{
		abstract public IntegerExp getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info);
	}

	@Override
	public String getString(EventData data) throws BailException
	{
		return ""+getValue(data);
	}
}