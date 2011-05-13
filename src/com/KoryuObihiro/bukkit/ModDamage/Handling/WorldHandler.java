package com.KoryuObihiro.bukkit.ModDamage.Handling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;



public class WorldHandler
{
//// MEMBERS ////
	public ModDamage plugin;
	public Logger log;
	public World world;
	
	public boolean isLoaded = false;
	public boolean groupsLoaded = false;
	
	final public DamageCalculator damageCalc;
	final public HealthCalculator healthCalc;
	final public ConfigurationNode offensiveNode;
	final public ConfigurationNode defensiveNode;
	final public ConfigurationNode mobHealthNode;
	
	//world-type and mob-type buffs
	final public HashMap<DamageType, List<String>> offensiveRoutines = new HashMap<DamageType, List<String>>();
	final public HashMap<DamageType, List<String>> defensiveRoutines = new HashMap<DamageType, List<String>>();
	final public HashMap<DamageType, String> mobHealthSettings = new HashMap<DamageType, String>();
	
	//item-buffing HashMap
	final public HashMap<Material, List<String>> offensiveItemRoutines = new HashMap<Material, List<String>>();
	final public HashMap<Material, List<String>> defensiveItemRoutines = new HashMap<Material, List<String>>();
	
	//GroupHandler HashMap
	final public HashMap<String, GroupHandler> groupHandlers = new HashMap<String, GroupHandler>();
	
//// CONSTRUCTOR ////
	public WorldHandler(ModDamage plugin, World world, ConfigurationNode offensiveNode, ConfigurationNode defensiveNode, ConfigurationNode mobHealthNode, DamageCalculator damageCalc, HealthCalculator healthCalc) 
	{
		this.world = world;
		this.plugin = plugin;
		this.log = ModDamage.log;
		this.offensiveNode = offensiveNode;
		this.defensiveNode = defensiveNode;
		this.mobHealthNode = mobHealthNode;
		this.damageCalc = damageCalc;
		this.healthCalc = healthCalc;
		
		isLoaded = loadGlobalRoutines();
		groupsLoaded = ((isLoaded)?loadGroupHandlers(true):false);
		
		if(isLoaded) log.info("[" + plugin.getDescription().getName() + "] Global configuration for world \"" 
				+ world.getName() + "\" initialized!");
		else log.warning("[" + plugin.getDescription().getName() + "] Global configuration for world \"" 
				+ world.getName() + "\" could not load.");
		
		if(groupsLoaded) log.info("[" + plugin.getDescription().getName() + "] Group configuration for world \""
				+ world.getName() + "\" initialized!");
		else log.warning("[" + plugin.getDescription().getName() + "] Group configuration for world \""
				+ world.getName() + "\" could not load.");
	}
	
	
//// CLASS-SPECIFIC FUNCTIONS ////
	public World getWorld(){ return world;}
	
