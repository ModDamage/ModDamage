package com.ModDamage.Alias;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ModDamage.LogUtil;
import com.ModDamage.Alias.RoutineAliaser.ScriptCapturedLines;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Routines.Routines;

public class RoutineAliaser extends Aliaser<ScriptCapturedLines, ScriptCapturedLines>
{
	public static RoutineAliaser aliaser = new RoutineAliaser();
	public static Routines match(String string, EventInfo info) { return aliaser.matchAlias(string, info); }
	
	public RoutineAliaser() { super("Routine"); }
	
	
	public static class ScriptCapturedLines implements ScriptLineHandler
	{
		public List<ScriptCapturedLine> children;

		@Override
		public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren)
		{
			if (children == null)
				children = new ArrayList<RoutineAliaser.ScriptCapturedLine>();
			ScriptCapturedLine child = new ScriptCapturedLine(line);
			children.add(child);
			return child.children;
		}

		@Override
		public void done() { }
		
		public void parse(ScriptLineHandler lineHandler)
		{
			for (ScriptCapturedLine child : children)
			{
				ScriptLineHandler nestedLineHandler = lineHandler.handleLine(child.scriptLine, child.children != null);
				if (nestedLineHandler == null) throw new IllegalArgumentException("nestedLineHandler cannot be null: " + lineHandler);
				
				if (child.children != null) {
					child.children.parse(nestedLineHandler);
				}
				nestedLineHandler.done();
			}
		}
	}
	
	public static class ScriptCapturedLine
	{
		public ScriptLine scriptLine;
		public ScriptCapturedLines children = new ScriptCapturedLines();
		
		public ScriptCapturedLine(ScriptLine scriptLine)
		{
			this.scriptLine = scriptLine;
		}
	}
	
	@Override
	public ScriptLineHandler handleLine(final ScriptLine nameLine, boolean hasChildren)
	{
		return new ScriptLineHandler() {
			ScriptCapturedLines lines = new ScriptCapturedLines();
			boolean hasValue;
			
			@Override
			public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren)
			{
				hasValue = true;
				return lines.handleLine(nameLine, hasChildren);
			}
			
			@Override
			public void done()
			{
				if (!hasValue) {
					LogUtil.error(nameLine, name+" alias _"+nameLine.line+" has no routines.");
					return;
				}
				putAlias("_"+nameLine.line, lines);
			}
		};
	}
	
	private static boolean isParsingAlias = false;
	public static boolean isParsingAlias() { return isParsingAlias; }
	private static List<Runnable> runWhenDone = new ArrayList<Runnable>();
	
	public static void whenDoneParsingAlias(Runnable runnable) {
		if (isParsingAlias) runWhenDone.add(runnable);
		else runnable.run();
	}
	
	public final Map<InfoOtherPair<String>, Routines> aliasedRoutines = new HashMap<InfoOtherPair<String>, Routines>();
	public Routines matchAlias(String alias, EventInfo info)
	{
		InfoOtherPair<String> infoPair = new InfoOtherPair<String>(alias, info);
		if (aliasedRoutines.containsKey(infoPair)) return aliasedRoutines.get(infoPair);
		
		
		ScriptCapturedLines lines = getAlias(alias);
		if (lines == null)
		{
			LogUtil.error("Unknown alias: \"" + alias + "\"");
			return null;
		}
		LogUtil.info("Routines in " + alias);
		
		isParsingAlias = true;
		
		Routines routines = new Routines();
		ScriptLineHandler routinesLineHandler = routines.getLineHandler(info);
		lines.parse(routinesLineHandler);
		routinesLineHandler.done();
		
		isParsingAlias = false;
		
		aliasedRoutines.put(infoPair, routines);

		if (!runWhenDone.isEmpty())
		{
			List<Runnable> toRun = runWhenDone;
			runWhenDone = new ArrayList<Runnable>();
			for (Runnable runnable : toRun)
				runnable.run();
		}
		return routines;
	}
	
	@Override
	public void clear()
	{
		super.clear();
		aliasedRoutines.clear();
	}
	
	//Parse routine strings recursively
//	public static Routines parseRoutines(Object object, EventInfo info)
//	{
//		Routines routines = new Routines();
//		ModDamage.changeIndentation(true);
//		boolean success = recursivelyParseRoutines(routines.routines, object, info);
//		ModDamage.changeIndentation(false);
//		if (!success) return null;
//		return routines;
//	}
//	@SuppressWarnings("unchecked")
//	private static boolean recursivelyParseRoutines(List<Routine> target, Object object, EventInfo info)
//	{
//		boolean success = true;
//		if(object != null)
//		{
//			if(object instanceof String)
//			{
//				String string = (String) object;
//				
//				Routine routine = Routine.getNew(string, info);
//				if(!elseOrAppend(target, routine))
//				{
//					LogUtil.error("Invalid base routine " + " \"" + string + "\"");
//					success = false;
//				}
//			}
//			else if(object instanceof LinkedHashMap)
//			{
//				LinkedHashMap<String, Object> someHashMap = (LinkedHashMap<String, Object>)object;
//				if(someHashMap.keySet().size() == 1)
//					for(Entry<String, Object> entry : someHashMap.entrySet())//A properly-formatted nested routine is a LinkedHashMap with only one key.
//					{
//						NestedRoutine routine = NestedRoutine.getNew(entry.getKey(), entry.getValue(), info);
//						if(!elseOrAppend(target, routine))
//						{
//							success = false;
//							break;
//						}
//					}
//				else LogUtil.error("Parse error: invalid nested routine \"" + someHashMap.toString() + "\"");
//			}
//			else if(object instanceof List)
//				for(Object nestedObject : (List<Object>)object)
//				{
//					if(!recursivelyParseRoutines(target, nestedObject, info))
//						success = false;
//				}
//			else
//			{
//				LogUtil.error("Parse error: did not recognize object " + object.toString() + " of type " + object.getClass().getName());
//				return false;
//			}
//		}
//		else
//		{
//			LogUtil.error("Parse error: null");
//			success = false;
//		}
//		return success;
//	}
	
//	private static boolean elseOrAppend(List<Routine> routines, Routine newRoutine)
//	{
//		if (newRoutine == null) return false;
//		
//		if (newRoutine instanceof If && ((If) newRoutine).isElse) {
//			if (routines.isEmpty() || !(routines.get(routines.size()-1) instanceof If)) {
//				LogUtil.error("Error: else not after if: '"+newRoutine+"'");
//				return false;
//			}
//			
//			If ifRoutine = (If) routines.get(routines.size()-1);
//			while (ifRoutine.elseRoutine != null)
//				ifRoutine = ifRoutine.elseRoutine;
//			
//			ifRoutine.elseRoutine = (If) newRoutine;
//			return true;
//		}
//		
//		routines.add(newRoutine);
//		
//		return true;
//	}
}