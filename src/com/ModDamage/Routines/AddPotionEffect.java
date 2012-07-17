package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class AddPotionEffect extends Routine
{
	private final IDataProvider<LivingEntity> livingDP;
	private final PotionEffectType type;
	private final IDataProvider<Integer> duration, amplifier;
	
	protected AddPotionEffect(String configString, IDataProvider<LivingEntity> livingDP, PotionEffectType type,
			IDataProvider<Integer> duration, IDataProvider<Integer> amplifier)
	{
		super(configString);
		this.livingDP = livingDP;
		this.type = type;
		this.duration = duration;
		this.amplifier = amplifier;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		LivingEntity entity = livingDP.get(data);
		if (entity == null) return;

		entity.addPotionEffect(new PotionEffect(type, duration.get(data), amplifier.get(data)), true);
	}

	public static void register()
	{
		Routine.registerRoutine(
				Pattern.compile("(.+?)(?:effect)?\\.addpotioneffect\\.(\\w+)\\.(.+)", Pattern.CASE_INSENSITIVE),
				new RoutineBuilder());
	}
	
	private static Pattern dotPattern = Pattern.compile("\\s*\\.\\s*");

	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public AddPotionEffect getNew(Matcher matcher, EventInfo info)
		{ 
			IDataProvider<LivingEntity> livingDP = DataProvider.parse(info, LivingEntity.class, matcher.group(1));
			if (livingDP == null) return null; 
			
			PotionEffectType type = PotionEffectType.getByName(matcher.group(2).toUpperCase());
			if (type == null)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown potion effect type '"+matcher.group(2)+"'");
				return null;
			}
			
			StringMatcher sm = new StringMatcher(matcher.group(3));
			IDataProvider<Integer> duration = DataProvider.parse(info, Integer.class, sm.spawn()); if (duration == null) return null;
			if (!sm.matchesFront(dotPattern)) return null;
			IDataProvider<Integer> amplifier = DataProvider.parse(info, Integer.class, sm.spawn()); if (amplifier == null) return null;
			if (!sm.isEmpty()) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "AddPotionEffect: to " + livingDP + ", " + type.getName() + ", " + duration + ", " + amplifier);
			return new AddPotionEffect(matcher.group(), livingDP, type, duration, amplifier);
		}
	}
}
