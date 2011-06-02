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
	
	final private HashMap<String, List<String>> armorOffensiveRoutines = new HashMap<String, List<String>>();
	final private HashMap<String, List<String>> armorDefensiveRoutines = new HashMap<String, List<String>>();
	
	//scan
	final private List<Material> groupScanItems = new ArrayList<Material>();
	
	final private ConfigurationNode offensiveNode;
	final private ConfigurationNode defensiveNode;
	final private ConfigurationNode scanNode;
	final public DamageCalculator damageCalc;
	final public WorldHandler worldHandler;
	final private String groupName;	
	
	
//// CONSTRUCTOR ////
	public GroupHandler(WorldHandler worldHandler, String name, ConfigurationNode offensiveGroupNode, ConfigurationNode defensiveGroupNode, ConfigurationNode scanGroupNode, DamageCalculator damageCalc) 
	{
		this.worldHandler = worldHandler;
		this.groupName = name;
		this.offensiveNode = offensiveGroupNode;
		this.defensiveNode = defensiveGroupNode;
		this.scanNode = scanGroupNode;
		this.damageCalc = damageCalc;
		
		reload();
	}
	
	
	public boolean reload()
	{ 
		isLoaded = loadRoutines();
		if(isLoaded && ModDamage.consoleDebugging_normal) 
			worldHandler.log.info("[" + worldHandler.plugin.getDescription().getName() + "] Group \"" + groupName + "\" configuration for world \"" 
				+ worldHandler.getWorld().getName() + "\" initialized!");
		else if(ModDamage.consoleDebugging_verbose)
			worldHandler.log.warning("[" + worldHandler.plugin.getDescription().getName() + "] Group \"" + groupName + "\" configuration for world \"" 
				+ worldHandler.getWorld().getName() + "\" could not load.");

		//load Scan item configuration
		scanLoaded = loadScanItems();
		
		if(isLoaded || scanLoaded) return true;
		return false;
	}

