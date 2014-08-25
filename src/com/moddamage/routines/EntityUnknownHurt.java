package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.ModDamage.LogUtil;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class EntityUnknownHurt extends Routine
{
	private final IDataProvider<LivingEntity> livingDP;
	private final IDataProvider<Number> hurt_amount;
	
	public EntityUnknownHurt(ScriptLine scriptLine, IDataProvider<LivingEntity> livingDP, IDataProvider<Number> hurt_amount)
	{
		super(scriptLine);
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
		Routine.registerRoutine(Pattern.compile("(.*?)(?:effect)?\\.unknownhurt(?::|\\s+by\\s)\\s*(.+)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}
	
	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			IDataProvider<LivingEntity> livingDP = DataProvider.parse(info, LivingEntity.class, name);
            if(livingDP == null) return null;


			IDataProvider<Number> hurt_amount = DataProvider.parse(info, Number.class, matcher.group(2));
            if (hurt_amount == null) return null;
            
            LogUtil.info("UnknownHurt " + livingDP + " by " + hurt_amount);

			return new RoutineBuilder(new EntityUnknownHurt(scriptLine, livingDP, hurt_amount));
		}
	}
}
