package com.moddamage.routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.LogUtil;
import com.moddamage.backend.BailException;
import com.moddamage.backend.EnchantmentsRef;
import com.moddamage.backend.ScriptLine;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

public class ClearEnchantments extends Routine
{
	private final IDataProvider<EnchantmentsRef> enchantmentsDP;
	
	protected ClearEnchantments(ScriptLine scriptLine, IDataProvider<EnchantmentsRef> enchantmentsDP)
	{
		super(scriptLine);
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
		Routine.registerRoutine(Pattern.compile("clearenchant(?:ment)?s", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}
	
	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			IDataProvider<EnchantmentsRef> enchantmentsDP = DataProvider.parse(info, EnchantmentsRef.class, "enchantments");
			if(enchantmentsDP == null) return null;
			
			LogUtil.info("Clear Enchantments");
			return new RoutineBuilder(new ClearEnchantments(scriptLine, enchantmentsDP));
		}
	}
}
