package com.KoryuObihiro.bukkit.ModDamage;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Handlers.ServerHandler;
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
	//TODO
	//- Deregister when Bukkit supports!
	//- Client-sided mod for displaying health?
	//- Add worldLoad-triggered loading of MD
	//- Aliasing?
	//- "Failed to reload" ingame
	//- per-world for disableDefault variables
	//- count characters in config for message length
	//- Refactor config to contain errors?
	
	//plugin-related
	public static boolean isEnabled = false;
	private final ModDamageEntityListener entityListener = new ModDamageEntityListener(this);
	public final static Logger log = Logger.getLogger("Minecraft");
	public static PermissionHandler Permissions = null;
	public Configuration config;
	public static String errorString_Permissions = ModDamageString(ChatColor.RED) + " You don't have access to that command.";
	public static String errorString_findWorld = ModDamageString(ChatColor.RED) + " Couldn't find matching world name.";
	
	//Alias hashmaps
	public final static HashMap<String, List<Material>> itemAliases = new HashMap<String, List<Material>>();
	public final static HashMap<String, List<String>> groupAliases = new HashMap<String, List<String>>();
	public final static HashMap<String, List<String>> mobAliases = new HashMap<String, List<String>>();
	
	//Configuration
	private ConfigurationNode pluginOffensiveNode, pluginDefensiveNode, pluginMobHealthNode, pluginScanNode;
	public static boolean multigroupPermissions = true;
	
	//User-customized config
	public static boolean consoleDebugging_normal = true;
	public static boolean consoleDebugging_verbose = false;
	public static boolean disable_DefaultDamage;
	public static boolean disable_DefaultHealth;
	public static boolean negative_Heal;
	final static List<String> emptyList = null; //FIXME Dunno if it can be just any null object, but at least it leaves things blank.		
	
	public static ServerHandler serverHandler;
	
////////////////////////// INITIALIZATION
	@Override
	public void onEnable() 
	{
	//PERMISSIONS
		Plugin permissionsPlugin = getServer().getPluginManager().getPlugin("Permissions");
		if (permissionsPlugin != null)
		{
			ModDamage.Permissions = ((Permissions)permissionsPlugin).getHandler();
			log.info("[" + getDescription().getName() + "] " + this.getDescription().getVersion() + " enabled [Permissions v" + permissionsPlugin.getDescription().getVersion() + " active]");
			
			//This is necessary for backwards-compatibility.
			multigroupPermissions = permissionsPlugin.getDescription().getVersion().startsWith("3.");
		}
		else log.info("[" + getDescription().getName() + "] " + this.getDescription().getVersion() + " enabled [Permissions not found]");
		
		//register plugin-related stuff with the server's plugin manager
		getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, this);
		getServer().getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Event.Priority.Highest, this);
		
		config = this.getConfiguration();
		reload(true);
		isEnabled = true;
	}

	@Override
	public void onDisable() 
	{
		log.info("[" + getDescription().getName() + "] disabled.");
	}