	//load settings from file
	public boolean loadGlobalRoutines()
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
					log.warning("{Couldn't find world \"" + world.getName() + "\" Offensive category node \"" + damageCategories[i] + "\"}");
				if(defensiveNode != null && !loadDamageTypeGroup(damageCategories[i], false) 
						&& ModDamage.consoleDebugging_verbose)
					log.warning("{Couldn't find world \"" + world.getName() + "\" Defensive category node \"" + damageCategories[i] + "\"}");
			}
			
	//load mob health
			if(mobHealthNode != null)
				loadMobHealth();
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
	}
	
	public boolean loadGroupHandlers(boolean force)
	{
		//get all of the groups in configuration
		List<String> groups = ((offensiveNode != null)?offensiveNode.getKeys("groups"):new ArrayList<String>());
		if(defensiveNode != null) groups.addAll(defensiveNode.getKeys("groups"));
		
		//load groups with offensive and defensive settings first
		for(String group : groups)
		{	
			if(groupHandlers.containsKey(group) && force)
			{
				groupHandlers.remove(group);
				if(ModDamage.consoleDebugging)
					ModDamage.log.info("{Forcing reload of group " + group + "...}");
			}
			if(!groupHandlers.containsKey(group))
				groupHandlers.put(group, new GroupHandler(this, group, 
								((offensiveNode != null)?offensiveNode.getNode("groups"):null),
								((defensiveNode != null)?defensiveNode.getNode("groups").getNode(group):null), 
								damageCalc));
		}
		return true;
	}
	
	public boolean loadItemRoutines(boolean isOffensive){ return loadItemRoutines(isOffensive, false);}
	public boolean loadItemRoutines(boolean isOffensive, boolean force)
	{
		ConfigurationNode itemNode = (isOffensive
											?offensiveNode.getNode("global").getNode("item")
											:defensiveNode.getNode("global").getNode("item"));
		if(itemNode != null)	
		{
			List<String> itemList = (isOffensive?offensiveNode.getNode("global"):defensiveNode.getNode("global")).getKeys("item");
			for(Material material : Material.values())
				if(itemList.contains(material.name()) || itemList.contains(material.getId()))
				{
					List<String> calcStrings = itemNode.getStringList(material.name(), null);
					log.warning(material.name() + " " + itemNode.getStringList(Integer.toString(material.getId()).toUpperCase(), null).toString());
					if(calcStrings == null) calcStrings = itemNode.getStringList(Integer.toString(material.getId()), null);
					if(!calcStrings.equals(null))
					{
						for(String calcString : calcStrings)
							if(!damageCalc.checkCommandString(calcString))
							{
								log.severe("Invalid command string \"" + calcString + "\" in " 
										+ (isOffensive?"Offensive":"Defensive") + " " + material.name() + "(" + material.getId()
										+ ") definition - refer to config for proper calculation node");
								calcStrings.clear();
							}
						//TODO Fix individual item typing
						if(calcStrings.size() > 0)
						{
							if(ModDamage.consoleDebugging) log.info(world.getName() 
									+ ":" + (isOffensive?"Offensive":"Defensive") + ":" + material.name() + "(" + material.getId() + ")"
									+ (ModDamage.consoleDebugging_verbose?(" " + calcStrings.toString()):""));//debugging
							if(!(isOffensive?offensiveItemRoutines:defensiveItemRoutines).containsKey(material))
								(isOffensive?offensiveItemRoutines:defensiveItemRoutines).put(material, calcStrings);
							else if(ModDamage.consoleDebugging) log.warning("[" + plugin.getDescription().getName() + "] Repetitive " 
									+ material.name() + "(" + material.getId() + ") definition in " + (isOffensive?"Offensive":"Defensive") + " item globals - ignoring");
						}
						else if(ModDamage.consoleDebugging_verbose)
						{
							log.warning("No instructions found for global " + material.name() + "(" + material.getId()
								+ ") item node in " + (isOffensive?"Offensive":"Defensive") + " - is this on purpose?");
						}
						if(ModDamage.consoleDebugging)
							ModDamage.log.info("-" + world.getName() + ":" + (isOffensive?"Offensive":"Defensive") + ":" 
									+ material.name() + "(" + material.getId() + ") "
									+ calcStrings.toString());//debugging
						return true;
					}
				}
		}
		return false;
	}
	public void useDefaults(){ isLoaded = false;}
	
	

	public boolean loadDamageTypeGroup(String damageDescriptor, boolean isOffensive)
	{
		ConfigurationNode relevantNode = (isOffensive
											?offensiveNode.getNode("global").getNode(damageDescriptor)
											:defensiveNode.getNode("global").getNode(damageDescriptor));
			if(relevantNode != null)
			{
				if(ModDamage.consoleDebugging) log.info("{Found global " + (isOffensive?"Offensive":"Defensive") + " " + damageDescriptor + " node}");
				for(DamageType damageType : DamageType.values())
					if(damageType.getDescriptor().equals(damageDescriptor))
					{
						//check for leaf-node buff strings
						List<String> calcStrings = relevantNode.getStringList(damageType.getConfigReference(), null);
						if(!calcStrings.equals(null)) //!calcStrings.equals(null)
						{
							for(String calcString : calcStrings)
								if(!damageCalc.checkCommandString(calcString))
								{
									log.severe("Invalid command string \"" 
											+ calcString + "\" in " + (isOffensive?"Offensive":"Defensive") + " " + damageType.getConfigReference() 
											+ " definition - refer to config for proper calculation node");
									calcStrings.clear();
								}
							if(calcStrings.size() > 0)
							{
								if(ModDamage.consoleDebugging) log.info("-" + world.getName() + ":" + (isOffensive?"Offensive":"Defensive") 
										+ ":" + damageType.getConfigReference() 
										+ (ModDamage.consoleDebugging_verbose?(" " + calcStrings.toString()):"") );//debugging
								if(!(isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageType))
									(isOffensive?offensiveRoutines:defensiveRoutines).put(damageType, calcStrings);
								else if(ModDamage.consoleDebugging) log.warning("Repetitive "+ damageType.getConfigReference() 
										+ " definition in " + (isOffensive?"Offensive":"Defensive") + " " + damageDescriptor + " globals - ignoring");
							}
							else if(ModDamage.consoleDebugging_verbose)
							{
								log.warning("No instructions found for global " + damageType.getConfigReference() 
									+ " node in " + (isOffensive?"Offensive":"Defensive") + " - is this on purpose?");
							}
						}
						else if(ModDamage.consoleDebugging_verbose) log.info("Global " + damageType.getConfigReference() 
								+ " node for" + (isOffensive?"Offensive":"Defensive") + " not found.");
					}
				return true;
			}
			return false;
	}
	

