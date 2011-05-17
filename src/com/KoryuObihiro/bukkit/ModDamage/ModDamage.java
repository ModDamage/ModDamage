package com.KoryuObihiro.bukkit.ModDamage;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.Handling.DamageCalculator;
import com.KoryuObihiro.bukkit.ModDamage.Handling.DamageType;
import com.KoryuObihiro.bukkit.ModDamage.Handling.HealthCalculator;
import com.KoryuObihiro.bukkit.ModDamage.Handling.WorldHandler;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import java.util.logging.Logger;

/**
 * "ModDamage" for Bukkit
 * 
 * @author Erich Gubler
 *
 */
public class ModDamage extends JavaPlugin
{
	//TODO Deregister when Bukkit supports!
	//TODO Create reference strings as worldHandlers/groupHandlers are loaded
	//TODO Idea: categories for scan?
	//TODO Client-sided mod for displaying health?
	//plugin-related
	private final ModDamageEntityListener entityListener = new ModDamageEntityListener(this);
	private final ModDamagePlayerListener playerListener = new ModDamagePlayerListener(this);
	public static Logger log = Logger.getLogger("Minecraft");
	public static PermissionHandler Permissions = null;
	private Configuration config;
	
	//config
	private ConfigurationNode pluginOffensiveNode, pluginDefensiveNode, pluginMobHealthNode, pluginScanNode;
	public static boolean consoleDebugging_normal = true;
	public static boolean consoleDebugging_verbose = false;
	public static boolean disable_DefaultDamage;
	public static boolean disable_DefaultHealth;
	public static boolean negative_Heal;
	public final HashMap<World, WorldHandler> worldHandlers = new HashMap<World, WorldHandler>(); //groupHandlers are allocated within the WorldHandler class
	private final DamageCalculator damageCalc = new DamageCalculator();
	private final HealthCalculator healthCalc = new HealthCalculator();
	
	

	
////////////////////////// INITIALIZATION ///////////////////////////////
	@Override
	public void onEnable() 
	{
	//PERMISSIONS
		Plugin test = getServer().getPluginManager().getPlugin("Permissions");
		if (test != null)
		{
			ModDamage.Permissions = ((Permissions)test).getHandler();
			log.info("[" + getDescription().getName() + "] " + this.getDescription().getVersion() 
					+ " enabled [Permissions v" + test.getDescription().getVersion() + " active]");
		}
		else
			log.info("[" + getDescription().getName() + "] " + this.getDescription().getVersion() 
					+ " enabled [Permissions not found]");
		
		//register plugin-related stuff with the server's plugin manager
		getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.High, this);
		getServer().getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Event.Priority.High, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Event.Priority.Normal, this);
		//getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
		
		loadConfig();
		
	}

	@Override
	public void onDisable() 
	{
		log.info("[" + getDescription().getName() + "] disabled.");	
		worldHandlers.clear();
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player player = ((sender instanceof Player)?((Player)sender):null);
		boolean fromConsole = (player == null);
		if (label.equalsIgnoreCase("ModDamage") || label.equalsIgnoreCase("md"))
		{
			if (args.length == 0)
			{
				sendUsage(player, false); //TODO Bool-returning necessary?
			}
			else if(args.length >= 0)
			{
				if(args[0].equalsIgnoreCase("debug"))
				{
					if(args.length == 1)
						toggleConsoleDebug(player);
					else if(args.length == 2)
					{
						String sendThis;
						if(args[1].equalsIgnoreCase("quiet"))
						{
							if(consoleDebugging_normal)
								sendThis = "[" + getDescription().getName() + "] \"Quiet\" mode already active!";
							else
							{
								consoleDebugging_normal = false;
								consoleDebugging_verbose = false;
								sendThis = "[" + getDescription().getName() + "] \"Quiet\" mode enabled - suppressing debug messages and warnings.";
							}
						}
						else if(args[1].equalsIgnoreCase("normal"))
						{
							if(consoleDebugging_normal && !consoleDebugging_verbose)
								sendThis = "[" + getDescription().getName() + "] Debugging already active!";
							else
							{
								consoleDebugging_normal = true;
								consoleDebugging_verbose = false;
								sendThis = "[" + getDescription().getName() + "] Debugging enabled.";
							}
						}
						else if(args[1].equalsIgnoreCase("verbose"))
						{
							if(!consoleDebugging_normal)
								sendThis = "[" + getDescription().getName() + "] Verbose debugging already active!";
							else
							{
								consoleDebugging_normal = true;
								consoleDebugging_verbose = true;
								sendThis = "[" + getDescription().getName() + "] Verbose debugging enabled.";
							}
						}
						else
						{
							sendUsage(player, true);
							return true;
						}
						log.info(sendThis);
						if(!fromConsole) player.sendMessage(ChatColor.GREEN + sendThis);
					}
					else sendUsage(null, true);
				}
				/*
				if(args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("w"))
				{
					if(args.length == 1)
					{
						if(player == null)
						{
							for(WorldHandler worldHandler : worldHandlers.values())
								worldHandler.sendWorldConfig(player, false);
						}
						else if(hasPermission(player, "moddamage.world")) 
						{
							worldHandlers.get(player.getWorld()).sendWorldConfig(player, false);
							return true;
						}
						else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] You don't have access to that command.");
					}
					else if(args.length == 2)
					{
						World worldMatch = null;
						for(World temp : getServer().getWorlds())
							for(int i = 0; i < (temp.getName().length() - args[1].length() - 1); i++)
								if(args[1].equalsIgnoreCase(temp.getName().substring(i, i + args[1].length())))
								{
									worldMatch = temp;
									break;
								}
						
						if(worldMatch != null)
						{
							if(player == null) return worldHandlers.get(worldMatch).sendWorldConfig(player, false);
							else if(hasPermission(player, "moddamage.world.others"))
								return worldHandlers.get(worldMatch).sendWorldConfig(player, false);
							else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] You don't have permission to check other worlds.");
						}
						else
						{
							if(player == null) log.info("Error: Couldn't find matching world substring.");
							else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] Couldn't find matching world name.");
						}
					}
				}
				
				if(args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("g"))
				{
					if(args.length == 1)
					{
						if(player == null)
						{
							log.info("Error: group not specified."); //TODO check all groups?
						}
						else if(hasPermission(player, "moddamage.group")) 
						{
							String playerGroup = Permissions.getGroup(player.getWorld().getName(), player.getName());
							worldHandlers.get(player.getWorld()).groupHandlers.get(playerGroup).sendGroupConfig(player, true);
						}
						else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] You don't have access to that command.");
					}
					else if(args.length == 2) //group world name
					{
						if(player == null)
						{
							String groupMatch = null;

							List<World> worldList = getServer().getWorlds();
							for(World world : worldList)
							{	
								for(GroupHandler groupHandler : worldHandlers.get(world).groupHandlers.values())
								{
									if(groupMatch != null)
									{
										if(groupHandler.getGroupName().equals(groupMatch))
											groupHandler.worldHandler.sendGroupConfig(player, groupMatch, false); //THaving to refer to the handler this way is bad - refactor later
										break;
									}
									for(int i = 0; i < (groupHandler.getGroupName().length() - args[1].length() - 1); i++)
										if(args[1].equalsIgnoreCase(groupHandler.getGroupName().substring(i, i + args[1].length())))
										{
											groupMatch = groupHandler.getGroupName();
											groupHandler.worldHandler.sendGroupConfig(player, groupMatch, false); //Having to refer to the handler this way is bad - refactor later
											break;
										}
								}
							}
							if(groupMatch != null){}
							else log.info("Error: Couldn't find matching group substring.");
						}
						
						String groupMatch = null;
						for(GroupHandler groupHandler : worldHandlers.get(player.getWorld()).groupHandlers.values())
							for(int i = 0; i < (groupHandler.getGroupName().length() - args[1].length() - 1); i++)
								if(args[1].equalsIgnoreCase(groupHandler.getGroupName().substring(i, i + args[1].length())))
								{
									groupMatch = groupHandler.getGroupName();
									break;
								}
						
						if(groupMatch != null)
						{
							if(hasPermission(player, "moddamage.group.other"))
								return worldHandlers.get(player.getWorld()).sendGroupConfig(player, groupMatch, true);
							else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] You don't have permission to check other groups.");
						}
						else
						{
							//if(player == null) log.info("Error: Couldn't find matching group substring.");
							//else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] Couldn't find matching group name.");
						}
					}
					else if(args.length == 3)
					{
						World worldMatch = null;
						for(World temp : getServer().getWorlds())
							for(int i = 0; i < (temp.getName().length() - args[1].length() - 1); i++)
								if(args[1].equalsIgnoreCase(temp.getName().substring(i, i + args[1].length())))
								{
									worldMatch = temp;
									break;
								}
						
						String groupMatch = null;
						for(GroupHandler groupHandler : worldHandlers.get(worldMatch).groupHandlers.values())
							for(int i = 0; i < (groupHandler.getGroupName().length() - args[1].length() - 1); i++)
								if(args[1].equalsIgnoreCase(groupHandler.getGroupName().substring(i, i + args[1].length())))
								{
									groupMatch = groupHandler.getGroupName();
									break;
								}
						
						if(worldMatch != null)
						{
							if(groupMatch != null)
							{
								if(player == null) return worldHandlers.get(worldMatch).sendGroupConfig(player, groupMatch, false);
								else if(hasPermission(player, "moddamage.group.other"))
									return worldHandlers.get(worldMatch).sendGroupConfig(player, groupMatch, false);
								else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] You don't have permission to check other group/world combinations.");
							}
							else
							{
								if(player == null) log.info("[" + getDescription().getName() + "] Couldn't find group substring in world " + worldMatch.getName());
								else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() 
										+ "] Couldn't find group in world " + worldMatch.getName());
							}
						}
						else
						{
							if(player == null) log.info("Error: Couldn't find matching world substring.");
							else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] Couldn't find matching world name.");
						}
					}
				}*/
				else if(args[0].equalsIgnoreCase("reload"))
				{
					if(args.length == 1)
					{
						if(fromConsole)
							reload();
						else if(hasPermission(player, "moddamage.reload")) 
						{
							log.info("Reload initiated by user " + player.getName());
							reload();
						}
						else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] You don't have access to that command.");
					}
					else if(args.length == 2)
					{
						World worldMatch = null;
						for(World temp : getServer().getWorlds())
							for(int i = 0; i < (temp.getName().length() - args[1].length() - 1); i++)
								if(args[1].equalsIgnoreCase(temp.getName().substring(i, i + args[1].length())))
								{
									worldMatch = temp;
									break;
								}
						
						if(worldMatch != null)
						{
							if(fromConsole) 
								{
									//TODO
									log.info("FIX ME DAMMIT");
								}
							else if(hasPermission(player, "moddamage.reload"))
								{
									if(worldHandlers.get(worldMatch).reload())
										player.sendMessage(ChatColor.GREEN + "[" + getDescription().getName() + "] World " 
												+ player.getWorld().getName() + " config reloaded.");
									else
										player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] World " 
												+ player.getWorld().getName() + " config failed to reload - see console.");
								}
							else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] You don't have permission to do that.");
						}
						else
						{
							if(fromConsole) log.info("Error: Couldn't find matching world substring.");
							else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] Couldn't find matching world name.");
						}
					}
				}
			}
		}
		sendUsage(player, true);
		return true;
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
		//TODO finish these
		if(player != null)
		{
			if(forError)
				player.sendMessage(ChatColor.RED + "Error: invalid command syntax.");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "ModDamage commands:");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/moddamage | /md - bring up this help message");
			if(hasPermission(player, "moddamage.world"))
				player.sendMessage(ChatColor.LIGHT_PURPLE + "/md (world|w)" 
						+ (hasPermission(player, "moddamage.world.other")?" [worldname]":"")
						+ "- check ModDamage global configuration for a world");
			if(hasPermission(player, "moddamage.group"))
				player.sendMessage(ChatColor.LIGHT_PURPLE + "/md (group|g)" 
						+ (hasPermission(player, "moddamage.group.other")?" [groupname]":"")
						+ "- check ModDamage configuration for a particular group/world combination");
		}
		else
		{
			if(forError)
				log.info("Error: invalid command syntax.");
			log.info("ModDamage commands: (/moddamage | /md) - bring up this help message");
			log.info("");
		}
	}
	
	
