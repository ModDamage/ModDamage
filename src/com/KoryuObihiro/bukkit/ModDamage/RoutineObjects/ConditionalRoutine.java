package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.ParentheticalParser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement.LogicalOperator;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement.StatementBuilder;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.Chance;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.Comparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityBlockStatus;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityRegion;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityStatus;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityTagged;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityTypeEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityWearing;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityWielding;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventEnvironment;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventHasRangedElement;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventWorldEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerGroupEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerHasItem;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerPermissionEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.ServerOnlineMode;

public class ConditionalRoutine extends NestedRoutine
{
	private static LinkedHashMap<Pattern, StatementBuilder> registeredConditionalStatements = new LinkedHashMap<Pattern, StatementBuilder>();
	
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
		{
			for(Entry<Pattern, StatementBuilder> entry : registeredConditionalStatements.entrySet())
			{
				Matcher matcher = entry.getKey().matcher(string);
				if(matcher.matches())
					return entry.getValue().getNew(matcher);
			}
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
		PlayerHasItem.register();
		PlayerPermissionEvaluation.register();
		//Event
		EventHasRangedElement.register();
		EventWorldEvaluation.register();
		//World
		EventEnvironment.register();
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
				NestedRoutine.paddedLogRecord(OutputPreset.INFO, "Conditional: \"" + matcher.group() + "\"");
				List<ConditionalStatement> statements = new ArrayList<ConditionalStatement>();
				List<LogicalOperator> operations = new ArrayList<LogicalOperator>();
				operations.add(LogicalOperator.OR);
				matcher.matches();
				try
				{
					if(ParentheticalParser.tokenize(matcher.group(2), conditionalStatementPart, LogicalOperator.logicalOperationPart, ConditionalRoutine.class.getMethod("getNewTerm", String.class), LogicalOperator.class.getMethod("match", String.class), statements, operations))
					{
						List<Routine> routines = new ArrayList<Routine>();
						if(RoutineAliaser.parseRoutines(routines, nestedContent))
						{
							NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, "End Conditional \"" + matcher.group() + "\"");
							return new ConditionalRoutine(matcher.group(), !matcher.group(1).equalsIgnoreCase("if"), statements, operations, routines);
						}
						else NestedRoutine.paddedLogRecord(OutputPreset.FAILURE, "Bad content in Conditional \"" + matcher.group() + "\"");
					}
					else NestedRoutine.paddedLogRecord(OutputPreset.FAILURE, "Invalid Conditional \"" + matcher.group() + "\"");
				}
				catch (Exception e){ e.printStackTrace();}
			}
			return null;
		}
	}
	
	public static void registerConditionalStatement(Pattern syntax, StatementBuilder builder)
	{
		Routine.registerRoutine(registeredConditionalStatements, syntax, builder);
	}
}
