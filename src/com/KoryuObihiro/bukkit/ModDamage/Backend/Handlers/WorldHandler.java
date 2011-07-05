package com.KoryuObihiro.bukkit.ModDamage.Backend.Handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculationAllocator;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculationAllocator;

public class WorldHandler
{
//// MEMBERS ////
	protected ModDamage plugin;
	protected Logger log;
	protected boolean damageRoutinesLoaded = false;
	protected boolean spawnRoutinesLoaded = false;
	protected boolean scanLoaded = false;

	private World world;
	
	//nodes for config loading
	protected static final DamageCalculationAllocator damageCalculationAllocator = new DamageCalculationAllocator();
	protected static final SpawnCalculationAllocator healthCalculationAllocator = new SpawnCalculationAllocator();

	final protected List<ModDamageCalculation> damageRoutines = new ArrayList<ModDamageCalculation>();
	final protected HashMap<DamageElement, List<ModDamageCalculation>> spawnRoutines = new HashMap<DamageElement, List<ModDamageCalculation>>();
	protected ConfigurationNode mobHealthNode;
	final protected SpawnCalculationAllocator healthAllocator;
	
	//Scan
	final protected List<Material> globalScanItems = new ArrayList<Material>();
	final protected HashMap<String, List<Material>> groupScanItems= new HashMap<String, List<Material>>();
	
	//Ingame
	protected int configPages = 0;
	protected List<String> configStrings = new ArrayList<String>();
	protected int additionalConfigChecks = 0;

////FUNCTIONS ////
//// CONSTRUCTOR ////
	public WorldHandler(ModDamage plugin, World world, ConfigurationNode damageWorldsNode, ConfigurationNode mobHealthNode, ConfigurationNode scanNode) 
	{
		this.plugin = plugin;
		this.log = ModDamage.log;
		this.world = world;
		this.offensiveNode = offensiveNode;
		this.offensiveGlobalNode = (offensiveNode != null?offensiveNode.getNode("global"):null);
		this.defensiveNode = defensiveNode;
		this.defensiveGlobalNode = (defensiveNode != null?defensiveNode.getNode("global"):null);
		this.scanNode = scanNode;
		this.scanGlobalNode = (scanNode != null?scanNode.getNode("global"):null);
		this.mobHealthNode = mobHealthNode;
		this.damageAllocator = damageAllocator;
		this.healthAllocator = healthAllocator;
		additionalConfigChecks = 1;
		
		load();
	}

		damageRoutinesLoaded = loadDamageRoutines(damageWorldsNode);
		spawnRoutinesLoaded = loadSpawnRoutines(mobHealthNode);
		scanLoaded = loadScanItems(scanNode);
	}

	
