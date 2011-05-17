package com.KoryuObihiro.bukkit.ModDamage;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
	//plugin-related
	private final ModDamageEntityListener entityListener = new ModDamageEntityListener(this);
	private final ModDamagePlayerListener playerListener = new ModDamagePlayerListener(this);
	public static Logger log = Logger.getLogger("Minecraft");
	public static PermissionHandler Permissions = null;
	private Configuration config;
	
	//config
	private ConfigurationNode pluginOffensiveNode, pluginDefensiveNode, pluginMobHealthNode;
	public static boolean consoleDebugging_quiet = false;
	public static boolean consoleDebugging_normal = true;
	public static boolean consoleDebugging_verbose = false;
	public static boolean disable_DefaultDamage;
	public static boolean disable_DefaultHealth;
	public static boolean negative_Heal;

	//plugin-specific
	public final HashMap<World, WorldHandler> worldHandlers = new HashMap<World, WorldHandler>();
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
		//TODO Deregister when Bukkit supports!
		log.info("["+getDescription().getName()+"] disabled.");	
		worldHandlers.clear();
		//configs.clear();
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		//TODO clean up return true statements
		Player player = null;
		//debugging
		String tempo = "";
		for(String string : args)
			tempo += " " + string;
		
		if (label.equalsIgnoreCase("ModDamage") || label.equalsIgnoreCase("md"))
		{
			// §
			if (sender instanceof Player)
				player = (Player)sender;
			if (args.length == 0)
			{
				sendUsage(player);
				return true;
			}
			else if(args.length >= 0)
			{
				if(args[0].equalsIgnoreCase("debug"))
				{
					if(args.length == 1)
						toggleConsoleDebug();
					else if(args.length == 2)
					{
						if(args[1].equalsIgnoreCase("on"))
						{
							if(consoleDebugging_normal)
								log.info("[" + getDescription().getName() + "] Console debugging already on!");
							else
							{
								consoleDebugging_normal = true;
								log.info("[" + getDescription().getName() + "] Console debugging enabled.");
							}
						}
						else if(args[1].equalsIgnoreCase("off"))
						{
							if(!consoleDebugging_normal)
								log.info("[" + getDescription().getName() + "] Console debugging already off!");
							else
							{
								consoleDebugging_normal = false;
								log.info("[" + getDescription().getName() + "] Console debugging disabled.");
							}
						}
					}
					else
					{
						log.info("[" + getDescription().getName() + "] Invalid debug command syntax.");
						sendUsage(null);
					}
					return true;
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
							return true;
						}
						else if(hasPermission(player, "moddamage.world")) 
						{
							worldHandlers.get(player.getWorld()).sendWorldConfig(player, false);
							return true;
						}
						else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] You don't have access to that command.");
						return true;
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
							return true;
						}
						else
						{
							if(player == null) log.info("Error: Couldn't find matching world substring.");
							else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] Couldn't find matching world name.");
							return true;
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
							return true;
						}
						else if(hasPermission(player, "moddamage.group")) 
						{
							String playerGroup = Permissions.getGroup(player.getWorld().getName(), player.getName());
							worldHandlers.get(player.getWorld()).groupHandlers.get(playerGroup).sendGroupConfig(player, true);
						}
						else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] You don't have access to that command.");
						return true;
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
							return true;
						}
						else
						{
							//if(player == null) log.info("Error: Couldn't find matching group substring.");
							//else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] Couldn't find matching group name.");
							return true;
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
								return true;
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
							return true;
						}
					}
				}*/
				else if(args[0].equalsIgnoreCase("reload"))
				{
					if(args.length == 1)
					{
						if(player == null)
						{
							for(WorldHandler worldHandler : worldHandlers.values())
							{
								if(worldHandler.reload())
								{
									log.info("[" + getDescription().getName() + "] World " 
										+ worldHandler.world.getName() + " config reloaded.");
								}
								else
								{
									log.info("[" + getDescription().getName() + "] World " 
											+ worldHandler.world.getName() + " failed to reload.");
								}
							}
							return true;
						}
						else if(hasPermission(player, "moddamage.reload")) 
						{
							log.info("Reload initiated by user " + player.getName());
							for(WorldHandler worldHandler : worldHandlers.values())
							{
								if(worldHandler.reload())
								{
									player.sendMessage(ChatColor.GREEN + "[" + getDescription().getName() + "] World " 
											+ worldHandler.world.getName() + " config reloaded.");
								}
								else
								{
									player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] World " 
											+ worldHandler.world.getName() + " config failed - see console.");
								}
							}	
							return true;
						}
						else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] You don't have access to that command.");
						return true;
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
							if(player == null) 
								{
									//TODO
									log.info("FIX ME DAMMIT");
									return true;
								}
							else if(hasPermission(player, "moddamage.reload"))
								{
									if(worldHandlers.get(worldMatch).reload())
										player.sendMessage(ChatColor.GREEN + "[" + getDescription().getName() + "] World " 
												+ player.getWorld().getName() + " config reloaded.");
									else
										player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] World " 
												+ player.getWorld().getName() + " config failed to reload - see console.");
									return true;
								}
							else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] You don't have permission to do that.");
							return true;
						}
						else
						{
							if(player == null) log.info("Error: Couldn't find matching world substring.");
							else player.sendMessage(ChatColor.RED + "[" + getDescription().getName() + "] Couldn't find matching world name.");
							return true;
						}
					}
				}
				
				else if(args[0].equalsIgnoreCase("test"))
				{
					DamageCalculator calc = new DamageCalculator();
					
					if(args.length == 1)
					{
						player.sendMessage("Roll for 5: " + calc.roll_simple(5));
						return true;
					}
					else if(args.length == 3 && args[1].equalsIgnoreCase("roll"))
					{
						try
						{
							int input = Integer.parseInt(args[2]);
							player.sendMessage("Roll for " + input + ": " + calc.roll_simple(input));
							return true;
						}
						catch(Exception e)
						{
							player.sendMessage(ChatColor.RED + "Roll parsing failed.");
							return true;
						}
					}
				}
				else if(args[0].equalsIgnoreCase("dc"))
				{
					
				}
			}
		}
		return sendUsage(player);
	}
	
	private void toggleConsoleDebug() 
	{
		log.info("[" + getDescription().getName() + "] Console debugging " + (consoleDebugging_normal?"disabled":"enabled") + ".");
		consoleDebugging_normal = (consoleDebugging_normal?false:true);
	}

	private boolean sendUsage(Player player) 
	{
		//TODO finish these
		if(player != null)
		{
			player.sendMessage(ChatColor.LIGHT_PURPLE + "ModDamage commands: (/moddamage | /md)");
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
			log.info("");
		}
		return true;
	}
	
	