////COMMAND PARSING ////
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
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
							if(fromConsole)
							{
								log.info("[" + getDescription().getName() + "] Sending server config info...");
								serverHandler.sendConfig(null, 9001);
								log.info("[" + getDescription().getName() + "] Done.");
							}
							else if(args.length == 1)
							{
								//Send everything if from console
								//Send list of loaded worlds
								if(hasPermission(player, "moddamage.check"))
								{
									serverHandler.sendConfig(player, 1);
								}
								else player.sendMessage(errorString_Permissions);
								return true;
							}
							//md check worldname || md check int
							else if(args.length == 2)
							{
								try
								{
									serverHandler.sendConfig(player, Integer.parseInt(args[1]));
								} 
								catch(NumberFormatException e)
								{
									serverHandler.sendConfig(player, args[1]);
								}
								return true;
							}
							//md check worldname groupname || md check worldname int
							else if(args.length == 3)
							{
								try
								{
									serverHandler.sendConfig(player, args[1], Integer.parseInt(args[2]));
								}
								catch(NumberFormatException e)
								{
									serverHandler.sendConfig(player, args[1], args[2]);
								}
								return true;
							}
							//md check worldname groupname int
							else if(args.length == 4)
							{
								try
								{
									serverHandler.sendConfig(player, args[1], args[2], Integer.parseInt(args[3]));
								}
								catch(NumberFormatException e){ player.sendMessage(ModDamageString(ChatColor.RED) + "Error: final parameter should be an integer." );}
								return true;
							}
						}
						else if(args[0].equalsIgnoreCase("checkgroup") || args[0].equalsIgnoreCase("cg"))
						{
							//md checkgroup worldname groupname
							if(args.length == 2)
								serverHandler.sendGroupConfig(player, args[1]);
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
	
////EVENT FUNCTIONS ////
	public void handleDamageEvent(EntityDamageEvent event) 
	{
		LivingEntity ent_damaged = (LivingEntity)event.getEntity();
	 	//simple check for noDamageTicks - not sure how this will affect other elements in Bukkit yet.
 		if(ent_damaged.getNoDamageTicks() > 40) return;
		
		if(serverHandler.loadedSomething())
		{
			DamageEventInfo eventInfo = null;
			
			if(DamageElement.matchNonlivingElement(event.getCause()) != null)
			{
			//Nonliving damage to LivingEntity
				//Nonliving vs Player
				if(ent_damaged instanceof Player)
					eventInfo = new DamageEventInfo((Player)ent_damaged, DamageElement.matchNonlivingElement(event.getCause()), event.getDamage());
				//Nonliving vs Mob
				else if(DamageElement.matchLivingElement(ent_damaged) != null)
					eventInfo = new DamageEventInfo(ent_damaged, DamageElement.matchLivingElement(ent_damaged), DamageElement.matchNonlivingElement(event.getCause()), event.getDamage());
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
					if(ent_damager instanceof Player) eventInfo = new DamageEventInfo((Player)ent_damaged, (Player)ent_damager, rangedElement, event.getDamage());
				//Mob vs Player
					else eventInfo = new DamageEventInfo((Player)ent_damaged, ent_damager, DamageElement.matchLivingElement(ent_damager), event.getDamage());
				}
			//Monster-targeted damage
				else if(DamageElement.matchLivingElement(ent_damaged) != null)
				{
				//Player vs Mob
					if(ent_damager instanceof Player) eventInfo = new DamageEventInfo(ent_damaged, DamageElement.matchLivingElement(ent_damaged), (Player)ent_damager, rangedElement, event.getDamage());
				//Mob vs Mob 
					else if(DamageElement.matchLivingElement(ent_damager) != null) 
						eventInfo = new DamageEventInfo(ent_damaged, DamageElement.matchLivingElement(ent_damaged), ent_damager, DamageElement.matchLivingElement(ent_damager), event.getDamage());
				
				}
			}
			else{ log.severe("Something horrible just happened. Bug KoryuObihiro about it.");}//TODO REMOVE....MEBBE
			serverHandler.doDamageCalculations(eventInfo);
			if(eventInfo.shouldScan)
			{
				int displayHealth = (eventInfo.entity_target).getHealth() - ((!(eventInfo.eventDamage < 0 && ModDamage.negative_Heal))?eventInfo.eventDamage:0);
				((Player)eventInfo.entity_attacker).sendMessage(ChatColor.DARK_PURPLE + eventInfo.damageElement_target.getReference() 
						+ "(" + (eventInfo.name_target != null?eventInfo.name_target:("id " + eventInfo.entity_target.getEntityId()))
						+ "): " + Integer.toString((displayHealth < 0)?0:displayHealth));
			}
			if(eventInfo.eventDamage < 0 && !ModDamage.negative_Heal) 
				eventInfo.eventDamage = 0;
			event.setDamage(eventInfo.eventDamage);
		}
	}

	public void handleSpawnEvent(CreatureSpawnEvent event)
	{
		if(event.getEntity() != null)
		{
			LivingEntity entity = (LivingEntity)event.getEntity();
			SpawnEventInfo eventInfo = ((entity instanceof Player)
											?new SpawnEventInfo((Player)entity)
											:new SpawnEventInfo((LivingEntity)entity));
			if(ModDamage.disable_DefaultHealth) eventInfo.eventHealth = 0;
			serverHandler.doSpawnCalculations(eventInfo);
			
			entity.setHealth(eventInfo.eventHealth);
			event.setCancelled(entity.getHealth() <= 0);
		}
	}

