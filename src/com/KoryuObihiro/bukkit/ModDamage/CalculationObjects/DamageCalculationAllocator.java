package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.AdditionCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.BinomialCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DiceRollAdditionCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DiceRollCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DivisionAdditionCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DivisionCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.MultiplicationCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.SetCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.EntityDrowningConditional;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.EntityHealthEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.EntityHealthGreaterThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.EntityHealthGreaterThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.EntityHealthLessThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.EntityHealthLessThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.EntityHealthNotEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.EntityOnFireConditional;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.EntityUnderwater;

public class DamageCalculationAllocator
{	
	//for file parsing
	public List<DamageCalculation> parseStrings(List<String> calcStrings) 
	{
		List<DamageCalculation> calculations = new ArrayList<DamageCalculation>();
		for(String calcString : calcStrings)
		{
			DamageCalculation calculation = parseString(calcString);
			if(calculation != null)
			{
				calculations.add(calculation);
				//Dooooon't test any conditionals with this stuff.
				//Logger.getLogger("Minecraft").info("Sample input of 5 yields " + calculation.calculate(null, 5));//TODO REMOVE ME
			}
		}
		return calculations;
	}
	
	private DamageCalculation parseString(String calcString) 
	{
		try
		{
			try{ return new AdditionCalculation(Integer.parseInt(calcString));}
			catch(Exception e){}
			
			String[] commandSplit = calcString.split("\\*");
			String[] args = commandSplit[0].split("\\.");
			if(args.length > 0)
			{        
				if(commandSplit.length == 2)
				{
					if(args[0].equalsIgnoreCase("binom")) return new BinomialCalculation(Integer.parseInt(args[1]), this.parseString(commandSplit[1]));
				}
				else if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("roll")) return new DiceRollCalculation();
				}
				else if(args.length == 2)
				{
					if(args[0].equalsIgnoreCase("binom"))		return new BinomialCalculation(Integer.parseInt(args[1]));
					else if(args[0].equalsIgnoreCase("div"))	return new DivisionCalculation(Integer.parseInt(args[1]));
					else if(args[0].equalsIgnoreCase("div_add"))return new DivisionAdditionCalculation(Integer.parseInt(args[1]));
					else if(args[0].equalsIgnoreCase("mult")) 	return new MultiplicationCalculation(Integer.parseInt(args[1]));
					else if(args[0].equalsIgnoreCase("roll")) 	return new DiceRollAdditionCalculation(Integer.parseInt(args[1]));
					else if(args[0].equalsIgnoreCase("set"))	return new SetCalculation(Integer.parseInt(args[1]));
				}
				
			}
			throw new Exception();
		}
		catch(Exception e){ return null;}
	}

	public DamageCalculation parseConditional(String[] args, String[] commandSplit, List<String> calculationStrings)
	{
		try
		{
			if(args.length == 3)
			{
				if(args[0].equals("if"))
				{
					if(args[1].equalsIgnoreCase("attackerIs")) 
					{
						if(args[2].equalsIgnoreCase("onFire"))
							return new EntityOnFireConditional(true, parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("isDrowning"))
							return new EntityDrowningConditional(true, parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("isUnderwater"))
							return new EntityUnderwater(true, parseStrings(calculationStrings));
					}
					else if(args[1].equalsIgnoreCase("targetIs"))
					{
						if(args[2].equalsIgnoreCase("onFire"))
							return new EntityOnFireConditional(false, parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("isDrowning"))
							return new EntityDrowningConditional(false, parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("isUnderwater"))
							return new EntityUnderwater(false, parseStrings(calculationStrings));
					}
				}
			}
			else if(args.length == 4)
			{
				if(args[0].equalsIgnoreCase("if"))
				{
					if(args[2].equalsIgnoreCase("attackerHealth"))
					{

						if(args[2].equalsIgnoreCase("lessThan"))
							return new EntityHealthLessThan(true, Integer.parseInt(args[3]), parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("lessThanEquals"))
							return new EntityHealthLessThanEquals(true, Integer.parseInt(args[3]), parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("greaterThan"))
							return new EntityHealthGreaterThan(true, Integer.parseInt(args[3]), parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("greaterThanEquals"))
							return new EntityHealthGreaterThanEquals(true, Integer.parseInt(args[3]), parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("equals"))
							return new EntityHealthEquals(true, Integer.parseInt(args[3]), parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("notEquals"))
							return new EntityHealthNotEquals(true, Integer.parseInt(args[3]), parseStrings(calculationStrings));
					}
					else if(args[2].equalsIgnoreCase("targetHealth"))
					{
						if(args[2].equalsIgnoreCase("lessThan"))
							return new EntityHealthLessThan(false, Integer.parseInt(args[3]), parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("lessThanEquals"))
							return new EntityHealthLessThanEquals(false, Integer.parseInt(args[3]), parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("greaterThan"))
							return new EntityHealthGreaterThan(false, Integer.parseInt(args[3]), parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("greaterThanEquals"))
							return new EntityHealthGreaterThanEquals(false, Integer.parseInt(args[3]), parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("equals"))
							return new EntityHealthEquals(false, Integer.parseInt(args[3]), parseStrings(calculationStrings));
						else if(args[2].equalsIgnoreCase("notEquals"))
							return new EntityHealthNotEquals(false, Integer.parseInt(args[3]), parseStrings(calculationStrings));
					}
				}
			}
			throw new Exception();
		}
		catch(Exception e){ return null;}
	}
	//TODO IDEA: damage based on entity resting on block of type BLAH? This would involve icky refactoring. :P
	//TODO IF #function
	//IFs:
	// entityis.onfire
	// entityis.underwater
	// entityis.drowning
	// entityhealth.*
	// playeris.wearingonly.ARMORSET
	// playeris.wearing.ARMORSET
	// playeris.wielding.ITEM_NAME
};