/////////////////// EVENT FUNCTIONS ////////////////////////////
	
	public void passDamageEvent(EntityDamageEvent event) 
	{
		World world = event.getEntity().getWorld();
		if(worldHandlers.containsKey(world) && worldHandlers.get(world).isLoaded)
		{

			int damage = event.getDamage();		
			Entity ent_damaged = event.getEntity();
			
			if(event instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
				Entity ent_damager = event_EE.getDamager();
				
				//Player-targeted damage
				if(ent_damaged instanceof Player)
				{
					String group_damaged = Permissions.getGroup(world.getName(), ((Player)ent_damaged).getName());//debug
				//PvP
					if(ent_damager instanceof Player)
					{
						String group_damager = Permissions.getGroup(world.getName(), ((Player)ent_damager).getName());//debug
						log.info("PEE VEE PEE: " + group_damager + " vs. " + group_damaged);
						if(group_damager != null && group_damaged != null)
						{
							damage -= worldHandlers.get(world).calcDefenseBuff(((Player)ent_damaged), ((Player)ent_damager), event.getDamage());
							damage += worldHandlers.get(world).calcAttackBuff(((Player)ent_damaged), ((Player)ent_damager), event.getDamage());
						}
					}
				//NPvP
					else if(ent_damager instanceof Creature)
					{
						DamageType mobType_damager = DamageType.matchEntityType(ent_damager);
						if(mobType_damager != null && group_damaged != null);
						{
							damage -= worldHandlers.get(world).calcDefenseBuff(((Player)ent_damaged), mobType_damager, event.getDamage());
							damage += worldHandlers.get(world).calcAttackBuff(((Player)ent_damaged), mobType_damager, event.getDamage());
						}
					}
				}
				//Monster-targeted damage
				else if(ent_damaged instanceof Creature)
				{

					DamageType mobType_damaged = DamageType.matchEntityType(ent_damaged);
				//PvNP
					if(ent_damager instanceof Player)
					{
						
						String group_damager = Permissions.getGroup(world.getName(), ((Player)ent_damager).getName());
						if(group_damager != null && mobType_damaged != null)
						{

							log.info("PvNP: " + ((Player)ent_damager).getName() + " vs. " + mobType_damaged.getConfigReference());
							log.info("Event: " + event.getDamage());
							damage -= worldHandlers.get(world).calcDefenseBuff(mobType_damaged, ((Player)ent_damager), event.getDamage());
							damage += worldHandlers.get(world).calcAttackBuff(mobType_damaged,((Player)ent_damager), event.getDamage());
							log.info("Result: " + damage);
							
							((Player)ent_damager).sendMessage(ChatColor.DARK_PURPLE + "Mob target " + mobType_damaged.getConfigReference() + " has " + ((Creature)ent_damaged).getHealth()); 
							//TODO Idea: "scan"-type ability for players with Permissions?
						}
					}
				//NPvNP damage
					else if(ent_damager instanceof Creature)
					{

						DamageType mobType_damager = DamageType.matchEntityType(ent_damager);
						if(mobType_damager != null && mobType_damaged != null);
						{
							damage -= worldHandlers.get(world).calcDefenseBuff(mobType_damaged, mobType_damager, event.getDamage());
							damage += worldHandlers.get(world).calcAttackBuff(mobType_damaged, mobType_damager, event.getDamage());
						}
					}
				}
			}
			else
			{
				if(ent_damaged instanceof Player)
				{
					//TODO Consider whether the group strings are necessary here
					String group_damaged = Permissions.getGroup(world.getName(), ((Player)ent_damaged).getName());
					if(DamageType.matchDamageCause(event.getCause()) != null)
					{
						DamageType damageType = DamageType.matchDamageCause(event.getCause());
						if(damageType != null && group_damaged != null)
						{
							damage -= worldHandlers.get(world).calcDefenseBuff(((Player)ent_damaged), damageType, event.getDamage());
							damage += worldHandlers.get(world).calcAttackBuff(((Player)ent_damaged), damageType, event.getDamage());
						}
					}
				}
				else if(ent_damaged instanceof Creature)
				{
					DamageType mobType_damaged = DamageType.matchEntityType(ent_damaged);
					if(DamageType.matchDamageCause(event.getCause()) != null)
					{
						DamageType damageType = DamageType.matchDamageCause(event.getCause());
						if(damageType != null && mobType_damaged != null)
						{
							damage -= worldHandlers.get(world).calcDefenseBuff(mobType_damaged, damageType, event.getDamage());
							damage += worldHandlers.get(world).calcAttackBuff(mobType_damaged, damageType, event.getDamage());
						}
					}
				}
				//world-type damage to a player
			}
			if(damage < 0)
			{
				if(negative_Heal) ((Creature)event.getEntity()).setHealth(((Creature)event.getEntity()).getHealth() - damage);
				event.setDamage(0);
			}
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
			
			if(worldOffensiveNode != null && worldDefensiveNode != null)
			{
				worldHandlers.put(world, new WorldHandler(this, world, worldOffensiveNode, worldDefensiveNode, worldMobHealthNode, damageCalc, healthCalc));
				return true;
			}
			else 
			{
				log.warning("Couldn't find nodes for world " + world.getName());
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

		//load debug settings
		loadPluginSettings();
		
		//try to initialize WorldHandlers
		String nodeNames[] = {"Offensive", "Defensive", "MobHealth"};
		if(pluginOffensiveNode != null && pluginDefensiveNode != null && pluginMobHealthNode != null )
			for(World world : getServer().getWorlds())
			{
				ConfigurationNode worldNodes[] = {pluginOffensiveNode.getNode(world.getName()), 
													pluginDefensiveNode.getNode(world.getName()), 
													pluginMobHealthNode.getNode(world.getName())};
				for(int i = 0; i < worldNodes.length; i++)
					if(worldNodes[i] == null && (consoleDebugging_normal))
						log.warning("{Couldn't find " + nodeNames[i] +  " node for world \"" + world.getName() + "\"}");
				worldHandlers.put(world, new WorldHandler(this, world, worldNodes[0], worldNodes[1], worldNodes[2], damageCalc, healthCalc));
			}
		else log.severe("Couldn't find configuration nodes - does the config file exist?");
	}

	private void loadPluginSettings() 
	{
		//debugging
		String debugString = (String)config.getProperty("debugging");
		if(debugString != null)
		{
			if(debugString.equals("quiet"))
			{
				consoleDebugging_quiet = true;
				consoleDebugging_normal = false;
			}
			else if(debugString.equals("normal"))
			{
				log.info("[" + getDescription().getName()+ "] Debugging active.");
			}
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