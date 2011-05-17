package com.KoryuObihiro.bukkit.ModDamage.Handling;
import java.util.Random;
import java.util.logging.Logger;



public class DamageCalculator
{
	//TODO 
	//TODO equipment handling, inclusive/exclusive sets
	//TODO use event damage for some types of formulae to be added later
	//TODO IF #function
	Logger log = Logger.getLogger("Minecraft");
	final Random random = new Random();
	
	//for file parsing
	public boolean checkCommandString(String commandString) 
	{
		if(commandString != null)
		{
			try
			{
				if(Math.abs(Integer.parseInt(commandString)) > 0)
					return true;
			}
			catch(Exception e){}
			
			String[] commandSplit = commandString.split("\\*");
			String[] args = commandSplit[0].split("\\."); //Splits according to ".", but because of regex metacharacters we need to use "\\."
			
			if(commandSplit.length > 1)
			{
				if(commandSplit.length == 2 && args.length == 2)
				{
					if(args[0].equals("binom"))
					{
						try
						{
							Integer.parseInt(args[1]);
							return checkCommandString(commandSplit[1]);
						}
						catch(Exception e){}
					}
				}
			}
			else if(args.length == 1)
			{
				if(args[0].equals("roll"))
				{
					return true;
				}
			}
			else if(args.length == 2)
			{			
				if(args[0].equals("roll"))
				{
					try
					{
						Integer.parseInt(args[1]);
						return true;
					}
					catch(Exception e){}
				}
				else if(args[0].equals("mult") || args[0].equals("mult_add"))
				{
					try
					{
						Integer.parseInt(args[1]);
						return true;
					}
					catch(Exception e){}
				}
				else if(args[0].equals("div") || args[0].equals("div_add"))
				{
					try
					{
						Integer.parseInt(args[1]);
						return true;
					}
					catch(Exception e){}
				}
				else if(args[0].equals("set"))
				{
					try
					{
						Integer.parseInt(args[1]);
						return true;
					}
					catch(Exception e){}
				}
				else if(args[0].equals("binom"))
				{
					try
					{
						Integer.parseInt(args[1]);
						return true;
					}
					catch(Exception e){}
				}
			}
			//else log.info("Number of arguments for \"" + commandString + "\" not recognized"); //debugging
		}
		//else log.info("Null string passed! D:"); //debugging
		return false;
	}
	
	//Parse commands for different command strings the handlers pass
	public int parseCommand(String commandString, int eventDamage, boolean isOffensive)
	{
		log.info("Passed " + commandString);
		try
		{
			int tryThis = Integer.parseInt(commandString);
			return tryThis;
		}
		catch(Exception e){}
		
		String[] commandSplit = commandString.split("\\*");
		String[] args = commandSplit[0].split("\\.");
		if(args.length > 0)
		{
			if(commandSplit.length > 1)
			{
				if(commandSplit.length == 2)
				{
					if(args[0].equals("binom"))
					{
						try
						{
							return roll_binomial(Integer.parseInt(args[1]), commandSplit[1], eventDamage, isOffensive);
						}
						catch(Exception e)
						{
							log.severe("Improper equation in configuration (binom)");
							return 0;
						}
					}
				}
			}
			else if(args.length == 1)
			{
				if(args[0].equals("roll"))
				{
					return roll_simple(eventDamage);
				}
			}
			else if(args.length == 2)
			{
				if(args[0].equals("roll"))
				{
					try
					{
						return roll_simple(Integer.parseInt(args[1]));
					}
					catch(Exception e)
					{
						log.severe("Improper equation in configuration (roll) - this shouldn't have happened.");
						return 0;
					}
				}
				else if(args[0].equals("mult"))
				{
					try
					{
						return multiply(eventDamage, Integer.parseInt(args[1]));
					}
					catch(Exception e)
					{
						log.severe("Improper input in configuration (multiply) - this shouldn't have happened.");
						return 0;
					}
				}
				else if(args[0].equals("mult_add"))
				{
					try
					{
						return multiply_add(eventDamage, Integer.parseInt(args[1]));
					}
					catch(Exception e)
					{
						log.severe("Improper input in configuration (multiply_add) - this shouldn't have happened.");
					}
				}
				else if(args[0].equals("div"))
				{
					try
					{
						return divide(eventDamage, Integer.parseInt(args[1]));
					}
					catch(Exception e)
					{
						log.severe("Improper input in configuration (divide) - this shouldn't have happened.");
						return 0;
					}
				}
				else if(args[0].equals("div_add"))
				{
					try
					{
						return divide_add(eventDamage, Integer.parseInt(args[1]));
					}
					catch(Exception e)
					{
						log.severe("Improper input in configuration (divide_add) - this shouldn't have happened.");
					}
				}
				else if(args[0].equals("set"))
				{
					try
					{
						return set(eventDamage, Integer.parseInt(args[1]), isOffensive);
					}
					catch(Exception e)
					{
						log.severe("Improper input in configuration (set) - this shouldn't have happened.");
					}
				}
				else if(args[0].equals("binom"))
				{
					try
					{
						return roll_binomial(Integer.parseInt(args[1]), eventDamage, isOffensive);
					}
					catch(Exception e){}
				}
				else
				{
					log.severe("Unrecognized equation in configuration - this shouldn't have happened");
					return 0;
				}
			}
		}
		return 0;
	}
	
	//rather self-explanatory
	public int roll_binomial(int chance, int eventDamage, boolean isOffensive)
	{
		if(chance < 0 || chance > 100)
			return 0;
		if(Math.abs(random.nextInt()%101) <= chance)
			return 0;
		return ((isOffensive?-1:1) * eventDamage);
	}
	
	public int roll_binomial(int chance, String input, int eventDamage, boolean isOffensive)
	{
		if(chance < 0 || chance > 100) return 0;
		if(Math.abs(random.nextInt()%101) <= chance)
			return parseCommand(input, eventDamage, isOffensive);
		return 0;
	}
	
	//gives an equal chance for all integers whose absolute value is less than the input
	public int roll_simple(int input)
	{
		return Math.abs(random.nextInt()%(input + 1));
	}
	
	public int multiply(int input, int factor){	return (input*(factor - 1));}
	public int multiply_add(int input, int factor){	return input*factor;}
	
	public int divide(int input, int factor)
	{
		if(factor != 0) return (input/factor - input); 
		else log.severe("[ModDamage] Divide by zero...really?");
		return 0;
	}
	public int divide_add(int input, int factor)
	{ 
		if(factor != 0)return input/factor; 
		else log.severe("[ModDamage] Divide by zero...really?");
		return 0;
	}
	
	public int set(int eventDamage, int input, boolean isOffensive)
	{ 
		return (isOffensive?(1):(-1)) * (input - eventDamage);
	}
	//TODO IDEA: damage based on entity resting on block of type BLAH? This would involve icky refactoring. :P
};