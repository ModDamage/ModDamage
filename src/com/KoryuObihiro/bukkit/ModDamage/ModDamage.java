package com.KoryuObihiro.bukkit.ModDamage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

import com.KoryuObihiro.bukkit.ModDamage.Handling.DamageCalculator;
import com.KoryuObihiro.bukkit.ModDamage.Handling.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Handling.GroupHandler;
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
	public static Logger log = Logger.getLogger("Minecraft");
	public static PermissionHandler Permissions = null;
	private Configuration config;
	private String errorString_Permissions = ModDamageString(ChatColor.RED) + " You don't have access to that command.";
	private String errorString_findWorld = ModDamageString(ChatColor.RED) + " Couldn't find matching world name.";
	
	//config
	private ConfigurationNode pluginOffensiveNode, pluginDefensiveNode, pluginMobHealthNode, pluginScanNode;
	public static boolean consoleDebugging_normal = true;
	public static boolean consoleDebugging_verbose = false;
	public static boolean disable_DefaultDamage;
	public static boolean disable_DefaultHealth;
	public static boolean negative_Heal;
	public final HashMap<String, List<Material>> scanKeywords = new HashMap<String, List<Material>>();
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
		
		//TODO Use something less gimmicky. :P
		scanKeywords.put("axe", Arrays.asList(Material.WOOD_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE));
		scanKeywords.put("hoe", Arrays.asList(Material.WOOD_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLD_HOE, Material.DIAMOND_HOE));
		scanKeywords.put("pickaxe", Arrays.asList(Material.WOOD_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE));
		scanKeywords.put("spade", Arrays.asList(Material.WOOD_SPADE, Material.STONE_SPADE, Material.IRON_SPADE, Material.GOLD_SPADE, Material.DIAMOND_SPADE));
		scanKeywords.put("sword", Arrays.asList(Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD));
		
		//register plugin-related stuff with the server's plugin manager
		getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, this);
		getServer().getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Event.Priority.Highest, this);
		
		loadConfig(false);
		
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
				return true;
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
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("reload"))
				{
					if(fromConsole) loadConfig(true);
					else if(hasPermission(player, "moddamage.reload")) 
					{
						log.info("[" + getDescription().getName() + "] Reload initiated by user " + player.getName());
						loadConfig(true);
						player.sendMessage(ModDamageString(ChatColor.GREEN) + " Reloaded!");
					}
					else player.sendMessage(errorString_Permissions);
					return true;
				}
				//TODO md list
				else if(args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("c"))
				{
					//md check
					if(args.length == 1)
					{
						//Send everything if from console
						if(fromConsole)
						{
							log.info("Sending config info...");
							for(WorldHandler worldHandler : worldHandlers.values())
								if(worldHandler.loadedSomething())
									worldHandler.sendWorldConfig(null, 9001);
						}
						//Send list of loaded worlds; TODO Page these?
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
												?ChatColor.BLUE + "Use /md check [worldname] for more information." //FIXME
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
							Integer.parseInt(args[1]);
							throw(new NumberFormatException());//TODO Replace with something here if and when "/md check" gets paged.
						} 
						catch(NumberFormatException e)
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
								if(fromConsole) return worldHandlers.get(worldMatch).sendWorldConfig(player, 1);
								else if(hasPermission(player, "moddamage.check." + worldMatch.getName()))
									if(!worldHandlers.get(worldMatch).sendWorldConfig(player, 0))
										player.sendMessage("Invalid page number for world \"" + worldMatch.getName() + "\".");
								else player.sendMessage(ModDamageString(ChatColor.RED) 
										+ " You don't have permission to check world \"" + worldMatch.getName() + "\"");
							}
							else
							{
								if(player == null) log.info("Error: Couldn't find matching world substring.");
								else player.sendMessage(errorString_findWorld);
							}
						}
						return true;
					}
					//md check worldname groupname || md check worldname int
					else if(args.length == 3)
					{
						try
						{
							int pageNumber = Integer.parseInt(args[2]);
							
							//TODO Refactor for a single function?
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
								if(fromConsole) return worldHandlers.get(worldMatch).sendWorldConfig(player, pageNumber);
								else if(hasPermission(player, "moddamage.check." + worldMatch.getName()))
									if(!worldHandlers.get(worldMatch).sendWorldConfig(player, pageNumber))
										player.sendMessage("Invalid page number for world \"" + worldMatch.getName() + "\".");
								else player.sendMessage(ModDamageString(ChatColor.RED) 
										+ " You don't have permission to check world \"" + worldMatch.getName() + "\"");
							}
							else
							{
								if(player == null) log.info("Error: Couldn't find matching world substring.");
								else player.sendMessage(errorString_findWorld);
							}
						}
						catch(NumberFormatException e)
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
								String groupMatch = null;
								if(worldHandlers.containsKey(worldMatch))
									for(GroupHandler groupHandler : worldHandlers.get(worldMatch).groupHandlers.values())
										for(int i = 0; i < (groupHandler.getGroupName().length() - args[1].length() - 1); i++)
											if(args[1].equalsIgnoreCase(groupHandler.getGroupName().substring(i, i + args[1].length())))
											{
												groupMatch = groupHandler.getGroupName();
												break;
											}
								
								if(groupMatch != null)
								{
									if(hasPermission(player, "moddamage.check." + worldMatch.getName() + "." + groupMatch))
										return worldHandlers.get(worldMatch).groupHandlers.get(groupMatch).sendGroupConfig(player, 1);
									else player.sendMessage(ModDamageString(ChatColor.RED) + " You don't have permission to check group \"" + groupMatch + "\".");
								}
								else
								{
									if(player == null) log.info("Error: Couldn't find matching group substring.");
									else player.sendMessage(ModDamageString(ChatColor.RED) + " Couldn't find matching group name.");
								}
							}
							else
							{
								if(player == null) log.info("Error: Couldn't find matching world substring.");
								else player.sendMessage(errorString_findWorld);
							}
						}
						return true;
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
			if(forError) player.sendMessage(ChatColor.RED + "Error: invalid command syntax.");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "ModDamage commands:");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/moddamage | /md - bring up this help message");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/md (check|c) - check which worlds are configured");// ModDamage configuration"); FIXME
			//player.sendMessage(ChatColor.LIGHT_PURPLE + "/md check [worldname] - check a specifed world configuration");
			//player.sendMessage(ChatColor.LIGHT_PURPLE + "/md check [worldname] [groupname] - check a group configuration for a world");
		}
		else
		{
			if(forError) log.info("Error: invalid command syntax.");
			log.info("ModDamage commands:\n" +
					"/moddamage | /md - bring up this help message\n" +
					"/md check - check configuration\n" +
					"/md debug [debugType] - change debugging type (quiet, normal, verbose)\n" +
					"/md reload - reload configuration");
		}
	}
	
	
