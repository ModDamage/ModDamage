package com.KoryuObihiro.bukkit.ModDamage.Backend.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculationAllocator;

public abstract class Handler
{
//// MEMBERS //// 
	protected ModDamage plugin;
	protected Logger log;
	protected boolean routinesLoaded = false;
	protected boolean scanLoaded = false;

	//nodes for config loading
	protected ConfigurationNode offensiveNode;
	protected ConfigurationNode offensiveGlobalNode;
	protected ConfigurationNode defensiveNode;
	protected ConfigurationNode defensiveGlobalNode;
	protected ConfigurationNode scanNode;
	protected ConfigurationNode scanGlobalNode;
	protected DamageCalculationAllocator damageAllocator;
	
	//O/D config
	final protected HashMap<DamageElement, List<DamageCalculation>> offensiveRoutines = new HashMap<DamageElement, List<DamageCalculation>>();
	final protected HashMap<DamageElement, List<DamageCalculation>> defensiveRoutines = new HashMap<DamageElement, List<DamageCalculation>>();
	final protected HashMap<Material, List<DamageCalculation>> meleeOffensiveRoutines = new HashMap<Material, List<DamageCalculation>>();
	final protected HashMap<Material, List<DamageCalculation>> meleeDefensiveRoutines = new HashMap<Material, List<DamageCalculation>>();
	final protected HashMap<String, List<DamageCalculation>> armorOffensiveRoutines = new HashMap<String, List<DamageCalculation>>();
	final protected HashMap<String, List<DamageCalculation>> armorDefensiveRoutines = new HashMap<String, List<DamageCalculation>>();
	final protected HashMap<String, List<DamageCalculation>> groupOffensiveRoutines = new HashMap<String, List<DamageCalculation>>();
	final protected HashMap<String, List<DamageCalculation>> groupDefensiveRoutines = new HashMap<String, List<DamageCalculation>>();
	
	//Scan
	final protected List<Material> scanItems = new ArrayList<Material>();
	
	//Ingame
	protected int configPages = 0;
	protected List<String> configStrings = new ArrayList<String>();
	protected int additionalConfigChecks = 0;
	
	
//// FUNCTIONS ////
//// CONFIG LOADING ////
	public boolean load()
	{
		routinesLoaded = loadDamageRoutines();
		loadAdditionalConfiguration();

		configPages = configStrings.size()/9 + ((configStrings.size()%9 > 0)?1:0);
		
		if(loadedSomething() && ModDamage.consoleDebugging_verbose) 
				log.info("[" + plugin.getDescription().getName() + "] " + getDisplayString(true) + " configuration initialized!");
			else if(ModDamage.consoleDebugging_verbose)
				log.warning("[" + plugin.getDescription().getName() + "] " + getDisplayString(true) + " configuration could not load.");
		
		return loadedSomething();
	}
	
	abstract protected void loadAdditionalConfiguration();
	
//// OFFENSIVE/DEFENSIVE ////
	protected boolean loadDamageRoutines() 
	{
		String progressString = "UNKNOWN";
		try
		{			
 			boolean loadedSomething = false;
			if(offensiveNode != null)
			{
				progressString = "damage elements in Offensive";
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
				
				progressString = "Offensive group routines";
				if(loadGroupRoutines(true))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);
			}
			if(defensiveNode != null)
			{
				progressString = "damage elements in Defensive";
				if(loadGenericRoutines(false))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);
				
				progressString = "Defensive armor routines";
				if(loadArmorRoutines(false))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);

