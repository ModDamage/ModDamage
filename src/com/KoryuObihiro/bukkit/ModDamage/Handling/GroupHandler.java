package com.KoryuObihiro.bukkit.ModDamage.Handling;

import java.util.ArrayList;
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
	public boolean scanLoaded = false;
	
	//world-type and mob-type damage
	final private HashMap<DamageElement, List<String>> offensiveRoutines = new HashMap<DamageElement, List<String>>();
	final private HashMap<DamageElement, List<String>> defensiveRoutines = new HashMap<DamageElement, List<String>>();
	
	//pvp damage
	final private HashMap<String, List<String>> pvpOffensiveRoutines = new HashMap<String, List<String>>();
	final private HashMap<String, List<String>> pvpDefensiveRoutines = new HashMap<String, List<String>>();
	
	//item damage
	final private HashMap<Material, List<String>> itemOffensiveRoutines = new HashMap<Material, List<String>>();
	final private HashMap<Material, List<String>> itemDefensiveRoutines = new HashMap<Material, List<String>>();
	
	//scan
	final private List<Material> groupScanItems = new ArrayList<Material>();
	
	final private ConfigurationNode offensiveNode;
	final private ConfigurationNode defensiveNode;
	final private ConfigurationNode scanNode;
	final public DamageCalculator damageCalc;
	final public WorldHandler worldHandler;
	final String groupName;
	
	
//// CONSTRUCTOR ////
	public GroupHandler(WorldHandler worldHandler, String name, ConfigurationNode offensiveGroupNode, ConfigurationNode defensiveGroupNode, ConfigurationNode scanGroupNode, DamageCalculator damageCalc) 
	{
		this.worldHandler = worldHandler;
		this.groupName = name;
		this.offensiveNode = offensiveGroupNode;
		this.defensiveNode = defensiveGroupNode;
		this.scanNode = scanGroupNode;
		this.damageCalc = damageCalc;
		
		this.isLoaded = loadRoutines();
		this.scanLoaded = loadScanItems();
	}
	

