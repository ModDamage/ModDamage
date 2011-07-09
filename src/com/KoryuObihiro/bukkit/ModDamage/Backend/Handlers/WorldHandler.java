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
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class WorldHandler
{
//// MEMBERS ////
	protected static ModDamage plugin;
	protected static Logger log;
	protected boolean damageRoutinesLoaded = false;
	protected boolean spawnRoutinesLoaded = false;
	protected boolean scanItemsLoaded = false;

	private String name;
	
	//nodes for config loading
	protected static final CalculationUtility calculationUtility = new CalculationUtility();

	final protected List<ModDamageCalculation> damageRoutines = new ArrayList<ModDamageCalculation>();
	final protected HashMap<DamageElement, List<ModDamageCalculation>> spawnRoutines = new HashMap<DamageElement, List<ModDamageCalculation>>();
	
	//Scan
	final protected List<Material> globalScanItems = new ArrayList<Material>();
	final protected HashMap<String, List<Material>> groupScanItems= new HashMap<String, List<Material>>();
	
	//Ingame
	protected int configPages = 0;
	protected List<String> configStrings = new ArrayList<String>();
	protected int additionalConfigChecks = 0;

////FUNCTIONS ////
//// CONSTRUCTOR ////
	public WorldHandler(String worldName, List<Object> damageStrings, ConfigurationNode mobHealthNode, List<String> globalScanItems, ConfigurationNode groupScanNode) 
	{
		WorldHandler.log = ModDamage.log;
		this.name = worldName;

		damageRoutinesLoaded = loadDamageRoutines(damageStrings);
		spawnRoutinesLoaded = loadSpawnRoutines(mobHealthNode);
		scanItemsLoaded = loadScanItems(globalScanItems, groupScanNode);
	}
	
//// DAMAGE ////
	protected boolean loadDamageRoutines(List<Object> calcStrings)
	{
		boolean loadedSomething = false;
		if(calcStrings != null)
		{
			if(ModDamage.consoleDebugging_normal) log.info("Damage configuration found for " + getDisplayString(false) + ", parsing...");
			List<ModDamageCalculation> calculations = calculationUtility.parseStrings(calcStrings, false);
			if(!calculations.isEmpty())
			{
				damageRoutines.addAll(calculations);
			}
		}
		return loadedSomething;
	}

	public void doDamageCalculations(DamageEventInfo eventInfo) 
	{
		for(ModDamageCalculation calculation : damageRoutines)
			calculation.calculate(eventInfo);
	}
	
//// MOBHEALTH ////
	protected boolean loadSpawnRoutines(ConfigurationNode configurationNode)
	{
		boolean loadedSomething = false;
		if(configurationNode != null) 
		{
			if(ModDamage.consoleDebugging_normal) log.info("MobHealth configuration found for " + getDisplayString(false) + ", parsing...");
			List<DamageElement> creatureTypes = new ArrayList<DamageElement>();
			creatureTypes.addAll(DamageElement.getElementsOf("animal"));
			creatureTypes.addAll(DamageElement.getElementsOf("mob"));
			//load Mob health settings
			for(DamageElement creatureType : creatureTypes)
			{
			//check the node property for a default spawn calculation
				List<Object> calcStrings = configurationNode.getList(creatureType.getReference());
				//So, when a list of calculations are called, they're just ArrayList<Object>
				// Normal calcStrings are just strings,
				// conditionals are represented with a LinkedHashMap.
				if(calcStrings != null)
				{
					List<ModDamageCalculation> calculations = calculationUtility.parseStrings(calcStrings, true);
					if(!calculations.isEmpty())
					{
						if(!spawnRoutines.containsKey(creatureType))
						{
							spawnRoutines.put(creatureType, calculations);
							addConfigString("-MobHealth:" + getCalculationHeader() + ":" + creatureType.getReference() + calcStrings.toString());
							loadedSomething = true;
						}
						else if(ModDamage.consoleDebugging_normal) log.warning("Repetitive " + creatureType.getReference() 
								+ " definition - ignoring");
					}
					else  log.severe("Invalid command string \"" + calcStrings.toString() + "\" in MobHealth " + creatureType.getReference() 
							+ " definition");
					
				}
				else if(ModDamage.consoleDebugging_verbose)
					log.warning("No instructions found for " + creatureType.getReference() + " - is this on purpose?");
			}
		}
		return loadedSomething;
	}
	
	public boolean doSpawnCalculations(SpawnEventInfo eventInfo)
	{
		//determine creature type
		if(eventInfo.spawnedElement != null && spawnRoutines.containsKey(eventInfo.spawnedElement))
		{
			for(ModDamageCalculation calculation : spawnRoutines.get(eventInfo.spawnedElement))
				calculation.calculate(eventInfo);
			return true;
		}
		return false;
	}	

//// SCAN ////
	protected boolean loadScanItems(List<String> itemList, ConfigurationNode groupNode) 
	{
		boolean loadedSomething = true;
		if(itemList != null)
		{
			if(ModDamage.consoleDebugging_normal) log.info("Global Scan configuration found for " + getDisplayString(false) + ", parsing...");
			for(String itemString : itemList)
			{
				List<Material> materials = ServerHandler.matchItems(itemString);
				if(!materials.isEmpty())
				{
					for(Material material : materials)
					{
						globalScanItems.add(material);
						addConfigString("-Scan:" + getCalculationHeader() + ":" + material.name() + "(" + material.getId() + ")");
						loadedSomething = true;
					}
				}
				else if(ModDamage.consoleDebugging_verbose) log.warning("Invalid Scan item \"" + itemString + "\" found in " + getDisplayString(false) + " globals - ignoring");
			}
		}
		if(groupNode != null)
		{
			if(ModDamage.consoleDebugging_normal) log.info("Group Scan configuration found for " + getDisplayString(false) + ", parsing...");
			for(String groupName : groupNode.getAll().keySet())
			{
				if(!groupScanItems.containsKey(groupName))
					groupScanItems.put(groupName, new ArrayList<Material>());
				for(String itemString : groupNode.getStringList(groupName, new ArrayList<String>()))
				{
					List<Material> materials = ServerHandler.matchItems(itemString);
					if(!materials.isEmpty())
					{
						for(Material material : materials)
						{
							groupScanItems.get(groupName).add(material);
							addConfigString("-Scan:" + getCalculationHeader() + ":" + groupName + ":" + material.name() + "(" + material.getId() + ")");
							loadedSomething = true;
						}
					}
					else if(ModDamage.consoleDebugging_verbose) log.warning("Invalid Scan item \"" + itemString + "\" found in " + getDisplayString(false) + " globals - ignoring");
				}
			}
		}
		return loadedSomething;
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
		return (scanItemsLoaded && groupCanScan);
	}
	
	protected boolean canScan(Material itemType, String groupName)
	{ 
		if(groupName == null) groupName = "";
		return ((scanItemsLoaded && (globalScanItems.contains(itemType) 
				|| ((groupScanItems.get(groupName) != null)
						?groupScanItems.get(groupName).contains(itemType)
						:false))));
	}

//// HELPER FUNCTIONS////
	public String getName(){ return name;}
	
	protected String getCalculationHeader(){ return "worlds:" + name;}

	protected String getDisplayString(boolean upperCase){ return (upperCase?"W":"w") + "orld \"" + name + "\"";}
	
	protected void clear() 
	{
		damageRoutines.clear();
		spawnRoutines.clear();
		globalScanItems.clear();
		groupScanItems.clear();
		configStrings.clear();
		
		damageRoutinesLoaded = spawnRoutinesLoaded = scanItemsLoaded = false;
	}

	public boolean loadedSomething(){ return damageRoutinesLoaded || spawnRoutinesLoaded || scanItemsLoaded;}

	public void addConfigString(String string) 
	{
		//FIXME Change so that character lengths are counted for accurate paging.
		configStrings.add(string);
		if(ModDamage.consoleDebugging_normal) log.info(string);
	}
	
//// COMMAND FUNCTIONS ////	
	protected String getConfigPath(){ return "worlds." + name;}
	
	public boolean sendConfig(Player player, int pageNumber)
	{
		if(player == null)
		{
			String printString = "Global configuration for " + getDisplayString(false) + ":";
			for(String configString : configStrings)
				printString += "\n" + configString;
			
			log.info(printString);
			
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

	public boolean printAdditionalConfiguration(Player player, int pageNumber){ return false;}
}
	