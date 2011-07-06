package com.KoryuObihiro.bukkit.ModDamage.Backend.Handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

public class ServerHandler extends WorldHandler
{
//// MEMBERS ////
	public static final HashMap<World, WorldHandler> worldHandlers = new HashMap<World, WorldHandler>(); //groupHandlers are allocated within the WorldHandler class
	public HashMap<String, WorldHandler> worldHandlers;
	
	private boolean worldHandlersLoaded;

//// FUNCTIONS ////
//// CONSTRUCTOR ////
	public ServerHandler(ModDamage plugin, ConfigurationNode damageNode, ConfigurationNode mobHealthNode, ConfigurationNode scanNode) 
	{
		super(null, (damageNode != null?damageNode.getList("global"):new ArrayList<Object>()), (mobHealthNode != null?mobHealthNode.getNode("global"):null), (scanNode != null?scanNode.getStringList("global", new ArrayList<String>()):new ArrayList<String>()), (scanNode != null?scanNode.getNode("groups"):null));
		WorldHandler.plugin = plugin;
		//try to initialize WorldHandlers
		String nodeNames[] = {"Damage node", "MobHealth node", "Global Scan items", "Group Scan items"};
		if((damageNode != null?damageNode.getNode("worlds"):null) != null 
			|| (mobHealthNode != null?mobHealthNode.getNode("worlds"):null) != null 
			|| (scanNode != null?scanNode.getNode("worlds"):null) != null)
		{
			for(World world : plugin.getServer().getWorlds())
			{
				List<Object> worldConfigurations = Arrays.asList(((damageNode != null && damageNode.getNode("worlds") != null)?damageNode.getNode("worlds").getList(world.getName()):null),
														((mobHealthNode != null && mobHealthNode.getNode("worlds") != null)?mobHealthNode.getNode("worlds").getNode(world.getName()):null),
														((scanNode != null && scanNode.getNode("worlds." + world.getName() + ".global") != null)?scanNode.getNode("worlds." + world.getName() + ".global").getStringList(world.getName(), null):null),
														((scanNode != null && scanNode.getNode("worlds." + world.getName() + ".groups") != null)?scanNode.getNode("worlds." + world.getName() + ".groups"):null));
				if(ModDamage.consoleDebugging_verbose) log.info("{Loading configuration for world \"" + world.getName() + "\"}");
				boolean foundSomething = false;
				for(int i = 0; i < worldConfigurations.size(); i++)
				{
					if(worldConfigurations.get(i) != null)
						foundSomething = true;
					else if(ModDamage.consoleDebugging_verbose) log.warning("{" + nodeNames[i] + " not found for world \"" + world.getName() + "\"}");
				}
				if(foundSomething)
				{
					WorldHandler worldHandler = new WorldHandler(world.getName(), (List<Object>)worldConfigurations.get(0), (ConfigurationNode)worldConfigurations.get(1), (List<String>)worldConfigurations.get(2), (ConfigurationNode)worldConfigurations.get(3));
					if(worldHandler.loadedSomething())
					{
						worldHandlers.put(world, worldHandler);
						worldHandlersLoaded = true;
					}
					else if(ModDamage.consoleDebugging_verbose) log.severe("{No configuration loaded for world " + world.getName() + "\"}");
				}
				else if(ModDamage.consoleDebugging_verbose) log.warning("{No configuration found for world " + world.getName() + "\"}");
			}
		}
		
		if(!loadedSomething()) log.severe("[" + plugin.getDescription().getName() + "] No configurations loaded! Are any calculation strings defined?");
		super(plugin, null, offensiveNode, defensiveNode, mobHealthNode, scanNode, damageCalculationAllocator, healthCalculationAllocator);
		additionalConfigChecks = 2;
		//TODO set aliases - this will be moved into reload() once dynamic nodes have been implemented.
		// DON'T FORGET - casing needs to be handled, so that it's not an issue.
		/*
		*/
		additionalConfigChecks = 2;
	}
	
//// CONFIG LOADING ////
	
//// DAMAGE ////
	@Override
	public void doDamageCalculations(DamageEventInfo eventInfo)
	{ 
		super.doDamageCalculations(eventInfo);
		if(worldHandlers.containsKey(eventInfo.world.getName())) worldHandlers.get(eventInfo.world.getName()).doDamageCalculations(eventInfo);
	}
	
//// SPAWN HANDLING ////
	@Override
	public boolean doSpawnCalculations(SpawnEventInfo eventInfo){ return super.doSpawnCalculations(eventInfo) || (worldHandlers.containsKey(eventInfo.world.getName())?worldHandlers.get(eventInfo.world.getName()).doSpawnCalculations(eventInfo):false);}	