				progressString = "Defensive melee routines";
				if(loadMeleeRoutines(false))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);
				
				progressString = "Defensive group routines";
				if(loadGroupRoutines(false))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("Could not load " + progressString);
			}
			return loadedSomething;
		}
		catch(Exception e)
		{
			log.severe("[" + plugin.getDescription().getName() 
					+ "] Invalid configuration for "+ getDisplayString(true) + " - failed to load " + progressString + ".");
			return false;
		}
	}

	protected boolean loadGenericRoutines(boolean isOffensive)
	{
		boolean loadedSomething = false;
		List<String> damageCategories = DamageElement.getGenericTypeStrings();
		if((isOffensive?offensiveGlobalNode:defensiveGlobalNode) != null)
		{
			ConfigurationNode genericNode = (isOffensive?offensiveGlobalNode:defensiveGlobalNode).getNode("generic");
			for(String damageCategory : damageCategories)
			{
				if(genericNode != null)
				{
					List<Object> calcStrings = genericNode.getList(damageCategory);
					DamageElement element = DamageElement.matchDamageElement(damageCategory);
					if(calcStrings != null)
					{
						if(ModDamage.consoleDebugging_verbose) log.info("{Found " + getDisplayString(false) + " generic " 
								+ (isOffensive?"Offensive":"Defensive") + " " + damageCategory + " node " + "}");
							List<DamageCalculation> calculations = damageAllocator.parseStrings(calcStrings);
						if(!calculations.isEmpty())
						{
							if(!(isOffensive?offensiveRoutines:defensiveRoutines).containsKey(element))
							{
								(isOffensive?offensiveRoutines:defensiveRoutines).put(element, calculations);
								addConfigString("-" + (isOffensive?"Offensive":"Defensive") + ":" + getCalculationHeader() + ":Generic:" + damageCategory + calcStrings.toString());
								loadedSomething = true;
							}
							else if(ModDamage.consoleDebugging_normal)
							{
								log.warning("Repetitive generic "  + damageCategory + " node in " + (isOffensive?"Offensive":"Defensive") + " - ignoring");
								continue;
							}
						}
						else log.severe("Invalid command string \"" + calcStrings.toString() + "\" in generic \"" + damageCategory + "\" definition");
					}
					else if(ModDamage.consoleDebugging_verbose) log.warning("No instructions found for generic " + damageCategory + " node - is this on purpose?");
				}
				if(DamageElement.matchDamageElement(damageCategory).hasSubConfiguration())
				{
					ConfigurationNode relevantNode = (isOffensive?offensiveGlobalNode:defensiveGlobalNode).getNode(damageCategory);
					if(relevantNode != null)
					{
						if(ModDamage.consoleDebugging_verbose) log.info("{Found " + getDisplayString(false) + " specific " + (isOffensive?"Offensive":"Defensive") + " " 
								+ damageCategory + "}");
						for(DamageElement damageElement : DamageElement.getElementsOf(damageCategory))
						{
							String elementReference = damageElement.getReference();
							//check for leaf-node buff strings
							List<Object> calcStrings = relevantNode.getList(elementReference);
	 						if(calcStrings != null) //!calcStrings.equals(null)
							{
								List<DamageCalculation> calculations = damageAllocator.parseStrings(calcStrings);
								if(!calculations.isEmpty())
								{
									if(!(isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageElement))
									{
										(isOffensive?offensiveRoutines:defensiveRoutines).put(damageElement, calculations);
										addConfigString("-" + (isOffensive?"Offensive":"Defensive") + ":" + getCalculationHeader() + ":" + damageCategory + ":" + elementReference + calcStrings.toString());
										loadedSomething = true;
									}
									else if(ModDamage.consoleDebugging_normal)
									{
										log.warning("Repetitive " + elementReference + " specific node in " + (isOffensive?"Offensive":"Defensive") + " - ignoring");
										continue;
									}
								}
								else log.severe("Invalid command string \"" + calcStrings.toString() + "\" in \"" + elementReference + " definition");
							}
							else if(ModDamage.consoleDebugging_verbose) log.warning("No instructions found for " + elementReference + " node - is this on purpose?");
						}
					}
				}
			}
		}
		return loadedSomething;
	}

	protected boolean loadMeleeRoutines(boolean isOffensive)
	{
		boolean loadedSomething = false;
		if((isOffensive?offensiveGlobalNode:defensiveGlobalNode) != null)
		{
			ConfigurationNode meleeNode = (isOffensive?offensiveGlobalNode:defensiveGlobalNode).getNode(DamageElement.GENERIC_MELEE.getReference());
			if(meleeNode != null)	
			{
				if(ModDamage.consoleDebugging_verbose) log.info("{Found group specific " + (isOffensive?"Offensive":"Defensive") + " " 
						+ "melee node for " + getDisplayString(false) + "}");
				List<String> itemList = (isOffensive?offensiveGlobalNode:defensiveGlobalNode).getKeys(DamageElement.GENERIC_MELEE.getReference());
				if(itemList != null)
					for(String itemString : itemList)
					{
						Material material = Material.matchMaterial(itemString);
						if(material != null)
						{
							List<Object> calcStrings = meleeNode.getList(itemString);
							if(calcStrings != null)
							{
								List<DamageCalculation> calculations = damageAllocator.parseStrings(calcStrings);
								if(!calculations.isEmpty())
								{
									if(!(isOffensive?meleeOffensiveRoutines:meleeDefensiveRoutines).containsKey(material))
									{
										(isOffensive?meleeOffensiveRoutines:meleeDefensiveRoutines).put(material, calculations);
										addConfigString("-" + (isOffensive?"Offensive":"Defensive") + getCalculationHeader() + ":" + material.name() + "(" + material.getId() + ")" + calcStrings.toString());
										loadedSomething = true;
									}
									else if(ModDamage.consoleDebugging_normal) 
										log.warning("[" + plugin.getDescription().getName() + "] Repetitive " 
											+ material.name() + "(" + material.getId() + ") definition in " + getDisplayString(false)
											+ " " + (isOffensive?"Offensive":"Defensive") + " item settings - ignoring");
								}
								else log.severe("Invalid command string \"" + calcStrings.toString() + "\" in melee " + material.name() + " definition");
							}
							else if(ModDamage.consoleDebugging_verbose)
								log.warning("No instructions found for " + getDisplayString(false) + " " + material.name() 
									+ "(" + material.getId()+ ") item node in " + (isOffensive?"Offensive":"Defensive") 
									+ " - is this on purpose?");
						}
						else if(!ModDamage.itemAliases.containsKey(itemString) && ModDamage.consoleDebugging_verbose)
								log.warning("Unrecognized item name \"" + itemString + "\" found in specific melee node for " + getDisplayString(false) + " - ignoring");
					}
				}
			}
		return loadedSomething;
	}
	
	protected boolean loadArmorRoutines(boolean isOffensive)
	{
		boolean loadedSomething = false;
		if((isOffensive?offensiveGlobalNode:defensiveGlobalNode) != null)
		{
			ConfigurationNode armorNode = (isOffensive?offensiveGlobalNode:defensiveGlobalNode).getNode(DamageElement.GENERIC_ARMOR.getReference());
			if(armorNode != null)
			{
				if(ModDamage.consoleDebugging_verbose) log.info("{Found group specific " + (isOffensive?"Offensive":"Defensive") + " " 
						+ "armor node for " + getDisplayString(false) + "}");
				List<String> armorSetList = (isOffensive?offensiveGlobalNode:defensiveGlobalNode).getKeys(DamageElement.GENERIC_ARMOR.getReference());
				for(String armorSetString : armorSetList)
				{
					ArmorSet armorSet = new ArmorSet(armorSetString);
					if(!armorSet.isEmpty())
					{
						List<Object> calcStrings = armorNode.getList(armorSetString);
						if(calcStrings != null)
						{
							List<DamageCalculation> calculations = damageAllocator.parseStrings(calcStrings);
							if(!calculations.isEmpty())
							{
								if(!(isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).containsKey(armorSet))
								{
									(isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).put(armorSet.toString(), calculations);
									addConfigString("-" + (isOffensive?"Offensive":"Defensive") + ":" + getCalculationHeader() + ":armor:" + armorSet.toString() + " " + calcStrings.toString());
									loadedSomething = true;
								}
								else if(ModDamage.consoleDebugging_normal) log.warning("[" + plugin.getDescription().getName() + "] Repetitive" 
										+ armorSet.toString() + "definition in " + (isOffensive?"Offensive":"Defensive") 
										+ " armor set for " + getDisplayString(false) + "'s settings - ignoring");
							}
							else log.severe("Invalid command string \"" + calcStrings.toString() + "\" in armor set \"" + armorSetString + "\" definition");
						}
						else if(ModDamage.consoleDebugging_verbose)
							log.warning("No instructions found for " + getDisplayString(false) + "\n\t:" 
									+ armorSet.toString() + "\narmor node in " + (isOffensive?"Offensive":"Defensive") 
									+ " - is this on purpose?");
					}
					else log.severe("");
				}
			}
		}
		return loadedSomething;
	}
	
	protected boolean loadGroupRoutines(boolean isOffensive)
	{
		boolean loadedSomething = false;
		//get all of the groups in configuration
		List<String> groups = (isOffensive?offensiveNode:defensiveNode).getKeys("groups");
		if(groups != null)
			for(String group : groups)
			{
				List<Object> calcStrings = (isOffensive?offensiveNode:defensiveNode).getNode("groups").getList(group);
				if(calcStrings != null)
				{
					List<DamageCalculation> calculations = damageAllocator.parseStrings(calcStrings);
					if(!calculations.isEmpty())
					{
						if(!(isOffensive?groupOffensiveRoutines:groupDefensiveRoutines).containsKey(group))
						{
							(isOffensive?groupOffensiveRoutines:groupDefensiveRoutines).put(group, calculations);
							addConfigString("-" + (isOffensive?"Offensive":"Defensive") + ":" + getCalculationHeader() + ":groups:" + group + " " + calcStrings.toString());
							loadedSomething = true;
						}
						else if(ModDamage.consoleDebugging_normal) 
							log.warning("Repetitive " + group + " definition in " + (isOffensive?"Offensive":"Defensive") + " settings for " + getDisplayString(false) + " - ignoring");
					}
					else log.severe("Invalid command string \"" + calcStrings.toString() + "\" in group \"" + group + "\" definition");
				}
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("No instructions found for " + getDisplayString(false) + " groups node in " + (isOffensive?"Offensive":"Defensive") + " - is this on purpose?");
			}
		return loadedSomething;
	}