//// DAMAGE ////
	protected boolean loadDamageRoutines(ConfigurationNode damageWorldsNode) //TODO Test, add debug output
	{
		boolean loadedSomething = false;
		if(damageWorldsNode != null)
		{
			List<Object> calcStrings = damageWorldsNode.getList(world.getName());
			if(!calcStrings.isEmpty())
			{
				List<ModDamageCalculation> calculations = damageCalculationAllocator.parseStrings(calcStrings);
				if(!calculations.isEmpty())
					damageRoutines.addAll(calculations);
			groups.addAll((offensiveNode != null && offensiveNode.getKeys("groups") != null)?offensiveNode.getKeys("groups"):new ArrayList<String>());
			groups.addAll((defensiveNode != null && defensiveNode.getKeys("groups") != null)?defensiveNode.getKeys("groups"):new ArrayList<String>());
			groups.addAll((scanNode != null && scanNode.getKeys("groups") != null)?scanNode.getKeys("groups"):new ArrayList<String>());
		}
		//load groups with offensive and defensive settings first
		if(!groups.isEmpty())
		{
			for(String group : groups)
			{	
				if(groupHandlers.containsKey(group))
				{
					if(ModDamage.consoleDebugging_normal) log.warning("Repetitive group definition found for group \"" + group + "\" found - ignoring.");
				}
				else 
				{
					GroupHandler groupHandler = new GroupHandler(plugin, this, log, group,
							((offensiveNode != null && offensiveNode.getNode("groups") != null)?offensiveNode.getNode("groups").getNode(group):null),
							((defensiveNode != null && defensiveNode.getNode("groups") != null)?defensiveNode.getNode("groups").getNode(group):null), 
							((scanNode != null && scanNode.getNode("groups") != null)?scanNode.getNode("groups"):null), 
							damageAllocator);
							
					if(groupHandler.loadedSomething())
					{
						groupHandlers.put(group, groupHandler);
						loadedSomething = true;
					}
				}
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
			if(ModDamage.consoleDebugging_verbose) log.info("{Found MobHealth node for " + getDisplayString(false) + "}");
			if(ModDamage.consoleDebugging_verbose) log.info("{Found MobHealth node for " + getCalculationHeader() + "}");
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
					List<ModDamageCalculation> calculations = healthCalculationAllocator.parseStrings(calcStrings);
					List<SpawnCalculation> calculations = healthAllocator.parseStrings(calcStrings);//healthAllocator.parseStrings(calcStrings);
					if(!calculations.isEmpty())
					{
						if(!spawnRoutines.containsKey(creatureType))
						{
							spawnRoutines.put(creatureType, calculations);
							String configString = "-MobHealth:" + getCalculationHeader() + ":" + creatureType.getReference() 
								+ " [" + calcStrings.toString() + "]";
							mobSpawnRoutines.put(creatureType, calculations);
							String configString = "-MobHealth:" + getCalculationHeader() + ":" + creatureType.getReference() + calcStrings.toString();
							configStrings.add(configString);
							if(ModDamage.consoleDebugging_normal) log.info(configString);
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
	protected boolean loadScanItems(ConfigurationNode configurationNode) 
	{
		boolean loadedSomething = true;
		if(configurationNode != null) 
		{
			if(ModDamage.consoleDebugging_verbose) log.info("{Found Scan node for " + getDisplayString(false) + "\"}");
			List<String> itemList = configurationNode.getStringList("global", null);
			if(!itemList.equals(null))
			{
				for(String itemString : itemList)
				{
					if(ModDamage.itemAliases.containsKey(itemString.toLowerCase()))
						for(Material material : ModDamage.itemAliases.get(itemString.toLowerCase()))
						{
							globalScanItems.add(material);
							addConfigString("-Scan:" + getCalculationHeader() + ":" + material.name() + "(" + material.getId() + ")");
							loadedSomething = true;
						}
					else
					{
						Material material = Material.matchMaterial(itemString);
						if(material != null)
						{
							globalScanItems.add(material);
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
	
	protected boolean canScan(Material itemType, String groupName)
	{ 
		if(groupName == null) groupName = "";
		return ((scanLoaded && (globalScanItems.contains(itemType) 
				|| ((groupScanItems.get(groupName) != null)
						?groupScanItems.get(groupName).contains(itemType)
						:false))));
	}

//// HELPER FUNCTIONS////
	protected String getCalculationHeader(){ return "worlds:" + world.getName();}

	protected String getDisplayString(boolean upperCase){ return (upperCase?"W":"w") + "orld \"" + world.getName() + "\"";}
	
	protected void clear() 
	{
		damageRoutines.clear();
		spawnRoutines.clear();
		globalScanItems.clear();
		groupScanItems.clear();
		configStrings.clear();
		
		damageRoutinesLoaded = spawnRoutinesLoaded = scanLoaded = false;
	}

	public boolean loadedSomething(){ return damageRoutinesLoaded || spawnRoutinesLoaded;}

	public World getWorld(){ return world;}

	protected void addConfigString(String string) 
	{
		//FIXME Change so that character lengths are counted for accurate paging.
		configStrings.add(string);
		if(ModDamage.consoleDebugging_normal) log.info(string);
	}
	
//// COMMAND FUNCTIONS ////	
	protected String getConfigPath(){ return "worlds." + world.getName();}
	
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
	
	
