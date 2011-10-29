package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;

public class ParentheticalParser
{
	public static <TermClass, OperatorClass> boolean tokenize(String input, String termRegex, String operatorRegex, Method newTermMethod, Method newOperatorMethod, List<TermClass> terms, List<OperatorClass> operators)
	{
		//This method does some sanity checking before we actually use the doTokenize method.
		if(sanityCheckString(termRegex, operatorRegex) && sanityCheckMethod(newTermMethod, terms) && sanityCheckMethod(newOperatorMethod, operators))
		{
			try
			{
				boolean returnValue = doTokenize(input.toCharArray(), termRegex, operatorRegex, newTermMethod, newOperatorMethod, terms, operators);
				if(!returnValue)
					ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: \"" + input + "\" is not a valid expression.", LoadState.FAILURE);
				return returnValue;
			}
			catch(Exception e)
			{
				System.out.println("Whoops, something went wrong. Tell Koryu he's an idiot.");//FIXME when this is done.
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private static boolean sanityCheckString(String...strings)
	{
		for(String string : strings)
			if(string.contains("\\s") || string.contains(" "))
				return false;
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private static <TargetClass> boolean sanityCheckMethod(Method method, List<TargetClass> list)
	{
		Object object = null;
		try
		{
			object = method.invoke(null, (String)null);
			list.add((TargetClass)object);
			list.remove(list.size() - 1);
		}
		catch(Exception e)
		{
			logError("Programmer error! Bug Koryu about his idiocy with the parenthetical parser."); //shouldn't happen
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private static <TermClass, OperatorClass> boolean doTokenize(char[] input, String termRegex, String operatorRegex, Method newTermMethod, Method newOperatorMethod, List<TermClass> terms, List<OperatorClass> operators) throws Exception
	{
		Pattern termPattern = Pattern.compile(termRegex, Pattern.CASE_INSENSITIVE);
		Pattern operatorPattern = Pattern.compile(operatorRegex, Pattern.CASE_INSENSITIVE);
		
		boolean gettingTerm = true;
		StringBuffer temp = new StringBuffer();
		int termParenthesesOpenCount = 0;
		boolean readyForMethod = false;
		boolean failFlag = false;
		
		int i = 0;
		if(Character.isWhitespace(input[i]))
			while(i < input.length && Character.isWhitespace(input[i]))
				i++;
		for(; i < input.length; i++)
		{
			switch(input[i])
			{
				case ')':
				case '(':
					if(gettingTerm)
					{
						if(input[i] == ')')
						{
							if(--termParenthesesOpenCount < 0)
							{
								failFlag = true;
								logError("Encountered unexpected closing parenthesis at index " + i + ".");
								return false;
							}
						}
						else termParenthesesOpenCount++;
						temp.append(input[i]);
					}
					else
					{
						logError("Encountered unexpected " + (input[i] == '('?"opening":"closing") + " parenthesis at index " + i + ".");
						return false;
					}
					break;
				default:
					if(termParenthesesOpenCount == 0)
					{
						if(Character.isWhitespace(input[i]))
						{
							readyForMethod = true;
							while(i + 1 < input.length && Character.isWhitespace(input[i + 1]))
								i++;
						}
						else
						{
							temp.append(input[i]);
							readyForMethod = i == input.length - 1;
						}
					}
					else temp.append(input[i]);
					break;
			}
			if(i == input.length - 1)
				readyForMethod = true;

			if(readyForMethod)
			{
				Object tokenObject = null;
				if(gettingTerm)
				{
					tokenObject = termPattern.matcher(temp).matches()?newTermMethod.invoke(null, String.valueOf(temp)):null;
					if(tokenObject != null)
						terms.add((TermClass)tokenObject);
				}
				else
				{
					tokenObject = operatorPattern.matcher(temp).matches()?newOperatorMethod.invoke(null, String.valueOf(temp)):null;
					if(tokenObject != null)
						operators.add((OperatorClass)tokenObject);
				}
				if(tokenObject == null)
				{
					failFlag = true;
					logError("Couldn't match " + (gettingTerm?"term":"operator") + " \"" + temp + "\"");
				}
				temp.delete(0, temp.length());
				readyForMethod = false;
				gettingTerm = !gettingTerm;
			}
		}

		if(gettingTerm)
		{
			logError("Expected a term to complete operation at the end of string.");
			failFlag = true;
		}
		return !failFlag;
	}
	
	private static void logError(String message)//FIXME Add to ModDamage main?
	{
		ModDamage.addToLogRecord(DebugSetting.QUIET, "Parse error: " + message, LoadState.FAILURE);
	}
}
