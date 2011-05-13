package com.KoryuObihiro.bukkit.ModDamage.Handling;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;



public class GroupHandler
{
//// MEMBERS //// 
	
	public boolean isLoaded = false;
	
	//world-type and mob-type damage
	final public HashMap<DamageType, List<String>> offensiveRoutines = new HashMap<DamageType, List<String>>();
	final public HashMap<DamageType, List<String>> defensiveRoutines = new HashMap<DamageType, List<String>>();
	
	//pvp damage
	final public HashMap<String, List<String>> pvpOffensiveRoutines = new HashMap<String, List<String>>();
	final public HashMap<String, List<String>> pvpDefensiveRoutines = new HashMap<String, List<String>>();
	
	//item damage
	final public HashMap<Material, List<String>> itemOffensiveRoutines = new HashMap<Material, List<String>>();
	final public HashMap<Material, List<String>> itemDefensiveRoutines = new HashMap<Material, List<String>>();
	
	final private ConfigurationNode offensiveNode;
	final private ConfigurationNode defensiveNode;
	final public DamageCalculator damageCalc;
	final public WorldHandler worldHandler;
	final String groupName;
	
	
//// CONSTRUCTOR ////
	public GroupHandler(WorldHandler worldHandler, String name, ConfigurationNode offensiveGroupNode, ConfigurationNode defensiveGroupNode, DamageCalculator damageCalc) 
	{
		this.worldHandler = worldHandler;
		this.groupName = name; //for debugging purposes
		this.offensiveNode = offensiveGroupNode;
		this.defensiveNode = defensiveGroupNode;
		this.damageCalc = damageCalc;
		this.isLoaded = loadRoutines();
	}
	
	
private boolean loadRoutines() 
{
	try
	{
		//TODO Looks a bit messy - refactor for fewer != null checks?
		//clear everything first
		offensiveRoutines.clear();
		defensiveRoutines.clear();
		mobHealthSettings.clear();
		offensiveItemRoutines.clear();
		defensiveItemRoutines.clear();

//load global settings (generic types define in DamageType enum)
		String damageCategories[] = {"animal", "item", "mob", "world"};
		for(int i = 0; i < damageCategories.length; i++)
		{
			if(offensiveNode != null && !loadDamageTypeGroup(damageCategories[i], true) 
					&& ModDamage.consoleDebugging_verbose)
				log.warning("Couldn't find world \"" + world.getName() + "\" Offensive category node \"" + damageCategories[i] + "\"");
			if(defensiveNode != null && !loadDamageTypeGroup(damageCategories[i], false) 
					&& ModDamage.consoleDebugging_verbose)
				log.warning("Couldn't find world \"" + world.getName() + "\" Defensive category node \"" + damageCategories[i] + "\"");
		}
		
//load mob health
		if(mobHealthNode != null)
			loadMobHealth();
		else if(ModDamage.consoleDebugging) log.warning("Couldn't find world \"" + world.getName() + "\" MobHealth node");
//load item settings - item-specifics are not handled by "item" damage category

		if(offensiveNode != null)
			loadItemRoutines(true);
		if(defensiveNode != null)
			loadItemRoutines(false);
	}
	catch(Exception e)
	{
		e.printStackTrace();
		log.severe("[" + plugin.getDescription().getName() 
				+ "] Invalid configuration for world \"" + world.getName()
				+ "\"; using default settings");
		useDefaults();
		return false;
	}
	return true;
	try
	{
		offensiveRoutines.clear();
		defensiveRoutines.clear();
		
//load group attack settings
		loadDamageType("animal", true);
		loadDamageType("mob", true);
		loadDamageType("item", true);
		loadDamageType("world", true);
		
//load group defense settings
		loadDamageType("animal", true);
		loadDamageType("mob", false);
		loadDamageType("item", false);
		loadDamageType("world", false);
	}
	catch(Exception e)
	{
		ModDamage.log.severe("[" + worldHandler.plugin.getDescription().getName() 
				+ "] Invalid configuration for group \"" + groupName + "\" for world \"" + worldHandler.world.getName()
				+ "\"; using default settings");
		useDefaults();
		return false;
	}
	
	return true;
}


	//// CLASS-SPECIFIC FUNCTIONS ////
	//getters
	public String getGroupName(){ return groupName;}
	
	//setters
	
	//TODO Clean up to just use simple return?
	public int calcAttackBuff(DamageType mobType_target, int eventDamage)
	{
		return runRoutines(mobType_target, true, eventDamage);
	}
	public int calcDefenseBuff(DamageType damageType, int eventDamage)
	{	
		return runRoutines(damageType, false, eventDamage);
	}
	
