package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.NumberExp;
import com.ModDamage.Routines.Routines;

public class EntityHeal extends NestedRoutine
{
	private final IDataProvider<LivingEntity> livingDP;
	private final IDataProvider<Number> heal_amount;
	
	public EntityHeal(String configString, IDataProvider<LivingEntity> livingDP, IDataProvider<Number> heal_amount)
	{
		super(configString);
		this.livingDP = livingDP;
		this.heal_amount = heal_amount;
	}

	static final EventInfo myInfo = new SimpleEventInfo(Number.class, "heal_amount", "-default");
	
	@Override
	public void run(EventData data) throws BailException
	{
		EventData myData = myInfo.makeChainedData(data, 0);
		
		Number ha = heal_amount.get(myData);
		if (ha == null) return;
		
		LivingEntity entity = livingDP.get(data);
		if (entity == null) return;
		
		entity.setHealth(Math.min(entity.getHealth() + ha.doubleValue(), entity.getMaxHealth()));
	}

	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*?)(?:effect)?\\.heal", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{	
		@Override
		public EntityHeal getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			IDataProvider<LivingEntity> livingDP = DataProvider.parse(info, LivingEntity.class, name);
            if (livingDP == null) return null;

            ModDamage.addToLogRecord(OutputPreset.INFO, "Heal "+livingDP+":");

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			IDataProvider<Number> heal_amount = NumberExp.getNew(routines, einfo);
            if (heal_amount == null) return null;

			return new EntityHeal(matcher.group(), livingDP, heal_amount);
		}
	}
}
