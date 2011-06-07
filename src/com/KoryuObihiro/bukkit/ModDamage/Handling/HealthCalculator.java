package com.KoryuObihiro.bukkit.ModDamage.Handling;
import java.util.Random;
import java.util.logging.Logger;



public class HealthCalculator
{
	//TODO use event damage for some types of formulae to be added later
	//TODO Add binomial health settings
	Logger log = Logger.getLogger("Minecraft");
	final Random random = new Random();
	
	//for file parsing
	public boolean checkCommandString(String commandString) 
	{
		if(commandString != null)
		{
			try
			{
				int test = Integer.parseInt(commandString);
				if(Math.abs(test) > 0)
					return true;
			}
			catch(NumberFormatException e){}
			
			String[] args = commandString.split("\\.");
		
			//TODO is this first one necessary?
			if(args.length == 1)
			{
				try
				{
					Integer.parseInt(args[0]);
					return true;
				}
				catch(Exception e)
				{
					return false;
				}
			}
			else if(args.length == 2)
			{
				return false;
			}
			else if(args.length == 3)
			{
				if(args[0].equals("range"))
				{
					try
					{
						return (Integer.parseInt(args[1]) < Integer.parseInt(args[2]));
					}
					catch(Exception e)
					{
						return false;
					}
				}
				else return false;
			}
			else if(args.length == 4)
			{
				if(args[0].equals("range"))
				{
					try
					{
						Integer.parseInt(args[1]);
						Integer.parseInt(args[2]);
						Integer.parseInt(args[3]);
						return true;
					}
					catch(Exception e)
					{
						return false;
					}
				}
				else return false;
			}
			return false;
		}
		return false;
	}
	
	//Parse commands for different command strings the handlers pass
	public int parseCommand(String commandString)
	{
		try
		{
			int tryThis = Integer.parseInt(commandString);
			return tryThis;
		}
		catch(Exception e){}
		
		String[] args = commandString.split("\\.");
		if(args.length == 2)
		{
			//if(false){}
			//else
			{
				log.severe("Unrecognized equation in configuration");
			}
		}
		else if(args.length == 3)
		{
			if(args[0].equals("range"))
			{
				try
				{
					return range_simple(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				}
				catch(Exception e)
				{
					log.severe("Improper equation in configuration (binom)");
					e.printStackTrace();
				}
			}
			else
			{
				log.severe("Unrecognized equation in configuration");
			}
		}
		else if(args.length == 4)
		{
			if(args[0].equals("range"))
			{
				try
				{
					return range_interval(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
				}
				catch(Exception e)
				{
					log.severe("Improper equation in configuration (binom)");
					e.printStackTrace();
				}
			}
			else
			{
				log.severe("Unrecognized equation in configuration");
			}
		}
		return 0;
	}
	
	//rather self-explanatory
	public int range_simple(int bottomBound, int topBound)
	{		
		int addMe = Math.abs(random.nextInt()%(topBound - bottomBound + 1));
		return bottomBound + addMe;
	}
	
	//gives an equal chance for all integers whose absolute value is less than the input
	public int range_interval(int base, int interval, int interval_range)
	{
		return base + (interval * (Math.abs(random.nextInt()%(interval_range + 1))));
	}
	
	//TODO IDEA: health spawn based on entity resting on block of type BLAH?

};