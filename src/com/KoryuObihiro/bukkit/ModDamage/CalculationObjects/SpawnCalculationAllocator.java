package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.IntervalRange;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.LiteralRange;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Set;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Binomial;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityAltitudeEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityAltitudeGreaterThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityAltitudeGreaterThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityAltitudeLessThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityAltitudeLessThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityExposedToSky;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityLightLevelEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityLightLevelGreaterThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityLightLevelGreaterThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityLightLevelLessThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityLightLevelLessThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityOnBlock;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityUnderwater;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.World.WorldEnvironment;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.World.WorldTime;

//IFs: TODO
// playeris.locatedIRL.$area
// serveris.onlinemode
// serveris.portedAt.#port
// spawn-reason based stuff (command vs. natural?)
// implement some syntax help
// implement and/or/else?
// send player message

public class SpawnCalculationAllocator
{
	//Parse commands for different command strings the handlers pass
	//parseStrings is used to determine the next calculation object's type, and pass if off accordingly.
	public List<SpawnCalculation> parseStrings(List<Object> commandStrings)
	{
		List<SpawnCalculation> calculations = new ArrayList<SpawnCalculation>();
		for(Object calculationString : commandStrings)	
		{
			SpawnCalculation calculation = null;
			
			if(calculationString instanceof LinkedHashMap)
				calculation = parseConditional((LinkedHashMap<String, List<Object>>)calculationString);
			else if(calculationString instanceof String)
				calculation = parseNormal((String)calculationString);
			
			if(calculation != null) calculations.add(calculation);
			else return new ArrayList<SpawnCalculation>();
		}
		return calculations;
	}
	
	//parseNormal seeks for a base calculation (i.e., not a conditional) and returns it (if found) for appending to the calling list.
	public SpawnCalculation parseNormal(String commandString)
	{
		try
		{
			try{ return new Set(Integer.parseInt(commandString));}//Set
			catch(NumberFormatException e){}

			String[] args = commandString.split("\\.");
			if(args.length == 3)
			{
				if(args[0].equalsIgnoreCase("range")) return new LiteralRange(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			}
			else if(args.length == 4)
			{
				if(args[0].equalsIgnoreCase("range_int")) return new IntervalRange(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			}
			throw new Exception();
		}
		catch(Exception e){ return null;}
	}
	
	//A conditional is a bit more tricky, as we're not sure how nested things will get. 
	// When we encounter a conditional, we're just grabbing the conditional itself, and passing the rest to another parseList call.
	private SpawnCalculation parseConditional(LinkedHashMap<String, List<Object>> conditionalStatement)
	{
		try
		{
			for(String key : conditionalStatement.keySet())//should only be one. :<
			{
				String[] args = key.split("\\.");
				//TODO Refactor this so it only runs as many times as necessary.
				List<SpawnCalculation> nestedCalculations = parseStrings(conditionalStatement.get(key));
				if(nestedCalculations.isEmpty()) return null;
				
				if(args[0].equalsIgnoreCase("if") || args[0].equalsIgnoreCase("if_not"))
				{
					boolean inverted = args[0].equalsIgnoreCase("if_not");
					if(args.length == 3)
					{
						if(args[1].equalsIgnoreCase("entityis"))
						{
							if(args[2].equalsIgnoreCase("exposedToSky"))		return new EntityExposedToSky(inverted, nestedCalculations);
							else if(args[2].equalsIgnoreCase("underwater")) 	return new EntityUnderwater(inverted, nestedCalculations);
						}
						else if(args[1].equalsIgnoreCase("binom")) 				return new Binomial(Integer.parseInt(args[1]), nestedCalculations);
					}
					else if(args.length == 4)
					{
						if(args[1].equalsIgnoreCase("entityis"))
						{
							if(args[2].equalsIgnoreCase("inBiome"))
							{
								Biome biome = CalculationUtility.matchBiome(args[3].toLowerCase());
								if(biome != null) 	return new EntityBiome(biome, inverted, nestedCalculations);
							}
							else if(args[2].equalsIgnoreCase("onBlock"))
							{
								Material material = Material.matchMaterial(args[3]);
								if(material != null) return new EntityOnBlock(material, inverted, nestedCalculations);
							}
						}
						else if(args[1].equalsIgnoreCase("entityAltitude"))
						{
							if(args[2].equalsIgnoreCase("equals")) 				return new EntityAltitudeEquals(inverted, Integer.parseInt(args[3]), nestedCalculations);
							if(args[2].equalsIgnoreCase("lessThan") || args[2].equalsIgnoreCase("<")) 				return new EntityAltitudeLessThan(inverted, Integer.parseInt(args[3]), nestedCalculations);
							else if(args[2].equalsIgnoreCase("lessThanEquals") || args[2].equalsIgnoreCase("<="))	return new EntityAltitudeLessThanEquals(inverted, Integer.parseInt(args[3]), nestedCalculations);
							else if(args[2].equalsIgnoreCase("greaterThan") || args[2].equalsIgnoreCase(">")) 		return new EntityAltitudeGreaterThan(inverted, Integer.parseInt(args[3]), nestedCalculations);
							else if(args[2].equalsIgnoreCase("greaterThanEquals") || args[2].equalsIgnoreCase(">="))return new EntityAltitudeGreaterThanEquals(inverted, Integer.parseInt(args[3]), nestedCalculations);
							else if(args[2].equalsIgnoreCase("equals") || args[2].equalsIgnoreCase("=")) 			return new EntityAltitudeEquals(inverted, Integer.parseInt(args[3]), nestedCalculations);
						}
						else if(args[1].equalsIgnoreCase("entityLight"))
						{
							if(args[2].equalsIgnoreCase("lessThan") || args[2].equalsIgnoreCase("<")) 				return new EntityLightLevelLessThan(inverted, Byte.parseByte(args[3]), nestedCalculations);
							else if(args[2].equalsIgnoreCase("lessThanEquals") || args[2].equalsIgnoreCase("<="))	return new EntityLightLevelLessThanEquals(inverted, Byte.parseByte(args[3]), nestedCalculations);
							else if(args[2].equalsIgnoreCase("greaterThan") || args[2].equalsIgnoreCase(">")) 		return new EntityLightLevelGreaterThan(inverted, Byte.parseByte(args[3]), nestedCalculations);
							else if(args[2].equalsIgnoreCase("greaterThanEquals") || args[2].equalsIgnoreCase(">="))return new EntityLightLevelGreaterThanEquals(inverted, Byte.parseByte(args[3]), nestedCalculations);
							else if(args[2].equalsIgnoreCase("equals") || args[2].equalsIgnoreCase("=")) 			return new EntityLightLevelEquals(inverted, Byte.parseByte(args[3]), nestedCalculations);
						}
						else if(args[1].equalsIgnoreCase("worldTime")) return new WorldTime(inverted, Integer.parseInt(args[2]), Integer.parseInt(args[3]), nestedCalculations);
						else if(args[1].equalsIgnoreCase("worldEnvironment"))
						{
							Environment environment = CalculationUtility.matchEnvironment(args[2]);
							if(environment != null) return new WorldEnvironment(inverted, environment, nestedCalculations);
						}
					}
				}
			}
		}
		catch(Exception e){}
		return null;
	}
}