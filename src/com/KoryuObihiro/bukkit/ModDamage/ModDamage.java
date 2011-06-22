package com.KoryuObihiro.bukkit.ModDamage;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.GroupHandler;
import com.KoryuObihiro.bukkit.ModDamage.Backend.WorldHandler;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculationAllocator;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.HealthCalculationAllocator;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * "ModDamage" for Bukkit
 * 
 * @author Erich Gubler
	 *
	 */
public class ModDamage extends JavaPlugin
{
	//TODO Deregister when Bukkit supports!
	//TODO Client-sided mod for displaying health?
	//TODO Add worldLoad-triggered loading of MD
	//plugin-related
	public boolean isEnabled = false;
	private final ModDamageEntityListener entityListener = new ModDamageEntityListener(this);
	public static Logger log = Logger.getLogger("Minecraft");
	public static PermissionHandler Permissions = null;
	private Configuration config;
	private String errorString_Permissions = ModDamageString(ChatColor.RED) + " You don't have access to that command.";
	private String errorString_findWorld = ModDamageString(ChatColor.RED) + " Couldn't find matching world name.";
	
	//Configuration
	private ConfigurationNode pluginOffensiveNode, pluginDefensiveNode, pluginMobHealthNode, pluginScanNode;
	public boolean multigroupPermissions = true;
	private final DamageCalculationAllocator damageCalc = new DamageCalculationAllocator();
	private final HealthCalculationAllocator healthCalc = new HealthCalculationAllocator();
	public final HashMap<String, List<Material>> itemKeywords = new HashMap<String, List<Material>>();
	
	//User-customized config
	public static boolean consoleDebugging_normal = true;
	public static boolean consoleDebugging_verbose = false;
	public static boolean disable_DefaultDamage;
	public static boolean disable_DefaultHealth;
	public static boolean negative_Heal;
	public final HashMap<World, WorldHandler> worldHandlers = new HashMap<World, WorldHandler>(); //groupHandlers are allocated within the WorldHandler class
	
////////////////////////// INITIALIZATION ///////////////////////////////
	@Override
	public void onEnable() 
	{
	//PERMISSIONS
		Plugin permissionsPlugin = getServer().getPluginManager().getPlugin("Permissions");
		if (permissionsPlugin != null)
		{
			ModDamage.Permissions = ((Permissions)permissionsPlugin).getHandler();
			log.info("[" + getDescription().getName() + "] " + this.getDescription().getVersion() 
					+ " enabled [Permissions v" + permissionsPlugin.getDescription().getVersion() + " active]");
			
			multigroupPermissions = permissionsPlugin.getDescription().getVersion().startsWith("3.");
		}
		else
			log.info("[" + getDescription().getName() + "] " + this.getDescription().getVersion() 
					+ " enabled [Permissions not found]");
		
		//TODO Use something less gimmicky. :P
		itemKeywords.put("axe", Arrays.asList(Material.WOOD_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE));
		itemKeywords.put("hoe", Arrays.asList(Material.WOOD_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLD_HOE, Material.DIAMOND_HOE));
		itemKeywords.put("pickaxe", Arrays.asList(Material.WOOD_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE));
		itemKeywords.put("spade", Arrays.asList(Material.WOOD_SPADE, Material.STONE_SPADE, Material.IRON_SPADE, Material.GOLD_SPADE, Material.DIAMOND_SPADE));
		itemKeywords.put("sword", Arrays.asList(Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD));
		
		//register plugin-related stuff with the server's plugin manager
		getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, this);
		getServer().getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Event.Priority.Highest, this);
		
		reload(true);
		
		isEnabled = true;
	}

	@Override
	public void onDisable() 
	{
		log.info("[" + getDescription().getName() + "] disabled.");	
		worldHandlers.clear();
	}