///////////////////// MOB SPAWNING ///////////////////////
	public void loadMobHealth()
	{
		
		log.info("Found MobHealth node");
		//load Mob health settings
		for(DamageType mobType : DamageType.values())
			if(mobType.getDescriptor().equals("mob") || mobType.getDescriptor().equals("animal"))
			{
			//check for leaf-node health strings
				String calcString = (String) mobHealthNode.getProperty(mobType.getConfigReference());
				if(calcString != null)
				{
					//calcString = calcString.substring(1, calcString.length() - 1); //use this when checking for leaf nodes, not properties
					if(!healthCalc.checkCommandString(calcString))
					{
						log.severe("Invalid command string \"" + calcString + "\" in MobHealth " + mobType.getConfigReference() 
								+ " definition - refer to config for proper calculation node");
					}
				//display debug message to acknowledge that the settings have been validated
					if(ModDamage.consoleDebugging) log.info("-" + world.getName() + ":MobHealth:" + mobType.getConfigReference() 
							+ (ModDamage.consoleDebugging_verbose?(" " + calcString.toString()):""));
				//check that this type of mob hasn't already been loaded
					if(!mobHealthSettings.containsKey(mobType))
						mobHealthSettings.put(mobType, calcString);
					else if(ModDamage.consoleDebugging) log.warning("Repetitive " + mobType.getConfigReference() 
							+ " definition for MobHealth " + mobType.getConfigReference() + " - ignoring");
				}
				else if(ModDamage.consoleDebugging_verbose)
				{
					log.warning("No instructions found for MobHealth " + mobType.getConfigReference() 
						+ " node - is this on purpose?");
				}
			}
	}
	
	public void setHealth(Entity entity)
	{
		//determine creature type
		DamageType creatureType = DamageType.matchEntityType(entity);
		if(creatureType != null && mobHealthSettings.containsKey(creatureType))
		{
			Creature creature = (Creature)entity;
			creature.setHealth(healthCalc.parseCommand(mobHealthSettings.get(creatureType)));
		}
		
	}	