///////////////////// OFFENSIVE/DEFENSIVE ///////////////////////	
	private boolean loadRoutines() 
	{
		String progressString = "UNKNOWN";
		try
		{
			//clear everything first
			clearRoutines();
			if(offensiveNode != null)
			{
				progressString = " generic damage types in Offensive";
				if(!(loadGenericRoutines(true)))
					return false;

				progressString = " Offensive item routines";
				if(!(loadItemRoutines(true)))
					return false;
				
				progressString = " Defensive PVP routines";
				if(!(loadPVPRoutines(true, true)))
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
				
				progressString = " Defensive PVP routines";
				if(!(loadPVPRoutines(false, true)))
					return false;
			}
		}
		catch(Exception e)
		{
			worldHandler.log.severe("[" + worldHandler.plugin.getDescription().getName() 
					+ "] Invalid configuration for group " + groupName + ", world \"" 
					+ worldHandler.world.getName() + "\" - failed to load" + progressString + ".");
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
					if(ModDamage.consoleDebugging_verbose) worldHandler.log.info("{Found global " + (isOffensive?"Offensive":"Defensive") + " " 
							+ damageCategory + " node for world \"" + worldHandler.world.getName() + "\"}");
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
									if(ModDamage.consoleDebugging_normal) worldHandler.log.info("-" + worldHandler.world.getName() + ":" 
											+ (isOffensive?"Offensive":"Defensive") + ":" + elementReference 
											+ (ModDamage.consoleDebugging_verbose?(" " + calcStrings.toString()):"") );//debugging
								}
								else if(ModDamage.consoleDebugging_normal)
								{
									worldHandler.log.warning("Repetitive "  + elementReference + " in " + (isOffensive?"Offensive":"Defensive") + " - ignoring");
									continue;
								}
							}
							else if(ModDamage.consoleDebugging_verbose)
								worldHandler.log.warning("No instructions found for " + elementReference + " node - is this on purpose?");
						}
						else if(ModDamage.consoleDebugging_verbose) worldHandler.log.info("Group \"" + groupName + "\" " 
								+ damageElement.getConfigReference() + " node for" 
								+ (isOffensive?"Offensive":"Defensive") + " not found.");
					}
					return true;
				}
			}
			return false;
	}
	
	public boolean loadItemRoutines(boolean isOffensive){ return loadItemRoutines(isOffensive, false);}
	public boolean loadItemRoutines(boolean isOffensive, boolean force)
	{
		ConfigurationNode itemNode = isOffensive?offensiveNode.getNode("item"):defensiveNode.getNode("item");
		if(itemNode != null)	
		{
			List<String> itemList = (isOffensive?offensiveNode:defensiveNode).getKeys("item");
			for(Material material : Material.values())
				if(itemList.contains(material.name()) || itemList.contains(material.getId()))
				{
					List<String> calcStrings = itemNode.getStringList(material.name(), null);
					worldHandler.log.warning(material.name() + " " + itemNode.getStringList(Integer.toString(material.getId()).toUpperCase(), null).toString());
					if(calcStrings == null) calcStrings = itemNode.getStringList(Integer.toString(material.getId()), null);
					if(!calcStrings.equals(null))
					{
						for(String calcString : calcStrings)
							if(!damageCalc.checkCommandString(calcString))
							{
								worldHandler.log.severe("Invalid command string \"" + calcString + "\" in " 
										+ (isOffensive?"Offensive":"Defensive") + " " + material.name() + "(" + material.getId()
										+ ") definition - refer to config for proper calculation node");
								calcStrings.clear();
							}
						if(calcStrings.size() > 0)
						{
							if(ModDamage.consoleDebugging_normal) worldHandler.log.info("-" + worldHandler.getWorld().getName() 
									+ ":" + groupName + ":" + (isOffensive?"Offensive":"Defensive") 
									+ ":" + material.name() + "(" + material.getId() + ")"
									+ (ModDamage.consoleDebugging_verbose?(" " + calcStrings.toString()):""));//debugging
							if(!(isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).containsKey(material))
								(isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).put(material, calcStrings);
							else if(ModDamage.consoleDebugging_normal) worldHandler.log.warning("[" + worldHandler.plugin.getDescription().getName() + "] Repetitive " 
									+ material.name() + "(" + material.getId() + ") definition in " + (isOffensive?"Offensive":"Defensive") 
									+ " item group settings - ignoring");
						}
						else if(ModDamage.consoleDebugging_verbose)
						{
							worldHandler.log.warning("No instructions found for group " + material.name() + "(" + material.getId()
								+ ") item node in " + (isOffensive?"Offensive":"Defensive") + " - is this on purpose?");
						}
						if(ModDamage.consoleDebugging_normal)
							worldHandler.log.info("-" + worldHandler.getWorld().getName() + ":" 
									+ ":" + groupName + (isOffensive?"Offensive":"Defensive") + ":" 
									+ material.name() + "(" + material.getId() + ") "
									+ calcStrings.toString());//debugging
						return true;
					}
				}
		}
		return false;
	}
	
	public boolean loadPVPRoutines(boolean isOffensive, boolean force)
	{
		//get all of the groups in configuration
		List<String> groups = (isOffensive?offensiveNode:defensiveNode).getKeys("groups");
		//load groups with offensive and defensive settings first
		if(groups != null)
			for(String group : groups)
			{
				List<String> calcStrings = (isOffensive?offensiveNode:defensiveNode).getNode("groups").getStringList(group, null);
				if(!calcStrings.equals(null))
					for(String calcString : calcStrings)
						if(!damageCalc.checkCommandString(calcString))
						{
							worldHandler.log.severe("Invalid command string \"" + calcString + "\" for group " + groupName 
								+ " in " + (isOffensive?"Offensive":"Defensive") + " \"" + group
								+ "\" PvP definition - refer to config for proper calculation node");
							calcStrings.clear();
						}
				if(calcStrings.size() > 0)
				{
					if(!(isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).containsKey(group))
						(isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).put(group, calcStrings);
					else if(ModDamage.consoleDebugging_normal) 
						worldHandler.log.warning("Repetitive " + group + " definition in " 
								+ (isOffensive?"Offensive":"Defensive") + " settings for group " + groupName + " - ignoring");
				}
				else if(ModDamage.consoleDebugging_verbose)
				{		
					worldHandler.log.warning("No instructions found for group " + groupName + " " + group 
						+ " PvP node in " + (isOffensive?"Offensive":"Defensive") + " for world " 
						+ worldHandler.world.getName() +  " - is this on purpose?");
				}
				if(ModDamage.consoleDebugging_normal)
					worldHandler.log.info("[" + worldHandler.plugin.getDescription().getName() + "] " + worldHandler.world.getName() 
						+ ":" + groupName + ":" + (isOffensive?"Offensive":"Defensive") + ":PvP:" + group + " " 
						+ (ModDamage.consoleDebugging_verbose?("\n" + calcStrings.toString()):""));//debugging
			}
		return true;
	}
	
	
