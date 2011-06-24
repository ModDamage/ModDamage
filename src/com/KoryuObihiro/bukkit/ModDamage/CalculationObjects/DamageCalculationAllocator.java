package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Addition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DiceRoll;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DiceRollAddition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Division;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DivisionAddition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Multiplication;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Set;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Binomial;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityDrowning;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityHealthEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityHealthGreaterThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityHealthGreaterThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityHealthLessThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityHealthLessThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityOnFire;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityUnderwater;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.World.WorldTime;

public class DamageCalculationAllocator
{	
	//for file parsing
	public List<DamageCalculation> parseStrings(List<String> calcStrings) 
	{
		List<DamageCalculation> calculations = new ArrayList<DamageCalculation>();
		for(String calcString : calcStrings)
		{
			List<String> commandSplit = Arrays.asList(calcString.split("\\*"));
			DamageCalculation calculation = parseString(commandSplit.get(0).split("\\."), commandSplit);
			if(calculation != null)
				calculations.add(calculation);
		}
		return calculations;
	}
	
	private DamageCalculation parseString(String[] args, List<String> commandSplit) 
	{
		try
		{
			
			if(args.length > 0)
			{        
				try{ return new Addition(Integer.parseInt(args[0]));}
				catch(Exception e){}
				
				if(commandSplit.size() > 1)
				{
					if(args.length == 2)
					{
						if(args[0].equalsIgnoreCase("binom")) return new Binomial(Integer.parseInt(args[1]), parseStrings(commandSplit.subList(1, commandSplit.size())));
					}
					if(args[0].equalsIgnoreCase("if"))
					{
						if(args.length == 3)
						{
							if(args[1].equalsIgnoreCase("attackerIs")) 
							{
								if(args[2].equalsIgnoreCase("onFire")) return new EntityOnFire(true, parseStrings(commandSplit.subList(1, commandSplit.size())));
								else if(args[2].equalsIgnoreCase("drowning")) return new EntityDrowning(true, parseStrings(commandSplit.subList(1, commandSplit.size())));
								else if(args[2].equalsIgnoreCase("underwater")) return new EntityUnderwater(true, parseStrings(commandSplit.subList(1, commandSplit.size())));
							}
							else if(args[1].equalsIgnoreCase("targetIs"))
							{
								if(args[2].equalsIgnoreCase("onFire")) return new EntityOnFire(false, parseStrings(commandSplit.subList(1, commandSplit.size())));
								else if(args[2].equalsIgnoreCase("drowning")) return new EntityDrowning(false, parseStrings(commandSplit.subList(1, commandSplit.size())));
								else if(args[2].equalsIgnoreCase("underwater")) return new EntityUnderwater(false, parseStrings(commandSplit.subList(1, commandSplit.size())));
							}
						}
						else if(args.length == 4)
						{
							if(args[1].equalsIgnoreCase("attackerHealth"))
							{
								if(args[2].equalsIgnoreCase("lessThan")) return new EntityHealthLessThan(true, Integer.parseInt(args[3]), parseStrings(commandSplit.subList(1, commandSplit.size())));
								else if(args[2].equalsIgnoreCase("lessThanEquals")) return new EntityHealthLessThanEquals(true, Integer.parseInt(args[3]), parseStrings(commandSplit.subList(1, commandSplit.size())));
								else if(args[2].equalsIgnoreCase("greaterThan")) return new EntityHealthGreaterThan(true, Integer.parseInt(args[3]), parseStrings(commandSplit.subList(1, commandSplit.size())));
								else if(args[2].equalsIgnoreCase("greaterThanEquals")) return new EntityHealthGreaterThanEquals(true, Integer.parseInt(args[3]), parseStrings(commandSplit.subList(1, commandSplit.size())));
								else if(args[2].equalsIgnoreCase("equals")) return new EntityHealthEquals(true, Integer.parseInt(args[3]), parseStrings(commandSplit.subList(1, commandSplit.size())));
							}
							else if(args[1].equalsIgnoreCase("targetHealth"))
							{
								if(args[2].equalsIgnoreCase("lessThan")) return new EntityHealthLessThan(false, Integer.parseInt(args[3]), parseStrings(commandSplit.subList(1, commandSplit.size())));
								else if(args[2].equalsIgnoreCase("lessThanEquals")) return new EntityHealthLessThanEquals(false, Integer.parseInt(args[3]), parseStrings(commandSplit.subList(1, commandSplit.size())));
								else if(args[2].equalsIgnoreCase("greaterThan")) return new EntityHealthGreaterThan(false, Integer.parseInt(args[3]), parseStrings(commandSplit.subList(1, commandSplit.size())));
								else if(args[2].equalsIgnoreCase("greaterThanEquals")) return new EntityHealthGreaterThanEquals(false, Integer.parseInt(args[3]), parseStrings(commandSplit.subList(1, commandSplit.size())));
								else if(args[2].equalsIgnoreCase("equals")) return new EntityHealthEquals(false, Integer.parseInt(args[3]), parseStrings(commandSplit.subList(1, commandSplit.size())));
							}
							else if(args[1].equalsIgnoreCase("worldTime"))
							{
								return new WorldTime(Integer.parseInt(args[2]), Integer.parseInt(args[3]), parseStrings(commandSplit.subList(1, commandSplit.size())));
							}
						}
					}
				}
				else if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("roll")) return new DiceRoll();
				}
				else if(args.length == 2)
				{
					if(args[0].equalsIgnoreCase("binom"))return new Binomial(Integer.parseInt(args[1]));
					else if(args[0].equalsIgnoreCase("div"))	return new Division(Integer.parseInt(args[1]));
					else if(args[0].equalsIgnoreCase("div_add"))return new DivisionAddition(Integer.parseInt(args[1]));
					else if(args[0].equalsIgnoreCase("mult")) 	return new Multiplication(Integer.parseInt(args[1]));
					else if(args[0].equalsIgnoreCase("roll")) 	return new DiceRollAddition(Integer.parseInt(args[1]));
					else if(args[0].equalsIgnoreCase("set"))	return new Set(Integer.parseInt(args[1]));
				}
				
			}
			throw new Exception();
		}
		catch(Exception e){ return null;}
	}
	//IFs(?): TODO mebbe
	// entityis.targetedByOther
	// playeris.locatedIRL.$area
	// playeris.wearingonly.ARMORSET
	// playeris.wearing.ARMORSET
	// playeris.wielding.ITEM_NAME
	// serveris.onlinemode
	// serveris.portedAt.#port
	// damageis.OPERATION.#value
	//
	//EFFECTs
	//(GET and SET)
	// setEntity.health.#value   <- Sets event damage to 0? (probably not)
	// setEntity.item.#material
	
	//TODO Implement CONDITION so that inverting is easy
};