///////////////////// DAMAGE HANDLING ///////////////////////
//Player-targeted damage
//PvP
	public int calcAttackBuff(Player player_target, Player player_attacking, int eventDamage)
	{
		if(isLoaded)
		{
			String group_target = ModDamage.Permissions.getGroup(player_target.getWorld().getName(), player_target.getName());
			String group_attacking = ModDamage.Permissions.getGroup(player_target.getWorld().getName(), player_target.getName());
			//apply global buff settings
			return runGlobalRoutines(DamageType.WORLD_PLAYER, true, eventDamage)
				+ runGlobalRoutines(DamageType.matchItemType(player_attacking.getItemInHand().getType()), true, eventDamage)
				+ runItemRoutines(player_attacking.getItemInHand().getType(), true, eventDamage)
			//apply group settings
				+ (groupHandlers.containsKey(group_attacking)
						?groupHandlers.get(group_attacking).calcAttackBuff(group_target, eventDamage)
								+groupHandlers.get(group_attacking).calcAttackBuff(player_attacking.getItemInHand().getType(), eventDamage)
							:0);
		}
		return 0;
	}
	public int calcDefenseBuff(Player player_target, Player player_attacking, int eventDamage)
	{		
		if(isLoaded)
		{
			//get group strings
			String group_target = ModDamage.Permissions.getGroup(player_target.getWorld().getName(), player_target.getName());
			String group_attacking = ModDamage.Permissions.getGroup(player_attacking.getWorld().getName(), player_attacking.getName());
			//apply global buff settings
			return runGlobalRoutines(DamageType.WORLD_PLAYER, false, eventDamage)
				+ runGlobalRoutines(DamageType.matchItemType(player_attacking.getItemInHand().getType()), false, eventDamage)
				+ runItemRoutines(player_attacking.getItemInHand().getType(), false, eventDamage)
			//apply group buff settings
				+ (groupHandlers.containsKey(group_target)
						?groupHandlers.get(group_target).calcDefenseBuff(group_attacking, eventDamage)
							+groupHandlers.get(group_target).calcDefenseBuff(player_attacking.getItemInHand().getType(), eventDamage)
						:0);
		}
		return 0;
	}
	//---
	
//handle NPvP
	public int calcAttackBuff(Player player_target, DamageType damageType, int eventDamage)
	{
		//String group_target = plugin.Permissions.getGroup(player_target.getWorld().getName(), player_target.getName());
		if(isLoaded)
		{
			return runGlobalRoutines(damageType, true, eventDamage);
		}
		return 0;
	}
	public int calcDefenseBuff(Player player_target, DamageType damageType, int eventDamage)
	{
		if(isLoaded)
		{
			String group_target = ModDamage.Permissions.getGroup(player_target.getWorld().getName(), player_target.getName());
			return runGlobalRoutines(damageType, false, eventDamage) 
				+ (groupHandlers.containsKey(group_target)?groupHandlers.get(group_target).calcDefenseBuff(damageType, eventDamage):0);
		}
		return 0;
	}
	//---
	
//Mob-targeted damage
//WORLD vs MOB
	public int calcAttackBuff(DamageType mobType_target, DamageType damageType, int eventDamage)
	{
		if(isLoaded)
		{
			//apply global buff settings
			return runGlobalRoutines(damageType, true, eventDamage);
		}
		return 0;
	}
	public int calcDefenseBuff(DamageType mobType_target, DamageType damageType, int eventDamage)
	{
		if(isLoaded)
		{
			return runGlobalRoutines(damageType, false, eventDamage);
		}
		return 0;
	}
	//---
