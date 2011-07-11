package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

//TODO	
// COUNTER FOR NEST DEPTH

//--Calculation Ideas:
// -implement some syntax help
// -implement and/or/else?
// -send player message
// -relative health/altitude/light
// -AoE clearance, block search nearby for Material?
// -check against an itemstack in the player's inventory

// -if.entityis.inRegion
// -if.playeris.locatedIRL.$area
// -if.serveris.onlinemode
// -if.serveris.portedAt.#port
// -switch.region
// -switch.entitygroup
// -switch.environment
// -switch.spawnreason
// -switch.type //Accepts any Damage Element.
// -switch.type.$DamageElement //Accepts only child Damage Elements of named element class.
// -entitytag.$aliasName

//--Refactor:
// -registry of MDCalcs with regexs
//--FIXME:
// -fix iswearing comparison (out of order results in no good unless exact match)


public class CalculationUtility
{
	static Logger log = Logger.getLogger("Minecraft");
	private static HashMap<Class<? extends ModDamageCalculation>, Pattern> registeredCalculations = new HashMap<Class<? extends ModDamageCalculation>, Pattern>();
	public static final String ifPart = "(if|if_not)";
	public static final String entityPart = "(entity|attacker|target))";
	public static final String comparisonPart = "(equals|notequals|lessthan|lessthanequals|greaterthan|greaterthanequals)";
	public static final String wordPart = "[a-z]+";
	public static final String aliasPart = "_" + wordPart;
	
	//Parse commands for different command strings the handlers pass
	//parseStrings is used to determine the next calculation object's type, and pass if off accordingly.
	
	public static List<ModDamageCalculation> parseStrings(List<Object> commandStrings, boolean forSpawn)
	{
		List<ModDamageCalculation> calculations = new ArrayList<ModDamageCalculation>();
		Logger.getLogger("Minecraft").info("Class of found configuration is " + commandStrings.getClass().getName());//TODO REMOVE ME?
		if(commandStrings instanceof ArrayList)
			Logger.getLogger("Minecraft").info(commandStrings.toString());
		
		for(Object calculationString : commandStrings)	
		{
			ModDamageCalculation calculation = null;
			
			if(calculationString instanceof LinkedHashMap)
			{
				for(String key : ((LinkedHashMap<String, List<Object>>)calculationString).keySet())
				{
					for(Class<? extends ModDamageCalculation> calculationClass : registeredCalculations.keySet())
					{
						Matcher matcher = registeredCalculations.get(calculationClass).matcher((String)calculationString);
						if(matcher.matches())
						{
							List<ModDamageCalculation> nestedCalculations = parseStrings(((LinkedHashMap<String, List<Object>>)calculationString).get(key), forSpawn);
							if(nestedCalculations.isEmpty()) return null;
							calculation = calculationClass.getConstructor(Matcher.class, List.class).newInstance(matcher, nestedCalculations);
						}
					}
				}
				return null;
			}
			else if(calculationString instanceof String)
			{
				for(Class<? extends ModDamageCalculation> calculationClass : registeredCalculations.keySet())
				{
					Matcher matcher = registeredCalculations.get(calculationClass).matcher((String)calculationString);
					if(matcher.matches())
						calculation = calculationClass.getConstructor(Matcher.class).newInstance(matcher);
				}
				return null;
			}
			
			if(calculation != null) calculations.add(calculation);
			else return new ArrayList<ModDamageCalculation>();
		}
		return calculations;
	}
	
	public static void register(Class<? extends ModDamageCalculation> calculationClass, Pattern syntax)
	{
		//TODO Code an info class for registered calculations? Not using "description" right now.
		boolean successfullyRegistered = false;
		if(syntax != null)
		{
			registeredCalculations.put(calculationClass, syntax);
			successfullyRegistered = true;
		}
		if(successfullyRegistered)
		{
			if(ModDamage.consoleDebugging_verbose) log.info("[ModDamage] Registering calculation " + calculationClass.toString() + " with pattern " + syntax.pattern());
		}
		else log.severe("[ModDamage] Error! Couldn't register calculation " + calculationClass.toString());
	}

	
//// INGAME MATCHING ////	
	//Frankly, most of the stuff below should be considered for implementation into Bukkit. :<
	public static Biome matchBiome(String biomeName)
	{
		for(Biome biome : Biome.values())
			if(biomeName.equalsIgnoreCase(biome.name()))
				return biome;
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