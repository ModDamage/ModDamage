package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.EntityEffect;
import org.bukkit.entity.Entity;

import com.ModDamage.LogUtil;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class PlayEntityEffect extends Routine
{
	private final IDataProvider<Entity> entityDP;
	private final EntityEffect entityEffect;
	protected PlayEntityEffect(ScriptLine scriptLine, IDataProvider<Entity> entityDP, EntityEffect entityEffect)
	{
		super(scriptLine);
		this.entityDP = entityDP;
		this.entityEffect = entityEffect;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Entity entity = entityDP.get(data);
		if (entity == null) return;

		entity.playEffect(entityEffect);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("([a-z]+)\\.playentityeffect\\.(\\w+)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}

	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			IDataProvider<Entity> entityDP = DataProvider.parse(scriptLine, info, Entity.class, matcher.group(1));
			if (entityDP == null) return null;
			
			EntityEffect effectType;
			try
			{
				effectType = EntityEffect.valueOf(matcher.group(2).toUpperCase());
			}
			catch (IllegalArgumentException e)
			{
				LogUtil.error(scriptLine, "Bad effect type: \""+matcher.group(2)+"\"");
				return null;
			}
			
			LogUtil.info("PlayEntityEffect: " + entityDP + " " + effectType);
			return new RoutineBuilder(new PlayEntityEffect(scriptLine, entityDP, effectType));
		}
	}
}