////////////////////////// COMMAND PARSING ///////////////////////////////
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		//TODO Add "add", "delete", etc.
		Player player = ((sender instanceof Player)?((Player)sender):null);
		boolean fromConsole = (player == null);
		
			if (label.equalsIgnoreCase("ModDamage") || label.equalsIgnoreCase("md"))
			{
				if (args.length == 0)
				{
					sendUsage(player, false);
					return true;
				}
				else if(args.length >= 0)
				{
					/*
					if(args[0].equalsIgnoreCase("configuration") || args[0].equalsIgnoreCase("config"))
					{
						if(fromConsole || hasPermission(player, "moddamage.configuration"))
						{
							if(args.length == 3)
								if(args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("del"))
									delete(args[2]);
							if(args.length >= 4)
							{
								if(args[1].equalsIgnoreCase("add"))
								{
									List<String> calcStrings = new ArrayList<String>();
									for(int i = 3; i < args.length; i++)
										calcStrings.add(args[i]);
									add(args[2], calcStrings);
								}
							}
							else sendUsage(player, true);
						}
						else player.sendMessage(errorString_Permissions);
					}
					else */
					if(args[0].equalsIgnoreCase("debug") || args[0].equalsIgnoreCase("d"))
						{
							if(fromConsole || hasPermission(player, "moddamage.debug"))
							{
								if(args.length == 1)
									toggleConsoleDebug(player);
								else if(args.length == 2)
								{
									String sendThis;
									if(args[1].equalsIgnoreCase("quiet") || args[0].equalsIgnoreCase("q"))
									{
										consoleDebugging_normal = false;
										consoleDebugging_verbose = false;
										sendThis = "[" + getDescription().getName() + "] \"Quiet\" mode " + (consoleDebugging_normal?"enabled - suppressing debug messages and warnings.":"already active!");
										config.setProperty("debugging", "quiet");
										config.save();
									}
									else if(args[1].equalsIgnoreCase("normal") || args[0].equalsIgnoreCase("n"))
									{
										if(consoleDebugging_normal && !consoleDebugging_verbose)
											sendThis = "[" + getDescription().getName() + "] Debugging already active!";
										else sendThis = "[" + getDescription().getName() + "] " + (consoleDebugging_verbose?"Debugging enabled.":"Verbose debugging disabled - normal debugging enabled.");
										consoleDebugging_normal = true;
										consoleDebugging_verbose = false;
										config.setProperty("debugging", "normal");
										config.save();
									}
									else if(args[1].equalsIgnoreCase("verbose") || args[0].equalsIgnoreCase("v"))
									{
										sendThis = "[" + getDescription().getName() + "] " + (consoleDebugging_normal?"Verbose debugging enabled.":"Verbose debugging already active!");
										consoleDebugging_normal = true;
										consoleDebugging_verbose = true;
										config.setProperty("debugging", "verbose");
										config.save();
									}
									else
									{
										sendUsage(player, true);
										return true;
									}
									log.info(sendThis);
									if(!fromConsole) player.sendMessage(ChatColor.GREEN + sendThis);
									return true;
								}
							}
							else player.sendMessage(errorString_Permissions);
							return true;
						}
						else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r"))
						{
							if(fromConsole) reload(true);
							else if(hasPermission(player, "moddamage.reload")) 
							{
								log.info("[" + getDescription().getName() + "] Reload initiated by user " + player.getName());
								if(reload(true)) player.sendMessage(ModDamageString(ChatColor.GREEN) + " Reloaded!");
								else player.sendMessage(ModDamageString(ChatColor.RED) + " No configurations loaded! Are any calculation strings defined?");
							}
							else player.sendMessage(errorString_Permissions);
							return true;
						}
						else if(args[0].equalsIgnoreCase("enable"))
						{
							if(fromConsole || hasPermission(player, "moddamage.enable"))
								setPluginStatus(player, true);
							else player.sendMessage(errorString_Permissions);
							return true;
						}
						else if(args[0].equalsIgnoreCase("disable"))
						{
							if(fromConsole || hasPermission(player, "moddamage.disable"))
									setPluginStatus(player, false);
							else player.sendMessage(errorString_Permissions);
							return true;
						}
					if(isEnabled)
					{
						if(args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("c"))
						{
							//md check
							if(args.length == 1)
							{
								//Send everything if from console
								if(fromConsole)
								{
									log.info("[" + getDescription().getName() + "] Sending server config info...");
									for(WorldHandler worldHandler : worldHandlers.values())
										if(worldHandler.loadedSomething())
											worldHandler.sendWorldConfig(null, 9001);
								}
								//Send list of loaded worlds
								else if(hasPermission(player, "moddamage.check"))
								{
									player.sendMessage(ModDamageString(ChatColor.YELLOW) + " The following worlds have been configured:");
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
								else player.sendMessage(errorString_Permissions);
								return true;
							}
							//md check worldname || md check int
							//Console doesn't need specifying commands, so just stop console stuff here.
							else if(fromConsole) sendUsage(player, true);
							else if(args.length == 2)
							{
								try
								{
									sendConfig(player, args[1]); //TODO Page the world output...eventually. :P
									//sendConfig(player, Integer.parseInt(args[1]));
								} 
								catch(NumberFormatException e){}
								return true;
							}
							//md check worldname groupname || md check worldname int
							else if(args.length == 3)
							{
								try
								{
									sendConfig(player, args[1], Integer.parseInt(args[2]));
								}
								catch(NumberFormatException e)
								{
									sendConfig(player, args[1], args[2]);
								}
								return true;
							}
							//md check worldname groupname int
							else if(args.length == 4)
							{
								try
								{
									sendConfig(player, args[1], args[2], Integer.parseInt(args[3]));
								}
								catch(NumberFormatException e){}
								return true;
							}
						}
						else
						{
							sendUsage(player, true);
							return true;
						}
					}
					else if(player == null)
						log.info("[" + getDescription().getName() + "] ModDamage must be enabled to use that command.");
					else player.sendMessage(ModDamageString(ChatColor.RED) + " ModDamage must be enabled to use that command.");
					return true;
				}
			}
		sendUsage(player, true);
		return true;
	}