////SCAN ////
	protected boolean loadScanItems() 
	{
		boolean loadedSomething = false;
		if(scanNode != null) 
		{
			if(ModDamage.consoleDebugging_verbose) log.info("{Found Scan node for " + getDisplayString(false) + "\"}");
			List<String> itemList = scanNode.getStringList("global", null);
			if(!itemList.equals(null))
			{
				for(String itemString : itemList)
				{
					if(ModDamage.itemAliases.containsKey(itemString.toLowerCase()))
						for(Material material : ModDamage.itemAliases.get(itemString.toLowerCase()))
						{
							scanItems.add(material);
							addConfigString("-Scan:" + getCalculationHeader() + ":" + material.name() + "(" + material.getId() + ")");
							loadedSomething = true;
						}
					else
					{
						Material material = Material.matchMaterial(itemString);
						if(material != null)
						{
							scanItems.add(material);
							addConfigString("-Scan:" + getCalculationHeader() + ":" + material.name() + "(" + material.getId() + ")");
							loadedSomething = true;
						}
						else if(ModDamage.consoleDebugging_verbose) log.warning("Invalid Scan item \"" + itemString + "\" found in " + getDisplayString(false) + " globals - ignoring");
					}
				}
			}
		}
		return loadedSomething;
	}

///////////////////// ROUTINE-SPECIFIC CALLS
	protected void runRoutines(DamageEventInfo eventInfo, boolean isOffensive)
	{ 
		DamageElement damageElement = (isOffensive?eventInfo.damageElement_attacker:eventInfo.damageElement_target);
		if((isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageElement.getType()))
			calculateDamage(eventInfo, (isOffensive?offensiveRoutines:defensiveRoutines).get(damageElement.getType()));
		if((isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageElement))
			calculateDamage(eventInfo, (isOffensive?offensiveRoutines:defensiveRoutines).get(damageElement));
	}
	
	protected void runEquipmentRoutines(DamageEventInfo eventInfo, boolean isOffensive)
	{
		if(eventInfo.rangedElement != null)
		{
			if((isOffensive?offensiveRoutines:defensiveRoutines).containsKey(DamageElement.GENERIC_RANGED))
				calculateDamage(eventInfo, (isOffensive?offensiveRoutines:defensiveRoutines).get(DamageElement.GENERIC_RANGED));
			if((isOffensive?offensiveRoutines:defensiveRoutines).containsKey(eventInfo.rangedElement))
				calculateDamage(eventInfo, (isOffensive?offensiveRoutines:defensiveRoutines).get(eventInfo.rangedElement));
		}
		else 
		{
			if((isOffensive?offensiveRoutines:defensiveRoutines).containsKey(DamageElement.GENERIC_MELEE))
				calculateDamage(eventInfo, (isOffensive?offensiveRoutines:defensiveRoutines).get(DamageElement.GENERIC_MELEE));

			if((isOffensive?offensiveRoutines:defensiveRoutines).containsKey(eventInfo.elementInHand_attacker))
				calculateDamage(eventInfo, (isOffensive?offensiveRoutines.get(eventInfo.elementInHand_attacker):defensiveRoutines.get(eventInfo.elementInHand_attacker)));

			if((isOffensive?meleeOffensiveRoutines:meleeDefensiveRoutines).containsKey(eventInfo.materialInHand_attacker))
				calculateDamage(eventInfo, (isOffensive?meleeOffensiveRoutines.get(eventInfo.materialInHand_attacker):meleeDefensiveRoutines.get(eventInfo.materialInHand_attacker)));
		}
		if((isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).containsKey(eventInfo.armorSetString_target))
			calculateDamage(eventInfo, (isOffensive?armorOffensiveRoutines.get(eventInfo.armorSetString_attacker):armorDefensiveRoutines.get(eventInfo.armorSetString_target)));
	}
	
	protected void calculateDamage(DamageEventInfo eventInfo, List<DamageCalculation> calculations) 
	{
		for(DamageCalculation Calculation : calculations)
			Calculation.calculate(eventInfo);
	}

