package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Addition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DiceRoll;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DiceRollAddition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Division;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DivisionAddition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Multiplication;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Set;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.DamageEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.DamageGreaterThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.DamageGreaterThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.DamageLessThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.DamageLessThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityAltitudeEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityAltitudeGreaterThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityAltitudeGreaterThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityAltitudeLessThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityAltitudeLessThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityDrowning;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityFallen;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityFalling;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityHealthEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityHealthGreaterThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityHealthGreaterThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityHealthLessThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityHealthLessThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityLightLevelEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityLightLevelGreaterThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityLightLevelGreaterThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityLightLevelLessThan;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityLightLevelLessThanEquals;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityOnBlock;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityOnFire;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityUnderwater;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityWearing;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityWearingOnly;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity.EntityWielding;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.World.WorldEnvironment;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.World.WorldTime;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity.EntityExplode;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity.EntityHeal;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity.EntityReflect;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity.EntitySetAirTicks;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity.EntitySetFireTicks;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity.EntitySetHealth;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity.EntitySetItem;

//IFs(?): TODO mebbe
// playeris.locatedIRL.$area
// serveris.onlinemode
// serveris.portedAt.#port
// relative health/altitude/light
// implement some syntax help
// implement and/or/else?
// entityEffect.increaseItem.amount
// entityEffect.decreaseItem.amount
// send player message
// entityis.inRegion //XXX Might require something like Regios
// entityis.ofgroup
// fix armorset comparison

public class DamageCalculationAllocator
{	
	public List<DamageCalculation> parseStrings(List<Object> calcStrings) 
	{
		List<DamageCalculation> calculations = new ArrayList<DamageCalculation>();
		for(Object calculationString : calcStrings)
		{
			DamageCalculation calculation = null;
			
			if(calculationString instanceof LinkedHashMap)
				calculation = parseConditional((LinkedHashMap<String, List<Object>>)calculationString);
			else if(calculationString instanceof String)
				calculation = parseNormal((String)calculationString);
			
			if(calculation != null) calculations.add(calculation);
			else return new ArrayList<DamageCalculation>();
		}
		return calculations;
	}
	
