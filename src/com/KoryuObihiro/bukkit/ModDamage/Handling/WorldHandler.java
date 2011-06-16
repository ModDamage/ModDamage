package com.KoryuObihiro.bukkit.ModDamage.Handling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;



public class WorldHandler
{
//// MEMBERS ////
	private ModDamage plugin;
	private Logger log; 
	private World world;
	
	public boolean globalsLoaded = false;
	public boolean groupsLoaded = false;
	public boolean scanLoaded = false;
	public boolean mobHealthLoaded = false;
	private List<String> configStrings = new ArrayList<String>();//TODO Implement this.
	private int configPages = 0;
	
	//nodes for config loading
	final private DamageCalculator damageCalc;
	final private HealthCalculator healthCalc;
	final private ConfigurationNode offensiveNode;
	final private ConfigurationNode defensiveNode;
	final private ConfigurationNode mobHealthNode;
	final private ConfigurationNode scanNode;
	
	//O/D routines
	final private HashMap<DamageElement, List<String>> offensiveRoutines = new HashMap<DamageElement, List<String>>();
	final private HashMap<DamageElement, List<String>> defensiveRoutines = new HashMap<DamageElement, List<String>>();
	final private HashMap<Material, List<String>> itemOffensiveRoutines = new HashMap<Material, List<String>>();
	final private HashMap<Material, List<String>> itemDefensiveRoutines = new HashMap<Material, List<String>>();
	final private HashMap<String, List<String>> armorOffensiveRoutines = new HashMap<String, List<String>>();
	final private HashMap<String, List<String>> armorDefensiveRoutines = new HashMap<String, List<String>>();
	//other MD config
	final private HashMap<DamageElement, String> mobHealthSettings = new HashMap<DamageElement, String>();
	final private List<Material> globalScanItems = new ArrayList<Material>();
	
	//Handlers
	public final HashMap<String, GroupHandler> groupHandlers = new HashMap<String, GroupHandler>();
	
	
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
		//clear everything first
		clear();
		
		//load Offensive configuration
		globalsLoaded = loadGlobalRoutines();

		//load Scan item configuration
		scanLoaded = loadScanItems();

		//load MobHealth configuration
		mobHealthLoaded = loadMobHealth();

		if(loadedSomething() && ModDamage.consoleDebugging_normal) 
			log.info("[" + plugin.getDescription().getName() + "] Configuration for world \"" 
				+ world.getName() + "\" initialized!");
		else if(ModDamage.consoleDebugging_verbose)
			log.warning("[" + plugin.getDescription().getName() + "] Configuration for world \"" 
				+ world.getName() + "\" could not load.");
		
		//load Defensive configuration
		groupsLoaded = loadGroupHandlers();

		configPages = configStrings.size()/9 + ((configStrings.size()%9 > 0)?1:0);
		
		return loadedSomething();
	}
	
	
//// CLASS-SPECIFIC FUNCTIONS ////

///////////////////// OFFENSIVE/DEFENSIVE ///////////////////////
	