/////////////////// EVENT FUNCTIONS ////////////////////////////
	public void passDamageEvent(EntityDamageEvent event) 
	{
		if(isEnabled && (event.getEntity() instanceof LivingEntity))
		{
			//simple check for noDamageTicks - 
			if(((LivingEntity)event.getEntity()).getNoDamageTicks() > 50) //give this some leeway because this may be the time it takes to execute
			{
				event.setCancelled(true);
				return;
			}
			LivingEntity ent_damaged = (LivingEntity)event.getEntity();
			WorldHandler worldHandler = (worldHandlers.containsKey(event.getEntity().getWorld())?(worldHandlers.get(event.getEntity().getWorld())):null);
			if(worldHandler.loadedSomething())
			{
				int damage = event.getDamage();
				EventInfo eventInfo = null;
				if(DamageElement.matchNonlivingElement(event.getCause()) != null)
				{
					DamageElement damageType_damager = DamageElement.matchNonlivingElement(event.getCause());
					if(ent_damaged instanceof Player)
						eventInfo = new EventInfo((Player)ent_damaged, damageType_damager);
					else if(DamageElement.matchLivingElement(ent_damaged) != null)
						eventInfo = new EventInfo(ent_damaged, damageType_damager);
				}
				else if(event instanceof EntityDamageByEntityEvent)
				{
					EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
					LivingEntity ent_damager = (LivingEntity)event_EE.getDamager();
					DamageElement rangedElement = ((event instanceof EntityDamageByProjectileEvent) 
														&& !(ent_damager instanceof Skeleton || ent_damager instanceof Ghast))
															?DamageElement.matchRangedElement(((EntityDamageByProjectileEvent)event).getProjectile())
															:null;
				//Player-targeted damage
					if(ent_damaged instanceof Player)
					{
					//Player vs Player
						if(ent_damager instanceof Player) eventInfo = new EventInfo((Player)ent_damaged, (Player)ent_damager, rangedElement);
					//Mob vs Player
						else eventInfo = new EventInfo((Player)ent_damaged, ent_damager);
					}
				//Monster-targeted damage
					else if(DamageElement.matchLivingElement(ent_damaged) != null)
					{
					//Player vs Mob
						if(ent_damager instanceof Player) eventInfo = new EventInfo(ent_damaged, (Player)ent_damager, rangedElement);
					//Mob vs Mob 
						else if(DamageElement.matchLivingElement(ent_damager) != null) eventInfo = new EventInfo(ent_damaged, (LivingEntity)ent_damager);
					//Nonliving vs Mob
					}
				}
				else
				{
				}
				worldHandler.doCalculations(eventInfo);
				if(damage < 0 && !negative_Heal) 
					damage = 0;
				event.setDamage(damage);
			}
		}
	}

	public void handleSpawnEvent(CreatureSpawnEvent event)
	{
		if(event.getEntity() != null)
		{
			World world = event.getEntity().getWorld();
			LivingEntity livingEntity = ((LivingEntity)event.getEntity());
			if(disable_DefaultHealth) livingEntity.setHealth(0);
			
			if(worldHandlers.containsKey(world))
				worldHandlers.get(world).setHealth(livingEntity);
			event.setCancelled(livingEntity.getHealth() <= 0);
		}
	}
	
