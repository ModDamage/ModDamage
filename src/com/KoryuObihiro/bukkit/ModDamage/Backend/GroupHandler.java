package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculationAllocator;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;



public class GroupHandler
{
//// MEMBERS //// 
	final private String groupName;	
	
	private ModDamage plugin;
	private Logger log;
	public boolean isLoaded = false;
	public boolean scanLoaded = false;
	private List<String> configStrings = new ArrayList<String>();
	private int configPages = 0;

	//nodes for config loading
	final private ConfigurationNode offensiveNode;
	final private ConfigurationNode defensiveNode;
	final private ConfigurationNode scanNode;
	final public DamageCalculationAllocator damageAllocator;
	final public WorldHandler worldHandler;
	
	//O/D config
	final private HashMap<DamageElement, List<DamageCalculation>> offensiveRoutines = new HashMap<DamageElement, List<DamageCalculation>>();
	final private HashMap<DamageElement, List<DamageCalculation>> defensiveRoutines = new HashMap<DamageElement, List<DamageCalculation>>();
	final private HashMap<Material, List<DamageCalculation>> meleeOffensiveRoutines = new HashMap<Material, List<DamageCalculation>>();
	final private HashMap<Material, List<DamageCalculation>> meleeDefensiveRoutines = new HashMap<Material, List<DamageCalculation>>();
	final private HashMap<String, List<DamageCalculation>> armorOffensiveRoutines = new HashMap<String, List<DamageCalculation>>();
	final private HashMap<String, List<DamageCalculation>> armorDefensiveRoutines = new HashMap<String, List<DamageCalculation>>();
	final private HashMap<String, List<DamageCalculation>> pvpOffensiveRoutines = new HashMap<String, List<DamageCalculation>>();
	final private HashMap<String, List<DamageCalculation>> pvpDefensiveRoutines = new HashMap<String, List<DamageCalculation>>();
	//other MD config
	final private List<Material> groupScanItems = new ArrayList<Material>();
	
	
//// CONSTRUCTOR ////
	public GroupHandler(ModDamage plugin, WorldHandler worldHandler, Logger log,  String name, ConfigurationNode offensiveGroupNode, ConfigurationNode defensiveGroupNode, ConfigurationNode scanGroupNode, DamageCalculationAllocator damageCalc) 
	{
		this.plugin = plugin;
		this.log = log;
		this.worldHandler = worldHandler;
		this.groupName = name;
		this.offensiveNode = offensiveGroupNode;
		this.defensiveNode = defensiveGroupNode;
		this.scanNode = scanGroupNode;
		this.damageAllocator = damageCalc;
		
		reload();
	}

////CONFIG LOADING ////
	public void reload()
	{ 
		this.clear();
		
		isLoaded = loadDamageRoutines();
		scanLoaded = loadScanItems();

		configPages = configStrings.size()/9 + ((configStrings.size()%9 > 0)?1:0);
		
		if(loadedSomething()) 
			log.info("[" + plugin.getDescription().getName() + "] Group \"" + groupName + "\" configuration for world \"" 
				+ worldHandler.getWorld().getName() + "\" initialized!");
		else if(ModDamage.consoleDebugging_verbose)
			log.warning("[" + plugin.getDescription().getName() + "] Group \"" + groupName + "\" configuration for world \"" 
				+ worldHandler.getWorld().getName() + "\" could not load.");
	}
	
///////////////////// OFFENSIVE/DEFENSIVE ///////////////////////
	private boolean loadDamageRoutines() 
	{
		String progressString = "UNKNOWN";
		try
		{
			//clear everything first
			clear();
			
			boolean loadedSomething = false;
			if(offensiveNode != null)
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
				
				progressString = "Offensive item routines";
				if(loadMeleeRoutines(true))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);
				
				progressString = "Offensive PVP routines";
				if(loadPVPRoutines(true, true))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);
			}
			if(defensiveNode != null)
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
				
				progressString = "Defensive PVP routines";
				if(loadPVPRoutines(false, true))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);
			}
			return loadedSomething;
		}
		catch(Exception e)
		{
			log.severe("[" + plugin.getDescription().getName() 
					+ "] Invalid configuration for group " + groupName + ", world \"" 
					+ worldHandler.getWorld().getName() + "\" - failed to load" + progressString + ".");
			return false;
		}
	}

	public boolean loadGenericRoutines(boolean isOffensive)
	{
		boolean loadedSomething = false;
		List<String>damageCategories = DamageElement.getGenericTypeStrings();
		ConfigurationNode genericNode = (isOffensive?offensiveNode:defensiveNode).getNode("generic");
		for(String damageCategory : damageCategories)
		{
			if(genericNode != null)
			{
				List<String> calcStrings = genericNode.getStringList(damageCategory, null);
				DamageElement element = DamageElement.matchDamageElement(damageCategory);
				if(calcStrings != null)
				{
					if(ModDamage.consoleDebugging_verbose) log.info("{Found group \"" + groupName + "\" generic " 
							+ (isOffensive?"Offensive":"Defensive") + " " + damageCategory + " node for world \"" 
							+ worldHandler.getWorld().getName() + "\"}");
					if(!calcStrings.equals(null)) //!calcStrings.equals(null)
					{
						List<DamageCalculation> damageCalculations = damageAllocator.parseStrings(calcStrings);
						if(damageCalculations != null)
						{
							if(!(isOffensive?offensiveRoutines:defensiveRoutines).containsKey(element))
							{
								(isOffensive?offensiveRoutines:defensiveRoutines).put(element, damageCalculations);
								String configString = "-" + (isOffensive?"Offensive":"Defensive") + ":" + worldHandler.getWorld().getName() 
									+ ":groups:" + groupName + ":Generic:" + damageCategory + calcStrings.toString();
								configStrings.add(configString);
								if(ModDamage.consoleDebugging_normal) log.info(configString);
								loadedSomething = true;
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
				else if(ModDamage.consoleDebugging_verbose) log.info("Group \"" + groupName + "\" generic " + element.getReference() 
						+ " node for" + (isOffensive?"Offensive":"Defensive") + " not found.");
			}
			if(DamageElement.matchDamageElement(damageCategory).hasSubConfiguration())
			{
				ConfigurationNode relevantNode = ((isOffensive?offensiveNode:defensiveNode).getNode(damageCategory));
				if(relevantNode != null)
				{
					if(ModDamage.consoleDebugging_verbose) log.info("{Found group \"" + groupName + "\" specific " + (isOffensive?"Offensive":"Defensive") + " " 
							+ damageCategory + " node for world \"" + worldHandler.getWorld().getName() + "\"}");
					for(DamageElement damageElement : DamageElement.getElementsOf(damageCategory))
					{
						String elementReference = damageElement.getReference();
						//check for leaf-node buff strings
						List<String> calcStrings = relevantNode.getStringList(elementReference, null);
						if(!calcStrings.equals(null)) //!calcStrings.equals(null)
						{
							List<DamageCalculation> damageCalculations = damageAllocator.parseStrings(calcStrings);
							if(damageCalculations != null)
							{
								if(!(isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageElement))
								{
									(isOffensive?offensiveRoutines:defensiveRoutines).put(damageElement, damageCalculations);
									String configString = "-" + (isOffensive?"Offensive":"Defensive") + ":" + worldHandler.getWorld().getName() 
										+ ":groups:" + groupName + ":" + damageCategory + ":" + elementReference + calcStrings.toString();
									configStrings.add(configString);
									if(ModDamage.consoleDebugging_normal) log.info(configString);
									loadedSomething = true;
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
						else if(ModDamage.consoleDebugging_verbose) log.info("Group \"" + groupName 
								+ "\" " + damageElement.getReference() + " node for" + (isOffensive?"Offensive":"Defensive") 
								+ " not found.");
					}
				}
			}
		}
		return loadedSomething;
	}
	
	public boolean loadMeleeRoutines(boolean isOffensive)
	{
		boolean loadedSomething = false;
		ConfigurationNode meleeNode = (isOffensive?offensiveNode:defensiveNode).getNode(DamageElement.GENERIC_MELEE.getReference());
		if(meleeNode != null)	
		{
			if(ModDamage.consoleDebugging_verbose) log.info("{Found group specific " + (isOffensive?"Offensive":"Defensive") + " " 
					+ "melee node for group \"" + groupName + "\" in world \"" + worldHandler.getWorld().getName() + "\"}");
			List<String> itemList = (isOffensive?offensiveNode:defensiveNode).getKeys(DamageElement.GENERIC_MELEE.getReference());
			List<String> calcStrings = null;
			if(!itemList.equals(null))
				for(String itemString : itemList)
				{
					Material material = Material.matchMaterial(itemString);
					if(material != null)
					{
						calcStrings = meleeNode.getStringList(itemString, null);
						if(calcStrings != null)
						{
							List<DamageCalculation> damageCalculations = damageAllocator.parseStrings(calcStrings);
							if(damageCalculations != null)
							{
								if(!(isOffensive?meleeOffensiveRoutines:meleeDefensiveRoutines).containsKey(material))
								{
									(isOffensive?meleeOffensiveRoutines:meleeDefensiveRoutines).put(material, damageCalculations);
									String configString = "-" + (isOffensive?"Offensive":"Defensive") 
									+ worldHandler.getWorld().getName() + ":groups:" + groupName + ":" + ":"
									+ material.name() + "(" + material.getId() + ")" + calcStrings.toString();
									configStrings.add(configString);
									if(ModDamage.consoleDebugging_normal) log.info(configString);
									loadedSomething = true;
								}
								else if(ModDamage.consoleDebugging_normal) 
									log.warning("[" + plugin.getDescription().getName() + "] Repetitive " 
										+ material.name() + "(" + material.getId() + ") definition in group " + groupName 
										+ " " + (isOffensive?"Offensive":"Defensive") + " item settings - ignoring");
							}
							else if(ModDamage.consoleDebugging_verbose)
								log.warning("No instructions found for group \"" + groupName + "\" " + material.name() 
									+ "(" + material.getId()+ ") item node in " + (isOffensive?"Offensive":"Defensive") 
									+ " - is this on purpose?");
							calcStrings = null;
						}
					}
					else if(!plugin.itemKeywords.containsKey(itemString) && ModDamage.consoleDebugging_verbose)
							log.warning("Unrecognized item name \"" + itemString + "\" found in specific melee node for group \"" 
								+ groupName + "\" in world \"" + worldHandler.getWorld().getName() + "\" - ignoring");
				}
		}
		return loadedSomething;
	}
	
	public boolean loadArmorRoutines(boolean isOffensive)
	{
		boolean loadedSomething = false;
		ConfigurationNode armorNode = (isOffensive?offensiveNode:defensiveNode).getNode(DamageElement.GENERIC_ARMOR.getReference());
		if(armorNode != null)
		{
			if(ModDamage.consoleDebugging_verbose) log.info("{Found group specific " + (isOffensive?"Offensive":"Defensive") + " " 
					+ "armor node for group \"" + groupName + "\" in world \"" + worldHandler.getWorld().getName() + "\"}");
			List<String> armorSetList = (isOffensive?offensiveNode:defensiveNode).getKeys(DamageElement.GENERIC_ARMOR.getReference());
			List<String> calcStrings = null;
			for(String armorSetString : armorSetList)
			{
				ArmorSet armorSet = new ArmorSet(armorSetString);
				if(!armorSet.isEmpty())
				{
					calcStrings = armorNode.getStringList(armorSetString, null);
					if(!calcStrings.equals(null))
					{
						List<DamageCalculation> damageCalculations = damageAllocator.parseStrings(calcStrings);
						if(damageCalculations != null)
						{
							if(!(isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).containsKey(armorSet))
							{
								(isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).put(armorSet.toString(), damageCalculations);
								String configString = "-" + (isOffensive?"Offensive":"Defensive") + ":" + worldHandler.getWorld().getName() 
									+ ":groups:" + groupName + ":armor:" + armorSet.toString() + " " + calcStrings.toString();
								configStrings.add(configString);
								if(ModDamage.consoleDebugging_normal) log.info(configString);
								loadedSomething = true;
							}
							else if(ModDamage.consoleDebugging_normal) log.warning("[" + plugin.getDescription().getName() + "] Repetitive" 
									+ armorSet.toString() + "definition in " + (isOffensive?"Offensive":"Defensive") 
									+ " armor set for group \"" + groupName + "\"'s settings - ignoring");
						}
						else if(ModDamage.consoleDebugging_verbose)
							log.warning("No instructions found for group" + groupName + "\n\t:" 
									+ armorSet.toString() + "\narmor node in " + (isOffensive?"Offensive":"Defensive") 
									+ " - is this on purpose?");
					}
				}
				calcStrings = null;
			}
		}
		return loadedSomething;
	}
	
	public boolean loadPVPRoutines(boolean isOffensive, boolean force)
	{
		boolean loadedSomething = false;
		//get all of the groups in configuration
		List<String> groups = (isOffensive?offensiveNode:defensiveNode).getKeys("groups");
		//load groups with offensive and defensive settings first
		if(groups != null)
			for(String group : groups)
			{
				List<String> calcStrings = (isOffensive?offensiveNode:defensiveNode).getNode("groups").getStringList(group, null);
				if(!calcStrings.equals(null))
					{
						List<DamageCalculation> damageCalculations = damageAllocator.parseStrings(calcStrings);
						if(damageCalculations != null)
						{
							if(!(isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).containsKey(group))
							{
								(isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).put(group, damageCalculations);
		
								String configString = "-" + (isOffensive?"Offensive":"Defensive") + ":" + worldHandler.getWorld().getName() 
										+ ":groups:" + groupName + ":" + ":groups:" + group + " " + calcStrings.toString();
								configStrings.add(configString);
								if(ModDamage.consoleDebugging_normal) log.info(configString);
								loadedSomething = true;
							}
							else if(ModDamage.consoleDebugging_normal) 
								log.warning("Repetitive " + group + " definition in " 
										+ (isOffensive?"Offensive":"Defensive") + " settings for group " + groupName + " - ignoring");
						}
						else if(ModDamage.consoleDebugging_verbose)
						{		
							log.warning("No instructions found for group " + groupName + " " + group 
								+ " PvP node in " + (isOffensive?"Offensive":"Defensive") + " for world " 
								+ worldHandler.getWorld().getName() +  " - is this on purpose?");
						}
					}
			}
		return loadedSomething;
	}
	
///////////////////// SCAN ///////////////////////	
	private boolean loadScanItems() 
	{
		boolean loadedSomething = false;
		if(scanNode != null) 
		{
			if(ModDamage.consoleDebugging_verbose) log.info("{Found group \"" + groupName 
				+ "\" Scan node for world \"" + worldHandler.getWorld().getName() + "\"}");
			List<String> itemList = scanNode.getStringList(groupName, null);
			if(!itemList.equals(null))
			{
				if(!itemList.equals(null))
					for(String itemString : itemList)
					{
						if(plugin.itemKeywords.containsKey(itemString.toLowerCase()))
							for(Material material : plugin.itemKeywords.get(itemString.toLowerCase()))
							{
								groupScanItems.add(material);
								String configString = "-Scan:" + worldHandler.getWorld().getName() + ":" + material.name() + "(" + material.getId() + ")";
								configStrings.add(configString);
								if(ModDamage.consoleDebugging_normal) log.info(configString);
								loadedSomething = true;
							}
						else
						{
							Material material = Material.matchMaterial(itemString);
							if(material != null)
							{
								groupScanItems.add(material);
								String configString = "-Scan:" + worldHandler.getWorld().getName() + ":" + material.name() + "(" + material.getId() + ") ";
								configStrings.add(configString);
								if(ModDamage.consoleDebugging_normal) log.info(configString);
								loadedSomething = true;
							}
							else if(ModDamage.consoleDebugging_verbose) log.warning("Invalid Scan item \"" + itemString + "\" found for group \"" 
								+ groupName + "\" in world \"" + worldHandler.getWorld().getName() + "\" - ignoring");
						}
					}
			}
		}
		return loadedSomething;
	}
	
	public boolean canScan(Material itemType){ return(groupScanItems.contains(itemType));}
	
///////////////////// DAMAGE HANDLING ///////////////////////	
	public int calcAttackBuff(DamageElement mobType_target, Material inHand, ArmorSet armorSet_attacking, int eventDamage, DamageElement rangedMaterial)
	{
		int result = eventDamage;
		result += runRoutines(mobType_target.getType(), true, eventDamage);
		result += runRoutines(null, null, mobType_target, true, eventDamage);
		result += ((rangedMaterial != null)
						?(runRoutines(DamageElement.GENERIC_RANGED, true, eventDamage) 
							+ runRoutines(rangedMaterial, true, eventDamage))
						:(runRoutines(DamageElement.matchMeleeElement(inHand), true, eventDamage) 
							+ runMeleeRoutines(inHand, true, eventDamage)));
		result += runArmorRoutines(armorSet_attacking, true, eventDamage);
		

		result += runRoutines(damageType.getType(), false, eventDamage)
 		result += runRoutines(damageType, false, eventDamage)
		result += runArmorRoutines(armorSet_attacked, false, eventDamage)
		
		return result;
	}
	
	public int calcAttackBuff(String group_target, Material inHand, ArmorSet armorSet_attacking, int eventDamage, DamageElement rangedMaterial)
	{
		return runRoutines(DamageElement.GENERIC_HUMAN, true, eventDamage) 
				+ ((rangedMaterial != null)
					?(runRoutines(DamageElement.GENERIC_RANGED, true, eventDamage) 
						+ runRoutines(rangedMaterial, true, eventDamage))
					:(runRoutines(DamageElement.matchMeleeElement(inHand), true, eventDamage) 
						+ runMeleeRoutines(inHand, true, eventDamage)))
				+ runArmorRoutines(armorSet_attacking, true, eventDamage)
				+ runPVPRoutines(group_target, true, eventDamage);
	}
	public int calcDefenseBuff(String group_attacking, Material inHand, ArmorSet armorSet_attacked, int eventDamage, DamageElement rangedMaterial)
	{	
		return runRoutines(DamageElement.GENERIC_HUMAN, false, eventDamage)
				+ ((rangedMaterial != null)
					?(runRoutines(DamageElement.GENERIC_RANGED, false, eventDamage) 
						+ runRoutines(rangedMaterial, false, eventDamage))
					:(runRoutines(DamageElement.matchMeleeElement(inHand), true, eventDamage) 
						+ runMeleeRoutines(inHand, false, eventDamage)))
				+ runArmorRoutines(armorSet_attacked, false, eventDamage)
				+ runPVPRoutines(group_attacking, false, eventDamage);
	}
	
	public void doAttackCalculations(EventInfo eventInfo) 
	{
		switch(eventInfo.eventType)
		{
///////////////////// Player vs. Player 
			case PLAYER_PLAYER:
				runRoutines(eventInfo, true);
				
				runRoutines(eventInfo, false);				
			return;

///////////////////// Player vs. Mob
			case PLAYER_MOB:
				runRoutines(eventInfo, true);//attack buff
				runPlayerRoutines(eventInfo, true);
				
				runRoutines(eventInfo, false);//defense buff
			return;
				
///////////////////// Mob vs. Player
			case MOB_PLAYER:
				runRoutines(eventInfo, true);//attack buff
				
				runRoutines(eventInfo, false);//defense buff
				runPlayerRoutines(eventInfo, false);
			return;
			
///////////////////// Nonliving vs. Player
			case NONLIVING_PLAYER:
				runRoutines(eventInfo, true);//attack buff
				
				runRoutines(eventInfo, false);//defense buff
				runPlayerRoutines(eventInfo, false);
			return;

///////////////////// Nonliving vs. Mob
			case NONLIVING_MOB:
				runRoutines(eventInfo, true);//attack buff
				runRoutines(eventInfo, false);//defense buff
			return;
			
			default: return;
		}
	}
	
	private int runRoutines(LivingEntity target, LivingEntity attacker, DamageElement damageType, boolean isOffensive, int eventDamage)
	{
		if(damageType != null && (isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageType))
			return this.calculateDamage(target, attacker, (isOffensive?offensiveRoutines:defensiveRoutines).get(damageType), eventDamage, isOffensive);
		return 0;
	}
	
	private int runPVPRoutines(LivingEntity target, LivingEntity attacker, String groupName, boolean isOffensive, int eventDamage)
	{
		if(groupName != null && (isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).containsKey(groupName))
			return this.calculateDamage(target, attacker, (isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).get(groupName), eventDamage, isOffensive);
		return 0;
	}
	
	private int runMeleeRoutines(LivingEntity target, LivingEntity attacker, Material material, boolean isOffensive, int eventDamage) 
	{
		if(material != null && (isOffensive?meleeOffensiveRoutines:meleeDefensiveRoutines).containsKey(material))
			return this.calculateDamage(target, attacker, (isOffensive?meleeOffensiveRoutines:meleeDefensiveRoutines).get(material), eventDamage, isOffensive);
		return 0;
	}
	
	private int runArmorRoutines(LivingEntity target, LivingEntity attacker, ArmorSet armorSet, boolean isOffensive, int eventDamage)
	{
		if(!armorSet.isEmpty() && (isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).containsKey(armorSet.toString()))
			return this.calculateDamage(target, attacker, (isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).get(armorSet.toString()), eventDamage, isOffensive);
		return 0;
	}
	
	private int calculateDamage(LivingEntity target, LivingEntity attacker, List<DamageCalculation> damageCalculations, int eventDamage, boolean isOffensive) 
	{
		int result = eventDamage;
		int defenseModifier = (isOffensive?1:-1);
		for(DamageCalculation damageCalculation : damageCalculations)
			result = damageCalculation.calculate(attacker, result);
		return (result - eventDamage) * defenseModifier;
	}

///////////////////// HELPER FUNCTIONS ///////////////////////	
	public String getGroupName(){ return groupName;}

	private void clear() 
	{
		offensiveRoutines.clear();
		defensiveRoutines.clear();
		pvpOffensiveRoutines.clear();
		pvpDefensiveRoutines.clear();
		meleeOffensiveRoutines.clear();
		meleeDefensiveRoutines.clear();
	}

	protected boolean loadedSomething(){ return (isLoaded || scanLoaded);}
///////////////////// INGAME COMMANDS ///////////////////////	
	public boolean sendGroupConfig(Player player, int pageNumber)
	{
		if(player == null)
		{
			if(configStrings.isEmpty())
			{
				log.severe("Well, frick...this shouldn't have happened. o_o"); //TODO REMOVE ME EVENTUALLY
				return false;
			}
			String printString = "Config for group \"" + groupName + "\" in world \"" + worldHandler.getWorld().getName() + "\":";
			for(String configString : configStrings)
				printString += "\n" + configString;
			log.info(printString);
			return true;
		}
		if(configPages > 0 && configPages >= pageNumber && pageNumber > 0)
		{
			player.sendMessage(plugin.ModDamageString(ChatColor.GOLD) +  " Group \"" + worldHandler.getWorld().getName().toUpperCase() 
					+ "\":\"" + groupName + "\" (" + pageNumber + "/" + configPages + ")");
			for(int i = (9 * (pageNumber - 1)); i < (configStrings.size() < (9 * pageNumber)
														?configStrings.size()
														:(9 * pageNumber)); i++)
				player.sendMessage(ChatColor.DARK_AQUA + configStrings.get(i));
			return true;
		}
		return false;
	}
}

	
