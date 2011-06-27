package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
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
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityAltitudeNotEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityExposedToSky;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityLightLevelEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityLightLevelGreaterThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityLightLevelGreaterThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityLightLevelLessThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityLightLevelLessThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityLightLevelNotEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityOnBlock;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity.EntityUnderwater;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.World.WorldTime;

public class SpawnCalculationAllocator
{
	//Parse commands for different command strings the handlers pass
	//TODO Do error output here. :<
	
	//parseList is used to determine the next calculation object's type, and pass if off accordingly.
	public List<SpawnCalculation> parseList(List<Object> commandStrings)
	{
		List<SpawnCalculation> calculations = new ArrayList<SpawnCalculation>();
		for(Object calculationString : commandStrings)	
		{
			SpawnCalculation calculation = null;
			
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
	
	//parseNormal seeks for a base calculation (i.e., not a conditional) and returns it (if found) for appending to the calling list.
	private SpawnCalculation parseNormal(String commandString)
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
	//TODO: Get rid of these warnings. I think the generics can be safely cast to <String, List<String>>, but I'm not sure.
	private SpawnCalculation parseConditional(LinkedHashMap conditionalStatement)
	{
		Logger.getLogger("Minecraft").info("Found conditional, keys: " + conditionalStatement.keySet().toString());
		try
		{
			for(Object key : conditionalStatement.keySet())//should only be one. :<
			{
				String[] args = ((String)key).split("\\.");
				if(args[0].equalsIgnoreCase("if") || args[0].equalsIgnoreCase("if_not"))
				{
					Logger.getLogger("FOUND A CONDITIONAL. :D");//TODO REMOVE ME
					boolean inverted = args[0].equalsIgnoreCase("if_not");
					if(args.length == 3)
					{
						if(args[1].equalsIgnoreCase("entityis"))
						{
							if(args[2].equalsIgnoreCase("exposedToSky")) return new EntityExposedToSky(inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							else if(args[2].equalsIgnoreCase("underwater")) return new EntityUnderwater(inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
						}
						else if(args[1].equalsIgnoreCase("entityAltitude"))
						{
							if(args[2].equalsIgnoreCase("equals")) return new EntityAltitudeEquals(Byte.parseByte(args[3]), inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							else if(args[2].equalsIgnoreCase("notEquals")) return new EntityAltitudeNotEquals(Byte.parseByte(args[3]), inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							else if(args[2].equalsIgnoreCase("lessThan")) return new EntityAltitudeLessThan(Byte.parseByte(args[3]), inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							else if(args[2].equalsIgnoreCase("lessThanEquals")) return new EntityAltitudeLessThanEquals(Byte.parseByte(args[3]), inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							else if(args[2].equalsIgnoreCase("greaterThan")) return new EntityAltitudeGreaterThan(Byte.parseByte(args[3]), inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							else if(args[2].equalsIgnoreCase("greaterThanEquals")) return new EntityAltitudeGreaterThanEquals(Byte.parseByte(args[3]), inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
						}
						else if(args[1].equalsIgnoreCase("entityLight"))
						{
							if(args[2].equalsIgnoreCase("equals")) return new EntityLightLevelEquals(Byte.parseByte(args[3]), inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							else if(args[2].equalsIgnoreCase("notEquals")) return new EntityLightLevelNotEquals(Byte.parseByte(args[3]), inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							else if(args[2].equalsIgnoreCase("lessThan")) return new EntityLightLevelLessThan(Byte.parseByte(args[3]), inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							else if(args[2].equalsIgnoreCase("lessThanEquals")) return new EntityLightLevelLessThanEquals(Byte.parseByte(args[3]), inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							else if(args[2].equalsIgnoreCase("greaterThan")) return new EntityLightLevelGreaterThan(Byte.parseByte(args[3]), inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							else if(args[2].equalsIgnoreCase("greaterThanEquals")) return new EntityLightLevelGreaterThanEquals(Byte.parseByte(args[3]), inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
						}
						else if(args[1].equalsIgnoreCase("binom")) return new Binomial(Integer.parseInt(args[1]), parseList((ArrayList<Object>)conditionalStatement.get(key)));
					}
					else if(args.length == 4)
					{
						if(args[1].equalsIgnoreCase("entityis"))
						{
							if(args[2].equalsIgnoreCase("inBiome"))
							{
								Biome biome = CalculationUtility.matchBiome(args[3].toLowerCase());
								if(biome != null) return new EntityBiome(biome, inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							}
							else if(args[2].equalsIgnoreCase("onBlock"))
							{
								Material material = Material.matchMaterial(args[3]);
								if(material != null) return new EntityOnBlock(material, inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							}
							else if(args[2].equalsIgnoreCase("inEnvironment"))
							{
								Biome biome = CalculationUtility.matchBiome(args[3].toLowerCase());
								if(biome != null) return new EntityBiome(biome, inverted, parseList((ArrayList<Object>)conditionalStatement.get(key)));
							}
						}
						else if(args[1].equalsIgnoreCase("worldTime")) return new WorldTime(inverted, Integer.parseInt(args[2]), Integer.parseInt(args[3]), parseList((ArrayList<Object>)conditionalStatement.get(key)));
					}
					else if(args.length == 5)
					{
						
					}
				}
				
				Logger.getLogger("Minecraft").info("Key found, is type " + key.getClass().getName());
				Logger.getLogger("Minecraft").info("Key: " + key.toString());
				Logger.getLogger("Minecraft").info("Value is type " + conditionalStatement.get(key).getClass().getName());
				Logger.getLogger("Minecraft").info("Value: " + conditionalStatement.get(key).toString());
			}
		}
		catch(Exception e){}//FIXME Print something here. :<
		return null;
	}
	//TODO REGEN?! :D Requires something else, though...
	//IFs: TODO
	// playeris.locatedIRL.$area
	// serveris.onlinemode
	// serveris.portedAt.#port
	// spawn reason
}