/////////////////// HELPER FUNCTIONS ////////////////////////////
	//check for Permissions
	private static boolean hasPermission(Player player, String permission)
	{
		if (ModDamage.Permissions != null)
		{
			if (ModDamage.Permissions.has(player, permission)) 
				return true;
			return false;
		}
		return player.isOp();
	}

	public World getWorldMatch(String name, boolean searchSubstrings)
	{
		for(World world : getServer().getWorlds())
			if(name.equalsIgnoreCase(world.getName()))
				return world;
		
		if(searchSubstrings)
			for(World world : getServer().getWorlds())
				for(int i = 0; i < (world.getName().length() - name.length() - 1); i++)
					if(name.equalsIgnoreCase(world.getName().substring(i, i + name.length())))
						return world;
		return null;
	}
	
	public String getGroupMatch(World world, String name, boolean searchSubstrings)
	{
		for(GroupHandler groupHandler : worldHandlers.get(world).groupHandlers.values())
			if(name.equalsIgnoreCase(groupHandler.getGroupName()))
				return groupHandler.getGroupName();
		if(searchSubstrings)
			for(GroupHandler groupHandler : worldHandlers.get(world).groupHandlers.values())
				for(int i = 0; i < (groupHandler.getGroupName().length() - name.length() - 1); i++)
					if(name.equalsIgnoreCase(groupHandler.getGroupName().substring(i, i + name.length())))
						return groupHandler.getGroupName();
		return null;
	}
	
	public String ModDamageString(ChatColor color){ return color + "[" + ChatColor.DARK_RED + "Mod" + ChatColor.DARK_BLUE + "Damage" + color + "]";}
	