///////////////////// OFFENSIVE/DEFENSIVE ///////////////////////	
	private boolean loadRoutines() 
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
					worldHandler.log.warning("Could not load " + progressString);

				progressString = "Offensive armor routines";
				if(loadArmorRoutines(true))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					worldHandler.log.warning("Could not load " + progressString);
				
				progressString = "Offensive item routines";
				if(loadItemRoutines(true))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					worldHandler.log.warning("Could not load " + progressString);
				
				progressString = "Offensive PVP routines";
				if(loadPVPRoutines(true, true))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					worldHandler.log.warning("Could not load " + progressString);
			}
			if(defensiveNode != null)
			{
				progressString = "generic damage types in Defensive";
				if(loadGenericRoutines(false))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					worldHandler.log.warning("Could not load " + progressString);
				
				progressString = "Defensive armor routines";
				if(loadArmorRoutines(false))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					worldHandler.log.warning("Could not load " + progressString);

				progressString = "Defensive item routines";
				if(loadItemRoutines(false))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					worldHandler.log.warning("Could not load " + progressString);
				
				progressString = "Defensive PVP routines";
				if(loadPVPRoutines(false, true))
					loadedSomething = true;
				else if(ModDamage.consoleDebugging_verbose)
					worldHandler.log.warning("Could not load " + progressString);
			}
			return loadedSomething;
		}
		catch(Exception e)
		{
			worldHandler.log.severe("[" + worldHandler.plugin.getDescription().getName() 
					+ "] Invalid configuration for group " + groupName + ", world \"" 
					+ worldHandler.getWorld().getName() + "\" - failed to load" + progressString + ".");
			return false;
		}
	}

	public boolean loadGenericRoutines(boolean isOffensive)
	{
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
					if(ModDamage.consoleDebugging_verbose) worldHandler.log.info("{Found group \"" + groupName + "\" generic " 
							+ (isOffensive?"Offensive":"Defensive") + " " + damageCategory + " node for world \"" 
							+ worldHandler.getWorld().getName() + "\"}");
					if(!calcStrings.equals(null)) //!calcStrings.equals(null)
					{
						damageCalc.checkCommandStrings(calcStrings, damageCategory, isOffensive);
						if(calcStrings.size() > 0)
						{
							if(!(isOffensive?offensiveRoutines:defensiveRoutines).containsKey(element))
							{
								(isOffensive?offensiveRoutines:defensiveRoutines).put(element, calcStrings);
								if(ModDamage.consoleDebugging_normal) worldHandler.log.info("-" + worldHandler.getWorld().getName() + ":" 
										+ groupName + ":" + (isOffensive?"Offensive":"Defensive") + ":Generic:" + damageCategory 
										+ calcStrings.toString());//debugging
							}
							else if(ModDamage.consoleDebugging_normal)
							{
								worldHandler.log.warning("Repetitive generic "  + damageCategory + " in " + (isOffensive?"Offensive":"Defensive") + " - ignoring");
								continue;
							}
						}
						else if(ModDamage.consoleDebugging_verbose)
							worldHandler.log.warning("No instructions found for generic " + damageCategory + " node - is this on purpose?");
					}
				}
				else if(ModDamage.consoleDebugging_verbose) worldHandler.log.info("Group \"" + groupName + "\" generic " + element.getReference() 
						+ " node for" + (isOffensive?"Offensive":"Defensive") + " not found.");
			}
			ConfigurationNode relevantNode = ((isOffensive?offensiveNode:defensiveNode).getNode(damageCategory));
			if(relevantNode != null)
			{
				if(ModDamage.consoleDebugging_verbose) worldHandler.log.info("{Found group \"" + groupName + "\" specific " + (isOffensive?"Offensive":"Defensive") + " " 
						+ damageCategory + " node for world \"" + worldHandler.getWorld().getName() + "\"}");
				if(DamageElement.matchDamageElement(damageCategory).hasSubConfiguration())
					for(DamageElement damageElement : DamageElement.getElementsOf(damageCategory))
					{
						String elementReference = damageElement.getReference();
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
									if(ModDamage.consoleDebugging_normal) worldHandler.log.info("-" + worldHandler.getWorld().getName() + ":" 
											+ groupName + ":" + (isOffensive?"Offensive":"Defensive") + ":" + elementReference 
											+ calcStrings.toString());//debugging
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
						else if(ModDamage.consoleDebugging_verbose) worldHandler.log.info("Group \"" + groupName 
								+ "\" " + damageElement.getReference() + " node for" + (isOffensive?"Offensive":"Defensive") 
								+ " not found.");
					}
			}
		}
		return true;
	}
	
	public boolean loadItemRoutines(boolean isOffensive){ return loadItemRoutines(isOffensive, false);}
	public boolean loadItemRoutines(boolean isOffensive, boolean force)
	{
		ConfigurationNode itemNode = (isOffensive?offensiveNode:defensiveNode).getNode(DamageElement.GENERIC_MELEE.getReference());
		if(itemNode != null)	
		{
			List<String> itemList = (isOffensive?offensiveNode:defensiveNode).getKeys(DamageElement.GENERIC_MELEE.getReference());
			List<String> calcStrings = null;
			for(Material material : Material.values())
			{
				if(itemList.contains(material.name())) //TODO Mess with casing here?
					calcStrings = itemNode.getStringList(material.name(), null);
				if(calcStrings != null)
				{
					damageCalc.checkCommandStrings(calcStrings, material.name(), isOffensive, groupName);
					if(calcStrings.size() > 0)
					{
						if(ModDamage.consoleDebugging_normal) 
							worldHandler.log.info("-" + worldHandler.getWorld().getName() 
								+ ":" + groupName + ":" + (isOffensive?"Offensive":"Defensive") 
								+ ":" + material.name() + "(" + material.getId() + ") " 
								+ calcStrings.toString());//debugging
						if(!(isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).containsKey(material))
							(isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).put(material, calcStrings);
						else if(ModDamage.consoleDebugging_normal) 
							worldHandler.log.warning("[" + worldHandler.plugin.getDescription().getName() + "] Repetitive " 
								+ material.name() + "(" + material.getId() + ") definition in group " + groupName 
								+ " " + (isOffensive?"Offensive":"Defensive") + " item settings - ignoring");
					}
					else if(ModDamage.consoleDebugging_verbose)
						worldHandler.log.warning("No instructions found for group \"" + groupName + "\" " + material.name() 
							+ "(" + material.getId()+ ") item node in " + (isOffensive?"Offensive":"Defensive") 
							+ " - is this on purpose?");
					calcStrings = null;
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean loadArmorRoutines(boolean isOffensive)
	{
		//if(ModDamage.consoleDebugging_normal) worldHandler.log.info("Loading " + (isOffensive?"Offensive":"Defensive") + " armor routines");//TODO remove this
		ConfigurationNode armorNode = (isOffensive?offensiveNode:defensiveNode).getNode(DamageElement.GENERIC_ARMOR.getReference());
		if(armorNode != null)
		{
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
						damageCalc.checkCommandStrings(calcStrings, armorSet.toString(), isOffensive, groupName);
						if(calcStrings.size() > 0)
						{
							if(ModDamage.consoleDebugging_normal) worldHandler.log.info("-" + worldHandler.getWorld().getName() 
									+ ":" + groupName + ":" + (isOffensive?"Offensive":"Defensive")
									+ ":armor:" + armorSet.toString() + " " + calcStrings.toString());//debugging
							if(!(isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).containsKey(armorSet))
								(isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).put(armorSet.toString(), calcStrings);
							else if(ModDamage.consoleDebugging_normal) worldHandler.log.warning("[" + worldHandler.plugin.getDescription().getName() + "] Repetitive\n\t:" 
									+ armorSet.toString() + "\ndefinition in " + (isOffensive?"Offensive":"Defensive") 
									+ " armor set for group \"" + groupName + "\"'s settings - ignoring");
						}
						else if(ModDamage.consoleDebugging_verbose)
							worldHandler.log.warning("No instructions found for group" + groupName + "\n\t:" 
									+ armorSet.toString() + "\narmor node in " + (isOffensive?"Offensive":"Defensive") 
									+ " - is this on purpose?");
					}
				}
				calcStrings = null;
			}
			return true;
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
					damageCalc.checkCommandStrings(calcStrings, "PVP", isOffensive, groupName);
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
						+ worldHandler.getWorld().getName() +  " - is this on purpose?");
				}
				if(ModDamage.consoleDebugging_normal)
					worldHandler.log.info("[" + worldHandler.plugin.getDescription().getName() + "] " + worldHandler.getWorld().getName() 
						+ ":" + groupName + ":" + (isOffensive?"Offensive":"Defensive") + ":PvP:" + group + " " + calcStrings.toString());//debugging
			}
		return true;
	}
	
	