// global
	private boolean loadGlobalRoutines()
	{
		String progressString = "UNKNOWN";
		boolean loadedSomething = false;
		try
		{
			ConfigurationNode offensiveGlobalNode = (offensiveNode != null)?offensiveNode.getNode("global"):null;
			ConfigurationNode defensiveGlobalNode = (defensiveNode != null)?defensiveNode.getNode("global"):null;
	//load "global" node routines
			if(offensiveGlobalNode != null)
			{
				progressString = "generic damage types in Offensive";
				if(loadGenericRoutines(true))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);
				
				progressString = "Offensive armor routines";
				if(loadArmorRoutines(true))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);
				
				progressString = "Offensive melee routines";
				if(loadMeleeRoutines(true))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);
			}
			if(defensiveGlobalNode != null)
			{
				progressString = "generic damage types in Defensive";
				if(loadGenericRoutines(false))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);

				progressString = "Defensive armor routines";
				if(loadArmorRoutines(false))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);

				progressString = "Defensive item routines";
				if(loadMeleeRoutines(false))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.severe("[" + plugin.getDescription().getName() 
					+ "] Invalid global configuration for world \"" + world.getName()
					+ "\" - failed while loading " + progressString + ".");
		}
		//loadedSomething = true;//TODO FIX ME
		return loadedSomething;
	}

	private boolean loadGenericRoutines(boolean isOffensive)
	{
		String configString;
		//get generics
		List<String>damageCategories = DamageElement.getGenericTypeStrings();
		ConfigurationNode genericNode = (isOffensive?offensiveNode:defensiveNode).getNode("global").getNode("generic");
		for(String damageCategory : damageCategories)
		{
			if(genericNode != null)
			{
				List<String> calcStrings = genericNode.getStringList(damageCategory, null);
				DamageElement element = DamageElement.matchDamageElement(damageCategory);
				if(calcStrings != null)
				{
					if(ModDamage.consoleDebugging_verbose) log.info("{Found global generic " + (isOffensive?"Offensive":"Defensive") + " " 
							+ damageCategory + " node for world \"" + world.getName() + "\"}");
					if(!calcStrings.equals(null)) //!calcStrings.equals(null)
					{
						damageCalc.checkCommandStrings(calcStrings, damageCategory, isOffensive);
						if(calcStrings.size() > 0)
						{
							if(!(isOffensive?offensiveRoutines:defensiveRoutines).containsKey(element))
							{
								(isOffensive?offensiveRoutines:defensiveRoutines).put(element, calcStrings);
								//add config string, print to console if debug is enabled
								configString = "-" + (isOffensive?"Offensive":"Defensive") + ":" + world.getName() + ":global:Generic:" 
									+ damageCategory + calcStrings.toString();
								configStrings.add(configString);
								if(ModDamage.consoleDebugging_normal) log.info(configString);
							}
							else if(ModDamage.consoleDebugging_normal)
							{
								log.warning("Repetitive generic "  + damageCategory + " node in " + (isOffensive?"Offensive":"Defensive") + " - ignoring");
								continue;
							}
						}
						else if(ModDamage.consoleDebugging_verbose)
							log.warning("No instructions found for generic " + damageCategory + " node - is this on purpose?");
					}
				}
				else if(ModDamage.consoleDebugging_verbose) log.info("Global generic " + element.getReference() 
						+ " node for" + (isOffensive?"Offensive":"Defensive") + " not found.");
			}
			//get specifics, if enum has been configured for it
			if(DamageElement.matchDamageElement(damageCategory).hasSubConfiguration())
			{
				ConfigurationNode relevantNode = ((isOffensive?offensiveNode:defensiveNode).getNode("global").getNode(damageCategory));
				
				if(relevantNode != null)
				{
					if(ModDamage.consoleDebugging_verbose) log.info("{Found global specific " + (isOffensive?"Offensive":"Defensive") + " " 
							+ damageCategory + " node for world \"" + world.getName() + "\"}");
					for(DamageElement damageElement : DamageElement.getElementsOf(damageCategory))
					{
						String elementReference = damageElement.getReference();
						//check for leaf-node buff strings
						List<String> calcStrings = relevantNode.getStringList(elementReference, null);
						if(!calcStrings.equals(null))
						{
							damageCalc.checkCommandStrings(calcStrings, elementReference, isOffensive);
							if(calcStrings.size() > 0)
							{
								if(!(isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageElement))
								{
									(isOffensive?offensiveRoutines:defensiveRoutines).put(damageElement, calcStrings);
									//add config string, print to console if debug is enabled
									configString = "-" + (isOffensive?"Offensive":"Defensive") + ":" +  world.getName() + ":global:" 
										+ damageCategory + ":" + elementReference + calcStrings.toString();
									configStrings.add(configString);
									if(ModDamage.consoleDebugging_normal) log.info(configString);
								}
								else if(ModDamage.consoleDebugging_normal)
								{
									log.warning("Repetitive " + elementReference + " specific node in " + (isOffensive?"Offensive":"Defensive") + " - ignoring");
									continue;
								}
							}
							else if(ModDamage.consoleDebugging_verbose)
								log.warning("No instructions found for " + elementReference + " node - is this on purpose?");
						}
						else if(ModDamage.consoleDebugging_verbose) log.info("Global " + damageElement.getReference() 
								+ " node for" + (isOffensive?"Offensive":"Defensive") + " not found.");
					}
				}
			}
		}
		return true;
	}

	private boolean loadMeleeRoutines(boolean isOffensive)
	{
		ConfigurationNode itemNode = (isOffensive?offensiveNode:defensiveNode).getNode("global").getNode(DamageElement.GENERIC_MELEE.getReference());
		if(itemNode != null)	
		{
			if(ModDamage.consoleDebugging_verbose) log.info("{Found global specific " + (isOffensive?"Offensive":"Defensive") + " " 
				+ "melee node for world \"" + world.getName() + "\"}");
			List<String> itemList = (isOffensive?offensiveNode:defensiveNode).getNode("global").getKeys(DamageElement.GENERIC_MELEE.getReference());
			List<String> calcStrings = null;
			if(!itemList.equals(null))
				for(String itemString : itemList)
				{
					Material material = Material.matchMaterial(itemString);
					if(material != null)
					{
						calcStrings = itemNode.getStringList(itemString, null);
						if(calcStrings != null)
						{
							damageCalc.checkCommandStrings(calcStrings, itemString, isOffensive);
							if(calcStrings.size() > 0)
							{
								if(!(isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).containsKey(material))
								{
									(isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).put(material, calcStrings);
									String configString = "-" + (isOffensive?"Offensive":"Defensive") + ":" +  world.getName() + ":global:melee:"
										+ material.name() + "(" + material.getId() + ") "+ calcStrings.toString();
									configStrings.add(configString);
									if(ModDamage.consoleDebugging_normal) log.info(configString);
								}
								else if(ModDamage.consoleDebugging_normal) log.warning("[" + plugin.getDescription().getName() + "] Repetitive " 
										+ material.name() + "(" + material.getId() + ") definition in " + (isOffensive?"Offensive":"Defensive") + " item globals - ignoring");
							}
							else if(!plugin.itemKeywords.containsKey(itemString) && ModDamage.consoleDebugging_verbose)
								log.warning("No instructions found for global " + material.name() + "(" + material.getId()
									+ ") item node in " + (isOffensive?"Offensive":"Defensive") + " - is this on purpose?");
							calcStrings = null;
						}
					}
					else if(ModDamage.consoleDebugging_verbose) log.warning("Unrecognized item name \"" + itemString + "\" found in specific melee node for world \"" 
							+ getWorld().getName() + "\" globals - ignoring");
				}
			return true;
		}
		return false;
	}

	private boolean loadArmorRoutines(boolean isOffensive)
	{
		ConfigurationNode armorNode = (isOffensive?offensiveNode:defensiveNode).getNode("global").getNode(DamageElement.GENERIC_ARMOR.getReference());
		if(armorNode != null)
		{
			if(ModDamage.consoleDebugging_verbose) log.info("{Found global specific " + (isOffensive?"Offensive":"Defensive") + " " 
				+ "armor node for world \"" + world.getName() + "\"}");
			List<String> armorSetList = (isOffensive?offensiveNode:defensiveNode).getNode("global").getKeys(DamageElement.GENERIC_ARMOR.getReference());
			List<String> calcStrings = null;
			for(String armorSetString : armorSetList)
			{
				ArmorSet armorSet = new ArmorSet(armorSetString);
				if(!armorSet.isEmpty())
				{
					calcStrings = armorNode.getStringList(armorSetString, null);
					if(!calcStrings.equals(null))
					{
						damageCalc.checkCommandStrings(calcStrings, armorSet.toString(), isOffensive);
						if(calcStrings.size() > 0)
						{
							
							if(!(isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).containsKey(armorSet))
							{
								//add config string, print to console if debug is enabled
								(isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).put(armorSet.toString(), calcStrings);
								String configString = "-" + world.getName() + ":" + (isOffensive?"Offensive":"Defensive") + ":armor:" 
										+ armorSet.toString() + " " + calcStrings.toString();
								configStrings.add(configString);
								if(ModDamage.consoleDebugging_normal) log.info(configString);
							}
							else if(ModDamage.consoleDebugging_normal) log.warning("[" + plugin.getDescription().getName() + "] Repetitive" 
									+ armorSet.toString() + " definition in " + (isOffensive?"Offensive":"Defensive") 
									+ " armor node - ignoring");
						}
						else if(ModDamage.consoleDebugging_verbose)
							log.warning("No instructions found for " + armorSet.toString() + " armor node in " 
									+ (isOffensive?"Offensive":"Defensive") + " - is this on purpose?");
					}
				}
				calcStrings = null;
			}
			return true;
		}
		return false;
	}

	private boolean loadGroupHandlers()
	{
		//get all of the groups in configuration
		List<String> groups = new ArrayList<String>();
		{
			groups.addAll((offensiveNode != null && offensiveNode.getKeys("groups") != null)?offensiveNode.getKeys("groups"):new ArrayList<String>());
			groups.addAll((defensiveNode != null && defensiveNode.getKeys("groups") != null)?defensiveNode.getKeys("groups"):new ArrayList<String>());
			groups.addAll((scanNode != null && scanNode.getKeys("groups") != null)?scanNode.getKeys("groups"):new ArrayList<String>());
		}
		//load groups with offensive and defensive settings first
		if(!groups.isEmpty())
		{
			configStrings.add(ChatColor.DARK_PURPLE + "Groups loaded:"); //TODO Customize colors later
			for(String group : groups)
			{	
				if(groupHandlers.containsKey(group))
				{
					if(ModDamage.consoleDebugging_normal)
						log.warning("Repetitive group definition found for group \"" + group + "\" found - ignoring.");
				}
				else 
				{
					groupHandlers.put(group, new GroupHandler(plugin, this, log, group,
							((offensiveNode != null && offensiveNode.getNode("groups") != null)?offensiveNode.getNode("groups").getNode(group):null),
							((defensiveNode != null && defensiveNode.getNode("groups") != null)?defensiveNode.getNode("groups").getNode(group):null), 
							((scanNode != null && scanNode.getNode("groups") != null)?scanNode.getNode("groups"):null), 
							damageCalc));
					configStrings.add(ChatColor.DARK_PURPLE + group);
				}
			}
		}
		return true;
	}
	
	