/////////////////// EVENT FUNCTIONS ////////////////////////////
	public void passDamageEvent(EntityDamageEvent event) 
	{
		//simple check for noDamageTicks - 
		if(((LivingEntity)event.getEntity()).getNoDamageTicks() > 50) //give this some leeway because this may be the time it takes to execute
		{
			event.setCancelled(true);
			return;
		}
		//TODO Event double-fires for projectile PvP. Bug Bukkit staff? :/
		World world = event.getEntity().getWorld();
		WorldHandler worldHandler = (worldHandlers.containsKey(world)?(worldHandlers.get(event.getEntity().getWorld())):null);
		if(worldHandler != null && (worldHandler.globalsLoaded || worldHandler.groupsLoaded || worldHandler.scanLoaded))
		{
			int damage = event.getDamage();
			LivingEntity ent_damaged = (LivingEntity)event.getEntity();
			if(event instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
				Entity ent_damager = event_EE.getDamager();
				DamageElement rangedElement = ((event instanceof EntityDamageByProjectileEvent) 
													&& !(ent_damager instanceof Skeleton || ent_damager instanceof Ghast))
														?DamageElement.matchRangedElement(((EntityDamageByProjectileEvent)event).getProjectile())
														:null;
				//Player-targeted damage
				if(ent_damaged instanceof Player)
				{
					Player player_damaged = (Player)ent_damaged;
				//PvP
					if(ent_damager instanceof Player)
					{
						Player player_damager = (Player)ent_damager;
						damage -= worldHandler.calcDefenseBuff(player_damaged, player_damager, event.getDamage(), rangedElement);
						damage += worldHandler.calcAttackBuff(player_damaged, player_damager, event.getDamage(), rangedElement);
						
						//send Scan
						if(hasPermission(player_damager, "moddamage.scan.pvp") 
								&& worldHandlers.get(player_damager.getWorld()).canScan(player_damager))
						{
							//Icky. Refactor?
							int displayHealth = ((LivingEntity)ent_damaged).getHealth() - ((!(damage < 0 && negative_Heal))?damage:0);
							player_damager.sendMessage(ChatColor.DARK_PURPLE + player_damaged.getName()
									+ ": " + Integer.toString((displayHealth < 0)?0:displayHealth));
						}
						
							
					}
				//NPvP
					else if(DamageElement.matchEntityElement(ent_damager) != null)
					{
						DamageElement mobType_damager = DamageElement.matchEntityElement(ent_damager);
						if(mobType_damager != null)
						{
							damage -= worldHandler.calcDefenseBuff(player_damaged, mobType_damager, event.getDamage());
							damage += worldHandler.calcAttackBuff(player_damaged, mobType_damager, event.getDamage());
						}
					}
				//nature-ent vs P
					else
					{
						//Lightning and explosion damage is technically an entity harming an entity
						DamageElement damageType = DamageElement.matchNatureElement(event.getCause());
						if(damageType != null)
						{
							damage -= worldHandler.calcDefenseBuff(player_damaged, damageType, event.getDamage());
							damage += worldHandler.calcAttackBuff(player_damaged, damageType, event.getDamage());
						}
					}
				}
				//Monster-targeted damage
				else if(DamageElement.matchEntityElement(ent_damaged) != null)
				{
					DamageElement mobType_damaged = DamageElement.matchEntityElement(ent_damaged);
				//PvNP
					if(ent_damager instanceof Player)
					{
						Player player_damager = (Player)ent_damager;
						if(mobType_damaged != null)
						{
							damage -= worldHandler.calcDefenseBuff(mobType_damaged, player_damager, event.getDamage(), rangedElement);
							damage += worldHandler.calcAttackBuff(mobType_damaged, player_damager, event.getDamage(), rangedElement);
						}
						//send Scan

						if(hasPermission(player_damager, "moddamage.scan." + mobType_damaged.getReference().toLowerCase()) 
								&& worldHandler.canScan(player_damager))
						{
							int displayHealth = ((LivingEntity)ent_damaged).getHealth() - ((!(damage < 0 && negative_Heal))?damage:0);
							player_damager.sendMessage(ChatColor.DARK_PURPLE + mobType_damaged.getReference() 
									+ "(id " + ent_damaged.getEntityId() + ")"
									+ ": " + Integer.toString((displayHealth < 0)?0:displayHealth));
						}
					}
				//NPvNP damage
					else if(DamageElement.matchEntityElement(ent_damager) != null)
					{
						DamageElement mobType_damager = DamageElement.matchEntityElement(ent_damager);
						if(mobType_damager != null && mobType_damaged != null);
						{
							damage -= worldHandler.calcDefenseBuff(mobType_damaged, mobType_damager, event.getDamage());
							damage += worldHandler.calcAttackBuff(mobType_damaged, mobType_damager, event.getDamage());
						}
					}
				//nature-ent vs NP
					else
					{
						//Lightning and explosion damage is technically an entity harming an entity
						DamageElement damageType = DamageElement.matchNatureElement(event.getCause());
						//log.info("Member of " + group_damaged + " got damaged by \"" + damageType.getConfigReference() + "\"");//debug
						if(damageType != null && mobType_damaged != null);
						{
							damage -= worldHandler.calcDefenseBuff(mobType_damaged, damageType, event.getDamage());
							damage += worldHandler.calcAttackBuff(mobType_damaged, damageType, event.getDamage());
						}
					}
				}
			}
			else
			{
				DamageElement damageType = DamageElement.matchNatureElement(event.getCause());
				if(ent_damaged instanceof Player)
				{
					Player player_damaged = (Player)ent_damaged;
					if(damageType != null)
					{
						damage -= worldHandler.calcDefenseBuff(player_damaged, damageType, event.getDamage());
						damage += worldHandler.calcAttackBuff(player_damaged, damageType, event.getDamage());
					}
				}
				else if(DamageElement.matchEntityElement(ent_damaged) != null)
				{
					DamageElement mobType_damaged = DamageElement.matchEntityElement(ent_damaged);
					if(damageType != null && mobType_damaged != null)
					{
						damage -= worldHandler.calcDefenseBuff(mobType_damaged, damageType, event.getDamage());
						damage += worldHandler.calcAttackBuff(mobType_damaged, damageType, event.getDamage());
					}
				}
			}
			if(damage < 0 && negative_Heal) 
				((LivingEntity)event.getEntity()).setHealth(((LivingEntity)event.getEntity()).getHealth() - damage);
			else event.setDamage(damage);
		}
	}


	public void passSpawnEvent(CreatureSpawnEvent event)
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
			
			if(worldOffensiveNode != null || worldDefensiveNode != null || worldMobHealthNode != null || worldScanNode != null)
			{
				worldHandlers.put(world, new WorldHandler(this, world, worldOffensiveNode, worldDefensiveNode, worldMobHealthNode, worldScanNode, damageCalc, healthCalc));
				return true;
			}
			else if(consoleDebugging_normal) log.warning("Couldn't find nodes for world " + world.getName());
		}
		return false;
	}
	
	private void loadConfig(boolean reloading)
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
		if(consoleDebugging_normal) log.info("[" + getDescription().getName()+ "] Default damage " + ((disable_DefaultDamage)?"dis":"en") + "abled.");
		disable_DefaultHealth = config.getBoolean("[" + getDescription().getName()+ "] disableDefaultHealth", false);
		if(consoleDebugging_normal) log.info("[" + getDescription().getName()+ "] Default health " + ((disable_DefaultDamage)?"dis":"en") + "abled.");
		negative_Heal = config.getBoolean("negativeHeal", false);
		if(consoleDebugging_normal) log.info("[" + getDescription().getName()+ "] Negative-damage healing " + ((disable_DefaultDamage)?"enabled.":"is not enabled."));
		
		//try to initialize WorldHandlers
		String nodeNames[] = {"Offensive", "Defensive", "MobHealth", "Scan"};
		if(pluginOffensiveNode != null || pluginDefensiveNode != null || pluginMobHealthNode != null || pluginScanNode != null)
			for(World world : getServer().getWorlds())
			{
				ConfigurationNode worldNodes[] = {
													(pluginOffensiveNode != null?pluginOffensiveNode.getNode(world.getName()):null), 
													(pluginDefensiveNode != null?pluginDefensiveNode.getNode(world.getName()):null), 
													(pluginMobHealthNode != null?pluginMobHealthNode.getNode(world.getName()):null),
													(pluginScanNode != null?pluginScanNode.getNode(world.getName()):null)};
				for(int i = 0; i < worldNodes.length; i++)
					if(worldNodes[i] == null && (consoleDebugging_verbose))
						log.warning("{Couldn't find " + nodeNames[i] +  " node for world \"" + world.getName() + "\"}");
				WorldHandler worldHandler = new WorldHandler(this, world, worldNodes[0], worldNodes[1], worldNodes[2], worldNodes[3], damageCalc, healthCalc);
				if(!worldHandler.equals(null)) worldHandlers.put(world, worldHandler);
			}
		else log.severe("Couldn't find configuration nodes - does the config file exist?");
		//cleanup(); TODO Doesn't  work yet. :(
	}

	private String ModDamageString(ChatColor color){ return color + "[" + ChatColor.DARK_RED + "Mod" + ChatColor.DARK_BLUE + "Damage" + color + "]";}
	
	public void cleanup()
	{
		for(WorldHandler worldHandler : worldHandlers.values())
			if(!worldHandler.loadedSomething()) 
				worldHandlers.remove(worldHandler.getWorld());
	}
}