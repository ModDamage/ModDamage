package com.moddamage.routines;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.LogUtil;
import com.moddamage.backend.BailException;
import com.moddamage.backend.NonNull;
import com.moddamage.backend.ScriptLine;
import com.moddamage.backend.ScriptLineHandler;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.routines.nested.Command;
import com.moddamage.routines.nested.DropItem;
import com.moddamage.routines.nested.EntityItemAction;
import com.moddamage.routines.nested.Message;
import com.moddamage.routines.nested.NestedRoutine;

abstract public class Routine
{
	protected static final Pattern anyPattern = Pattern.compile(".*");
	//private static final RoutineBuilder builder = new RoutineBuilder();
	public static final Map<Pattern, RoutineFactory> registeredRoutines = new LinkedHashMap<Pattern, RoutineFactory>();

	private final ScriptLine scriptLine;

	protected Routine(ScriptLine scriptLine)
	{
		this.scriptLine = scriptLine;
	}

	public final ScriptLine getScriptLine()
	{
		return scriptLine;
	}

	@Override
	public String toString()
	{
		return scriptLine.line;
	}

	abstract public void run(final EventData data) throws BailException;

	public static void registerVanillaRoutines()
	{
		registeredRoutines.clear();
		AliasedRoutine.register();
		Untag.registerRoutine();
		PlayEffect.register();
		PlayEntityEffect.register();
		PlaySound.register();
		Message.registerRoutine();
		EntityItemAction.registerRoutine();
		DropItem.registerRoutine();
		Command.registerRoutine();
		ClearEnchantments.register();
		Teleport.register();
		AddPotionEffect.register();
		RemovePotionEffect.register();
		Cancel.register();
		RepeatControl.register();
		Despawn.register();
		ClearList.register();
		Lightning.register();
		EntityHurt.register();
		EntityUnknownHurt.register();
		EntityHeal.register();
		SetProperty.register();
		
		NestedRoutine.registerVanillaRoutines();
	}

	protected static void registerRoutine(Pattern pattern, RoutineFactory factory)
	{
		registeredRoutines.put(pattern, factory);
	}

	public static IRoutineBuilder getNew(ScriptLine line, EventInfo info)
	{
		for(Entry<Pattern, RoutineFactory> entry : registeredRoutines.entrySet())
		{
			Matcher anotherMatcher = entry.getKey().matcher(line.line);
			if(anotherMatcher.matches())
			{
				IRoutineBuilder builder = entry.getValue().getNew(anotherMatcher, line, info);
				if(builder != null)
					return builder;
			}
		}
		LogUtil.error(" No match found for routine \"" + line.line + "\"");
		return null;
	}

	public abstract static class RoutineFactory
	{
		public abstract IRoutineBuilder getNew(@NonNull Matcher matcher, ScriptLine scriptLine, EventInfo info);
	}
	
	public interface IRoutineBuilder
	{
		public ScriptLineHandler getScriptLineHandler();
		public Routine buildRoutine();
	}
	
	protected static class RoutineBuilder implements IRoutineBuilder
	{
		Routine routine;
		public RoutineBuilder(Routine routine) {
			this.routine = routine;
		}
		@Override
		public ScriptLineHandler getScriptLineHandler()
		{
			return null;
		}
		
		@Override
		public Routine buildRoutine()
		{
			return routine;
		}
	}
}