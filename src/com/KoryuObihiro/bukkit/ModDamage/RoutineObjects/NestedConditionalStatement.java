package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.ParentheticalParser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine.LogicalOperator;

public final class NestedConditionalStatement extends ConditionalStatement
{
	final List<ConditionalStatement> statements;
	final List<LogicalOperator> operators;
	private NestedConditionalStatement(boolean inverted, List<ConditionalStatement> statements, List<LogicalOperator> operators)
	{
		super(inverted);
		this.statements = statements;
		this.operators = operators;
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{
		boolean result = false;
		for(int i = 0; i < statements.size(); i++)
			 result = operators.get(i).operate(eventInfo, result, statements.get(i));
		return result;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(NestedConditionalStatement.class, Pattern.compile("(!?)\\((.*)\\)", Pattern.CASE_INSENSITIVE));
	}
	
	public static NestedConditionalStatement getNew(Matcher matcher)
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
