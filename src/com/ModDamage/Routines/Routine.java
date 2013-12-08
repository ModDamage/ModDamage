package com.ModDamage.Routines;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.NonNull;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Routines.Nested.Command;
import com.ModDamage.Routines.Nested.DropItem;
import com.ModDamage.Routines.Nested.EntityItemAction;
import com.ModDamage.Routines.Nested.Message;
import com.ModDamage.Routines.Nested.NestedRoutine;

abstract public class Routine
{
	protected static final Pattern anyPattern = Pattern.compile(".*");
	//private static final RoutineBuilder builder = new RoutineBuilder();
	private static final Map<Pattern, RoutineFactory> registeredRoutines = new LinkedHashMap<Pattern, RoutineFactory>();

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
		ModDamage.addToLogRecord(OutputPreset.FAILURE, " No match found for routine \"" + line.line + "\"");
		return null;
	}

	protected abstract static class RoutineFactory
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