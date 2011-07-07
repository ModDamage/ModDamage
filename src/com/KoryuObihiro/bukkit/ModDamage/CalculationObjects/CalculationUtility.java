package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.Addition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.DiceRoll;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.DiceRollAddition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.Division;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.DivisionAddition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.IntervalRange;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.LiteralRange;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.Multiplication;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.Set;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Binomial;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.EventValueComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityAltitudeComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityDrowning;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityExposedToSky;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityFallen;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityFalling;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityHealthComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityLightComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityOnBlock;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityOnFire;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityUnderwater;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityWearing;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityWearingOnly;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity.EntityWielding;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.World.WorldEnvironment;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.World.WorldTime;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Effect.EntityExplode;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Effect.EntityHeal;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Effect.EntityReflect;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Effect.EntitySetAirTicks;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Effect.EntitySetFireTicks;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Effect.EntitySetHealth;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Effect.EntitySetItem;
//TODO
//--Calculation Ideas:
// -implement some syntax help
// -implement and/or/else?
// -send player message
// -relative health/altitude/light
// -entityEffect.increaseItem.amount
// -entityEffect.decreaseItem.amount
// -if.entityis.inRegion
// -if.playeris.locatedIRL.$area
// -if.serveris.onlinemode
// -if.serveris.portedAt.#port
// -switch.region
// -switch.entitygroup
// -switch.worldtime
// -switch.environment
// -switch.spawnreason
//--Refactor:
// -Give each major division of MDCalcs their own function
//--Fix:
// -fix armorset comparison


public class CalculationUtility
{
	//Parse commands for different command strings the handlers pass
	//parseStrings is used to determine the next calculation object's type, and pass if off accordingly.
	public List<ModDamageCalculation> parseStrings(List<Object> commandStrings, boolean forSpawn)
	{
		List<ModDamageCalculation> calculations = new ArrayList<ModDamageCalculation>();
		for(Object calculationString : commandStrings)	
		{
			ModDamageCalculation calculation = null;
			
			if(calculationString instanceof LinkedHashMap)
				calculation = parseNestableCalculation((LinkedHashMap<String, List<Object>>)calculationString, forSpawn);
			else if(calculationString instanceof String)
				calculation = parseBaseCalculation((String)calculationString, forSpawn);
			
			if(calculation != null) calculations.add(calculation);
			else return new ArrayList<ModDamageCalculation>();
		}
		return calculations;
	}
	
