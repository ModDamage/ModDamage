package com.moddamage.routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.moddamage.LogUtil;
import com.moddamage.backend.BailException;
import com.moddamage.backend.ScriptLine;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

public class Despawn extends Routine
{
	private final IDataProvider<Entity> entityDP;
	protected Despawn(ScriptLine scriptLine, IDataProvider<Entity> entityDP)
	{
		super(scriptLine);
		this.entityDP = entityDP;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Entity entity = entityDP.get(data);
		if (entity == null) return;

		entity.remove();
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(.+?)\\.(?:despawn|remove)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}

	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{ 
			IDataProvider<Entity> entityDP = DataProvider.parse(info, Entity.class, matcher.group(1));
			if (entityDP == null) return null;
			
			LogUtil.info("Despawn: " + entityDP);
			return new RoutineBuilder(new Despawn(scriptLine, entityDP));
		}
	}
}