	public DamageCalculation parseNormal(String argString) 
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
						if(args[0].equalsIgnoreCase("roll")) 		return new DiceRoll();
					}
					else if(args.length == 2)
					{
						if(args[0].equalsIgnoreCase("div"))			return new Division(Integer.parseInt(args[1]));
						else if(args[0].equalsIgnoreCase("div_add"))return new DivisionAddition(Integer.parseInt(args[1]));
						else if(args[0].equalsIgnoreCase("mult")) 	return new Multiplication(Integer.parseInt(args[1]));
						else if(args[0].equalsIgnoreCase("roll")) 	return new DiceRollAddition(Integer.parseInt(args[1]));
						else if(args[0].equalsIgnoreCase("set"))	return new Set(Integer.parseInt(args[1]));
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
		return null;
	}
	
	private DamageCalculation parseConditional(LinkedHashMap<String, List<Object>> conditionalStatement)
	{
		for(Object key : conditionalStatement.keySet())//should only be one. :<
		{
			String[] args = ((String)key).split("\\.");
			//TODO Refactor this so it only runs as many times as necessary.
			List<DamageCalculation> nestedCalculations = parseStrings(conditionalStatement.get(key));
			if(nestedCalculations.isEmpty()) return null;
			
			if(args[0].equalsIgnoreCase("if") || args[0].equalsIgnoreCase("if_not"))
			{
				boolean inverted = args[0].equalsIgnoreCase("if_not");
				if(args.length == 2)
				{
					if(args[0].equalsIgnoreCase("attackerEffect") || args[0].equalsIgnoreCase("targetEffect"))
					{
						boolean forAttacker = args[1].equalsIgnoreCase("attackerEffect");
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
						if(args[1].equalsIgnoreCase("reflect")) return new EntityReflect(nestedCalculations);
				}
				else if(args.length == 3)
				{
					if(args[1].equalsIgnoreCase("damageIs"))
					{
						if(args[2].equalsIgnoreCase("lessThan") || args[2].equalsIgnoreCase("<")) 				return new DamageLessThan(inverted, Integer.parseInt(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("lessThanEquals") || args[2].equalsIgnoreCase("<="))	return new DamageLessThanEquals(inverted, Integer.parseInt(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("greaterThan") || args[2].equalsIgnoreCase(">")) 		return new DamageGreaterThan(inverted, Integer.parseInt(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("greaterThanEquals") || args[2].equalsIgnoreCase(">="))return new DamageGreaterThanEquals(inverted, Integer.parseInt(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("equals") || args[2].equalsIgnoreCase("="))			return new DamageEquals(inverted, Integer.parseInt(args[3]), nestedCalculations);
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
						if(args[2].equalsIgnoreCase("lessThan") || args[2].equalsIgnoreCase("<")) 				return new EntityAltitudeLessThan(inverted, forAttacker, Integer.parseInt(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("lessThanEquals") || args[2].equalsIgnoreCase("<="))	return new EntityAltitudeLessThanEquals(inverted, forAttacker, Integer.parseInt(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("greaterThan") || args[2].equalsIgnoreCase(">")) 		return new EntityAltitudeGreaterThan(inverted, forAttacker, Integer.parseInt(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("greaterThanEquals") || args[2].equalsIgnoreCase(">="))return new EntityAltitudeGreaterThanEquals(inverted, forAttacker, Integer.parseInt(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("equals") || args[2].equalsIgnoreCase("=")) 			return new EntityAltitudeEquals(inverted, forAttacker, Integer.parseInt(args[3]), nestedCalculations);
					}
					if(args[1].equalsIgnoreCase("attackerHealth") || args[1].equalsIgnoreCase("targetHealth"))
					{
						boolean forAttacker = args[0].equalsIgnoreCase("attackerHealth");
						if(args[2].equalsIgnoreCase("lessThan") || args[2].equalsIgnoreCase("<")) 				return new EntityHealthLessThan(inverted, forAttacker, Integer.parseInt(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("lessThanEquals") || args[2].equalsIgnoreCase("<="))		return new EntityHealthLessThanEquals(inverted, forAttacker, Integer.parseInt(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("greaterThan") || args[2].equalsIgnoreCase(">")) 		return new EntityHealthGreaterThan(inverted, forAttacker, Integer.parseInt(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("greaterThanEquals")) 	return new EntityHealthGreaterThanEquals(inverted, forAttacker, Integer.parseInt(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("equals")) 			return new EntityHealthEquals(inverted, forAttacker, Integer.parseInt(args[3]), nestedCalculations);
					}
					if(args[1].equalsIgnoreCase("attackerLight") || args[1].equalsIgnoreCase("targetLight"))
					{
						boolean forAttacker = args[0].equalsIgnoreCase("attackerLightLevel");
						if(args[2].equalsIgnoreCase("lessThan") || args[2].equalsIgnoreCase("<")) 				return new EntityLightLevelLessThan(inverted, forAttacker, Byte.parseByte(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("lessThanEquals") || args[2].equalsIgnoreCase("<="))	return new EntityLightLevelLessThanEquals(inverted, forAttacker, Byte.parseByte(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("greaterThan") || args[2].equalsIgnoreCase(">")) 		return new EntityLightLevelGreaterThan(inverted, forAttacker, Byte.parseByte(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("greaterThanEquals") || args[2].equalsIgnoreCase(">="))return new EntityLightLevelGreaterThanEquals(inverted, forAttacker, Byte.parseByte(args[3]), nestedCalculations);
						else if(args[2].equalsIgnoreCase("equals") || args[2].equalsIgnoreCase("=")) 			return new EntityLightLevelEquals(inverted, forAttacker, Byte.parseByte(args[3]), nestedCalculations);
					}
					else if(args[1].equalsIgnoreCase("worldTime")) 				return new WorldTime(inverted, Integer.parseInt(args[2]), Integer.parseInt(args[3]), nestedCalculations);
					else if(args[1].equalsIgnoreCase("worldEnvironment"))
					{
						Environment environment = CalculationUtility.matchEnvironment(args[2]);
						if(environment != null) return new WorldEnvironment(inverted, environment, nestedCalculations);
					}
				}
			}
		}
		return null;
	}
}