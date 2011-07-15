package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

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
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.NestedRoutine;
import com.mysql.jdbc.AssertionFailedException;

//TODO	
// COUNTER FOR NEST DEPTH

//--Calculation Ideas:
// -implement some syntax help
// -send player message
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
// -switch.conditional

//--Refactor:
// -registration of MDCalcs with regexs
//--FIXME:
// -fix iswearing comparison (out of order results in no good unless exact match)
// -regex parts for comparison (add ==, !=, etc.)


public class RoutineUtility
{
	static Logger log = Logger.getLogger("Minecraft");
	private static HashMap<Pattern, Method> registeredBaseRoutines = new HashMap<Pattern, Method>();
	private static HashMap<Pattern, Method> registeredNestedRoutines = new HashMap<Pattern, Method>();
	public static final String numberPart = "([0-9]+)";
	public static final String wordPart = "([a-z]+)";
	public static final String entityPart = "(entity|attacker|target)\\.";
	public static final String aliasPart = "_([a-z0-9]+)";

	public static String comparisonRegex;
	public static String biomeRegex;
	public static String environmentRegex;
	public static String materialRegex;
	public static String armorRegex;
	public static String logicalRegex;
	
	public RoutineUtility()
	{
		biomeRegex = "(";
		for(Biome biome : Biome.values())
			biomeRegex += biome.name() + "|";
		biomeRegex = aliasPart + ")";
		
		environmentRegex = "(";
		for(Environment environment : Environment.values())
			environmentRegex += environment.name() + "|";
		environmentRegex = aliasPart + ")";

		String[] armorParts = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS" };
		materialRegex = armorRegex = "(";
		for(Material material : Material.values())
		{
			materialRegex += material.name() + "|";
			for(String part : armorParts)
				if(material.name().endsWith(part))
					armorRegex += material.name() + "|";
					
		}
		materialRegex += aliasPart + ")";
		armorRegex += aliasPart + "){1-4}";
		
		logicalRegex = "(";
		for(LogicalOperation operation : LogicalOperation.values())
			logicalRegex += operation.name() + "|" + operation.getShortHand() + "|";
		logicalRegex = aliasPart + ")";		
		
		comparisonRegex = "(";
		for(ComparisonType type : ComparisonType.values())
			comparisonRegex += type.name() + "|" + type.getShortHand() + "|";
		comparisonRegex += "\\.";
	}
	
	//Parse commands for different command strings the handlers pass
	//parseStrings is used to determine the next calculation object's type, and pass if off accordingly.
	public static List<Routine> parseStrings(List<Object> commandStrings, boolean forSpawn)
	{
		for(Object something : commandStrings)
		{
			parseString(something, false);
		}
		return null;
		/*
		List<Routine> calculations = new ArrayList<Routine>();
		for(Object routineString : commandStrings)	
		{
			Routine routine = null;
			//Base routine is a string.
			if(routineString instanceof String)
				for(Pattern pattern : registeredBaseRoutines.keySet())
				{
					Matcher matcher = pattern.matcher((String)routineString);
					if(matcher.matches())
					{
						Method method = registeredBaseRoutines.get(pattern);
						try 
						{
							routine = (Routine) method.getDeclaringClass().cast(method.invoke(null, matcher));
						}
						catch (Exception e){ e.printStackTrace();}
					}
				}
			//Conditional routine is a LinkedHashMap with only one key.
			// Properly-formatted conditionals
			//Switch routine is a LinkedHashMap with more than one key.
			// In order to have a LinkedHashMap with more than one key, in YAML one must do something like this:
			//
			// ```yaml
			// Damage:
			//      - 'switch.something':
			//          - firstcase:
			//            - 'base'
			//            - 'conditional':
			//                - 'base'
			//            lastcase:
			//              - 'switch.asdfasdfasdf':
			// ```
			else if(routineString instanceof LinkedHashMap)
				for(String key : ((LinkedHashMap<String, List<Object>>)routineString).keySet())//should only be one, supposedly
					for(Pattern pattern : registeredNestedRoutines.keySet())
					{
						Matcher matcher = pattern.matcher((String)routineString);
						if(matcher.matches())
						{
							Method method = registeredNestedRoutines.get(pattern);
							List<Routine> nestedCalculations = parseStrings(((LinkedHashMap<String, List<Object>>)routineString).get(key), forSpawn);
							if(nestedCalculations.isEmpty()) return null;
							{
								try 
								{
									routine = (Routine) method.getDeclaringClass().cast(method.invoke(null, matcher, nestedCalculations));
								}
								catch (Exception e){ e.printStackTrace();}
							}
						}
					}
			if(routine != null) calculations.add(routine);
			else return new ArrayList<Routine>();
		}
		return calculations;
		*/
	}
	
