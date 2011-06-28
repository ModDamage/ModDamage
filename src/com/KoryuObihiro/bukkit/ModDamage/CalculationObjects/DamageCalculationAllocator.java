package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Addition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DiceRoll;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DiceRollAddition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Division;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DivisionAddition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Multiplication;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Set;
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
	public List<DamageCalculation> parseStrings(List<Object> calcStrings) 
	{
		List<DamageCalculation> calculations = new ArrayList<DamageCalculation>();
		for(Object calculationString : calcStrings)
		{
			DamageCalculation calculation = null;
			
			if(calculationString instanceof LinkedHashMap)
				calculation = parseConditional((LinkedHashMap)calculationString);
			else if(calculationString instanceof String)
				calculation = parseNormal((String)calculationString);
			
			if(calculation != null)
			{
				calculations.add(calculation);
				Logger.getLogger("Minecraft").info("Yay, added something!");
			}
		}
		return calculations;
	}
	
	private DamageCalculation parseNormal(String argString) 
	{

		try{ return new Addition(Integer.parseInt(argString));}
		catch(Exception e){}
		
		try
		{
			String[] args = argString.split("\\.");
			if(args.length > 0)
			{ 
					if(args.length == 1)
					{
						if(args[0].equalsIgnoreCase("roll")) return new DiceRoll();
					}
					else if(args.length == 2)
					{
						if(args[0].equalsIgnoreCase("div"))	return new Division(Integer.parseInt(args[1]));
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
	
	private DamageCalculation parseConditional(LinkedHashMap conditionalStatement)
	{
		
		for(Object key : conditionalStatement.keySet())//should only be one. :<
		{
			String[] args = ((String)key).split("\\.");
			if(args[0].equalsIgnoreCase("if") || args[0].equalsIgnoreCase("if_not"))
			{
				boolean inverted = args[0].equalsIgnoreCase("if_not");
				if(args.length == 3)
				{
					Logger.getLogger("FOUND A CONDITIONAL. :D");//TODO REMOVE ME
					if(args[1].equalsIgnoreCase("attackerIs") || args[1].equalsIgnoreCase("targetIs")) 
					{
						boolean forAttacker = args[1].equalsIgnoreCase("attackerIs");
						if(args[2].equalsIgnoreCase("onFire")) return new EntityOnFire(forAttacker, parseStrings((ArrayList<Object>)conditionalStatement.get(key)));
						else if(args[2].equalsIgnoreCase("drowning")) return new EntityDrowning(forAttacker, parseStrings((ArrayList<Object>)conditionalStatement.get(key)));
						else if(args[2].equalsIgnoreCase("underwater")) return new EntityUnderwater(forAttacker, parseStrings((ArrayList<Object>)conditionalStatement.get(key)));
					}
				}
				else if(args.length == 4)
				{
					if(args[1].equalsIgnoreCase("attackerHealth") || args[1].equalsIgnoreCase("targetHealth"))
					{
						boolean forAttacker = args[0].equalsIgnoreCase("attackerHealth");
						if(args[2].equalsIgnoreCase("lessThan")) return new EntityHealthLessThan(forAttacker, Integer.parseInt(args[3]), parseStrings((ArrayList<Object>)conditionalStatement.get(key)));
						else if(args[2].equalsIgnoreCase("lessThanEquals")) return new EntityHealthLessThanEquals(forAttacker, Integer.parseInt(args[3]), parseStrings((ArrayList<Object>)conditionalStatement.get(key)));
						else if(args[2].equalsIgnoreCase("greaterThan")) return new EntityHealthGreaterThan(forAttacker, Integer.parseInt(args[3]), parseStrings((ArrayList<Object>)conditionalStatement.get(key)));
						else if(args[2].equalsIgnoreCase("greaterThanEquals")) return new EntityHealthGreaterThanEquals(forAttacker, Integer.parseInt(args[3]), parseStrings((ArrayList<Object>)conditionalStatement.get(key)));
						else if(args[2].equalsIgnoreCase("equals")) return new EntityHealthEquals(forAttacker, Integer.parseInt(args[3]), parseStrings((ArrayList<Object>)conditionalStatement.get(key)));
					}
					else if(args[1].equalsIgnoreCase("worldTime"))
					{
						return new WorldTime(Integer.parseInt(args[2]), Integer.parseInt(args[3]), parseStrings((ArrayList<Object>)conditionalStatement.get(key)));
					}
				}
			}
		}
		return null;
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