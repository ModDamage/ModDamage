package com.KoryuObihiro.bukkit.ModDamage.Backend.Handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculationAllocator;

public class GroupHandler extends Handler 
{
	final protected String groupName;
	
	final private WorldHandler worldHandler;
	
	public GroupHandler(ModDamage plugin, WorldHandler worldHandler, Logger log,  String name, ConfigurationNode offensiveNode, ConfigurationNode defensiveNode, ConfigurationNode scanNode, DamageCalculationAllocator damageCalcAllocator) 
	{
		this.isGroupHandler = true;
		this.plugin = plugin;
		this.log = log;
		this.worldHandler = worldHandler;
		this.groupName = name;
		this.offensiveNode = offensiveNode;
		this.defensiveNode = defensiveNode;
		this.scanNode = scanNode;
		this.damageAllocator = damageCalcAllocator;
		
		reload();
	}

	@Override
	protected void loadAdditionalConfiguration(){ scanLoaded = loadScanItems();}
	
	
///////////////////// DAMAGE HANDLING ///////////////////////
	public void doAttackCalculations(DamageEventInfo eventInfo) 
	{
		switch(eventInfo.eventType)
		{
///////////////////// Player vs. Player 
			case PLAYER_PLAYER:
				runRoutines(eventInfo, true);		
				runEquipmentRoutines(eventInfo, true);
			return;

///////////////////// Player vs. Mob
			case PLAYER_MOB:
				runRoutines(eventInfo, true);
				runEquipmentRoutines(eventInfo, true);
			return;
			
			default:
				log.severe("[ModDamage] Oops...THAT wasn't supposed to happen.(Att)");//TODO REMOVE ME
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
			
			default: 
				log.severe("[ModDamage] Oops...THAT wasn't supposed to happen. (Def)");//TODO REMOVE ME
			return;
		}
	}
	
	@Override
	protected void runRoutines(DamageEventInfo eventInfo, boolean isOffensive)
	{
		super.runRoutines(eventInfo, isOffensive);
		super.runEquipmentRoutines(eventInfo, isOffensive);
	}
	protected void runGroupRoutines(DamageEventInfo eventInfo, boolean isOffensive)
	{
		if((isOffensive?eventInfo.groups_target:eventInfo.groups_attacker) != null)
			for(String group_other : (isOffensive?eventInfo.groups_target:eventInfo.groups_attacker))
				if((isOffensive?groupOffensiveRoutines:groupDefensiveRoutines).containsKey(group_other))
					calculateDamage(eventInfo, (isOffensive?groupOffensiveRoutines:groupDefensiveRoutines).get(group_other));
	}
	
///////////////////// SCAN ///////////////////////	
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
							String configString = "-Scan:" + getConfigPath() + ":" + material.name() + "(" + material.getId() + ")";
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
							String configString = "-Scan:" + getConfigPath() + ":" + material.name() + "(" + material.getId() + ") ";
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
	@Override
	protected String getConfigPath(){ return worldHandler.getWorld().getName() + ":groups:" + groupName;}
	
	@Override
	protected String getDisplayString(boolean upperCase){ return (upperCase?"G":"g") + "roup \"" + groupName + "\" (world \"" + worldHandler.getWorld().getName() + "\")";}
	
	public String getGroupName(){ return groupName;}
}