//PvNP	
	public int calcAttackBuff(DamageType mobType_target, Player player_attacking, int eventDamage)
	{
		if(isLoaded)
		{
			String group_attacking = ModDamage.Permissions.getGroup(player_attacking.getWorld().getName(), player_attacking.getName());
			return runGlobalRoutines(DamageType.WORLD_PLAYER, true, eventDamage) 
					+ runGlobalRoutines(DamageType.matchItemType(player_attacking.getItemInHand().getType()), true, eventDamage)
					+ (groupHandlers.containsKey(group_attacking)
							?groupHandlers.get(group_attacking).calcAttackBuff(mobType_target, eventDamage)
							+ groupHandlers.get(group_attacking).calcAttackBuff(player_attacking.getItemInHand().getType(),eventDamage)
							:0)
					+ runItemRoutines(player_attacking.getItemInHand().getType(), true, eventDamage);
		}
		return 0;
	}
	public int calcDefenseBuff(DamageType mobType_target, Player player_attacking, int eventDamage)
	{
		if(isLoaded)
		{
			//String group_attacking = ModDamage.Permissions.getGroup(player_attacking.getWorld().getName(), player_attacking.getName());
			return runGlobalRoutines(mobType_target, false, eventDamage) 
				+ runGlobalRoutines(DamageType.matchItemType(player_attacking.getItemInHand().getType()), false, eventDamage)
				+ runItemRoutines(player_attacking.getItemInHand().getType(), false, eventDamage);
		}
		return 0;
	}
	//---
	
//Routine handlers
	private int runGlobalRoutines(DamageType damageType, boolean isOffensive, int eventDamage)
	{ return runGlobalRoutines(damageType, isOffensive, eventDamage, false);}
	private int runGlobalRoutines(DamageType damageType, boolean isOffensive, int eventDamage, boolean printDebugging)
	{
		int result = 0;
		if(damageType != null && (isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageType))
		{
			for(String calcString : (isOffensive?offensiveRoutines:defensiveRoutines).get(damageType))
				result += damageCalc.parseCommand(calcString, eventDamage);
			if(printDebugging)
				log.info((isOffensive?"AttBuff: ":"DefBuff: ") + result);
		}
		return result;
	}
	
	private int runItemRoutines(Material materialType, boolean isOffensive, int eventDamage)
	{ return runItemRoutines(materialType, isOffensive, eventDamage, false);}
	private int runItemRoutines(Material materialType, boolean isOffensive, int eventDamage, boolean printDebugging) 
	{
		int result = 0;
		if(materialType != null && (isOffensive?offensiveItemRoutines:defensiveItemRoutines).containsKey(materialType))
		{
			for(String calcString : (isOffensive?offensiveItemRoutines:defensiveItemRoutines).get(materialType))
				result += damageCalc.parseCommand(calcString, eventDamage);
			if(printDebugging)
				log.info((isOffensive?"AttBuff: ":"DefBuff: ") + result);
		}
		return result;
	}
//----
	
///////////////////// HELPER FUNCTIONS ///////////////////////
	
	public boolean group_isLoaded(String groupName){ return groupHandlers.containsKey(groupName);}
	public boolean attackType_isLoaded(DamageType damageType){ return offensiveRoutines.containsKey(damageType);}
	public boolean defenseType_isLoaded(DamageType damageType){ return defensiveRoutines.containsKey(damageType);}
	//TODO More verbose debugging in general?
	
	public boolean sendWorldConfig(Player player, String configReference)
	{
		if(isLoaded)
		{
			if(player != null)
			{
				//send specified category stuff - perhaps format upon loading for performance? (TODO)
			}
			else //send to console
			{
				
			}
		}
		return false;
	}

	public boolean reload()
	{ 
		isLoaded = loadGlobalRoutines();
		groupsLoaded = loadGroupHandlers(true);
		return (isLoaded && groupsLoaded);
	}

	public boolean sendGroupConfig(Player player, String groupName, String configReference) 
	{
		if(groupHandlers.containsKey(groupName))
			return groupHandlers.get(groupName).sendGroupConfig(player, configReference);
		else return false;
	}
	
		
}

	