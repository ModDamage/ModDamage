package com.KoryuObihiro.bukkit.ModDamage.Handling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;
import org.json.simple.ItemList;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;



public class WorldHandler
{
//// MEMBERS ////
	public ModDamage plugin;
	public Logger log; 
	protected World world;
	
	public boolean globalsLoaded = false;
	public boolean groupsLoaded = false;
	public boolean mobHealthLoaded = false;
	public boolean scanLoaded = false;
	
	final private DamageCalculator damageCalc;
	final private HealthCalculator healthCalc;
	final private ConfigurationNode offensiveNode;
	final private ConfigurationNode defensiveNode;
	final private ConfigurationNode mobHealthNode;
	final private ConfigurationNode scanNode;
	
	//generic and 
	final private HashMap<DamageElement, List<String>> offensiveRoutines = new HashMap<DamageElement, List<String>>();
	final private HashMap<DamageElement, List<String>> defensiveRoutines = new HashMap<DamageElement, List<String>>();
	final private HashMap<DamageElement, String> mobHealthSettings = new HashMap<DamageElement, String>();
	
	//specific-item HashMap
	final private HashMap<Material, List<String>> offensiveItemRoutines = new HashMap<Material, List<String>>();
	final private HashMap<Material, List<String>> defensiveItemRoutines = new HashMap<Material, List<String>>();
	
	//GroupHandler HashMap
	final private HashMap<String, GroupHandler> groupHandlers = new HashMap<String, GroupHandler>();
	
	final private List<Material> globalScanItems = new ArrayList<Material>();
	
//// CONSTRUCTOR ////
	public WorldHandler(ModDamage plugin, World world, ConfigurationNode offensiveNode, ConfigurationNode defensiveNode, ConfigurationNode mobHealthNode, ConfigurationNode scanNode, DamageCalculator damageCalc, HealthCalculator healthCalc) 
	{
		this.world = world;
		this.plugin = plugin;
		this.log = ModDamage.log;
		this.offensiveNode = offensiveNode;
		this.defensiveNode = defensiveNode;
		this.mobHealthNode = mobHealthNode;
		this.scanNode = scanNode;
		this.damageCalc = damageCalc;
		this.healthCalc = healthCalc;
		
		reload();
	}

	public boolean reload()
	{ 
		//load Offensive configuration
		globalsLoaded = loadGlobalRoutines();
		if(globalsLoaded && ModDamage.consoleDebugging_normal) 
			log.info("[" + plugin.getDescription().getName() + "] Global configuration for world \"" 
				+ world.getName() + "\" initialized!");
		else if(ModDamage.consoleDebugging_verbose)
			log.warning("[" + plugin.getDescription().getName() + "] Global configuration for world \"" 
				+ world.getName() + "\" could not load.");
		
		//load Defensive configuration
		groupsLoaded = ((globalsLoaded)?loadGroupHandlers(true):false);
		if(groupsLoaded && ModDamage.consoleDebugging_normal) log.info("[" + plugin.getDescription().getName() + "] Group configuration for world \""
				+ world.getName() + "\" initialized!");
		else if(ModDamage.consoleDebugging_verbose) 
			log.warning("[" + plugin.getDescription().getName() + "] Group configuration for world \""
				+ world.getName() + "\" could not load.");

		//load Scan item configuration
		scanLoaded = loadScanItems();
		//load MobHealth configuration
		mobHealthLoaded = loadMobHealth();
		
		if(globalsLoaded || groupsLoaded || scanLoaded || mobHealthLoaded)
			return true;
		return false;
	}
	
	
//// CLASS-SPECIFIC FUNCTIONS ////

///////////////////// OFFENSIVE/DEFENSIVE ///////////////////////
	
// global
	public boolean loadGlobalRoutines()
	{
		String progressString = "UNKNOWN";
		try
		{
			//clear everything first
			clearRoutines();
	
	//load "global" node routines
			if(offensiveNode != null)
			{
				progressString = " generic damage types in Offensive";
				if(!(loadGenericRoutines(true)))
					return false;

				progressString = " Offensive item routines";
				if(!(loadItemRoutines(true)))
					return false;
			}
			if(defensiveNode != null)
			{
				progressString = " generic damage types in Defensive";
				if(!(loadGenericRoutines(false)))
					return false;

				progressString = " Defensive item routines";
				if(!(loadItemRoutines(false)))
					return false;
			}			
		}
		catch(Exception e)
		{
			log.severe("[" + plugin.getDescription().getName() 
					+ "] Invalid global configuration for world \"" + world.getName()
					+ "\" - failed to load" + progressString + ".");
			return false;
		}
		return true;
	}

