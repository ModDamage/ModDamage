package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import java.util.Arrays;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.IntervalRange;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.LiteralRange;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Set;

public class SpawnCalculationAllocator
{
	//Parse commands for different command strings the handlers pass
	public SpawnCalculation parseDefault(String commandString)
	{
		try
		{
			try{ return new Set(Integer.parseInt(commandString));}
			catch(NumberFormatException e){}

			//String[] commandSplit = commandString.split("\\*");
			String[] args = commandString.split("\\.");
			//if(commandSplit.length > 1)
			//else
				/*if(args.length == 2)
				{
					if(args[0].equals("binom")) return new LiteralRange(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				}
				else */if(args.length == 3)
				{
					if(args[0].equals("range")) return new LiteralRange(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				}
				else if(args.length == 4)
				{
					if(args[0].equals("range_int")) return new IntervalRange(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
				}
				throw new Exception();
		}
		catch(Exception e){ return null;}
	}
/*
	public SpawnCalculation parseConditionals(List<String> commandStrings)
	{
		try
		{
			String[] args = commandString.split("\\.");
			if(args.length == 3)
			{
				if(args[0].equals("range")) return new LiteralRange(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			}
			else if(args.length == 4)
			{
				if(args[0].equals("range_int")) return new IntervalRange(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			}
			throw new Exception();
		}
		catch(Exception e){ return null;}
	}
*/

	//TODO REGEN?! :D Requires something else, though...
	//IFs: TODO
	// entityis.onblock.MATERIAL
	// entityis.inlightlevel.#value
	// entityis.exposedtoSky
	// entityis.inBiome.$biome
	// entityis.inEnvironment.$environment
	// entityis.atAltitude.#value
	// playeris.locatedIRL.$area
	// serveris.onlinemode
	// serveris.portedAt.#port
}