	@Override
	public boolean load()
	{
		worldHandlersLoaded = false;
		worldHandlers = new HashMap<String, WorldHandler>();
	//try to initialize WorldHandlers
		String nodeNames[] = {"Offensive", "Defensive", "MobHealth", "Scan"};
		if(offensiveNode != null || defensiveNode != null || mobHealthNode != null || scanNode != null)
			for(World world : plugin.getServer().getWorlds())
			{
				ConfigurationNode worldNodes[] = {	(offensiveNode != null && offensiveNode.getNode("worlds") != null?offensiveNode.getNode("worlds").getNode(world.getName()):null), 
													(defensiveNode != null && defensiveNode.getNode("worlds") != null?defensiveNode.getNode("worlds").getNode(world.getName()):null), 
													(mobHealthNode != null && mobHealthNode.getNode("worlds") != null?mobHealthNode.getNode("worlds").getNode(world.getName()):null),
													(scanNode != null && scanNode.getNode("worlds") != null?scanNode.getNode("worlds").getNode(world.getName()):null)};
				for(int i = 0; i < worldNodes.length; i++)
					if(worldNodes[i] == null && (ModDamage.consoleDebugging_verbose))
						log.warning("{Couldn't find " + nodeNames[i] +  " node for world \"" + world.getName() + "\"}");
				WorldHandler worldHandler = new WorldHandler(plugin, world, worldNodes[0], worldNodes[1], worldNodes[2], worldNodes[3], damageCalculationAllocator, healthCalculationAllocator);
				if(worldHandler.loadedSomething())
				{
					worldHandlers.put(world.getName(), worldHandler);
					worldHandlersLoaded = true;
				}
			}

		mobHealthNode = (mobHealthNode != null?mobHealthNode.getNode("global"):null);
		super.load();
		if(!loadedSomething()) log.severe("[" + plugin.getDescription().getName() + "] No configurations loaded! Are any calculation strings defined?");
		return loadedSomething();
	}

//// HELPER FUNCTIONS ////
	@Override
	protected String getDisplayString(boolean uppercase){ return (uppercase?"S":"s") + "erver";}
	
	@Override
	protected String getCalculationHeader(){ return "server";}

	private String getWorldMatch(String name, boolean searchSubstrings)
	{
		for(World world : plugin.getServer().getWorlds())
			if(name.equalsIgnoreCase(world.getName()))
				return world.getName();
		
		if(searchSubstrings)
			for(World world : plugin.getServer().getWorlds())
				for(int i = 0; i < (world.getName().length() - name.length() - 1); i++)
					if(name.equalsIgnoreCase(world.getName().substring(i, i + name.length())))
						return world.getName();
		return null;
	}

	@Override
	public boolean loadedSomething(){ return super.loadedSomething() || worldHandlersLoaded;}

//// INGAME CONFIG ////
	@Override
	public boolean printAdditionalConfiguration(Player player, int pageNumber)
	{
		if(pageNumber == configPages + 1)
		{
			player.sendMessage(ModDamage.ModDamageString(ChatColor.GOLD) + " " + getDisplayString(true) + " (" + pageNumber + "/" + (configPages + 2) + ")");
			player.sendMessage(ModDamage.ModDamageString(ChatColor.YELLOW) + " The following worlds have been configured:");
			if(!worldHandlers.isEmpty())
			{
				for(WorldHandler worldHandler : worldHandlers.values())
						player.sendMessage(ChatColor.GREEN + worldHandler.getName());
				player.sendMessage(ChatColor.BLUE + "Use /md check [worldname] for more info.");
			}
			else player.sendMessage(ChatColor.RED + "No worlds configured!");
			return true;
		}
		return false;
	}
	
	public void sendConfig(Player player, String worldSearchTerm){ sendConfig(player, worldSearchTerm, 1);}
	public void sendConfig(Player player, String worldSearchTerm, int pageNumber)
	{
		//TODO Refactor for a single function?
		String worldMatch = getWorldMatch(worldSearchTerm, true);
		if(worldMatch != null)
		{
			if(ModDamage.hasPermission(player, "moddamage.check." + worldMatch))
			{
				if(!worldHandlers.get(worldMatch).sendConfig(player, pageNumber))
					player.sendMessage(ModDamage.ModDamageString(ChatColor.RED) + " Invalid page number for world \"" + worldMatch + "\".");
			}
			else player.sendMessage(ModDamage.ModDamageString(ChatColor.RED) 
					+ " You don't have permission to check world \"" + worldMatch + "\"");
		}
		else player.sendMessage(ModDamage.errorString_findWorld);
	}
}