/////////////////// EVENT FUNCTIONS ////////////////////////////
	
	public void passDamageEvent(EntityDamageEvent event) 
	{
		World world = event.getEntity().getWorld();
		if(worldHandlers.containsKey(world) && worldHandlers.get(world).isLoaded)
		{

			int damage = event.getDamage();		
			Entity ent_damaged = event.getEntity();
			DamageType.matchEntityType(ent_damaged);
			
			if(event instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
				Entity ent_damager = event_EE.getDamager();
				//Player-targeted damage
				if(ent_damaged instanceof Player)
				{
					String group_damaged = Permissions.getGroup(world.getName(), ((Player)ent_damaged).getName());
				//PvP
					if(ent_damager instanceof Player)
					{
						String group_damager = Permissions.getGroup(world.getName(), ((Player)ent_damager).getName());
						Player player_damager = (Player)ent_damager, player_damaged = (Player)ent_damaged;
						
						log.info("PEE VEE PEE: " + group_damager + " vs. " + group_damaged); //TODO REMOVE ME EVENTUALLY
						
						if(group_damager != null && group_damaged != null)
						{
							damage -= worldHandlers.get(world).calcDefenseBuff(((Player)ent_damaged), ((Player)ent_damager), event.getDamage());
							damage += worldHandlers.get(world).calcAttackBuff(((Player)ent_damaged), ((Player)ent_damager), event.getDamage());
						}
						
						if(hasPermission(player_damager, "moddamage.scan.pvp") 
								&& worldHandlers.get(player_damager.getWorld()).isGlobalScanItem(player_damager.getItemInHand().getType()));
							((Player)ent_damager).sendMessage(ChatColor.DARK_PURPLE + player_damaged.getName()
									+ ": " + Integer.toString(player_damaged.getHealth() - damage));
					}
				//NPvP
					else if(DamageType.matchEntityType(ent_damager) != null)
					{
						DamageType mobType_damager = DamageType.matchEntityType(ent_damager);
						if(group_damaged != null);
						{
							damage -= worldHandlers.get(world).calcDefenseBuff(((Player)ent_damaged), mobType_damager, event.getDamage());
							damage += worldHandlers.get(world).calcAttackBuff(((Player)ent_damaged), mobType_damager, event.getDamage());
						}
					}
				//nature-ent vs P
					else
					{
						//Lightning and explosion damage is technically an entity harming an entity
						DamageType damageType = DamageType.matchDamageCause(event.getCause());
						//log.info("Member of " + group_damaged + " got damaged by \"" + damageType.getConfigReference() + "\"");//debug
						if(damageType != null && group_damaged != null);
						{
							damage -= worldHandlers.get(world).calcDefenseBuff(((Player)ent_damaged), damageType, event.getDamage());
							damage += worldHandlers.get(world).calcAttackBuff(((Player)ent_damaged), damageType, event.getDamage());
						}
					}
				}
				//Monster-targeted damage
				else if(DamageType.matchEntityType(ent_damaged) != null)
				{
					DamageType mobType_damaged = DamageType.matchEntityType(ent_damaged);
				//PvNP
					if(ent_damager instanceof Player)
					{
						String group_damager = Permissions.getGroup(world.getName(), ((Player)ent_damager).getName());
						Player player_damager = (Player)ent_damager;
						if(group_damager != null && mobType_damaged != null)
						{

							//log.info("PvNP: " + ((Player)ent_damager).getName() + " vs. " + mobType_damaged.getConfigReference()); //debug
							damage -= worldHandlers.get(world).calcDefenseBuff(mobType_damaged, player_damager, event.getDamage());
							damage += worldHandlers.get(world).calcAttackBuff(mobType_damaged,player_damager, event.getDamage());

							if(hasPermission(player_damager, "moddamage.scan." + mobType_damaged.getConfigReference()) 
									&& worldHandlers.get(player_damager.getWorld()).isGlobalScanItem(player_damager.getItemInHand().getType()));
								((Player)ent_damager).sendMessage(ChatColor.DARK_PURPLE + mobType_damaged.getConfigReference() 
										+ "(id " + ent_damaged.getEntityId() + ")"
										+ ": " + Integer.toString(((LivingEntity)ent_damaged).getHealth() - damage));
						}
					}
				//NPvNP damage
					else if(DamageType.matchEntityType(ent_damager) != null)
					{
						DamageType mobType_damager = DamageType.matchEntityType(ent_damager);
						if(mobType_damager != null && mobType_damaged != null);
						{
							damage -= worldHandlers.get(world).calcDefenseBuff(mobType_damaged, mobType_damager, event.getDamage());
							damage += worldHandlers.get(world).calcAttackBuff(mobType_damaged, mobType_damager, event.getDamage());
						}
					}
				//nature-ent vs NP
					else
					{
						//Lightning and explosion damage is technically an entity harming an entity
						DamageType damageType = DamageType.matchDamageCause(event.getCause());
						//log.info("Member of " + group_damaged + " got damaged by \"" + damageType.getConfigReference() + "\"");//debug
						if(damageType != null && mobType_damaged != null);
						{
							damage -= worldHandlers.get(world).calcDefenseBuff(mobType_damaged, damageType, event.getDamage());
							damage += worldHandlers.get(world).calcAttackBuff(mobType_damaged, damageType, event.getDamage());
						}
					}
				}
			}
			else
			{
				if(ent_damaged instanceof Player)
				{
					Player player_damaged = (Player)ent_damaged;
					DamageType damageType = DamageType.matchDamageCause(event.getCause());
					if(damageType != null && player_damaged != null)
					{
						damage -= worldHandlers.get(world).calcDefenseBuff(player_damaged, damageType, event.getDamage());
						damage += worldHandlers.get(world).calcAttackBuff(player_damaged, damageType, event.getDamage());
					}
				}
				else if(DamageType.matchEntityType(ent_damaged) != null)
				{
					DamageType mobType_damaged = DamageType.matchEntityType(ent_damaged);
					DamageType damageType = DamageType.matchDamageCause(event.getCause());
					if(DamageType.matchDamageCause(event.getCause()) != null && mobType_damaged != null)
					{
						damage -= worldHandlers.get(world).calcDefenseBuff(mobType_damaged, damageType, event.getDamage());
						damage += worldHandlers.get(world).calcAttackBuff(mobType_damaged, damageType, event.getDamage());
					}
				}
			}
			if(damage < 0)
			{
				if(negative_Heal) ((Creature)event.getEntity()).setHealth(((Creature)event.getEntity()).getHealth() - damage);
				event.setDamage(0);
			}
			else event.setDamage(damage);
		}
	}

	public void passSpawnEvent(CreatureSpawnEvent event)
	{
		World world = event.getEntity().getWorld();
		if(worldHandlers.containsKey(world))
		{
			if(disable_DefaultHealth) ((Creature)event.getEntity()).setHealth(0);
			event.setCancelled(!worldHandlers.get(world).setHealth(event.getEntity()));
		}
	}
	