	private static void parseString(Object object, boolean inSwitch)
	{
		if(object instanceof String) log.info("Found base routine \"" + (String)object + "\"");
		else if(object instanceof LinkedHashMap)
		{
			HashMap<String, Object> lhm = (HashMap<String, Object>)object;
			if(lhm.keySet().size() == 1 || inSwitch)
			{
				for(String key : lhm.keySet())
				{
					log.info("Found nestable routine \"" + (String)object + "\"...getting values");
					parseString(lhm.get(key), false);
					log.info("End nestable routine \"" + (String)object + "\"");
				}
			}
			else log.info("Found bad nestable. D:");
		}
		else if(object instanceof HashMap)
		{
			HashMap<String, Object> hm = (HashMap<String, Object>)object;
			if(hm.keySet().size() == 1)
				for(String routine : hm.keySet())
				{
					log.info("Found switch routine \"" + routine + "\"");
					Object value = hm.get(routine);
					if(value instanceof LinkedHashMap)
					{
						log.info("...and found its cases!");
						parseString(value, true);
					}
					log.info("End switch routine \"" + (String)object + "\"");
				}
			else log.info("Found switch routine, but it's got too many names.");
		}
		else log.info("Found something...but I have no idea how the hell to parse it.");
	}
	
	//TODO Make a checking function, instead of repeating code
	public static void register(Class<? extends Routine> calculationClass, Pattern syntax)
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
					registeredBaseRoutines.put(syntax, method);
					successfullyRegistered = true;
				}
				else log.severe("Method getNew not found for class ");
			}
			catch(AssertionFailedException e){ log.severe("[ModDamage] Error: getNew doesn't return registered class " + calculationClass.getName() + "!");}
			catch(SecurityException e){ log.severe("[ModDamage] Error: getNew isn't public for registered class " + calculationClass.getName() + "!");}
			catch(NullPointerException e){ log.severe("[ModDamage] Error: getNew for class " + calculationClass.getName() + " is not static!");}
			catch(NoSuchMethodException e){ log.severe("[ModDamage] Error: Class \"" + calculationClass.toString() + "\" does not have a getNew() method!");} 
			catch (IllegalArgumentException e){ log.severe("[ModDamage] Error: Class \"" + calculationClass.toString() + "\" does not have matching method getNew(Matcher)!");} 
			catch (IllegalAccessException e){ log.severe("[ModDamage] Error: Class \"" + calculationClass.toString() + "\" does not have public getNew(Matcher) method!");} 
			catch (InvocationTargetException e){ log.severe("[ModDamage] Error: Class \"" + calculationClass.toString() + "\" does not have valid getNew(Matcher) method!");} 	
		}
		else log.severe("[ModDamage] Error: Bad regex in routine class \"" + calculationClass.toString() + "\"!");
		if(successfullyRegistered)
		{
			if(ModDamage.consoleDebugging_verbose) log.info("[ModDamage] Registering routine class " + calculationClass.toString() + " with pattern " + syntax.pattern());
		}
	}
	
	public static void registerNestable(Class<? extends NestedRoutine> calculationClass, Pattern syntax)
	{
		boolean successfullyRegistered = false;
		if(syntax != null)
		{
			try
			{
				Method method = calculationClass.getMethod("getNew", Matcher.class, List.class);
				if(method != null)
				{
					assert(method.getReturnType().equals(calculationClass));
					method.invoke(null, (Matcher)null, (List<Routine>)null);
					registeredBaseRoutines.put(syntax, method);
					successfullyRegistered = true;
				}
				else log.severe("Method getNew not found for class ");
			}
			catch(AssertionFailedException e){ log.severe("[ModDamage] Error: getNew doesn't return registered class " + calculationClass.getName() + "!");}
			catch(SecurityException e){ log.severe("[ModDamage] Error: getNew isn't public for registered class " + calculationClass.getName() + "!");}
			catch(NullPointerException e){ log.severe("[ModDamage] Error: getNew for class " + calculationClass.getName() + " is not static!");}
			catch(NoSuchMethodException e){ log.severe("[ModDamage] Error: Routine class \"" + calculationClass.toString() + "\" does not have a getNew() method!");} 
			catch (IllegalArgumentException e){ log.severe("[ModDamage] Error: Routine class \"" + calculationClass.toString() + "\" does not have matching method getNew(Matcher)!");} 
			catch (IllegalAccessException e){ log.severe("[ModDamage] Error: Routine class \"" + calculationClass.toString() + "\" does not have valid getNew() method!");} 
			catch (InvocationTargetException e){ log.severe("[ModDamage] Error: Routine class \"" + calculationClass.toString() + "\" does not have valid getNew() method!");} 	
		}
		else log.severe("[ModDamage] Error: Bad regex in routine class \"" + calculationClass.toString() + "\"!");
		if(successfullyRegistered)
		{
			if(ModDamage.consoleDebugging_verbose) log.info("[ModDamage] Registering routine class " + calculationClass.toString() + " with pattern " + syntax.pattern());
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
}