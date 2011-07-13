package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.mysql.jdbc.AssertionFailedException;

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
// -tag.$aliasName
// -ability to clear non-static tags

//--Refactor:
// -registration of MDCalcs with regexs
//--FIXME:
// -fix iswearing comparison (out of order results in no good unless exact match)
// -regex parts for comparison (add ==, !=, etc.)


public class CalculationUtility
{
	static Logger log = Logger.getLogger("Minecraft");
	private static HashMap<Pattern, Method> registeredCalculations = new HashMap<Pattern, Method>();
	public static final String ifPart = "(if|if_not)\\.";
	public static final String entityPart = "(entity|attacker|target)\\.";
	public static final String comparisonPart = "(equals|notequals|lessthan|lessthanequals|greaterthan|greaterthanequals)\\.";
	public static final String aliasPart = "_[a-z]+";
	
	public static String biomeRegex;
	public static String environmentRegex;
	public static String materialRegex;
	public static String armorRegex;
	
	public CalculationUtility()
	{
		biomeRegex = "(";
		for(Biome biome : Biome.values())
			biomeRegex += biome.name() + "|";
		biomeRegex = aliasPart + ")";
		
		environmentRegex = "(";
		for(Environment environment : Environment.values())
			environmentRegex += environment.name() + "|";
		environmentRegex = aliasPart + ")";
		
		materialRegex = "(";
		for(Material material : Material.values())
			materialRegex += material.name() + "|";
		materialRegex = aliasPart + ")";
		
		String[] materialParts = { "LEATHER", "GOLD", "IRON", "DIAMOND" };
		String[] armorParts = {"_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS" };
		armorRegex = "(";
		for(String material : materialParts)
			for(String equipType : armorParts)
				armorRegex += material + equipType + "|";
		armorRegex += aliasPart + "){1-4}";
	}
	
	//Parse commands for different command strings the handlers pass
	//parseStrings is used to determine the next calculation object's type, and pass if off accordingly.
	
	public static List<ModDamageCalculation> parseStrings(List<Object> commandStrings, boolean forSpawn)
	{
		List<ModDamageCalculation> calculations = new ArrayList<ModDamageCalculation>();
		for(Object calculationString : commandStrings)	
		{
			ModDamageCalculation calculation = null;
			if(calculationString instanceof LinkedHashMap)
			{
				for(String key : ((LinkedHashMap<String, List<Object>>)calculationString).keySet())//should only be one, supposedly
				{
					for(Pattern pattern : registeredCalculations.keySet())
					{
						Matcher matcher = pattern.matcher((String)calculationString);
						if(matcher.matches())
						{
							Method method = registeredCalculations.get(pattern);
							List<ModDamageCalculation> nestedCalculations = parseStrings(((LinkedHashMap<String, List<Object>>)calculationString).get(key), forSpawn);
							if(nestedCalculations.isEmpty()) return null;
							{
								try 
								{
									calculation = (ModDamageCalculation) method.getDeclaringClass().cast(method.invoke(null, matcher, nestedCalculations));
								}
								catch (Exception e){ e.printStackTrace();}
							}
						}
					}
				}
			}
			else if(calculationString instanceof String)
			{
				for(Pattern pattern : registeredCalculations.keySet())
				{
					Matcher matcher = pattern.matcher((String)calculationString);
					if(matcher.matches())
					{
						Method method = registeredCalculations.get(pattern);
						try 
						{
							calculation = (ModDamageCalculation) method.getDeclaringClass().cast(method.invoke(null, matcher));
						}
						catch (Exception e){ e.printStackTrace();}
					}
				}
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
			try
			{
				Method method = calculationClass.getMethod("getNew", Matcher.class);
				if(method != null)
				{
					assert(method.getReturnType().equals(calculationClass));
					method.invoke(null, (Matcher)null);
					registeredCalculations.put(syntax, method);
					successfullyRegistered = true;
				}
				else log.severe("Method getNew not found for class ");
			}
			catch(AssertionFailedException e){ log.severe("[ModDamage] Error: getNew doesn't return registered class " + calculationClass.getName() + "!");}
			catch(NullPointerException e){ log.severe("[ModDamage] Error: getNew for class " + calculationClass.getName() + " is not static!");}
			catch(NoSuchMethodException e){ log.severe("[ModDamage] Error: Calculation class \"" + calculationClass.toString() + "\" does not have a getNew() method!");} 
			catch (IllegalArgumentException e){ log.severe("[ModDamage] Error: Calculation class \"" + calculationClass.toString() + "\" does not have matching method getNew(Matcher)!");} 
			catch (IllegalAccessException e){ log.severe("[ModDamage] Error: Calculation class \"" + calculationClass.toString() + "\" does not have valid getNew() method!");} 
			catch (InvocationTargetException e){ log.severe("[ModDamage] Error: Calculation class \"" + calculationClass.toString() + "\" does not have valid getNew() method!");} 
			
		}
		else log.severe("[ModDamage] Error: Bad regex in calculation class \"" + calculationClass.toString() + "\"!");
		if(successfullyRegistered)
		{
			if(ModDamage.consoleDebugging_verbose) log.info("[ModDamage] Registering calculation " + calculationClass.toString() + " with pattern " + syntax.pattern());
		}
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
	
//An enum would be nice, but I want this integrated into the CalculationUtility at the moment.
	public static final int CONTINUE = 0x00;
	public static final int SKIP_ELSE = 0x01;
	public static final int STOP = 0x02;
	
	public static int matchState(String key)
	{
		if(key.equalsIgnoreCase("CONTINUE")) return CONTINUE;
		if(key.equalsIgnoreCase("SKIP_ELSE")) return SKIP_ELSE;
		if(key.equalsIgnoreCase("STOP")) return STOP;
		return 0;
	}
}