/////////////////// HELPER FUNCTIONS ////////////////////////////
	//check for Permissions
	public static boolean hasPermission(Player player, String permission)
	{
		if (ModDamage.Permissions != null)
		{
			if (ModDamage.Permissions.has(player, permission)) 
				return true;
			return false;
		}
		return player.isOp();
	}

	public boolean loadWorldHandler(World world) 
	{
		if(!worldHandlers.containsKey(world))
		{
			ConfigurationNode worldOffensiveNode = pluginOffensiveNode.getNode(world.getName());
			ConfigurationNode worldDefensiveNode = pluginDefensiveNode.getNode(world.getName());
			ConfigurationNode worldMobHealthNode = pluginMobHealthNode.getNode(world.getName());
			ConfigurationNode worldScanNode = pluginScanNode.getNode(world.getName());
			
			if(worldOffensiveNode != null && worldDefensiveNode != null && worldMobHealthNode != null && worldScanNode != null) //TODO Change to OR-type eval
			{
				worldHandlers.put(world, new WorldHandler(this, world, worldOffensiveNode, worldDefensiveNode, worldMobHealthNode, worldScanNode, damageCalc, healthCalc));
				return true;
			}
			else 
			{
				if(consoleDebugging_normal) log.warning("Couldn't find nodes for world " + world.getName());
				return false;
			}
		}
		return false;
	}
	
	private void loadConfig()
	{
		//CONFIGURATION
		//get plugin config.yml
		config = this.getConfiguration();
		config.load();
		pluginOffensiveNode = config.getNode("Offensive");
		pluginDefensiveNode = config.getNode("Defensive");
		pluginMobHealthNode = config.getNode("MobHealth");
		pluginScanNode = config.getNode("Scan");

		//load debug settings
		loadPluginSettings();
		
		//try to initialize WorldHandlers
		String nodeNames[] = {"Offensive", "Defensive", "MobHealth", "Scan"};
		if(pluginOffensiveNode != null && pluginDefensiveNode != null && pluginMobHealthNode != null && pluginScanNode != null)
			for(World world : getServer().getWorlds())
			{
				ConfigurationNode worldNodes[] = {pluginOffensiveNode.getNode(world.getName()), 
													pluginDefensiveNode.getNode(world.getName()), 
													pluginMobHealthNode.getNode(world.getName()),
													pluginScanNode.getNode(world.getName())};
				for(int i = 0; i < worldNodes.length; i++)
					if(worldNodes[i] == null && (consoleDebugging_normal))
						log.warning("{Couldn't find " + nodeNames[i] +  " node for world \"" + world.getName() + "\"}");
				worldHandlers.put(world, new WorldHandler(this, world, worldNodes[0], worldNodes[1], worldNodes[2], worldNodes[3], damageCalc, healthCalc));
			}
		else log.severe("Couldn't find configuration nodes - does the config file exist?");
	}

	private void reload()
	{
		loadPluginSettings();
		for(WorldHandler worldHandler : worldHandlers.values())
		{
			if(worldHandler.reload() && consoleDebugging_normal)
				log.info("[" + getDescription().getName() + "] World " 
					+ worldHandler.world.getName() + " config reloaded.");
			else if(consoleDebugging_normal)
				log.warning("[" + getDescription().getName() + "] World " 
						+ worldHandler.world.getName() + " failed to reload.");
		}
	}
	private void loadPluginSettings() 
	{
		//debugging
		String debugString = (String)config.getProperty("debugging");
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
		disable_DefaultDamage = config.getBoolean("disableDefaultDamage", false);
		disable_DefaultHealth = config.getBoolean("disableDefaultHealth", false);
		negative_Heal = config.getBoolean("negativeHeal", false);
	}
}