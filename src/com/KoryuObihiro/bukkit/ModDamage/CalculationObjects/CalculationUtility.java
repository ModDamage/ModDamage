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
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.Binomial;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityAltitudeComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityDrowning;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityExposedToSky;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityFallComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityHealthComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityLightComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityOnBlock;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityOnFire;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityUnderwater;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EventValueComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.PlayerWearing;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.PlayerWearingOnly;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.PlayerWielding;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.WorldEnvironment;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.WorldTime;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.EntityExplode;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.EntityHeal;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.EntityReflect;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.EntitySetAirTicks;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.EntitySetFireTicks;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.EntitySetHealth;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.PlayerSetItem;

//TODO	
//--Calculation Ideas:
// -implement some syntax help
// -implement and/or/else?
// -send player message
// -relative health/altitude/light
// -AoE clearance, block search nearby for Material?

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
// -registry of MDCalcs with regexs
//--Fix:
// -fix iswearing comparison (out of order results in no good unless exact match)


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
									if(material != null) return new PlayerSetItem(forAttacker, material, Integer.parseInt(args[2]));
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
								ComparisonType comparisonType = ComparisonType.matchType(args[2]);
								if(comparisonType != null) return new EntityAltitudeComparison(inverted, false, Integer.parseInt(args[3]), comparisonType, nestedCalculations);
							}
							else if(args[1].equalsIgnoreCase("entityLight"))
							{
								ComparisonType comparisonType = ComparisonType.matchType(args[2]);
								if(comparisonType != null) return new EntityLightComparison(inverted, false, Byte.parseByte(args[3]), comparisonType, nestedCalculations);
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
								if(material != null) return new PlayerSetItem(forAttacker, material, nestedCalculations);
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
								ComparisonType comparisonType = ComparisonType.matchType(args[2]);
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
									if(!armorSet.isEmpty()) return new PlayerWearing(inverted, forAttacker, armorSet.toString(), nestedCalculations);
								}
								else if(args[2].equalsIgnoreCase("wearingOnly"))
								{
									ArmorSet armorSet = new ArmorSet(args[3]);
									if(!armorSet.isEmpty()) return new PlayerWearingOnly(inverted, forAttacker, armorSet.toString(), nestedCalculations);
								}
								else if(args[2].equalsIgnoreCase("wielding"))
								{
									Material material = Material.matchMaterial(args[3]);
									if(material != null) return new PlayerWielding(inverted, forAttacker, material, nestedCalculations);
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
								else if(args[2].equalsIgnoreCase("fallen")) return new EntityFallComparison(inverted, forAttacker, Integer.parseInt(args[3]), ComparisonType.GREATER_THAN_EQUALS, nestedCalculations);
								else if(args[2].equalsIgnoreCase("falling")) return new EntityFallComparison(inverted, forAttacker, 3, ComparisonType.GREATER_THAN_EQUALS, nestedCalculations);
								
							}
							if(args[1].equalsIgnoreCase("attackerAltitude") || args[1].equalsIgnoreCase("targetAltitude"))
							{
								boolean forAttacker = args[0].equalsIgnoreCase("attackerAltitude");
								ComparisonType comparisonType = ComparisonType.matchType(args[2]);
								if(comparisonType != null) return new EntityAltitudeComparison(inverted, forAttacker, Integer.parseInt(args[3]), comparisonType, nestedCalculations);
							}
							if(args[1].equalsIgnoreCase("attackerHealth") || args[1].equalsIgnoreCase("targetHealth"))
							{
								boolean forAttacker = args[0].equalsIgnoreCase("attackerHealth");
								ComparisonType comparisonType = ComparisonType.matchType(args[2]);
								if(comparisonType != null) return new EntityHealthComparison(inverted, forAttacker, Integer.parseInt(args[3]), comparisonType, nestedCalculations);
							}
							if(args[1].equalsIgnoreCase("attackerLight") || args[1].equalsIgnoreCase("targetLight"))
							{
								boolean forAttacker = args[0].equalsIgnoreCase("attackerLight");
								ComparisonType comparisonType = ComparisonType.matchType(args[2]);
								if(comparisonType != null) return new EntityLightComparison(inverted, forAttacker, Byte.parseByte(args[3]), comparisonType, nestedCalculations);
							}
							else if(args[1].equalsIgnoreCase("worldTime")) return new WorldTime(inverted, null, Integer.parseInt(args[2]), Integer.parseInt(args[3]), nestedCalculations);
							else if(args[1].equalsIgnoreCase("worldEnvironment"))
							{
								Environment environment = CalculationUtility.matchEnvironment(args[2]);
								if(environment != null) return new WorldEnvironment(inverted, null, environment, nestedCalculations);
							}
						}
					}
				}
			}
			catch(Exception e){}
		}
		return null;
	}

	private ModDamageCalculation parseSwitchCalculation(LinkedHashMap<String, List<Object>> switchStatement, boolean forSpawn)
	{
		for(String key : switchStatement.keySet())//should only be one. :<
		{
		
		if(!forSpawn)
		{
			try
			{
			}
			catch(Exception e){}
		}
		else
		{
			try
			{
			}
			catch(Exception e){}
		}
		}
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