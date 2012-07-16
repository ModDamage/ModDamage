package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Effect;
import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Variables.Int.Constant;

public class PlayEffect extends Routine
{
	enum EffectType {
		BOW_FIRE(Effect.BOW_FIRE),
		CLICK1(Effect.CLICK1),
		CLICK2(Effect.CLICK2),
		DOOR_TOGGLE(Effect.DOOR_TOGGLE),
		EXTINGUISH(Effect.EXTINGUISH),
		RECORD_PLAY(Effect.RECORD_PLAY),
		GHAST_SHRIEK(Effect.GHAST_SHRIEK),
		GHAST_SHOOT(Effect.GHAST_SHOOT),
		BLAZE_SHOOT(Effect.BLAZE_SHOOT),
		SMOKE(Effect.SMOKE),
		BLOCK_BREAK(Effect.STEP_SOUND),
		POTION_BREAK(Effect.POTION_BREAK),
		ENDER_SIGNAL(Effect.ENDER_SIGNAL),
		MOBSPAWNER_FLAMES(Effect.MOBSPAWNER_FLAMES);
		
		private final Effect effect;
		private EffectType(Effect effect) { this.effect = effect; }
		public Integer dataForExtra(String extra) { return null; }
	}
	
	private final IDataProvider<Entity> entityDP;
	private final EffectType effectType;
	private final IDataProvider<Integer> effectData;
	private final IDataProvider<Integer> radius;
	
	protected PlayEffect(String configString, IDataProvider<Entity> entityDP, EffectType effectType, IDataProvider<Integer> effectData, IDataProvider<Integer> radius)
	{
		super(configString);
		this.entityDP = entityDP;
		this.effectType = effectType;
		this.effectData = effectData;
		this.radius = radius;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Entity entity = entityDP.get(data);
		if (entity == null) return;
		
		if (radius == null)
			entity.getWorld().playEffect(entity.getLocation(), effectType.effect, effectData.get(data));
		else
			entity.getWorld().playEffect(entity.getLocation(), effectType.effect, effectData.get(data).intValue(), radius.get(data).intValue());
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("([a-z]+).playeffect.(\\w+)(?:\\.([^.]+))?(?:\\.radius\\.(.+))?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public PlayEffect getNew(Matcher matcher, EventInfo info)
		{ 
			IDataProvider<Entity> entityDP = DataProvider.parse(info, Entity.class, matcher.group(1));
			if (entityDP == null) return null;
			
			EffectType effectType;
			try
			{
				effectType = EffectType.valueOf(matcher.group(2).toUpperCase());
			}
			catch (IllegalArgumentException e)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad effect type: \""+matcher.group(2)+"\"");
				return null;
			}
			IDataProvider<Integer> data;
			if (matcher.group(3) == null)
				data = new Constant(0);
			else {
				Integer ndata = effectType.dataForExtra(matcher.group(3));
				if (ndata == null)
				{
					data = DataProvider.parse(info, Integer.class, matcher.group(3));
					
					if (data == null)
					{
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad extra data: \""+matcher.group(3)+"\" for " + effectType + " effect.");
						return null;
					}
				}
				else
					data = new Constant(ndata);
			}
			
			IDataProvider<Integer> radius = null;
			if (matcher.group(4) != null)
			{
				radius = DataProvider.parse(info, Integer.class, matcher.group(4));
				if (radius == null)
				{
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \""+matcher.group(4)+"\"");
					return null;
				}
			}
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "PlayEffect: " + entityDP + " " + effectType + " " + data + (radius != null? " " + radius : ""));
			return new PlayEffect(matcher.group(), entityDP, effectType, data, radius);
		}
	}
}
