package com.moddamage.routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import com.moddamage.LogUtil;
import com.moddamage.backend.BailException;
import com.moddamage.backend.ScriptLine;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

public class RemovePotionEffect extends Routine
{
	private final IDataProvider<LivingEntity> livingDP;
	private final PotionEffectType type;
	
	protected RemovePotionEffect(ScriptLine scriptLine, IDataProvider<LivingEntity> livingDP, PotionEffectType type)
	{
		super(scriptLine);
		this.livingDP = livingDP;
		this.type = type;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		LivingEntity entity = livingDP.get(data);
		if (entity == null) return;

		entity.removePotionEffect(type);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("([a-z]+?)(?:effect)?\\.removepotioneffect\\.(\\w+)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}

	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{ 
			IDataProvider<LivingEntity> livingDP = DataProvider.parse(info, LivingEntity.class, matcher.group(1));
			if (livingDP == null) return null;
			
			PotionEffectType type = PotionEffectType.getByName(matcher.group(2).toUpperCase());
			if (type == null)
			{
				LogUtil.error("Unknown potion effect type '"+matcher.group(2)+"'");
				return null;
			}
			
			LogUtil.info("RemovePotionEffect: from " + livingDP + ", " + type.getName());
			return new RoutineBuilder(new RemovePotionEffect(scriptLine, livingDP, type));
		}
	}
}
