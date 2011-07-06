package com.KoryuObihiro.bukkit.ModDamage.Backend.Handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculationAllocator;

public class WorldHandler extends Handler
{
//// MEMBERS ////
	private World world;
	
	protected boolean groupsLoaded = false;
	protected boolean scanLoaded = false;
	protected boolean mobHealthLoaded = false;
	
	//nodes for config loading
	protected ConfigurationNode mobHealthNode;
	final protected SpawnCalculationAllocator healthAllocator;
	
	//MobHealth
	final protected HashMap<DamageElement, List<SpawnCalculation>> mobSpawnRoutines = new HashMap<DamageElement, List<SpawnCalculation>>();
	
	//Handlers
	protected final HashMap<String, GroupHandler> groupHandlers = new HashMap<String, GroupHandler>();

////FUNCTIONS ////
//// CONSTRUCTOR ////
	public WorldHandler(ModDamage plugin, World world, ConfigurationNode offensiveNode, ConfigurationNode defensiveNode, ConfigurationNode mobHealthNode, ConfigurationNode scanNode, DamageCalculationAllocator damageAllocator, SpawnCalculationAllocator healthAllocator) 
	{
		this.world = world;
		this.plugin = plugin;
		this.log = ModDamage.log;
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
		
		reload();
	}

//// CONFIG LOADING ////

	@Override
	protected void loadAdditionalConfiguration()
	{
		scanLoaded = loadScanItems();
		mobHealthLoaded = loadMobHealth();
	}
	