	public boolean loadGenericRoutines(boolean isOffensive)
	{
		List<String>damageCategories = DamageElement.getTypeStrings();
		for(String damageCategory : damageCategories)
		{
			ConfigurationNode relevantNode = (isOffensive
												?offensiveNode.getNode("global").getNode(damageCategory)
												:defensiveNode.getNode("global").getNode(damageCategory));
				if(relevantNode != null)
				{
					if(ModDamage.consoleDebugging_verbose) log.info("{Found global " + (isOffensive?"Offensive":"Defensive") + " " 
							+ damageCategory + " node for world \"" + world.getName() + "\"}");
					for(DamageElement damageElement : DamageElement.getTypeElements(damageCategory))
					{
						String elementReference = damageElement.getConfigReference();
						//check for leaf-node buff strings
						List<String> calcStrings = relevantNode.getStringList(elementReference, null);
						if(!calcStrings.equals(null)) //!calcStrings.equals(null)
						{
							damageCalc.checkCommandStrings(calcStrings, elementReference, isOffensive);
							if(calcStrings.size() > 0)
							{
								if(!(isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageElement))
								{
									(isOffensive?offensiveRoutines:defensiveRoutines).put(damageElement, calcStrings);
									if(ModDamage.consoleDebugging_normal) log.info("-" + world.getName() + ":" 
											+ (isOffensive?"Offensive":"Defensive") + ":" + elementReference 
											+ (ModDamage.consoleDebugging_verbose?(" " + calcStrings.toString()):"") );//debugging
								}
								else if(ModDamage.consoleDebugging_normal)
								{
									log.warning("Repetitive "  + elementReference + " in " + (isOffensive?"Offensive":"Defensive") + " - ignoring");
									continue;
								}
							}
							else if(ModDamage.consoleDebugging_verbose)
								log.warning("No instructions found for " + elementReference + " node - is this on purpose?");
						}
						else if(ModDamage.consoleDebugging_verbose) log.info("Global " + damageElement.getConfigReference() 
								+ " node for" + (isOffensive?"Offensive":"Defensive") + " not found.");
					}
					return true;
				}
			}
			return false;
	}

	public boolean loadItemRoutines(boolean isOffensive){ return loadItemRoutines(isOffensive, false);}
	public boolean loadItemRoutines(boolean isOffensive, boolean force)
	{
		ConfigurationNode itemNode = (isOffensive
											?offensiveNode.getNode("global").getNode("item")
											:defensiveNode.getNode("global").getNode("item"));
		if(itemNode != null)	
		{
			List<String> itemList = (isOffensive?offensiveNode:defensiveNode).getNode("global").getKeys("item");
			List<String> calcStrings = null;
			for(Material material : Material.values())	
			{
				if(itemList.contains(material.name())) //TODO Mess with casing here?
					calcStrings = itemNode.getStringList(material.name(), null);
				//else if(itemList.contains(material.getId())) //getStringList does NOT like integers, for some reason. :(
					//calcStrings = itemNode.getStringList(material.getId(), null);
				if(calcStrings != null)
				{
					log.info("calcStrings not null for " + material.name() + ", size is " + calcStrings.size());
					if(calcStrings.size() > 0)
					{
						for(String calcString : calcStrings)
						{
							log.info("Checking string \"" + calcString + "\"");
							if(!damageCalc.checkCommandString(calcString))
							{
								log.severe("Invalid command string \"" + calcString + "\" in " 
										+ (isOffensive?"Offensive":"Defensive") + " " + material.name() + "(" + material.getId()
										+ ") definition - refer to config for proper calculation node");
								calcStrings.clear();
							}
						}
						if(ModDamage.consoleDebugging_normal) log.info(world.getName() 
								+ ":" + (isOffensive?"Offensive":"Defensive") + ":" + material.name() + "(" + material.getId() + ")"
								+ (ModDamage.consoleDebugging_verbose?(" " + calcStrings.toString()):""));//debugging
						if(!(isOffensive?offensiveItemRoutines:defensiveItemRoutines).containsKey(material))
							(isOffensive?offensiveItemRoutines:defensiveItemRoutines).put(material, calcStrings);
						else if(ModDamage.consoleDebugging_normal) log.warning("[" + plugin.getDescription().getName() + "] Repetitive " 
								+ material.name() + "(" + material.getId() + ") definition in " + (isOffensive?"Offensive":"Defensive") + " item globals - ignoring");
					}
					else if(ModDamage.consoleDebugging_verbose)
					{
						log.warning("No instructions found for global " + material.name() + "(" + material.getId()
							+ ") item node in " + (isOffensive?"Offensive":"Defensive") + " - is this on purpose?");
					}
					if(ModDamage.consoleDebugging_normal)
						ModDamage.log.info("-" + world.getName() + ":" + (isOffensive?"Offensive":"Defensive") + ":" 
								+ material.name() + "(" + material.getId() + ") "
								+ calcStrings.toString());//debugging
					return true;
				}
			}
		}
		return false;
	}

