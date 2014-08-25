package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Effect;
import org.bukkit.Location;

import com.ModDamage.LogUtil;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.LiteralNumber;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

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
		MOBSPAWNER_FLAMES(Effect.MOBSPAWNER_FLAMES),
		STEP_SOUND(Effect.STEP_SOUND),
		ZOMBIE_CHEW_IRON_DOOR(Effect.ZOMBIE_CHEW_IRON_DOOR),
		ZOMBIE_CHEW_WOODEN_DOOR(Effect.ZOMBIE_CHEW_WOODEN_DOOR),
		ZOMBIE_DESTROY_DOOR(Effect.ZOMBIE_DESTROY_DOOR);
		
		private final Effect effect;
		private EffectType(Effect effect) { this.effect = effect; }
		public Integer dataForExtra(String extra) { return null; }
	}
	
	private final IDataProvider<Location> locDP;
	private final EffectType effectType;
	private final IDataProvider<? extends Number> effectData;
	private final IDataProvider<Integer> radius;
	
	protected PlayEffect(ScriptLine scriptLine, IDataProvider<Location> locDP, EffectType effectType, IDataProvider<? extends Number> data, IDataProvider<Integer> radius)
	{
		super(scriptLine);
		this.locDP = locDP;
		this.effectType = effectType;
		this.effectData = data;
		this.radius = radius;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Location loc = locDP.get(data);
		if (loc == null) return;
		
		Number eData = effectData.get(data);
		if (eData == null) return;
		
		if (radius == null)
			loc.getWorld().playEffect(loc, effectType.effect, eData.intValue());
		else {
			Number rad = radius.get(data);
			if (rad == null) return;
			
			loc.getWorld().playEffect(loc, effectType.effect, eData.intValue(), rad.intValue());
		}
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(.+?)\\.playeffect\\.(\\w+)(?:\\.([^.]+))?(?:\\.radius\\.(.+))?", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}

	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{ 
			IDataProvider<Location> locDP = DataProvider.parse(scriptLine, info, Location.class, matcher.group(1));
			if (locDP == null) return null;
			
			EffectType effectType;
			try
			{
				effectType = EffectType.valueOf(matcher.group(2).toUpperCase());
			}
			catch (IllegalArgumentException e)
			{
				LogUtil.error("Bad effect type: \""+matcher.group(2)+"\"");
				return null;
			}
			IDataProvider<? extends Number> data;
			if (matcher.group(3) == null)
				data = new LiteralNumber(0);
			else {
				Integer ndata = effectType.dataForExtra(matcher.group(3));
				if (ndata == null)
				{
					data = DataProvider.parse(scriptLine, info, Integer.class, matcher.group(3));
					
					if (data == null)
					{
						LogUtil.error("Bad extra data: \""+matcher.group(3)+"\" for " + effectType + " effect.");
						return null;
					}
				}
				else
					data = new LiteralNumber(ndata);
			}
			
			IDataProvider<Integer> radius = null;
			if (matcher.group(4) != null)
			{
				radius = DataProvider.parse(scriptLine, info, Integer.class, matcher.group(4));
				if (radius == null)
				{
					LogUtil.error("Unable to match expression: \""+matcher.group(4)+"\"");
					return null;
				}
			}
			
			LogUtil.info("PlayEffect: " + locDP + " " + effectType + " " + data + (radius != null? " " + radius : ""));
			return new RoutineBuilder(new PlayEffect(scriptLine, locDP, effectType, data, radius));
		}
	}
}
