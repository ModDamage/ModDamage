package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

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
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityAirTicksComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityCoordinateComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityDrowning;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityExposedToSky;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityFallComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityFalling;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityFireTicksComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityHealthComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityLightComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityOnBlock;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityOnFire;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityTargetedByOther;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityUnderwater;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EventValueComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.PlayerWearing;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.PlayerWearingOnly;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.PlayerWielding;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.ServerOnlineMode;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.ServerPlayerCount;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.WorldEnvironment;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.WorldTime;

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
	private static HashMap<Class<? extends ModDamageCalculation>, Pattern> registeredCalculations = new HashMap<Class<? extends ModDamageCalculation>, Pattern>();
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
		
//Base Calculations
		Addition.register();
		DiceRoll.register();
		DiceRollAddition.register();
		Division.register();
		DivisionAddition.register();
		IntervalRange.register();
		LiteralRange.register();
		Multiplication.register();
		Set.register();		
//Nestable Calculations
	//Conditionals
		Binomial.register();
		//Entity
		EntityAirTicksComparison.register();
		EntityBiome.register();
		EntityCoordinateComparison.register();
		EntityDrowning.register();
		EntityExposedToSky.register();
		EntityFallComparison.register();
		EntityFalling.register();
		EntityFireTicksComparison.register();
		EntityHealthComparison.register();
		EntityLightComparison.register();
		EntityOnBlock.register();
		EntityOnFire.register();
		EntityTargetedByOther.register();
		EntityUnderwater.register();
		EventValueComparison.register();
		PlayerWearing.register();
		PlayerWearingOnly.register();
		PlayerWielding.register();
		//World
		WorldTime.register();
		WorldEnvironment.register();
		//Server
		ServerOnlineMode.register();
		ServerPlayerCount.register();
		//Event
		EventValueComparison.register();
	//Effects
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
				for(String key : ((LinkedHashMap<String, List<Object>>)calculationString).keySet())
				{
					for(Class<? extends ModDamageCalculation> calculationClass : registeredCalculations.keySet())
					{
						Matcher matcher = registeredCalculations.get(calculationClass).matcher((String)calculationString);
						if(matcher.matches())
						{
							List<ModDamageCalculation> nestedCalculations = parseStrings(((LinkedHashMap<String, List<Object>>)calculationString).get(key), forSpawn);
							if(nestedCalculations.isEmpty()) return null;
							{
								try 
								{
									calculation = calculationClass.getConstructor(Matcher.class, List.class).newInstance(matcher, nestedCalculations);
								}
								catch (Exception e){ e.printStackTrace();}
								if(!calculation.loaded()) calculation = null;
							}
						}
					}
				}
			}
			else if(calculationString instanceof String)
			{
				for(Class<? extends ModDamageCalculation> calculationClass : registeredCalculations.keySet())
				{
					Matcher matcher = registeredCalculations.get(calculationClass).matcher((String)calculationString);
					if(matcher.matches())
					{
						try
						{
							calculation = calculationClass.getConstructor(Matcher.class).newInstance(matcher);
						}
						catch (Exception e){ e.printStackTrace();}
						if(!calculation.loaded()) calculation = null;
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