///////////////////// MOBHEALTH ///////////////////////	
	public boolean loadMobHealth()
	{
		if(mobHealthNode != null) 
		{
			if(ModDamage.consoleDebugging_verbose) log.info("{Found MobHealth node for world \"" + world.getName() + "\"}");
			List<DamageElement> creatureTypes = new ArrayList<DamageElement>();
			creatureTypes.addAll(DamageElement.getElementsOf("animal"));
			creatureTypes.addAll(DamageElement.getElementsOf("mob"));
			//load Mob health settings
			for(DamageElement creatureType : creatureTypes)
			{
			//check for leaf-node health strings
				String calcString = (String)mobHealthNode.getProperty(creatureType.getReference());
				if(calcString != null)
				{
					if(!healthCalc.checkCommandString(calcString))
						log.severe("Invalid command string \"" + calcString + "\" in MobHealth " + creatureType.getReference() 
								+ " definition - refer to config for proper calculation node");
				//display debug message to acknowledge that the settings have been validated
					else 
					{
						//check that this type of mob hasn't already been loaded
						if(!mobHealthSettings.containsKey(creatureType))
						{
							mobHealthSettings.put(creatureType, calcString);
							String configString = "-MobHealth:" + world.getName() + ":" + creatureType.getReference() 
								+ " [" + calcString.toString() + "]";
							configStrings.add(configString);
							if(ModDamage.consoleDebugging_normal) log.info(configString);
						}
						else if(ModDamage.consoleDebugging_normal) log.warning("Repetitive " + creatureType.getReference() 
								+ " definition - ignoring");
					}
				}
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("No instructions found for " + creatureType.getReference() + " - is this on purpose?");
			}
			return true;
		}
		return false;
	}
	
	public boolean setHealth(LivingEntity entity)
	{
		//determine creature type
		DamageElement creatureType = DamageElement.matchEntityElement(entity);
		if(creatureType != null)
		{
			if(mobHealthSettings.containsKey(creatureType))
				entity.setHealth(healthCalc.parseCommand(mobHealthSettings.get(creatureType)));
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
				for(String itemString : itemList)
				{
					if(plugin.itemKeywords.containsKey(itemString.toLowerCase()))
						for(Material material : plugin.itemKeywords.get(itemString.toLowerCase()))
						{
							globalScanItems.add(material);
							String configString = "-Scan:" + world.getName() + ":" + material.name() + "(" + material.getId() + ")";
							configStrings.add(configString);
							if(ModDamage.consoleDebugging_normal) log.info(configString);
						}
					else
					{
						Material material = Material.matchMaterial(itemString);
						if(material != null)
						{
							globalScanItems.add(material);
							String configString = "-Scan:" + world.getName() + ":" + material.name() + "(" + material.getId() + ") ";
							configStrings.add(configString);
							if(ModDamage.consoleDebugging_normal) log.info(configString);
						}
						else if(ModDamage.consoleDebugging_verbose) log.warning("Invalid Scan item \"" + itemString + "\" found in world \"" 
							+ world.getName() + "\" globals - ignoring");
					}
				}
			}
			return true;
		}
		return false;
	}

	public boolean canScan(Player player)
	{ 
		boolean groupCanScan = false;
		for(String group : ModDamage.Permissions.getGroups(player.getWorld().getName(), player.getName()))
			if(canScan(player.getItemInHand().getType(), group))
			{
				groupCanScan = true;
				break;
			}
		return (scanLoaded && groupCanScan);
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
//Player-targeted damage
//PvP
	public int calcAttackBuff(Player player_target, Player player_attacking, int eventDamage, DamageElement rangedElement)
	{
		ArmorSet armorSet_attacking = new ArmorSet(player_attacking);
		Material inHand_attacking = player_attacking.getItemInHand().getType();
		//calculate group buff
		String[] groups_target = null;
		String[] groups_attacking = null;
		int groupBuff = 0;
		try
		{
			groups_target = ModDamage.Permissions.getGroups(player_target.getWorld().getName(), player_target.getName());
			groups_attacking = ModDamage.Permissions.getGroups(player_attacking.getWorld().getName(), player_attacking.getName());
			for(String group_attacking : groups_attacking)
				if(groupHandlers.containsKey(group_attacking))
					for(String group_target : groups_target)
					{
						log.info("PvP (attackBuff): " + group_attacking + " vs. " + group_target);
						groupBuff += groupHandlers.get(group_attacking).calcAttackBuff(group_target, inHand_attacking, armorSet_attacking, eventDamage, rangedElement);
					}
		}
		catch(Exception e)
		{
			if(groups_target == null)
				log.warning("[" + plugin.getDescription().getName() + "] No groups found for player \"" 
						+ player_target.getName() + "\" in world \"" + world.getName() + "\" - add this player to a group in Permissions!");
			else if(groups_attacking == null)
				log.warning("[" + plugin.getDescription().getName() + "] No groups found for player \"" 
						+ player_attacking.getName() + "\" in world \"" + world.getName() + "\" - add this player to a group in Permissions!");
		}
	
		//apply calculations
		return runGlobalRoutines(DamageElement.GENERIC_HUMAN, true, eventDamage)
				+ ((rangedElement != null)
					?(runGlobalRoutines(DamageElement.GENERIC_RANGED, true, eventDamage) 
						+ runGlobalRoutines(rangedElement, true, eventDamage))
					:(runGlobalRoutines(DamageElement.matchMeleeElement(inHand_attacking), true, eventDamage) 
						+ runMeleeRoutines(inHand_attacking, true, eventDamage)))
				+ runArmorRoutines(armorSet_attacking, true, eventDamage)
				+ groupBuff;
	}
	public int calcDefenseBuff(Player player_target, Player player_attacking, int eventDamage, DamageElement rangedElement)
	{		
		ArmorSet armorSet_target = new ArmorSet(player_target);
		Material inHand_attacking = player_attacking.getItemInHand().getType();
		//calculate group buff
		int groupBuff = 0;
		String[] groups_target = null;
		String[] groups_attacking = null;
		try
		{
			groups_target = ModDamage.Permissions.getGroups(player_target.getWorld().getName(), player_target.getName());
			groups_attacking = ModDamage.Permissions.getGroups(player_attacking.getWorld().getName(), player_attacking.getName());
			for(String group_target : groups_target)
				if(groupHandlers.containsKey(group_target))
					for(String group_attacking : groups_attacking)
						groupBuff += groupHandlers.get(group_target).calcDefenseBuff(group_attacking, inHand_attacking, armorSet_target, eventDamage, rangedElement);
		}
		catch(Exception e)
		{
			if(groups_target == null)
				log.warning("[" + plugin.getDescription().getName() + "] No groups found for player \"" 
						+ player_target.getName() + "\" in world \"" + world.getName() + "\" - add this player to a group in Permissions!");
			else if(groups_attacking == null)
				log.warning("[" + plugin.getDescription().getName() + "] No groups found for player \"" 
						+ player_attacking.getName() + "\" in world \"" + world.getName() + "\" - add this player to a group in Permissions!");
		}
		//apply calculations
		return runGlobalRoutines(DamageElement.GENERIC_HUMAN, false, eventDamage)
				+ ((rangedElement != null)
					?(runGlobalRoutines(DamageElement.GENERIC_RANGED, false, eventDamage) 
						+ runGlobalRoutines(rangedElement, false, eventDamage))
					:(runGlobalRoutines(DamageElement.matchMeleeElement(inHand_attacking), false, eventDamage) 
						+ runMeleeRoutines(inHand_attacking, false, eventDamage)))
				+ runArmorRoutines(armorSet_target, false, eventDamage)
				+ groupBuff;
	}
	//---
	
//handle NPvP
	public int calcAttackBuff(Player player_target, DamageElement damageType, int eventDamage)
	{
		return runGlobalRoutines(damageType.getType(), true, eventDamage)
				+ runGlobalRoutines(damageType, true, eventDamage);
	}
	public int calcDefenseBuff(Player player_target, DamageElement damageType, int eventDamage)
	{
		ArmorSet armorSet_target = new ArmorSet(player_target);
		//calculate group buff
		

		int groupBuff = 0;
		try
		{
			String[] groups_target = ModDamage.Permissions.getGroups(player_target.getWorld().getName(), player_target.getName());
			for(String group_target : groups_target)
				if(groupHandlers.containsKey(group_target))
					groupBuff += groupHandlers.get(group_target).calcDefenseBuff(damageType, armorSet_target, eventDamage);
		}
		catch(Exception e)
		{
			log.warning("[" + plugin.getDescription().getName() + "] No groups found for player \"" 
					+ player_target.getName() + "\" in world \"" + world.getName() + "\" - add this player to a group in Permissions!");
		}
		//apply calculations
		return runGlobalRoutines(DamageElement.GENERIC_HUMAN, false, eventDamage)
				+ runArmorRoutines(armorSet_target, false, eventDamage)
				+ groupBuff;
	}
	//---
	
//Mob-targeted damage
//WORLD vs MOB
	public int calcAttackBuff(DamageElement mobType_target, DamageElement damageType, int eventDamage)
	{
		//apply calculations
		return runGlobalRoutines(damageType.getType(), true, eventDamage)
				+ runGlobalRoutines(damageType, true, eventDamage);
	}
	public int calcDefenseBuff(DamageElement mobType_target, DamageElement damageType, int eventDamage)
	{
		//apply calculations
		return runGlobalRoutines(mobType_target.getType(), false, eventDamage)
				+ runGlobalRoutines(mobType_target, false, eventDamage);
	}
	//---
//PvNP	
	public int calcAttackBuff(DamageElement mobType_target, Player player_attacking, int eventDamage, DamageElement rangedElement)
	{
		ArmorSet armorSet_attacking = new ArmorSet(player_attacking);
		Material inHand_attacking = player_attacking.getItemInHand().getType();
		//calculate group buff
		int groupBuff = 0;
		try
		{
			String[] groups_attacking = ModDamage.Permissions.getGroups(player_attacking.getWorld().getName(), player_attacking.getName());
			for(String group_attacking : groups_attacking)
				if(groupHandlers.containsKey(group_attacking))
					groupBuff += groupHandlers.get(group_attacking).calcAttackBuff(mobType_target, inHand_attacking, armorSet_attacking, eventDamage, rangedElement);
		}
		catch(Exception e)
		{
			log.warning("[" + plugin.getDescription().getName() + "] No groups found for player \"" 
					+ player_attacking.getName() + "\" in world \"" + world.getName() + "\" - add this player to a group in Permissions!");
		}
		//apply calculations
		return runGlobalRoutines(DamageElement.GENERIC_HUMAN, true, eventDamage) 
				+ ((rangedElement != null)
					?(runGlobalRoutines(DamageElement.GENERIC_RANGED, true, eventDamage) 
						+ runGlobalRoutines(rangedElement, true, eventDamage))
					:(runGlobalRoutines(DamageElement.matchMeleeElement(inHand_attacking), true, eventDamage) 
						+ runMeleeRoutines(inHand_attacking, true, eventDamage)))
				+ runArmorRoutines(armorSet_attacking, true, eventDamage)
				+ groupBuff;
	}
	public int calcDefenseBuff(DamageElement mobType_target, Player player_attacking, int eventDamage, DamageElement rangedElement)
	{
		if(globalsLoaded)
			//apply calculations
			return runGlobalRoutines(mobType_target.getType(), false, eventDamage) 
					+ runGlobalRoutines(mobType_target, false, eventDamage);
		return 0;
	}
	//---
	
//Routine handlers
	private int runGlobalRoutines(DamageElement damageType, boolean isOffensive, int eventDamage)
	{ return runGlobalRoutines(damageType, isOffensive, eventDamage, false);}
	private int runGlobalRoutines(DamageElement damageType, boolean isOffensive, int eventDamage, boolean printDebugging)
	{
		if(damageType != null && (isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageType))
			return damageCalc.parseCommands((isOffensive?offensiveRoutines:defensiveRoutines).get(damageType), eventDamage, isOffensive);
		return 0;
	}
	
	private int runMeleeRoutines(Material materialType, boolean isOffensive, int eventDamage)
	{
		if(materialType != null && (isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).containsKey(materialType))
			return damageCalc.parseCommands((isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).get(materialType), eventDamage, isOffensive);
		return 0;
	}
	
	private int runArmorRoutines(ArmorSet armorSet, boolean isOffensive, int eventDamage)
	{
		if(!armorSet.isEmpty() && (isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).containsKey(armorSet.toString()))
			return damageCalc.parseCommands((isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).get(armorSet.toString()), eventDamage, isOffensive);
		return 0;
	}
	
//----
	
///////////////////// HELPER FUNCTIONS ///////////////////////
	public World getWorld(){ return world;}
///////////////////// INGAME COMMANDS ///////////////////////
	public boolean sendWorldConfig(Player player, int pageNumber)
	{
		//10 lines per page (including header)
		if(player == null)
		{
			if(configStrings.isEmpty())
			{
				log.severe("Well, frick...this shouldn't have happened. o_o"); //TODO REMOVE ME EVENTUALLY
				return false;
			}
			String printString = "Config for world \"" + world.getName() + "\":";
			for(String configString : configStrings)
				printString += "\n" + configString;
			log.info(printString);
			for(GroupHandler groupHandler : groupHandlers.values())
			{
				groupHandler.sendGroupConfig(player, pageNumber);
			}
			return true;
		}
		else if(configPages > 0 && configPages >= pageNumber && pageNumber > 0)
		{
			player.sendMessage(plugin.ModDamageString(ChatColor.GOLD) + " World \"" + world.getName().toUpperCase() 
					+ "\" (" + pageNumber + "/" + configPages + ")");
			for(int i = (9 * (pageNumber - 1)); i < (configStrings.size() < (9 * pageNumber)
														?configStrings.size()
														:(9 * pageNumber)); i++)
				player.sendMessage(ChatColor.DARK_AQUA + configStrings.get(i));
			return true;
		}
		return false;
	}
	
	public boolean loadedSomething()
	{
		//TODO Mebbe make the cleanup its own function?
		boolean groupsLoadedSomething = false;
		for(GroupHandler groupHandler : groupHandlers.values())
		{
			if(groupHandler.loadedSomething())
				groupsLoadedSomething = true;
			else groupHandlers.remove(groupHandler.getGroupName());
		}
		return (!offensiveRoutines.isEmpty() || !defensiveRoutines.isEmpty()
				|| !itemOffensiveRoutines.isEmpty() || !itemDefensiveRoutines.isEmpty()
				|| !globalScanItems.isEmpty() || !mobHealthSettings.isEmpty()
				|| groupsLoadedSomething);
	}
	
	public boolean add()
	{
		//TODO
		return false;
	}
	
	public boolean remove()
	{
		//TODO
		return false;
	}
	
	public void clear()
	{
		offensiveRoutines.clear();
		defensiveRoutines.clear();
		itemOffensiveRoutines.clear();
		itemDefensiveRoutines.clear();
		globalScanItems.clear();
		mobHealthSettings.clear();
		groupHandlers.clear();
		configStrings.clear();
	}
}

	