///////////////////// SCAN ///////////////////////	
	private boolean loadScanItems() 
	{
		if(scanNode != null) 
		{
			if(ModDamage.consoleDebugging_verbose) worldHandler.log.info("{Found group \"" + groupName 
				+ "\" Scan node for world \"" + worldHandler.getWorld().getName() + "\"}");
			List<String> itemList = scanNode.getStringList(groupName, null);
			if(!itemList.equals(null))
			{
				//search for keyword-defined sets of materials
				for(String keyword : worldHandler.plugin.scanKeywords.keySet())
					if(itemList.contains(keyword)) //TODO Mess with casing here?
						if(!groupScanItems.contains(keyword)) 
							for(Material material : worldHandler.plugin.scanKeywords.get(keyword))
							{
								groupScanItems.add(material);
								if(ModDamage.consoleDebugging_normal)
									ModDamage.log.info("-" + worldHandler.getWorld().getName() + ":Scan:" 
										+ material.name() + "(" + material.getId() + ") ");
							}	
				//searching for normally-defined materials
				for(Material material : Material.values())	
					if(itemList.contains(material.name()) || itemList.contains(Integer.toString(material.getId()))) //TODO Mess with casing here?
						if(!groupScanItems.contains(material)) 
						{
							groupScanItems.add(material);
							if(ModDamage.consoleDebugging_normal)
								ModDamage.log.info("-" + worldHandler.getWorld().getName() + ":" + groupName + ":Scan:" 
									+ material.name() + "(" + material.getId() + ") ");
						}
				return true;
			}
		}
		return false;
	}
	
	public boolean canScan(Material itemType){ return(groupScanItems.contains(itemType));}
	

