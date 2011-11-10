package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.ParentheticalParser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement.LogicalOperator;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement.StatementBuilder;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.Chance;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.Comparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityBlockStatus;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityStatus;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityTagged;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityTypeEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityWearing;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityWielding;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventHasRangedElement;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventWorldEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.ServerOnlineMode;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.WorldEnvironment;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Permissions.PlayerGroupEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Permissions.PlayerPermissionEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Regions.EntityRegion;

public class ConditionalRoutine extends NestedRoutine
{
	private static HashMap<Pattern, StatementBuilder> registeredConditionalStatements = new HashMap<Pattern, StatementBuilder>();
	
	protected static final String conditionalStatementPart = "!?(.*|\\(.*\\))";

	protected final boolean inverted;
	protected final List<ConditionalStatement> statements;
	protected final List<LogicalOperator> operators;
	protected final List<Routine> routines;
	private ConditionalRoutine(String configString, boolean inverted, List<ConditionalStatement> statements, List<LogicalOperator> operators, List<Routine> routines)
	{
		super(configString);
		this.inverted = inverted;
		this.statements = statements;
		this.operators = operators;
		this.routines = routines;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		if(ConditionalStatement.evaluateStatements(eventInfo, statements, operators) ^ inverted)
			for(Routine routine : routines)
				routine.run(eventInfo);
	}
	
	public static ConditionalStatement getNewTerm(String string)
	{
		if(string != null)
			for(Pattern pattern : registeredConditionalStatements.keySet())
			{
				Matcher matcher = pattern.matcher(string);
				if(matcher.matches())
					return registeredConditionalStatements.get(pattern).getNew(matcher);
			}
		return null;
	}
	
	public static void register()
	{
		registeredConditionalStatements.clear();
		NestedRoutine.registerRoutine(Pattern.compile("(if|if_not)\\s+(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
		NestedConditionalStatement.register();
		Chance.register();
		Comparison.register();
		//Entity
		EntityBiome.register();
		EntityBlockStatus.register();
		EntityRegion.register();
		EntityStatus.register();
		EntityTagged.register();
		EntityTypeEvaluation.register();
		EntityWearing.register();
		EntityWielding.register();
		EventWorldEvaluation.register();
		PlayerGroupEvaluation.register();
		PlayerPermissionEvaluation.register();
		//Event
		EventHasRangedElement.register();
		EventWorldEvaluation.register();
		//World
		WorldEnvironment.register();
		//Server
		ServerOnlineMode.register();
	}

	protected final static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public ConditionalRoutine getNew(Matcher matcher, Object nestedContent)
		{
			if(matcher != null && nestedContent != null)
			{
				ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
				ModDamage.addToLogRecord(DebugSetting.NORMAL, "Conditional: \"" + matcher.group() + "\"", LoadState.SUCCESS);
				
				List<ConditionalStatement> statements = new ArrayList<ConditionalStatement>();
				List<LogicalOperator> operations = new ArrayList<LogicalOperator>();
				operations.add(LogicalOperator.OR);
				
				matcher.matches();
				try
				{
					boolean couldReadStatements = ParentheticalParser.tokenize(matcher.group(2), conditionalStatementPart, LogicalOperator.logicalOperationPart, ConditionalRoutine.class.getMethod("getNewTerm", String.class), LogicalOperator.class.getMethod("match", String.class), statements, operations);
					
					ModDamage.indentation++;
					LoadState[] stateMachine = { LoadState.SUCCESS };
					List<Routine> routines = RoutineAliaser.parse(nestedContent, stateMachine);
					ModDamage.indentation--;
						
					if(couldReadStatements && stateMachine[0].equals(LoadState.SUCCESS))
					{
						ModDamage.addToLogRecord(DebugSetting.VERBOSE, "End Conditional \"" + matcher.group() + "\"", LoadState.SUCCESS);
						ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
						return new ConditionalRoutine(matcher.group(), !matcher.group(1).equalsIgnoreCase("if"), statements, operations, routines);
					}
				}
				catch (Exception e){ e.printStackTrace();}
				ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid Conditional \"" + matcher.group() + "\"", LoadState.FAILURE);
				ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
			}
			return null;
		}
	}
	
	public static void registerConditionalStatement(Pattern syntax, StatementBuilder builder)
	{
		Routine.registerRoutine(registeredConditionalStatements, syntax, builder);
	}
}
