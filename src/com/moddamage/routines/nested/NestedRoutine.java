package com.moddamage.routines.nested;

import com.moddamage.LogUtil;
import com.moddamage.ModDamage;
import com.moddamage.MDLogger.OutputPreset;
import com.moddamage.backend.ScriptLine;
import com.moddamage.backend.ScriptLineHandler;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.external.mcMMO.ModifySkill;
import com.moddamage.routines.Explode;
import com.moddamage.routines.Routine;
import com.moddamage.routines.Routines;

public abstract class NestedRoutine extends Routine
{
//	public static LinkedHashMap<Pattern, RoutineFactory> registeredNestedRoutines = new LinkedHashMap<Pattern, RoutineFactory>();
	
	public final Routines routines = new Routines();

	protected NestedRoutine(ScriptLine scriptLine){ super(scriptLine); }

	public static void registerVanillaRoutines()
	{
//		registeredNestedRoutines.clear();
		If.register();
		While.register();
		With.register();
		Foreach.register();
		For.register();
		Delay.register();
		Knockback.register();
		SwitchRoutine.register();
		Spawn.register();
		EntityItemAction.registerNested();
		DropItem.registerNested();
		Explode.register();
		LaunchProjectile.register();
		Nearby.register();
		ModifySkill.register();
		Command.registerNested();
		PlayerChat.registerNested();
		LogMessage.register();
	}

//	protected static void registerRoutine(Pattern pattern, RoutineFactory builder)
//	{
//		registeredNestedRoutines.put(pattern, builder);
//	}

//	public static NestedRoutineBuilder getNew(String string, Object nestedContent, EventInfo info)
//	{
//		for(Entry<Pattern, RoutineFactory> entry : registeredNestedRoutines.entrySet())
//		{
//			Matcher matcher = entry.getKey().matcher(string);
//			if(matcher.matches())
//			{
//				NestedRoutine routine = entry.getValue().getNew(matcher, nestedContent, info);
//				if (routine != null)
//					return routine;
//			}
//		}
//		LogUtil.error(" No match found for nested routine \"" + string + "\"");		
//		return null;
//	}

	protected static class NestedRoutineBuilder extends RoutineBuilder
	{
		Routines routines;
		EventInfo info;
		public NestedRoutineBuilder(Routine routine, Routines routines, EventInfo info) {
			super(routine);
			this.routines = routines;
			this.info = info;
		}
		@Override
		public ScriptLineHandler getScriptLineHandler()
		{
			return routines.getLineHandler(info);
		}
	}

	public static void paddedLogRecord(OutputPreset preset, String message)
	{		
		LogUtil.console_only("");
		ModDamage.addToLogRecord(preset, message);
		LogUtil.console_only("");
	}
}