///////////////////// SCAN ///////////////////////	
	private boolean loadScanItems() 
	{
		if(scanNode != null) 
		{
			if(ModDamage.consoleDebugging_verbose) worldHandler.log.info("{Found global Scan node for world \"" + worldHandler.world.getName() + "\"}");
			List<String> itemList = scanNode.getStringList("global", null);
			if(!itemList.equals(null))
			{
				for(Material material : Material.values())	
					if(itemList.contains(material.name()) || itemList.contains(Integer.toString(material.getId()))) //TODO Mess with casing here?
						if(!groupScanItems.contains(material)) 
						{
							groupScanItems.add(material);
							if(ModDamage.consoleDebugging_normal)
								ModDamage.log.info("-" + worldHandler.world.getName() + ":Scan:" 
									+ material.name() + "(" + material.getId() + ") ");
						}
				return true;
			}
		}
		return false;
	}
	
	public boolean canScan(Material itemType){ return(groupScanItems.contains(itemType));}
	

///////////////////// DAMAGE HANDLING ///////////////////////	
	
	public int calcAttackBuff(DamageElement mobType_target, int eventDamage)
	{
		return runRoutines(mobType_target, true, eventDamage);
	}
	public int calcDefenseBuff(DamageElement damageType, int eventDamage)
	{	
		return runRoutines(damageType, false, eventDamage);
	}
	
	public int calcAttackBuff(String group_target, int eventDamage)
	{
		//TODO BOW weapon type
		worldHandler.log.info("Grouphandler: " + groupName + " says to " + group_target + ": LOLWUT");
		return runRoutines(DamageElement.GENERIC_PLAYER, true, eventDamage) 
			+ runPVPRoutines(group_target, true, eventDamage);
	}
	public int calcDefenseBuff(String group_attacking, int eventDamage)
	{	
		worldHandler.log.info("Grouphandler: " + groupName + " says to " + group_attacking + ": BITE ME");
		return runRoutines(DamageElement.GENERIC_PLAYER, false, eventDamage) 
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
	
	private int runRoutines(DamageElement damageType, boolean isOffensive, int eventDamage)
	{
		int result = 0;
		if(damageType != null && (isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageType))
				for(String calcString : (isOffensive?offensiveRoutines:defensiveRoutines).get(damageType))
					result += damageCalc.parseCommand(calcString, eventDamage, isOffensive);
		return result;
	}
	
	private int runPVPRoutines(String groupName, boolean isOffensive, int eventDamage)
	{
		int result = 0;
		if(groupName != null && (isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).containsKey(groupName))
				for(String calcString : (isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).get(groupName))
					result += damageCalc.parseCommand(calcString, eventDamage, isOffensive);
		return result;
	}
	
	private int runItemRoutines(Material material, boolean isOffensive, int eventDamage) 
	{
		int result = 0;
		if(material != null && (isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).containsKey(material))
			for(String calcString : (isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).get(material))
				result += damageCalc.parseCommand(calcString, eventDamage, isOffensive);
		return result;
	}	

///////////////////// HELPER FUNCTIONS ///////////////////////	
	public String getGroupName(){ return groupName;}

	private void clearRoutines() 
	{
		offensiveRoutines.clear();
		defensiveRoutines.clear();
		pvpOffensiveRoutines.clear();
		pvpDefensiveRoutines.clear();
		itemOffensiveRoutines.clear();
		itemDefensiveRoutines.clear();
	}

///////////////////// INGAME COMMANDS ///////////////////////	
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
	
	public boolean damageType_isLoaded(DamageElement damageType){ return offensiveRoutines.containsKey(damageType);}
}

	