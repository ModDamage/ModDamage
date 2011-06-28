package com.KoryuObihiro.bukkit.ModDamage.Backend.Handlers;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculationAllocator;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculationAllocator;



public class ServerHandler extends WorldHandler
{
	public static final HashMap<World, WorldHandler> worldHandlers = new HashMap<World, WorldHandler>(); //groupHandlers are allocated within the WorldHandler class
	private static final DamageCalculationAllocator damageCalculationAllocator = new DamageCalculationAllocator();
	private static final SpawnCalculationAllocator healthCalculationAllocator = new SpawnCalculationAllocator();
	
	private boolean worldHandlersLoaded = false;
	
	public ServerHandler(ModDamage plugin, ConfigurationNode offensiveNode, ConfigurationNode defensiveNode, ConfigurationNode mobHealthNode, ConfigurationNode scanNode) 
	{
		super(plugin, null, offensiveNode, defensiveNode, mobHealthNode, scanNode, damageCalculationAllocator, healthCalculationAllocator);

		//TODO set aliases - this will be moved into reload() once dynamic nodes have been implemented.
		// DON'T FORGET - casing needs to be handled, so that it's not an issue.
		ModDamage.itemAliases.put("axe", Arrays.asList(Material.WOOD_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE));
		ModDamage.itemAliases.put("hoe", Arrays.asList(Material.WOOD_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLD_HOE, Material.DIAMOND_HOE));
		ModDamage.itemAliases.put("pickaxe", Arrays.asList(Material.WOOD_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE));
		ModDamage.itemAliases.put("spade", Arrays.asList(Material.WOOD_SPADE, Material.STONE_SPADE, Material.IRON_SPADE, Material.GOLD_SPADE, Material.DIAMOND_SPADE));
		ModDamage.itemAliases.put("sword", Arrays.asList(Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD));
	}
	
//// DAMAGE HANDLING ////
	@Override
	public void doDamageCalculations(DamageEventInfo eventInfo)
	{ 
		super.doDamageCalculations(eventInfo);
		if(worldHandlers.containsKey(eventInfo.world)) worldHandlers.get(eventInfo.world).doDamageCalculations(eventInfo);
	}
	
//// SPAWN HANDLING ////
	@Override
	public boolean doSpawnCalculations(SpawnEventInfo eventInfo){ return super.doSpawnCalculations(eventInfo) || (worldHandlers.containsKey(eventInfo.world)?worldHandlers.get(eventInfo.world).doSpawnCalculations(eventInfo):false);}	

	@Override
	public boolean reload()
	{
		super.reload();
		
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
					worldHandlers.put(world, worldHandler);
					worldHandlersLoaded = true;
				}
			}
		if(!loadedSomething()) log.severe("[" + plugin.getDescription().getName() + "] No configurations loaded! Are any calculation strings defined?");
		return loadedSomething();
	}
	
//// HELPER FUNCTIONS ////
	@Override
	public void clear()
	{
		super.clear();
		worldHandlers.clear();
	}
	
	@Override
	protected String getDisplayString(boolean uppercase){ return (uppercase?"S":"s") + "erver";}
	
	@Override
	protected String getConfigPath(){ return "";}

	private World getWorldMatch(String name, boolean searchSubstrings)
	{
		for(World world : plugin.getServer().getWorlds())
			if(name.equalsIgnoreCase(world.getName()))
				return world;
		
		if(searchSubstrings)
			for(World world : plugin.getServer().getWorlds())
				for(int i = 0; i < (world.getName().length() - name.length() - 1); i++)
					if(name.equalsIgnoreCase(world.getName().substring(i, i + name.length())))
						return world;
		return null;
	}
	
	private String getGroupMatch(World world, String name, boolean searchSubstrings)
	{
		for(GroupHandler groupHandler : worldHandlers.get(world).getGroupHandlers())
			if(name.equalsIgnoreCase(groupHandler.getGroupName()))
				return groupHandler.getGroupName();
		if(searchSubstrings)
			for(GroupHandler groupHandler : worldHandlers.get(world).getGroupHandlers())
				for(int i = 0; i < (groupHandler.getGroupName().length() - name.length() - 1); i++)
					if(name.equalsIgnoreCase(groupHandler.getGroupName().substring(i, i + name.length())))
						return groupHandler.getGroupName();
		return null;
	}

	@Override
	public boolean loadedSomething(){ return super.loadedSomething() || worldHandlersLoaded;}

//// INGAME CONFIG ////
	public void sendConfig(Player player, int pageNumber)
	{
		if(player == null)
		{
			for(WorldHandler worldHandler : worldHandlers.values())
				if(worldHandler.loadedSomething())
					super.sendWorldConfig(player, pageNumber);
		}
		else
		{
			player.sendMessage(ModDamage.ModDamageString(ChatColor.YELLOW) + " The following worlds have been configured:");
			boolean sentSomething = false;
			for(WorldHandler worldHandler : worldHandlers.values())
				if(worldHandler.loadedSomething())
				{
					player.sendMessage(ChatColor.GREEN + worldHandler.getWorld().getName());
					sentSomething = true;
				}
			player.sendMessage(sentSomething
								?ChatColor.BLUE + "Use /md check [worldname] for more information."
								:ChatColor.RED + "No worlds configured!");
		}
	}
	
	public void sendConfig(Player player, String worldSearchTerm){ sendConfig(player, worldSearchTerm, 1);}
	public void sendConfig(Player player, String worldSearchTerm, int pageNumber)
	{
		//TODO Refactor for a single function?
		World worldMatch = getWorldMatch(worldSearchTerm, true);
		if(worldMatch != null)
		{
			if(ModDamage.hasPermission(player, "moddamage.check." + worldMatch.getName()))
			{
				if(!worldHandlers.get(worldMatch).sendWorldConfig(player, pageNumber))
					player.sendMessage(ModDamage.ModDamageString(ChatColor.RED) + " Invalid page number for world \"" + worldMatch.getName() + "\".");
			}
			else player.sendMessage(ModDamage.ModDamageString(ChatColor.RED) 
					+ " You don't have permission to check world \"" + worldMatch.getName() + "\"");
		}
		else player.sendMessage(ModDamage.errorString_findWorld);
	}
	public void sendConfig(Player player, String worldSearchTerm, String groupSearchTerm){ sendConfig(player, worldSearchTerm, groupSearchTerm, 1);}
	public void sendConfig(Player player, String worldSearchTerm, String groupSearchTerm, int pageNumber) 
	{
		World worldMatch = getWorldMatch(worldSearchTerm, true);
		if(worldMatch != null)
		{
			String groupMatch = getGroupMatch(worldMatch, groupSearchTerm, true);
			if(groupMatch != null)
			{
				if(ModDamage.hasPermission(player, "moddamage.check." + worldMatch.getName() + "." + groupMatch))
				{
					if(!worldHandlers.get(worldMatch).getGroupHandler(groupMatch).sendGroupConfig(player, pageNumber))
						player.sendMessage(ModDamage.ModDamageString(ChatColor.RED) + " You don't have permission to check group \"" 
								+ groupMatch + "\" for world \"" + worldMatch.getName() + "\".");
				}
				else player.sendMessage(ModDamage.ModDamageString(ChatColor.RED) + " You don't have permission to check group \"" + groupMatch + "\".");
			}
			else player.sendMessage(ModDamage.ModDamageString(ChatColor.RED) + " Couldn't find matching group name.");
		}
		else
		{
			if(player == null) log.info("Error: Couldn't find matching world substring.");
			else player.sendMessage(ModDamage.errorString_findWorld);
		}
	}

}