///// HELPER FUNCTIONS ////
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

	public static String ModDamageString(ChatColor color){ return color + "[" + ChatColor.DARK_RED + "Mod" + ChatColor.DARK_BLUE + "Damage" + color + "]";}
	
//// PLUGIN CONFIGURATION ////
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

/////////////////// MECHANICS CONFIGURATION 
	private boolean reload(boolean printToConsole)
	{
	//CONFIGURATION
		config.load();
		//get plugin config.yml...if it doesn't exist, create it.
		if(!(new File(this.getDataFolder(), "config.yml")).exists()) writeDefaults();

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
		
	//single-property configs
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
		
	//TODO aliases 
		//if(loadAliases() && consoleDebugging_normal) log.info("Aliases loaded!");
		//else log.warning
		
		
	//try to initialize WorldHandlers

		pluginOffensiveNode = config.getNode("Offensive");
		pluginDefensiveNode = config.getNode("Defensive");
		pluginMobHealthNode = config.getNode("MobHealth");
		pluginScanNode = config.getNode("Scan");
		ModDamage.serverHandler = new ServerHandler(this, pluginOffensiveNode, pluginDefensiveNode, pluginMobHealthNode, pluginScanNode);
		return serverHandler.loadedSomething();
	}

	private void writeDefaults() 
	{		
		//TODO Migrate this into the Handler class.
	//set single-property stuff
		log.severe("[" + getDescription().getName() + "] No configuration file found! Writing a blank config...");
		config.setProperty("debugging", "normal");

	//write server globals
		writeDamageElements("global");
		writeMobHealthElements("global");
		config.setProperty("Scan.global", emptyList);
		config.save();
		
	//write world globals
		for(World world : this.getServer().getWorlds())
		{
			String configPath = "worlds." + world.getName();
			writeDamageElements(configPath + ".global");
			writeMobHealthElements(configPath);
			config.setProperty("Scan." + configPath, emptyList);
			config.setProperty("Scan." + configPath + ".groups", emptyList);
		}
		
		config.save();
		config.load();//TODO Necessary?
		log.severe("[" + getDescription().getName() + "] Done! Don't forget that you can define armor, melee, and group nodes further.");
	}


	private void writeDamageElements(String configPath)
	{
		for(DamageElement damageCategory : DamageElement.getGenericElements())
		{
		//write generics
			config.setProperty("Offensive." + configPath + ".generic." + damageCategory.getReference(), emptyList);
			if(!damageCategory.equals(DamageElement.GENERIC_NATURE))
				config.setProperty("Defensive." + configPath + ".generic." + damageCategory.getReference(), emptyList);
		//write specifics
			if(damageCategory.hasSubConfiguration())
				for(DamageElement subElement : DamageElement.getElementsOf(damageCategory))
				{
					config.setProperty("Offensive." + configPath + "." + damageCategory.getReference() + "." + subElement.getReference(), emptyList);
					if(!damageCategory.equals(DamageElement.GENERIC_NATURE))
						config.setProperty("Defensive." + configPath + "." + damageCategory.getReference() + "." + subElement.getReference(), emptyList);
				}
			config.setProperty("Offensive." + configPath + ".armor", emptyList);
			config.setProperty("Defensive." + configPath + ".armor", emptyList);
			config.setProperty("Offensive." + configPath + ".groups", emptyList);
			config.setProperty("Defensive." + configPath + ".groups", emptyList);
		}
	}
	
	private void writeMobHealthElements(String configPath)
	{
		List<DamageElement> mobHealthList = DamageElement.getElementsOf(DamageElement.GENERIC_ANIMAL);
		mobHealthList.addAll(DamageElement.getElementsOf(DamageElement.GENERIC_MOB));
			for(DamageElement creatureElement : mobHealthList)
				config.setProperty("MobHealth." + configPath + "." + creatureElement.getReference(), emptyList);
	}
	

	/*
	private boolean loadAliases()
	{
		return false;
	}
	
	private void saveAliases(){}
	*/
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