//// HELPER FUNCTIONS ////
	protected boolean loadedSomething(){ return routinesLoaded || scanLoaded;}

	abstract protected String getConfigPath();
	
	abstract protected String getCalculationHeader();
	
	abstract protected String getDisplayString(boolean upperCase);
	
	private void addConfigString(String string) 
	{
		//FIXME Change so that character lengths are counted for accurate paging.
		configStrings.add(string);
		if(ModDamage.consoleDebugging_normal) log.info(string);
	}
	
//// COMMAND FUNCTIONS ////
	public boolean sendConfig(Player player, int pageNumber)
	{
		if(player == null)
		{
			String printString = "Global configuration for " + getDisplayString(false) + ":";
			for(String configString : configStrings)
				printString += "\n" + configString;
			
			log.info(printString);
			printAdditionalStrings();
			
			return true;
		}
		else if(pageNumber > 0)
		{
			if(pageNumber <= configPages)
			{
				player.sendMessage(ModDamage.ModDamageString(ChatColor.GOLD) + " " + getDisplayString(true) + ": (" + pageNumber + "/" + (configPages + additionalConfigChecks) + ")");
				for(int i = (9 * (pageNumber - 1)); i < (configStrings.size() < (9 * pageNumber)
															?configStrings.size()
															:(9 * pageNumber)); i++)
					player.sendMessage(ChatColor.DARK_AQUA + configStrings.get(i));
				return true;
			}
			return printAdditionalConfiguration(player, pageNumber);
		}
		return false;
	}

	abstract public void printAdditionalStrings();
	
	abstract public boolean printAdditionalConfiguration(Player player, int pageNumber);
	
	
}

	