	public boolean loadGroupHandlers(boolean force)
	{
		//get all of the groups in configuration
		List<String> groups = new ArrayList<String>();
		{
			groups.addAll((offensiveNode != null && offensiveNode.getKeys("groups") != null)?offensiveNode.getKeys("groups"):new ArrayList<String>());
			groups.addAll((defensiveNode != null && defensiveNode.getKeys("groups") != null)?defensiveNode.getKeys("groups"):new ArrayList<String>());
		}
		//load groups with offensive and defensive settings first
		if(!groups.isEmpty())
			for(String group : groups)
			{	
				if(groupHandlers.containsKey(group) && force)
				{
					groupHandlers.remove(group);
					if(ModDamage.consoleDebugging_normal)
						ModDamage.log.info("{Forcing reload of group " + group + "...}");
				}
				if(!groupHandlers.containsKey(group))
					groupHandlers.put(group, new GroupHandler(this, group, 
									((offensiveNode != null && offensiveNode.getNode("groups") != null)?offensiveNode.getNode("groups").getNode(group):null),
									((defensiveNode != null && defensiveNode.getNode("groups") != null)?defensiveNode.getNode("groups").getNode(group):null), 
									((scanNode != null && scanNode.getNode("groups") != null)?scanNode.getNode("groups").getNode(group):null), 
									damageCalc));
			}
		return true;
	}
	
///////////////////// MOBHEALTH ///////////////////////	
	public boolean loadMobHealth()
	{
		if(mobHealthNode != null) 
		{
			if(ModDamage.consoleDebugging_verbose) log.info("{Found MobHealth node for world \"" + world.getName() + "\"}");
			//load Mob health settings
			for(DamageElement mobType : DamageElement.values())
				if(mobType.getType().equals("mob") || mobType.getType().equals("animal"))
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
						if(ModDamage.consoleDebugging_normal) log.info("-" + world.getName() + ":MobHealth:" + mobType.getConfigReference() 
								+ (ModDamage.consoleDebugging_verbose?(" " + calcString.toString()):""));
					//check that this type of mob hasn't already been loaded
						if(!mobHealthSettings.containsKey(mobType))
							mobHealthSettings.put(mobType, calcString);
						else if(ModDamage.consoleDebugging_normal) log.warning("Repetitive " + mobType.getConfigReference() 
								+ " definition - ignoring");
					}
					else if(ModDamage.consoleDebugging_verbose)
						log.warning("No instructions found for " + mobType.getConfigReference() + " - is this on purpose?");
				}
			return true;
		}
		return false;
	}
	
	public boolean setHealth(Entity entity)
	{
		//determine creature type
		DamageElement creatureType = DamageElement.matchEntityType(entity);
		if(creatureType != null)
		{
			if(mobHealthSettings.containsKey(creatureType))
				((LivingEntity)entity).setHealth(healthCalc.parseCommand(mobHealthSettings.get(creatureType)));
			return true;
		}
		return false;
	}	