	public int calcAttackBuff(String group_target, int eventDamage)
	{
		return runRoutines(DamageType.WORLD_PLAYER, true, eventDamage) 
			+ runPVPRoutines(group_target, true, eventDamage);
	}
	public int calcDefenseBuff(String group_attacking, int eventDamage)
	{	
		return runRoutines(DamageType.WORLD_PLAYER, false, eventDamage) 
			+ runPVPRoutines(group_attacking, false, eventDamage);
	}
	
	public int calcAttackBuff(Material inHand_attacking, int eventDamage)
	{
		return runItemRoutines(inHand_attacking, true, eventDamage);
	}

	public int calcDefenseBuff(Material inHand_attacking, int eventDamage)
	{
		return runItemRoutines(inHand_attacking, false, eventDamage);
	}
	public boolean damageType_isLoaded(DamageType damageType){ return offensiveRoutines.containsKey(damageType);}
	
	public boolean sendGroupConfig(Player player, String configReference)
	{ //TODO see sendWorldConfig for template to use here
		if(isLoaded)
		{
			if(player != null)
			{
				
			}
			else
			{
				
			}
			return true;
		}
		return false;
	}
	
	public void loadDamageType(String damageDescriptor, boolean isOffensive)
	{
		ConfigurationNode relevantNode = (isOffensive
											?offensiveNode.getNode(damageDescriptor)
											:defensiveNode.getNode(damageDescriptor));
		for(DamageType damageType : DamageType.values())
			if(damageType.getDescriptor().equals("mob"))
			{
				List<String> calcStrings = relevantNode.getStringList(damageType.getConfigReference(), null);
				if(calcStrings.equals(null))
					for(String calcString : calcStrings)
						if(!damageCalc.checkCommandString(calcString))
						{
							ModDamage.log.severe("[" + worldHandler.plugin.getDescription().getName() + "] Invalid command string \"" 
								+ calcString + "\" for group " + groupName + " in " + (isOffensive?"Offensive ":"Defensive ") + damageType.getConfigReference() 
								+ " definition - refer to config for proper calculation node");
							calcStrings.clear();
						}
				if(calcStrings.size() > 0)
				{
					if(!(isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageType))
						(isOffensive?offensiveRoutines:defensiveRoutines).put(damageType, calcStrings);
					else if(ModDamage.consoleDebugging) 
						ModDamage.log.warning("[" + worldHandler.plugin.getDescription().getName() + "] Repetitive " 
							+ damageType.getConfigReference() + " definition in " + (isOffensive?"Offensive":"Defensive") + " settings for group " + groupName + " - ignoring");
				}
				else if(ModDamage.consoleDebugging_verbose)
				{		
					ModDamage.log.warning("[" + worldHandler.plugin.getDescription().getName() + "] No instructions found for group " 
						+ groupName + " " + damageType.getConfigReference() + " node in " + (isOffensive?"Offensive":"Defensive") + " for world " 
						+ worldHandler.world.getName() +  " - is this on purpose?");
				}
				if(ModDamage.consoleDebugging)
					ModDamage.log.info("[" + worldHandler.plugin.getDescription().getName() + "] " + worldHandler.world.getName() 
						+ ":" + groupName + ":" + (isOffensive?"Offensive":"Defensive") + ":" 
						+ damageType.getDescriptor() + ":" + damageType.getConfigReference() + " " 
						+ (ModDamage.consoleDebugging_verbose?("\n" + calcStrings.toString()):""));//debugging
			}
	}
	
	private int runRoutines(DamageType damageType, boolean isOffensive, int eventDamage)
	{
		int result = 0;
		if(damageType != null && (isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageType))
				for(String calcString : (isOffensive?offensiveRoutines:defensiveRoutines).get(damageType))
					result += damageCalc.parseCommand(calcString, eventDamage);
		return result;
	}
	private int runPVPRoutines(String groupName, boolean isOffensive, int eventDamage)
	{
		int result = 0;
		if(groupName != null && (isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).containsKey(groupName))
				for(String calcString : (isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).get(groupName))
					result += damageCalc.parseCommand(calcString, eventDamage);
		return result;
	}
	
	private int runItemRoutines(Material material, boolean isOffensive, int eventDamage) 
	{
		int result = 0;
		if(material != null && (isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).containsKey(material))
			for(String calcString : (isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).get(material))
				result += damageCalc.parseCommand(calcString, eventDamage);
		return result;
	}
	
	public void useDefaults(){ isLoaded = false;}
}

	