/////////////////// PLUGIN CONFIGURATION ////////////////////////////
	private void setPluginStatus(Player player, boolean state) 
	{
		if(state)
		{
			if(isEnabled)
			{
				if(player != null) player.sendMessage(ModDamageString(ChatColor.RED) + " Already enabled!");
				else log.info("[" + getDescription().getName() + "] Already enabled!");
			}
			else
			{
				isEnabled = true;
				log.info("[" + getDescription().getName() + "] Plugin enabled.");
				if(player != null) player.sendMessage(ModDamageString(ChatColor.GREEN) + " Plugin enabled.");
			}
		}
		else 
		{
			if(isEnabled)
			{
				isEnabled = false;
				log.info("[" + getDescription().getName() + "] Plugin disabled.");
				if(player != null) player.sendMessage(ModDamageString(ChatColor.GREEN) + " Plugin disabled.");
			}
			else
			{
				if(player != null) player.sendMessage(ModDamageString(ChatColor.RED) + " Already disabled!");
				else log.info("[" + getDescription().getName() + "] Already disabled!");
					
			}
		}
	}

	private void toggleConsoleDebug(Player player) 
	{
		boolean fromConsole = (player == null);
		String sendThis;
		if(consoleDebugging_verbose) //verbose was active, go to quiet
		{
			consoleDebugging_normal = false;
			consoleDebugging_verbose = false;
			sendThis = "[" + getDescription().getName() + "] \"Quiet\" mode active.";
		}
		else if(consoleDebugging_normal) //normal was active, go to verbose
		{
			consoleDebugging_verbose = true;
			sendThis = "[" + getDescription().getName() + "] Verbose debugging active.";
		}
		else //quiet was active, go to normal
		{
			consoleDebugging_normal = true;
			sendThis = "[" + getDescription().getName() + "] Debugging active.";
		}
		log.info(sendThis);
		if(!fromConsole) player.sendMessage(ChatColor.GREEN + sendThis);
	}

	private void sendUsage(Player player, boolean forError) 
	{
		if(player != null)
		{
			if(forError) player.sendMessage(ChatColor.RED + "Error: invalid command syntax.");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "ModDamage commands:");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/moddamage | /md - bring up this help message");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/md (check | c) - check configuration");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/md (debug | d) [debugType] - change debug type");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/md disable - disable ModDamage");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/md enable - enable ModDamage");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/md (reload | r) - reload configuration");
		}
		else
		{
			if(forError) log.info("Error: invalid command syntax.");
			log.info("ModDamage commands:\n" +
					"/moddamage | /md - bring up this help message\n" +
					"/md check - check configuration\n" +
					"/md debug [debugType] - change debugging type (quiet, normal, verbose)\n" +
					"/md disable - disable ModDamage\n" +
					"/md enable - enable ModDamage\n" +
					"/md reload - reload configuration");
		}
	}