///////////////////// SCAN ///////////////////////
	private boolean loadScanItems() 
	{
		if(scanNode != null) 
		{
			if(ModDamage.consoleDebugging_verbose) log.info("{Found global Scan node for world \"" + world.getName() + "\"}");
			List<String> itemList = scanNode.getStringList("global", null);
			if(!itemList.equals(null))
			{
				for(Material material : Material.values())	
					if(itemList.contains(material.name()) || itemList.contains(Integer.toString(material.getId()))) //TODO Mess with casing here?
						if(!globalScanItems.contains(material)) 
						{
							globalScanItems.add(material);
							if(ModDamage.consoleDebugging_normal)
								ModDamage.log.info("-" + world.getName() + ":Scan:" 
									+ material.name() + "(" + material.getId() + ") ");
						}
				return true;
			}
		}
		return false;
	}

	public boolean canScan(Player player)
	{ 
		return (scanLoaded && canScan(player.getItemInHand().getType(), 
				ModDamage.Permissions.getGroup(player.getName(), player.getWorld().getName())));
	}
	public boolean canScan(Material itemType, String groupName)
	{ 
		if(groupName == null) groupName = "";
		return ((scanLoaded && globalScanItems.contains(itemType) 
				|| ((groupHandlers.get(groupName) != null)
						?groupHandlers.get(groupName).canScan(itemType)
						:false)));
	}
	
///////////////////// DAMAGE HANDLING ///////////////////////
	//TODO Possibly handle the event here, instead of the main plugin body?
