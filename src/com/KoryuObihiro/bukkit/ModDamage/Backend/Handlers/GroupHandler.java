package com.KoryuObihiro.bukkit.ModDamage.Backend.Handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculationAllocator;

public class GroupHandler extends Handler 
{
//// MEMBERS //// 
	final protected String groupName;
	
	final private WorldHandler parentHandler;

//// FUNCTIONS ////
//// CONSTRUCTOR ////
	public GroupHandler(ModDamage plugin, WorldHandler worldHandler, Logger log,  String name, ConfigurationNode offensiveNode, ConfigurationNode defensiveNode, ConfigurationNode scanNode, DamageCalculationAllocator damageCalcAllocator) 
	{
		this.plugin = plugin;
		this.log = log;
		this.parentHandler = worldHandler;
		this.groupName = name;
		this.offensiveNode = this.offensiveGlobalNode = offensiveNode;
		this.defensiveNode = this.defensiveGlobalNode =defensiveNode;
		this.scanNode = this.scanGlobalNode = scanNode;
		this.damageAllocator = damageCalcAllocator;
		
		load();
	}

//// CONFIG LOADING ////
	@Override
	protected void loadAdditionalConfiguration(){ scanLoaded = loadScanItems();}

//// DAMAGE HANDLING ////
	public void doAttackCalculations(DamageEventInfo eventInfo) 
	{
		switch(eventInfo.eventType)
		{
///////////////////// Player vs. Player 
			case PLAYER_PLAYER:
				runRoutines(eventInfo, true);		
				runEquipmentRoutines(eventInfo, true);
				runGroupRoutines(eventInfo, true);
			return;

///////////////////// Player vs. Mob
			case PLAYER_MOB:
				runRoutines(eventInfo, true);
				runEquipmentRoutines(eventInfo, true);
			return;
			
			default:
				log.severe("[ModDamage] Oops...THAT wasn't supposed to happen.(Att)");//TODO REMOVE ME...MEBBE
			return;
		}
	}
	
	public void doDefenseCalculations(DamageEventInfo eventInfo)
	{
		switch(eventInfo.eventType)
		{
///////////////////// Player vs. Player 
			case PLAYER_PLAYER:
				runRoutines(eventInfo, false);
				runEquipmentRoutines(eventInfo, false);
				runGroupRoutines(eventInfo, false);
			return;
				
///////////////////// Mob vs. Player
			case MOB_PLAYER:
				runRoutines(eventInfo, false);//defense buff
				runEquipmentRoutines(eventInfo, false);
			return;
			
///////////////////// Nonliving vs. Player
			case NONLIVING_PLAYER:
				runRoutines(eventInfo, false);//defense buff
				runEquipmentRoutines(eventInfo, false);
			return;
		}
	}

///////////////////// ROUTINE-SPECIFIC CALLS	
	@Override
	protected void runRoutines(DamageEventInfo eventInfo, boolean isOffensive)
	{ 
		DamageElement damageElement = (isOffensive?eventInfo.damageElement_target:eventInfo.damageElement_attacker);
		if((isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageElement.getType()))
			calculateDamage(eventInfo, (isOffensive?offensiveRoutines:defensiveRoutines).get(damageElement.getType()));
		if((isOffensive?offensiveRoutines:defensiveRoutines).containsKey(damageElement))
			calculateDamage(eventInfo, (isOffensive?offensiveRoutines:defensiveRoutines).get(damageElement));
	}

	@Override
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
			calculateDamage(eventInfo, (isOffensive?armorOffensiveRoutines.get(eventInfo.armorSetString_target):armorDefensiveRoutines.get(eventInfo.armorSetString_target)));
	}
	
	protected void runGroupRoutines(DamageEventInfo eventInfo, boolean isOffensive)
	{
		if((isOffensive?eventInfo.groups_target:eventInfo.groups_attacker) != null)
			for(String group_other : (isOffensive?eventInfo.groups_target:eventInfo.groups_attacker))
				if((isOffensive?groupOffensiveRoutines:groupDefensiveRoutines).containsKey(group_other))
					calculateDamage(eventInfo, (isOffensive?groupOffensiveRoutines:groupDefensiveRoutines).get(group_other));
	}

//// SCAN ////
	protected boolean loadScanItems() 
	{
		boolean loadedSomething = false;
		if(scanNode != null) 
		{
			if(ModDamage.consoleDebugging_verbose) log.info("{Found Scan node for " + getDisplayString(false) + "}");
			List<String> itemList = scanNode.getStringList(groupName, new ArrayList<String>());
			if(!itemList.isEmpty())
				for(String itemString : itemList)
				{
					if(ModDamage.itemAliases.containsKey(itemString.toLowerCase()))
						for(Material material : ModDamage.itemAliases.get(itemString.toLowerCase()))
						{
							scanItems.add(material);
							String configString = "-Scan:" + getCalculationHeader() + ":" + material.name() + "(" + material.getId() + ")";
							configStrings.add(configString);
							if(ModDamage.consoleDebugging_normal) log.info(configString);
							loadedSomething = true;
						}
					else
					{
						Material material = Material.matchMaterial(itemString);
						if(material != null)
						{
							scanItems.add(material);
							String configString = "-Scan:" + getCalculationHeader() + ":" + material.name() + "(" + material.getId() + ") ";
							configStrings.add(configString);
							if(ModDamage.consoleDebugging_normal) log.info(configString);
							loadedSomething = true;
						}
						else if(ModDamage.consoleDebugging_verbose) log.warning("Invalid Scan item \"" + itemString + "\" found for " + getDisplayString(false) + " - ignoring");
					}
				}
		}
		return loadedSomething;
	}
	
	public boolean canScan(Material itemType){ return(scanItems.contains(itemType));}

//// HELPER FUNCTIONS ////
	@Override
	protected String getCalculationHeader(){ return parentHandler.getCalculationHeader() + ":groups:" + groupName;}
	
	@Override
	protected String getDisplayString(boolean upperCase){ return (upperCase?"G":"g") + "roup \"" + groupName + "\" (" + parentHandler.getDisplayString(false) + ")";}
	
	public String getGroupName(){ return groupName;}

////INGAME COMMANDS ////
	@Override
	public void printAdditionalStrings(){}

	@Override
	public boolean printAdditionalConfiguration(Player player, int pageNumber){ return false;}

	@Override
	protected String getConfigPath(){ return parentHandler.getConfigPath() + "groups." + groupName + ".";}
}
