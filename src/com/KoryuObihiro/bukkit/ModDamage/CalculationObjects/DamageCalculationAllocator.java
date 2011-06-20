package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import java.util.ArrayList;
import java.util.List;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.AdditionCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.BinomialCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DiceRollAdditionCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DiceRollCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DivisionAdditionCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DivisionCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.MultiplicationCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.SetCalculation;

public class DamageCalculationAllocator
{	
	//for file parsing
	public List<DamageCalculation> parseStrings(List<String> calcStrings, boolean isOffensive) 
	{
		List<DamageCalculation> calculations = new ArrayList<DamageCalculation>();
		for(String calcString : calcStrings)
		{
			DamageCalculation calculation = parseString(calcString, isOffensive);
			if(calculation != null)
				calculations.add(calculation);
		}
		return (calculations.isEmpty()?null:calculations);
	}
	
	private DamageCalculation parseString(String calcString, boolean isOffensive) 
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
					if(args[0].equals("binom")) return new BinomialCalculation(Integer.parseInt(args[1]), this.parseString(commandSplit[1], isOffensive));
				}
				else if(args.length == 1)
				{
					if(args[0].equals("roll")) return new DiceRollCalculation();
				}
				else if(args.length == 2)
				{
					if(args[0].equals("roll")) 			return new DiceRollAdditionCalculation(Integer.parseInt(args[1]));
					else if(args[0].equals("mult")) 	return new MultiplicationCalculation(Integer.parseInt(args[1]));
					else if(args[0].equals("div"))		return new DivisionCalculation(Integer.parseInt(args[1]));
					else if(args[0].equals("div_add"))	return new DivisionAdditionCalculation(Integer.parseInt(args[1]));
					else if(args[0].equals("set"))		return new SetCalculation(Integer.parseInt(args[1]));
					else if(args[0].equals("binom"))	return new BinomialCalculation(Integer.parseInt(args[1]));
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
	// 
};