///////////////////// DAMAGE HANDLING ///////////////////////	
	
	public int calcAttackBuff(DamageElement mobType_target, Material inHand, ArmorSet armorSet_attacking, int eventDamage, DamageElement rangedMaterial)
	{
		return runRoutines(mobType_target.getType(), true, eventDamage)
		 		+ runRoutines(mobType_target, true, eventDamage)
				+ runRoutines(DamageElement.matchItemType(inHand), true, eventDamage)
				+ runArmorRoutines(armorSet_attacking, true, eventDamage)
				+ ((rangedMaterial != null)
					?(runRoutines(DamageElement.GENERIC_RANGED, true, eventDamage) + runRoutines(rangedMaterial, true, eventDamage))
					:runMeleeRoutines(inHand, true, eventDamage));
	}
	public int calcDefenseBuff(DamageElement damageType, ArmorSet armorSet_attacked, int eventDamage)
	{	
		return runRoutines(damageType.getType(), false, eventDamage)
 				+ runRoutines(damageType, false, eventDamage)
				+ runArmorRoutines(armorSet_attacked, false, eventDamage);
	}
	
	public int calcAttackBuff(String group_target, Material inHand, ArmorSet armorSet_attacking, int eventDamage, DamageElement rangedMaterial)
	{
		//TODO BOW weapon type
		worldHandler.log.info("Grouphandler: " + groupName + " says to " + group_target + ": LOLWUT");//TODO Remove me
		return runRoutines(DamageElement.GENERIC_HUMAN, true, eventDamage) 
				+ runRoutines(DamageElement.matchItemType(inHand), true, eventDamage)
				+ ((rangedMaterial != null)
					?(runRoutines(DamageElement.GENERIC_RANGED, true, eventDamage) + runRoutines(rangedMaterial, true, eventDamage))
					:runMeleeRoutines(inHand, true, eventDamage))
				+ runArmorRoutines(armorSet_attacking, true, eventDamage)
				+ runPVPRoutines(group_target, true, eventDamage);
	}
	public int calcDefenseBuff(String group_attacking, Material inHand, ArmorSet armorSet_attacked, int eventDamage, DamageElement rangedMaterial)
	{	
		worldHandler.log.info("Grouphandler: " + groupName + " says to " + group_attacking + ": BITE ME");//TODO Remove me
		return runRoutines(DamageElement.GENERIC_HUMAN, false, eventDamage)
				+ runRoutines(DamageElement.matchItemType(inHand), false, eventDamage)
				+ ((rangedMaterial != null)
					?(runRoutines(DamageElement.GENERIC_RANGED, false, eventDamage) + runRoutines(rangedMaterial, false, eventDamage))
					:runMeleeRoutines(inHand, false, eventDamage))
				+ runArmorRoutines(armorSet_attacked, false, eventDamage)
				+ runPVPRoutines(group_attacking, false, eventDamage);
	}
	
	private int runRoutines(DamageElement damageType, boolean isOffensive, int eventDamage)
	{
		if(damageType != null && (isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageType))
			return damageCalc.parseCommands((isOffensive?offensiveRoutines:defensiveRoutines).get(damageType), eventDamage, isOffensive);
		return 0;
	}
	
	private int runPVPRoutines(String groupName, boolean isOffensive, int eventDamage)
	{
		if(groupName != null && (isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).containsKey(groupName))
			return damageCalc.parseCommands((isOffensive?pvpOffensiveRoutines:pvpDefensiveRoutines).get(groupName), eventDamage, isOffensive);
		return 0;
	}
	
	private int runMeleeRoutines(Material material, boolean isOffensive, int eventDamage) 
	{
		if(material != null && (isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).containsKey(material))
			return damageCalc.parseCommands((isOffensive?itemOffensiveRoutines:itemDefensiveRoutines).get(material), eventDamage, isOffensive);
		return 0;
	}
	
	private int runArmorRoutines(ArmorSet armorSet, boolean isOffensive, int eventDamage)
	{
		worldHandler.log.info("Is he wearing clothes? " + armorSet.isEmpty() + armorSet.toString()); //TODO Remove me
		worldHandler.log.info("Matches preexisting: " + Boolean.toString((isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).containsKey(armorSet.toMaterialArray())));
		if(!armorSet.isEmpty() && (isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).containsKey(armorSet.toString()))
			return damageCalc.parseCommands((isOffensive?armorOffensiveRoutines:armorDefensiveRoutines).get(armorSet.toString()), eventDamage, isOffensive);
		return 0;
	}

///////////////////// HELPER FUNCTIONS ///////////////////////	
	public String getGroupName(){ return groupName;}

	private void clear() 
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
	
	protected boolean loadedSomething()
	{
		return (!offensiveRoutines.isEmpty() || !defensiveRoutines.isEmpty() 
				|| !pvpOffensiveRoutines.isEmpty() || !pvpDefensiveRoutines.isEmpty()
				|| !itemOffensiveRoutines.isEmpty() || !itemDefensiveRoutines.isEmpty()
				|| !groupScanItems.isEmpty());
	}
}

	