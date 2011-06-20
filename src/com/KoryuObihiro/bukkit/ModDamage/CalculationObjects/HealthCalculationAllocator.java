package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import java.util.Random;
import java.util.logging.Logger;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Health.IntervalRangeCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Health.LiteralRangeCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Health.SetCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Health.HealthCalculation;

public class HealthCalculationAllocator
{
	//TODO use event damage for some types of formulae to be added later
	//TODO Add binomial health settings
	Logger log;
	final Random random = new Random();
	
	public HealthCalculationAllocator(Logger log){ this.log = log;}
	
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
				if(args[0].equals("range"))
					return new LiteralRangeCalculation(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			}
			else if(args.length == 4)
			{
				if(args[0].equals("range_int"))
					return new IntervalRangeCalculation(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			}
			throw new Exception();
		}
		catch(Exception e){ return null;}
	//TODO IDEA: health spawn based on entity resting on block of type BLAH?
	}
}