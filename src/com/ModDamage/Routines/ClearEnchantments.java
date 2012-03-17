package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.EnchantmentsRef;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class ClearEnchantments extends Routine
{
	private final DataRef<EnchantmentsRef> enchantmentsRef;
	
	protected ClearEnchantments(String configString, DataRef<EnchantmentsRef> enchantmentsRef)
	{
		super(configString);
		this.enchantmentsRef = enchantmentsRef;
	}


	@Override
	public void run(EventData data)
	{
		enchantmentsRef.get(data).map.clear();
	}
	
	public static void register()
	{
		ValueChange.registerRoutine(Pattern.compile("clearenchant(?:ment)s", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public ClearEnchantments getNew(Matcher matcher, EventInfo info)
		{
			DataRef<EnchantmentsRef> enchantmentsRef = info.get(EnchantmentsRef.class, "-enchantments");
			if(enchantmentsRef != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Clear Enchantments");
				return new ClearEnchantments(matcher.group(), enchantmentsRef);
			}
			return null;
		}
	}
}
