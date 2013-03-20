package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EnchantmentsRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class ClearEnchantments extends Routine
{
	private final IDataProvider<EnchantmentsRef> enchantmentsDP;
	
	protected ClearEnchantments(String configString, IDataProvider<EnchantmentsRef> enchantmentsDP)
	{
		super(configString);
		this.enchantmentsDP = enchantmentsDP;
	}


	@Override
	public void run(EventData data) throws BailException
	{
		EnchantmentsRef ench = enchantmentsDP.get(data);
		if (ench == null) return;
		
		ench.map.clear();
	}
	
	public static void register()
	{
		ValueChange.registerRoutine(Pattern.compile("clearenchant(?:ment)?s", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public ClearEnchantments getNew(Matcher matcher, EventInfo info)
		{
			IDataProvider<EnchantmentsRef> enchantmentsDP = DataProvider.parse(info, EnchantmentsRef.class, "enchantments");
			if(enchantmentsDP == null) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Clear Enchantments");
			return new ClearEnchantments(matcher.group(), enchantmentsDP);
		}
	}
}
