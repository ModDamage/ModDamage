package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.NumberExp;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routines;

public class EntityUnknownHurt extends NestedRoutine
{
	private final IDataProvider<LivingEntity> livingDP;
	private final IDataProvider<Number> hurt_amount;
	
	public EntityUnknownHurt(String configString, IDataProvider<LivingEntity> livingDP, IDataProvider<Number> hurt_amount)
	{
		super(configString);
		this.livingDP = livingDP;
		this.hurt_amount = hurt_amount;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(Number.class, "hurt_amount", "-default");
	
	@Override
	public void run(EventData data) throws BailException
	{
		EventData myData = myInfo.makeChainedData(data, 0);
		LivingEntity entity = livingDP.get(data);
		if(entity == null) return;
		
		Number ha = hurt_amount.get(myData);
		if (ha == null) return;
		
		entity.damage(ha.doubleValue());
	}

	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*?)(?:effect)?\\.unknownhurt", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{	
		@Override
		public EntityUnknownHurt getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			IDataProvider<LivingEntity> livingDP = DataProvider.parse(info, LivingEntity.class, name);
            if(livingDP == null) return null;

            ModDamage.addToLogRecord(OutputPreset.INFO, "UnknownHurt "+livingDP+":");

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			IDataProvider<Number> hurt_amount = NumberExp.getNew(routines, einfo);
            if (hurt_amount == null) return null;

			return new EntityUnknownHurt(matcher.group(), livingDP, hurt_amount);
		}
	}
}
