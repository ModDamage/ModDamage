package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

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
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown potion effect type '"+matcher.group(2)+"'");
				return null;
			}
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "RemovePotionEffect: from " + livingDP + ", " + type.getName());
			return new RoutineBuilder(new RemovePotionEffect(scriptLine, livingDP, type));
		}
	}
}