	//parseNormal seeks for a base calculation (i.e., not a conditional) and returns it (if found) for appending to the calling list.
	public ModDamageCalculation parseBaseCalculation(String commandString, boolean forSpawn)
	{
//// SPAWN SYNTAX ////
		if(forSpawn)
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
			catch(Exception e){}
		}
//// DAMAGE SYNTAX ////
		else 
		{
			try{ return new Addition(Integer.parseInt(commandString));}
			catch(Exception e){}
			
			try
			{
				String[] args = commandString.split("\\.");
				if(args.length > 0)
				{ 
						if(args.length == 1)
						{
							if(args[0].equalsIgnoreCase("roll")) 		return new DiceRoll();
						}
						else if(args.length == 2)
						{
							if(args[0].equalsIgnoreCase("div"))			return new Division(Integer.parseInt(args[1]));
							else if(args[0].equalsIgnoreCase("div_add"))return new DivisionAddition(Integer.parseInt(args[1]));
							else if(args[0].equalsIgnoreCase("mult")) 	return new Multiplication(Integer.parseInt(args[1]));
							else if(args[0].equalsIgnoreCase("roll")) 	return new DiceRollAddition(Integer.parseInt(args[1]));
							else if(args[0].equalsIgnoreCase("set"))	return new Set(Integer.parseInt(args[1]));
							else if(args[0].equalsIgnoreCase("binom"))	return new Binomial(Integer.parseInt(args[1]));
						}
						else if(args.length == 3)
						{
							if(args[0].equalsIgnoreCase("attackerEffect") || args[0].equalsIgnoreCase("targetEffect"))
							{
								boolean forAttacker = args[0].equalsIgnoreCase("attackerEffect");
								if(args[1].equalsIgnoreCase("heal")) 				return new EntityHeal(forAttacker, Integer.parseInt(args[2]));
	 							else if(args[1].equalsIgnoreCase("explode"))		return new EntityExplode(forAttacker, Integer.parseInt(args[2]));
								else if(args[1].equalsIgnoreCase("setAirTicks"))	return new EntitySetAirTicks(forAttacker, Integer.parseInt(args[2]));
								else if(args[0].equalsIgnoreCase("setFireTicks"))	return new EntitySetFireTicks(forAttacker, Integer.parseInt(args[2]));
								else if(args[1].equalsIgnoreCase("setHealth"))		return new EntitySetHealth(forAttacker, Integer.parseInt(args[2]));
								else if(args[1].equalsIgnoreCase("setItem"))
								{
									Material material = Material.matchMaterial(args[2]);
									if(material != null) return new EntitySetItem(forAttacker, material, Integer.parseInt(args[2]));
								}
							}
							else if(args[0].equalsIgnoreCase("effect"))
								if(args[1].equalsIgnoreCase("reflect")) return new EntityReflect(Integer.parseInt(args[2]));
						}
					}
			}
			catch(Exception e){}
		}
		return null;
	}
	
	//A conditional is a bit more tricky, as we're not sure how nested things will get. 
	// When we encounter a conditional, we're just grabbing the conditional itself, and passing the rest to another parseList call.
	private ModDamageCalculation parseNestableCalculation(LinkedHashMap<String, List<Object>> conditionalStatement, boolean forSpawn)
	{
//// DAMAGE SYNTAX ////
		if(!forSpawn)
		{
			try
			{
				for(String key : conditionalStatement.keySet())//should only be one. :<
				{
					String[] args = key.split("\\.");
					//TODO Refactor this so it only runs as many times as necessary.
					List<ModDamageCalculation> nestedCalculations = parseStrings(conditionalStatement.get(key), forSpawn);
					if(nestedCalculations.isEmpty()) return null;
					
					if(args[0].equalsIgnoreCase("if") || args[0].equalsIgnoreCase("if_not"))
					{
						boolean inverted = args[0].equalsIgnoreCase("if_not");
						if(args.length == 3)
						{
							if(args[1].equalsIgnoreCase("entityis"))
							{
								if(args[2].equalsIgnoreCase("exposedToSky"))		return new EntityExposedToSky(inverted, false, nestedCalculations);
								else if(args[2].equalsIgnoreCase("underwater")) 	return new EntityUnderwater(inverted, false, nestedCalculations);
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
									if(biome != null) return new EntityBiome(inverted, false, biome, nestedCalculations);
								}
								else if(args[2].equalsIgnoreCase("onBlock"))
								{
									Material material = Material.matchMaterial(args[3]);
									if(material != null) return new EntityOnBlock(inverted, false, material, nestedCalculations);
								}
							}
							else if(args[1].equalsIgnoreCase("entityAltitude"))
							{
								ComparisonUtility comparisonType = ComparisonUtility.matchType(args[2]);
								if(comparisonType != null) return new EntityAltitudeComparison(inverted, false, comparisonType, Integer.parseInt(args[3]), nestedCalculations);
							}
							else if(args[1].equalsIgnoreCase("entityLight"))
							{
								ComparisonUtility comparisonType = ComparisonUtility.matchType(args[2]);
								if(comparisonType != null) return new EntityLightComparison(inverted, false, comparisonType, Byte.parseByte(args[3]), nestedCalculations);
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
		}
//// SPAWN SYNTAX ////
		else
		{
			try
			{
				for(Object key : conditionalStatement.keySet())//should only be one. :<
				{
					String[] args = ((String)key).split("\\.");
					//TODO Refactor this so it only runs as many times as necessary.
					List<ModDamageCalculation> nestedCalculations = parseStrings(conditionalStatement.get(key), forSpawn);
					if(nestedCalculations.isEmpty()) return null;
					
					if(args.length == 2)
					{
						if(args[0].equalsIgnoreCase("attackerEffect") || args[0].equalsIgnoreCase("targetEffect"))
						{
							boolean forAttacker = args[0].equalsIgnoreCase("attackerEffect");
							if(args[1].equalsIgnoreCase("heal")) 				return new EntityHeal(forAttacker, nestedCalculations);
							else if(args[1].equalsIgnoreCase("explode"))		return new EntityExplode(forAttacker, nestedCalculations);
							else if(args[1].equalsIgnoreCase("setAirTicks"))	return new EntitySetAirTicks(forAttacker, nestedCalculations);
							else if(args[1].equalsIgnoreCase("setFireTicks"))	return new EntitySetFireTicks(forAttacker, nestedCalculations);
							else if(args[1].equalsIgnoreCase("setHealth"))		return new EntitySetHealth(forAttacker, nestedCalculations);
							else if(args[1].equalsIgnoreCase("setItem"))		
							{
								Material material = Material.matchMaterial(args[2]);
								if(material != null) return new EntitySetItem(forAttacker, material, nestedCalculations);
							}
							else if(args[1].equalsIgnoreCase("setHealth"))		
							{
								Material material = Material.matchMaterial(args[2]);
								if(material != null) return new EntitySetHealth(forAttacker, nestedCalculations);
							}
						}
						else if(args[0].equalsIgnoreCase("effect"))
						{
							if(args[1].equalsIgnoreCase("reflect")) return new EntityReflect(nestedCalculations);
						}
						else if(args[0].equalsIgnoreCase("binom"))
						{
							return new Binomial(Integer.parseInt(args[1]), nestedCalculations);
						}
					}
					else if(args[0].equalsIgnoreCase("if") || args[0].equalsIgnoreCase("if_not"))
					{
						boolean inverted = args[0].equalsIgnoreCase("if_not");
						
						if(args.length == 3)
						{
							if(args[1].equalsIgnoreCase("damageIs"))
							{
								ComparisonUtility comparisonType = ComparisonUtility.matchType(args[2]);
								if(comparisonType != null) return new EventValueComparison(inverted, comparisonType, Integer.parseInt(args[3]), nestedCalculations);
							}
							else if(args[1].equalsIgnoreCase("attackerIs") || args[1].equalsIgnoreCase("targetIs")) 
							{
								boolean forAttacker = args[1].equalsIgnoreCase("attackerIs");
								if(args[2].equalsIgnoreCase("onFire")) 			return new EntityOnFire(inverted, forAttacker, nestedCalculations);
								else if(args[2].equalsIgnoreCase("drowning")) 	return new EntityDrowning(inverted, forAttacker, nestedCalculations);
								else if(args[2].equalsIgnoreCase("underwater")) return new EntityUnderwater(inverted, forAttacker, nestedCalculations);
							}
						}
						else if(args.length == 4)
						{
							if(args[1].equalsIgnoreCase("attackerIs") || args[1].equalsIgnoreCase("targetIs")) 
							{
								boolean forAttacker = args[1].equalsIgnoreCase("attackerIs");
								if(args[2].equalsIgnoreCase("wearing"))
								{
									ArmorSet armorSet = new ArmorSet(args[3]);
									if(!armorSet.isEmpty()) return new EntityWearing(inverted, forAttacker, armorSet.toString(), nestedCalculations);
								}
								else if(args[2].equalsIgnoreCase("wearingOnly"))
								{
									ArmorSet armorSet = new ArmorSet(args[3]);
									if(!armorSet.isEmpty()) return new EntityWearingOnly(inverted, forAttacker, armorSet.toString(), nestedCalculations);
								}
								else if(args[2].equalsIgnoreCase("wielding"))
								{
									Material material = Material.matchMaterial(args[3]);
									if(material != null) return new EntityWielding(inverted, forAttacker, material, nestedCalculations);
								}
								else if(args[2].equalsIgnoreCase("inBiome"))
								{
									Biome biome = CalculationUtility.matchBiome(args[3].toLowerCase());
									if(biome != null) 	return new EntityBiome(inverted, forAttacker, biome, nestedCalculations);
								}
								else if(args[2].equalsIgnoreCase("onBlock"))
								{
									Material material = Material.matchMaterial(args[3]);
									if(material != null) return new EntityOnBlock(forAttacker, inverted, material, nestedCalculations);
								}
								else if(args[2].equalsIgnoreCase("fallen")) return new EntityFallen(inverted, forAttacker, Integer.parseInt(args[3]), nestedCalculations);
								else if(args[2].equalsIgnoreCase("falling")) return new EntityFalling(inverted, forAttacker, nestedCalculations);
								
							}
							if(args[1].equalsIgnoreCase("attackerAltitude") || args[1].equalsIgnoreCase("targetAltitude"))
							{
								boolean forAttacker = args[0].equalsIgnoreCase("attackerAltitude");
								ComparisonUtility comparisonType = ComparisonUtility.matchType(args[2]);
								if(comparisonType != null) return new EntityAltitudeComparison(inverted, forAttacker, comparisonType, Integer.parseInt(args[3]), nestedCalculations);
							}
							if(args[1].equalsIgnoreCase("attackerHealth") || args[1].equalsIgnoreCase("targetHealth"))
							{
								boolean forAttacker = args[0].equalsIgnoreCase("attackerHealth");
								ComparisonUtility comparisonType = ComparisonUtility.matchType(args[2]);
								if(comparisonType != null) return new EntityHealthComparison(inverted, forAttacker, comparisonType, Integer.parseInt(args[3]), nestedCalculations);
							}
							if(args[1].equalsIgnoreCase("attackerLight") || args[1].equalsIgnoreCase("targetLight"))
							{
								boolean forAttacker = args[0].equalsIgnoreCase("attackerLight");
								ComparisonUtility comparisonType = ComparisonUtility.matchType(args[2]);
								if(comparisonType != null) return new EntityLightComparison(inverted, forAttacker, comparisonType, Byte.parseByte(args[3]), nestedCalculations);
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
		}
		return null;
	}

//// CALCULATION ////	
	//Frankly, most of the stuff below should be considered for implementation into Bukkit. :<
	public static Biome matchBiome(String biomeName)
	{
		if(biomeName.equalsIgnoreCase("DESERT")) return Biome.DESERT;
		else if(biomeName.equalsIgnoreCase("FOREST")) return Biome.FOREST;
		else if(biomeName.equalsIgnoreCase("HELL")) return Biome.HELL;
		else if(biomeName.equalsIgnoreCase("ICE_DESERT")) return Biome.ICE_DESERT;
		else if(biomeName.equalsIgnoreCase("PLAINS")) return Biome.PLAINS;
		else if(biomeName.equalsIgnoreCase("RAINFOREST")) return Biome.RAINFOREST;
		else if(biomeName.equalsIgnoreCase("SAVANNA")) return Biome.SAVANNA;
		else if(biomeName.equalsIgnoreCase("SEASONAL_FOREST")) return Biome.SEASONAL_FOREST;
		else if(biomeName.equalsIgnoreCase("SHRUBLAND")) return Biome.SHRUBLAND;
		else if(biomeName.equalsIgnoreCase("SKY")) return Biome.SKY;
		else if(biomeName.equalsIgnoreCase("SWAMPLAND")) return Biome.SWAMPLAND;
		else if(biomeName.equalsIgnoreCase("TAIGA")) return Biome.TAIGA;
		else if(biomeName.equalsIgnoreCase("TUNDRA")) return Biome.TUNDRA;
		return null;
	}
	
	public static Environment matchEnvironment(String environmentName)
	{
		if(environmentName.equalsIgnoreCase("NORMAL")) return Environment.NORMAL;
		else if(environmentName.equalsIgnoreCase("NETHER")) return Environment.NETHER;
		else if(environmentName.equalsIgnoreCase("SKYLANDS")) return Environment.SKYLANDS;
		return null;
	}
}