//Player-targeted damage
//PvP
	public int calcAttackBuff(Player player_target, Player player_attacking, int eventDamage)
	{
		if(globalsLoaded)
		{
			String group_target = ModDamage.Permissions.getGroup(player_target.getWorld().getName(), player_target.getName());
			String group_attacking = ModDamage.Permissions.getGroup(player_target.getWorld().getName(), player_target.getName());
			//apply global buff settings
			return runGlobalRoutines(DamageElement.GENERIC_PLAYER, true, eventDamage)
				+ runGlobalRoutines(DamageElement.matchItemType(player_attacking.getItemInHand().getType()), true, eventDamage)
				+ runItemRoutines(player_attacking.getItemInHand().getType(), true, eventDamage)
			//apply group settings
				+ (groupHandlers.containsKey(group_attacking)
						?groupHandlers.get(group_attacking).calcAttackBuff(group_target, eventDamage)
								+ groupHandlers.get(group_attacking).calcAttackBuff(player_attacking.getItemInHand().getType(), eventDamage)
							:0);
		}
		return 0;
	}
	public int calcDefenseBuff(Player player_target, Player player_attacking, int eventDamage)
	{		
		if(globalsLoaded)
		{
			//get group strings
			String group_target = ModDamage.Permissions.getGroup(player_target.getWorld().getName(), player_target.getName());
			String group_attacking = ModDamage.Permissions.getGroup(player_attacking.getWorld().getName(), player_attacking.getName());
			//apply global buff settings
			return runGlobalRoutines(DamageElement.GENERIC_PLAYER, false, eventDamage)
				+ runGlobalRoutines(DamageElement.matchItemType(player_attacking.getItemInHand().getType()), false, eventDamage)
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
	public int calcAttackBuff(Player player_target, DamageElement damageType, int eventDamage)
	{
		//String group_target = plugin.Permissions.getGroup(player_target.getWorld().getName(), player_target.getName());
		if(globalsLoaded)
		{
			return runGlobalRoutines(damageType, true, eventDamage);
		}
		return 0;
	}
	public int calcDefenseBuff(Player player_target, DamageElement damageType, int eventDamage)
	{
		if(globalsLoaded)
		{
			String group_target = ModDamage.Permissions.getGroup(player_target.getWorld().getName(), player_target.getName());
			return runGlobalRoutines(damageType, false, eventDamage) 
				+ runGlobalRoutines(DamageElement.matchItemType(player_target.getItemInHand().getType()), false, eventDamage)
				+ runItemRoutines(player_target.getItemInHand().getType(), false, eventDamage)
				+ (groupHandlers.containsKey(group_target)?groupHandlers.get(group_target).calcDefenseBuff(damageType, eventDamage):0);
		}
		return 0;
	}
	//---
	
//Mob-targeted damage
//WORLD vs MOB
	public int calcAttackBuff(DamageElement mobType_target, DamageElement damageType, int eventDamage)
	{
		if(globalsLoaded)
		{
			//apply global buff settings
			return runGlobalRoutines(damageType, true, eventDamage);
		}
		return 0;
	}
	public int calcDefenseBuff(DamageElement mobType_target, DamageElement damageType, int eventDamage)
	{
		if(globalsLoaded)
		{
			return runGlobalRoutines(damageType, false, eventDamage);
		}
		return 0;
	}
	//---
//PvNP	
	public int calcAttackBuff(DamageElement mobType_target, Player player_attacking, int eventDamage)
	{
		if(globalsLoaded)
		{
			String group_attacking = ModDamage.Permissions.getGroup(player_attacking.getWorld().getName(), player_attacking.getName());
			return runGlobalRoutines(DamageElement.GENERIC_PLAYER, true, eventDamage) 
					+ runGlobalRoutines(DamageElement.matchItemType(player_attacking.getItemInHand().getType()), true, eventDamage)
					+ (groupHandlers.containsKey(group_attacking)
							?groupHandlers.get(group_attacking).calcAttackBuff(mobType_target, eventDamage)
							+ groupHandlers.get(group_attacking).calcAttackBuff(player_attacking.getItemInHand().getType(),eventDamage)
							:0)
					+ runItemRoutines(player_attacking.getItemInHand().getType(), true, eventDamage);
		}
		return 0;
	}
	public int calcDefenseBuff(DamageElement mobType_target, Player player_attacking, int eventDamage)
	{
		if(globalsLoaded)
		{
			//String group_attacking = ModDamage.Permissions.getGroup(player_attacking.getWorld().getName(), player_attacking.getName());
			return runGlobalRoutines(mobType_target, false, eventDamage);
		}
		return 0;
	}
	//---
	
//Routine handlers
	private int runGlobalRoutines(DamageElement damageType, boolean isOffensive, int eventDamage)
	{ return runGlobalRoutines(damageType, isOffensive, eventDamage, false);}
	private int runGlobalRoutines(DamageElement damageType, boolean isOffensive, int eventDamage, boolean printDebugging)
	{
		int result = 0;
		if(damageType != null && (isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageType))
		{
			for(String calcString : (isOffensive?offensiveRoutines:defensiveRoutines).get(damageType))
				result += damageCalc.parseCommand(calcString, eventDamage, isOffensive);
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
				result += damageCalc.parseCommand(calcString, eventDamage, isOffensive);
			if(printDebugging)
				log.info((isOffensive?"AttBuff: ":"DefBuff: ") + result);
		}
		return result;
	}
//----
	
///////////////////// HELPER FUNCTIONS ///////////////////////
	public World getWorld(){ return world;}
	
	private void clearRoutines() 
	{
		offensiveRoutines.clear();
		defensiveRoutines.clear();
		offensiveItemRoutines.clear();
		defensiveItemRoutines.clear();
		globalScanItems.clear();
		mobHealthSettings.clear();
	}

///////////////////// INGAME COMMANDS ///////////////////////
	public boolean sendWorldConfig(Player player, String configReference)
	{
		if(globalsLoaded)
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

	public boolean sendGroupConfig(Player player, String groupName, String configReference) 
	{
		if(groupHandlers.containsKey(groupName))
			return groupHandlers.get(groupName).sendGroupConfig(player, configReference);
		else return false;
	}
	

	public boolean group_isLoaded(String groupName){ return groupHandlers.containsKey(groupName);}
	public boolean attackType_isLoaded(DamageElement damageType){ return offensiveRoutines.containsKey(damageType);}
	public boolean defenseType_isLoaded(DamageElement damageType){ return defensiveRoutines.containsKey(damageType);}
}

	