	@Override
	protected boolean loadGroupRoutines(boolean isOffensive)
	{
		boolean loadedSomething = false;
	//load GroupHandlers
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
	
//// MOBHEALTH ////
	public boolean loadMobHealth()
	{
		boolean loadedSomething = false;
		if(mobHealthNode != null) 
		{
			if(ModDamage.consoleDebugging_verbose) log.info("{Found MobHealth node for " + getCalculationHeader() + "}");
			List<DamageElement> creatureTypes = new ArrayList<DamageElement>();
			creatureTypes.addAll(DamageElement.getElementsOf("animal"));
			creatureTypes.addAll(DamageElement.getElementsOf("mob"));
			//load Mob health settings
			for(DamageElement creatureType : creatureTypes)
			{
			//check the node property for a default spawn calculation
				List<Object> calcStrings = mobHealthNode.getList(creatureType.getReference());
				//So, when a list of calculations are called, they're just ArrayList<Object>
				// Normal calcStrings are just strings,
				// conditionals are represented with a LinkedHashMap.
				if(calcStrings != null)
				{
					List<SpawnCalculation> calculations = healthAllocator.parseStrings(calcStrings);//healthAllocator.parseStrings(calcStrings);
					if(!calculations.isEmpty())
					{
						if(!mobSpawnRoutines.containsKey(creatureType))
						{
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
		if(eventInfo.damageElement != null && mobSpawnRoutines.containsKey(eventInfo.damageElement))
		{
			for(SpawnCalculation calculation : mobSpawnRoutines.get(eventInfo.damageElement))
				calculation.calculate(eventInfo);
			return true;
		}
		return false;
	}	

//// SCAN ////
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
		return ((scanLoaded && scanItems.contains(itemType) 
				|| ((groupHandlers.get(groupName) != null)
						?groupHandlers.get(groupName).canScan(itemType)
						:false)));
	}

//// DAMAGE HANDLING ////
	public void doDamageCalculations(DamageEventInfo eventInfo) 
	{
		switch(eventInfo.eventType)
		{
///////////////////// Player vs. Player 
			case PLAYER_PLAYER:
				runRoutines(eventInfo, true);//attack buff
				runEquipmentRoutines(eventInfo, true);
				
				runRoutines(eventInfo, false);//defense buff
				runEquipmentRoutines(eventInfo, false);
				
			//calculate group buff
				try
				{
					for(String group_attacker : eventInfo.groups_attacker)
						if(groupHandlers.containsKey(group_attacker))
							for(String group_target : eventInfo.groups_target)
							{
								groupHandlers.get(group_attacker).doAttackCalculations(eventInfo);//attack buff
								groupHandlers.get(group_target).doDefenseCalculations(eventInfo);//defense buff		
							}
				}
				catch(Exception e)
				{
					if(eventInfo.groups_target == null)
						log.warning("[" + plugin.getDescription().getName() + "] No groups found for player \"" 
								+ eventInfo.name_target + "\" in world \"" + eventInfo.world.getName() + "\" - add this player to a group in Permissions!");
					else if(eventInfo.groups_attacker == null)
						log.warning("[" + plugin.getDescription().getName() + "] No groups found for player \"" 
								+ eventInfo.name_attacker + "\" in world \"" + eventInfo.world.getName() + "\" - add this player to a group in Permissions!");
				}
				
			return;

///////////////////// Player vs. Mob
			case PLAYER_MOB:
				runRoutines(eventInfo, true);//attack buff
				runEquipmentRoutines(eventInfo, true);
				
				runRoutines(eventInfo, false);//defense buff
			
			//group buff
				try
				{
					for(String group_attacking : eventInfo.groups_attacker)
						if(groupHandlers.containsKey(group_attacking))
							groupHandlers.get(group_attacking).doAttackCalculations(eventInfo);
				}
				catch(Exception e)
				{
					log.warning("[" + plugin.getDescription().getName() + "] No groups found for player \"" 
							+ eventInfo.name_attacker + "\" in world \"" + eventInfo.world.getName() + "\" - add this player to a group in Permissions!");
				}
			return;
				
///////////////////// Mob vs. Player
			case MOB_PLAYER:
				runRoutines(eventInfo, true);//attack buff
				
				runRoutines(eventInfo, false);//defense buff
				runEquipmentRoutines(eventInfo, false);
				
			//calculate group buff
				try
				{
					String[] groups_target = ModDamage.Permissions.getGroups(eventInfo.world.getName(), ((Player)eventInfo.entity_target).getName());
					for(String group_target : groups_target)
						if(groupHandlers.containsKey(group_target))
							groupHandlers.get(group_target).doDefenseCalculations(eventInfo);
				}
				catch(Exception e)
				{
					log.warning("[" + plugin.getDescription().getName() + "] No groups found for player \"" 
							+ eventInfo.name_target + "\" in world \"" + eventInfo.world.getName() + "\" - add this player to a group in Permissions!");
				}
			return;
			

///////////////////// Mob vs. Mob
			case MOB_MOB:
				runRoutines(eventInfo, true);//attack buff

				runRoutines(eventInfo, false);//defense buff
			return;
	
///////////////////// Nonliving vs. Player
			case NONLIVING_PLAYER:
				runRoutines(eventInfo, true);//attack buff
				
				runRoutines(eventInfo, false);//defense buff
				runEquipmentRoutines(eventInfo, false);
				
			//calculate group buff
				try
				{
					for(String group_target : eventInfo.groups_target)
						if(groupHandlers.containsKey(group_target))
							groupHandlers.get(group_target).doDefenseCalculations(eventInfo);
				}
				catch(Exception e)
				{
					log.warning("[" + plugin.getDescription().getName() + "] No groups found for player \"" 
						+ eventInfo.name_target + "\" in world \"" + eventInfo.world.getName() + "\" - add this player to a group in Permissions!");
				}
			return;

///////////////////// Nonliving vs. Mob
			case NONLIVING_MOB:
				runRoutines(eventInfo, true);//attack buff
				runRoutines(eventInfo, false);//defense buff
			return;
			
			default: return;
		}
	}
	
//// HELPER FUNCTIONS////
	@Override
	protected String getCalculationHeader(){ return "worlds:" + world.getName();}

	@Override
	protected String getDisplayString(boolean upperCase){ return (upperCase?"W":"w") + "orld \"" + world.getName() + "\"";}
	
	@Override
	public void clear()
	{
		super.clear();
		mobSpawnRoutines.clear();
		groupHandlers.clear();
	}

	@Override
	public boolean loadedSomething(){ return super.loadedSomething() || groupsLoaded || mobHealthLoaded;}

	public World getWorld(){ return world;}
	
	public GroupHandler getGroupHandler(String groupMatch){ return groupHandlers.get(groupMatch);}

	public Collection<GroupHandler> getGroupHandlers(){ return groupHandlers.values();}
	
//// COMMAND FUNCTIONS ////
	@Override
	public void printAdditionalStrings()
	{
		for(GroupHandler groupHandler : groupHandlers.values())
			groupHandler.sendConfig(null, 9001);
	}

	@Override
	public boolean printAdditionalConfiguration(Player player, int pageNumber)
	{
		if(pageNumber == configPages + 1)
		{
			player.sendMessage(ModDamage.ModDamageString(ChatColor.GOLD) + " " + getDisplayString(true) + ": (" + pageNumber + "/" + (configPages + additionalConfigChecks) + ")");
			player.sendMessage(ModDamage.ModDamageString(ChatColor.YELLOW) + " The following groups have been configured:");
			if(!groupHandlers.isEmpty())
			{
				for(GroupHandler groupHandler : groupHandlers.values())
						player.sendMessage(ChatColor.GREEN + groupHandler.getGroupName());
				player.sendMessage(ChatColor.BLUE + "Use /md check " + world.getName() + " [groupname] or /md check " + world.getName() + " [page] for more info.");
			}
			else player.sendMessage(ChatColor.RED + "No groups configured!");
			return true;
		}
		return false;
	}

	
	@Override
	protected String getConfigPath(){ return "worlds." + world.getName();}
}
	