/////////////////// MECHANICS CONFIGURATION ////////////////////////////
	private boolean reload(boolean printToConsole)
	{
	//CONFIGURATION
		config = this.getConfiguration();
		config.load();
		//get plugin config.yml...if it doesn't exist, create it.
		if(!(new File("plugins\\" + getDescription().getName(), "config.yml")).exists()) writeDefaults();
	
		pluginOffensiveNode = config.getNode("Offensive");
		pluginDefensiveNode = config.getNode("Defensive");
		pluginMobHealthNode = config.getNode("MobHealth");
		pluginScanNode = config.getNode("Scan");

	//load debug settings
		String debugString = config.getString("debugging");
		if(debugString != null)
		{
			if(debugString.equals("quiet"))
			{
				consoleDebugging_normal = false;
				log.info("[" + getDescription().getName()+ "] \"Quiet\" mode active - suppressing debug messages and warnings.");
			}
			else if(debugString.equals("normal"))
				log.info("[" + getDescription().getName()+ "] Debugging active.");
			else if(debugString.equals("verbose"))
			{
				consoleDebugging_verbose = true;
				log.info("[" + getDescription().getName()+ "] Verbose debugging active.");
			}
			else log.info("[" + getDescription().getName()+ "] Debug string not recognized - defaulting to normal settings.");
		}
		
	//"disable"-type configs
		//TODO Clean up this output stuff. Surely there's a more clever way to output? :P
		disable_DefaultDamage = config.getBoolean("disableDefaultDamage", false);
		if(consoleDebugging_normal && disable_DefaultDamage)
			log.info("[" + getDescription().getName()+ "] Default damage disabled.");
		else if(consoleDebugging_verbose && !disable_DefaultDamage)
			log.info("[" + getDescription().getName()+ "] Default damage enabled.");
		
		disable_DefaultHealth = config.getBoolean("disableDefaultHealth", false);
		if(consoleDebugging_normal && disable_DefaultHealth)
			log.info("[" + getDescription().getName()+ "] Default health disabled.");
		else if(consoleDebugging_verbose && !disable_DefaultHealth)
			log.info("[" + getDescription().getName()+ "] Default health enabled.");
		
		negative_Heal = config.getBoolean("negativeHeal", false);
		if(consoleDebugging_normal && negative_Heal) 
			log.info("[" + getDescription().getName()+ "] Negative-damage healing enabled.");
		else if(consoleDebugging_verbose && !negative_Heal)
			log.info("[" + getDescription().getName()+ "] Negative-damage healing disabled.");
		
		config.load(); //Discard any changes made to the file by the above reads.
		
	//try to initialize WorldHandlers
		boolean loadedSomething = false;
		
		String nodeNames[] = {"Offensive", "Defensive", "MobHealth", "Scan"};
		if(pluginOffensiveNode != null || pluginDefensiveNode != null || pluginMobHealthNode != null || pluginScanNode != null)
			for(World world : getServer().getWorlds())
			{
				ConfigurationNode worldNodes[] = {	(pluginOffensiveNode != null?pluginOffensiveNode.getNode(world.getName()):null), 
													(pluginDefensiveNode != null?pluginDefensiveNode.getNode(world.getName()):null), 
													(pluginMobHealthNode != null?pluginMobHealthNode.getNode(world.getName()):null),
													(pluginScanNode != null?pluginScanNode.getNode(world.getName()):null)};
				for(int i = 0; i < worldNodes.length; i++)
					if(worldNodes[i] == null && (consoleDebugging_verbose))
						log.warning("{Couldn't find " + nodeNames[i] +  " node for world \"" + world.getName() + "\"}");
				WorldHandler worldHandler = new WorldHandler(this, world, worldNodes[0], worldNodes[1], worldNodes[2], worldNodes[3], damageCalc, healthCalc);
				if(worldHandler.loadedSomething())
				{
					worldHandlers.put(world, worldHandler);
					loadedSomething = true;
				}
			}
		if(!loadedSomething) log.severe("[" + getDescription().getName() + "] No configurations loaded! Are any calculation strings defined?");
		return loadedSomething;
	}

	private void writeDefaults() 
	{
		log.severe("[" + getDescription().getName() + "] No configuration file found! Writing a blank config...");
		config.setProperty("debugging", "normal");
		config.setProperty("disableDefaultHealth", "false");
		config.setProperty("disableDefaultDamage", "false");
		config.setProperty("negativeHeal", "false");
		for(World world : getServer().getWorlds())
		{
			String worldName = world.getName();
			List<String> emptyList = null; //Dunno if it can be just any null object, but at least it leaves things blank.

			config.setProperty("Scan." + worldName + ".global", emptyList);
			for(DamageElement damageCategory : DamageElement.getGenericElements())
			{
				config.setProperty("Offensive." + worldName + ".global.generic." + damageCategory.getReference(), emptyList);
				if(!damageCategory.equals(DamageElement.GENERIC_NATURE))
					config.setProperty("Defensive." + worldName + ".global.generic." + damageCategory.getReference(), emptyList);
				if(damageCategory.hasSubConfiguration())
					for(DamageElement subElement : DamageElement.getElementsOf(damageCategory))
					{
						config.setProperty("Offensive." + worldName + ".global."+ damageCategory.getReference() + "." + subElement.getReference(), emptyList);
						if(!damageCategory.equals(DamageElement.GENERIC_NATURE))
							config.setProperty("Defensive." + worldName + ".global."+ damageCategory.getReference() + "." + subElement.getReference(), emptyList);
					}
				if(damageCategory.equals(DamageElement.GENERIC_ANIMAL) || damageCategory.equals(DamageElement.GENERIC_MOB))
				{
					for(DamageElement creatureElement : DamageElement.getElementsOf(damageCategory))
						config.setProperty("MobHealth." + worldName + "." + creatureElement.getReference(), emptyList);
				}
				
				config.setProperty("Offensive." + worldName + ".global.armor", emptyList);
				config.setProperty("Offensive." + worldName + ".groups", emptyList);
				
				/*//FIXME Documentation for Perms 3.0+ is lacking at best right now.
				for(Group group : Permissions.getGroups(worldName))
				{
					log.info(group.toString());
					String groupName = group.getName();
					config.setProperty("Offensive." + worldName + ".groups." + groupName + ".generic." + damageCategory.getReference(), emptyList);
					config.setProperty("Defensive." + worldName + ".groups." + groupName + ".generic." + damageCategory.getReference(), emptyList);
					if(damageCategory.hasSubConfiguration())
						for(DamageElement subElement : DamageElement.getElementsOf(damageCategory))
						{
							config.setProperty("Offensive." + worldName + ".groups." + groupName + "." + damageCategory.getReference() + "." + subElement.getReference(), emptyList);
							config.setProperty("Defensive." + worldName + ".groups." + groupName + "." + damageCategory.getReference() + "." + subElement.getReference(), emptyList);
						}
					config.setProperty("Scan." + worldName + ".groups." + groupName, emptyList);
				}
				*/
			}
		}
		config.save();
		config.load();//TODO Necessary?
		log.severe("[" + getDescription().getName() + "] Done! Don't forget that you can define armor, melee, and group nodes further.");
	}
	
	/*
	private boolean add(String string, List<String> calcStrings) 
	{
		try
		{
			String[] args = string.split(":");
			World literalWorldMatch = getWorldMatch(args[1], false);
			if(literalWorldMatch != null && (args[0] == "Offensive" || args[0] == "Defensive" || args[0] == "Scan" || args[0] == "MobHealth"))
			{
				WorldHandler worldHandler = null;
				if(!worldHandlers.containsKey(literalWorldMatch))
					worldHandler = new WorldHandler(this, literalWorldMatch, null, null, null, null, damageCalc, healthCalc);
				else worldHandler = worldHandlers.get(literalWorldMatch);
				
				return worldHandler.add(args, calcStrings);
			}
			return false;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			
			return false;
		}
	}

	private boolean delete(String string)
	{
		String[] args = string.split(":");
		if(args.length == 1) string.split("\\.");
		return false;
	}
	 */

	private void sendConfig(Player player, String worldSearchTerm){ sendConfig(player, worldSearchTerm, 1);}
	private void sendConfig(Player player, String worldSearchTerm, int pageNumber)
	{
		//TODO Refactor for a single function?
		World worldMatch = getWorldMatch(worldSearchTerm, true);
		if(worldMatch != null)
		{
			if(hasPermission(player, "moddamage.check." + worldMatch.getName()))
			{
				if(!worldHandlers.get(worldMatch).sendWorldConfig(player, pageNumber))
					player.sendMessage(ModDamageString(ChatColor.RED) + " Invalid page number for world \"" + worldMatch.getName() + "\".");
			}
			else player.sendMessage(ModDamageString(ChatColor.RED) 
					+ " You don't have permission to check world \"" + worldMatch.getName() + "\"");
		}
		else player.sendMessage(errorString_findWorld);
	}
	private void sendConfig(Player player, String worldSearchTerm, String groupSearchTerm){ sendConfig(player, worldSearchTerm, groupSearchTerm, 1);}
	private void sendConfig(Player player, String worldSearchTerm, String groupSearchTerm, int pageNumber) 
	{
		World worldMatch = getWorldMatch(worldSearchTerm, true);
		if(worldMatch != null)
		{
			String groupMatch = getGroupMatch(worldMatch, groupSearchTerm, true);
			if(groupMatch != null)
			{
				if(hasPermission(player, "moddamage.check." + worldMatch.getName() + "." + groupMatch))
				{
					if(!worldHandlers.get(worldMatch).groupHandlers.get(groupMatch).sendGroupConfig(player, pageNumber))
						player.sendMessage(ModDamageString(ChatColor.RED) + " You don't have permission to check group \"" 
								+ groupMatch + "\" for world \"" + worldMatch.getName() + "\".");
				}
				else player.sendMessage(ModDamageString(ChatColor.RED) + " You don't have permission to check group \"" + groupMatch + "\".");
			}
			else player.sendMessage(ModDamageString(ChatColor.RED) + " Couldn't find matching group name.");
		}
		else
		{
			if(player == null) log.info("Error: Couldn't find matching world substring.");
			else player.sendMessage(errorString_findWorld);
		}
	}
	
	
}