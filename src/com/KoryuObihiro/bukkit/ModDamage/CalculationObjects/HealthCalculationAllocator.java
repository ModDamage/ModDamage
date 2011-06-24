package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Health.HealthCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Health.IntervalRangeCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Health.LiteralRangeCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Health.SetCalculation;

public class HealthCalculationAllocator
{
	//Parse commands for different command strings the handlers pass
	public HealthCalculation parseString(String commandString)
	{
		try
		{
			try{ return new SetCalculation(Integer.parseInt(commandString));}
			catch(NumberFormatException e){}
			
			String[] args = commandString.split("\\.");
			if(args.length == 3)
			{
				if(args[0].equals("range")) return new LiteralRangeCalculation(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			}
			else if(args.length == 4)
			{
				if(args[0].equals("range_int")) return new IntervalRangeCalculation(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			}
			throw new Exception();
		}
		catch(Exception e){ return null;}
	//TODO IDEA: health spawn based on entity resting on block of type BLAH?
	//TODO Add binomial health settings
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
}