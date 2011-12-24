package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.ParentheticalParser;

public final class NestedConditionalStatement extends ConditionalStatement
{
	private static final Pattern syntax = Pattern.compile("(!?)\\((.*)\\)", Pattern.CASE_INSENSITIVE);
	private static final StatementBuilder builder = new StatementBuilder();

	final List<ConditionalStatement> statements;
	final List<LogicalOperator> operators;
	private NestedConditionalStatement(boolean inverted, List<ConditionalStatement> statements, List<LogicalOperator> operators)
	{
		super(inverted);
		this.statements = statements;
		this.operators = operators;
	}

	@Override
	public boolean condition(TargetEventInfo eventInfo)
	{
		return ConditionalStatement.evaluateStatements(eventInfo, statements, operators);
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(syntax, builder);
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{
		@Override
		public NestedConditionalStatement getNew(Matcher matcher)
		{
			if(matcher != null)
			{
				List<ConditionalStatement> statements = new ArrayList<ConditionalStatement>();
				List<LogicalOperator> operations = new ArrayList<LogicalOperator>();
				operations.add(LogicalOperator.OR);
				
				try
				{
					if(ParentheticalParser.tokenize(matcher.group(2), ConditionalRoutine.conditionalStatementPart, LogicalOperator.logicalOperationPart, ConditionalRoutine.class.getMethod("getNewTerm", String.class), LogicalOperator.class.getMethod("match", String.class), statements, operations))
						return new NestedConditionalStatement(matcher.group(1).equals("!"), statements, operations);
				}
				catch(Exception e){ e.printStackTrace();}//shouldn't happen
			}
			return null;
		}
	}
	
	public static NestedConditionalStatement getNew(String string)
	{
		if(string != null)
		{
			Matcher matcher = syntax.matcher(string);
			if(matcher.matches())
				return builder.getNew(matcher);
		}
		return null;
	}
}
