package com.KoryuObihiro.bukkit.ModDamage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Server;
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

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Handlers.ServerHandler;
import com.elbukkit.api.elregions.elRegionsPlugin;
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
	//--ModDamage Main
	// -Deregister when Bukkit supports!
	// -Client-sided mod for displaying health?
	// -"Failed to reload" ingame
	// -per-world for disableDefault variables
	// -count characters in config for message length
	// -change md debug messages to reflect previous state
	// -single-line configuration!
	
	//--EntityListener
	// -Add worldLoad-triggered loading of MD
	
	//--CalculationUtility
	// -Refactor config to contain errors?
	
	//plugin-related
	public static boolean isEnabled = false;
	public static Server server;
	private final ModDamageEntityListener entityListener = new ModDamageEntityListener(this);
	public final static Logger log = Logger.getLogger("Minecraft");
	public static PermissionHandler Permissions = null;
	public static elRegionsPlugin elRegions = null;
	public Configuration config;
	public static String errorString_Permissions = ModDamageString(ChatColor.RED) + " You don't have access to that command.";
	public static String errorString_findWorld = ModDamageString(ChatColor.RED) + " Couldn't find matching world name.";
	
	//External Configuration
	public static boolean elRegions_enabled = false;
	public static boolean multigroupPermissions = true;
	
	//User-customized config
	public static boolean consoleDebugging_normal = true;
	public static boolean consoleDebugging_verbose = false;
	public static boolean disable_DefaultDamage;
	public static boolean disable_DefaultHealth;
	public static boolean negative_Heal;
	public static final List<String> emptyList = null; //FIXME Dunno if it can be just any null object, but at least it leaves things blank.		
	
	public static ServerHandler serverHandler;
	public static boolean using_Permissions = false;
	public static boolean using_elRegions = false;
	
////////////////////////// INITIALIZATION
	@Override
	public void onEnable() 
	{
		ModDamage.server = getServer();
		
	//PERMISSIONS
		Plugin permissionsPlugin = getServer().getPluginManager().getPlugin("Permissions");
		if (permissionsPlugin != null)
		{
			using_Permissions = true;
			ModDamage.Permissions = ((Permissions)permissionsPlugin).getHandler();
			log.info("[" + getDescription().getName() + "] " + this.getDescription().getVersion() + " enabled [Permissions v" + permissionsPlugin.getDescription().getVersion() + " active]");
			
			//This is necessary for backwards-compatibility.
			multigroupPermissions = permissionsPlugin.getDescription().getVersion().startsWith("3.");
		}
		else log.info("[" + getDescription().getName() + "] " + this.getDescription().getVersion() + " enabled [Permissions not found]");
		
		elRegions = (elRegionsPlugin) this.getServer().getPluginManager().getPlugin("elRegions");
		if (elRegions != null) 
		{
			using_elRegions = true;
		    log.info("[" + getDescription().getName() + "] Found elRegions v" + elRegions.getDescription().getVersion());
		    elRegions_enabled = true;
		}
		
		/*
        elRegionsPlugin elRegions = (elRegionsPlugin) this.getServer().getPluginManager().getPlugin("elRegions");
		RegionManager el = elRegions.getRegionManager((World)null);
		*/
		
		//register plugin-related stuff with the server's plugin manager
		getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, this);
		getServer().getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Event.Priority.Highest, this);
		
		config = this.getConfiguration();
		isEnabled = reload(true);
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
								log.info("[" + getDescription().getName() + "] Reload initiated by user " + player.getName() + "...");
								if(reload(true)) player.sendMessage(ModDamageString(ChatColor.GREEN) + " Reloaded!");
								else player.sendMessage(ModDamageString(ChatColor.RED) + " No configurations loaded! Are any calculation strings defined?");
								log.info("[" + getDescription().getName() + "] Reload complete.");
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
		//simple check for noDamageTicks - the appropriate event-firing check should be implemented in Bukkit soon.
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
				else if(DamageElement.matchMobType(ent_damaged) != null)
					eventInfo = new DamageEventInfo(ent_damaged, DamageElement.matchMobType(ent_damaged), DamageElement.matchNonlivingElement(event.getCause()), event.getDamage());
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
					else eventInfo = new DamageEventInfo((Player)ent_damaged, ent_damager, DamageElement.matchMobType(ent_damager), event.getDamage());
				}
			//Monster-targeted damage
				else if(DamageElement.matchMobType(ent_damaged) != null)
				{
				//Player vs Mob
					if(ent_damager instanceof Player) eventInfo = new DamageEventInfo(ent_damaged, DamageElement.matchMobType(ent_damaged), (Player)ent_damager, rangedElement, event.getDamage());
				//Mob vs Mob 
					else if(DamageElement.matchMobType(ent_damager) != null) 
						eventInfo = new DamageEventInfo(ent_damaged, DamageElement.matchMobType(ent_damaged), ent_damager, DamageElement.matchMobType(ent_damager), event.getDamage());
				
				}
			}
			else{ log.severe("[" + getDescription().getName() + "] Error! Unhandled damage event. Is this plugin up-to-date?");}
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
		else serverHandler = new ServerHandler(this, config.getNode("Damage"), config.getNode("MobHealth"), config.getNode("Scan"));

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
		return serverHandler.loadedSomething();
	}

	private void writeDefaults() 
	{        
        serverHandler = new ServerHandler(this, null, null, null);
	//set single-property stuff
		log.severe("[" + getDescription().getName() + "] No configuration file found! Writing a blank config...");
		config.setProperty("debugging", "normal");

	//write server globals
		config.setProperty("Damage.global", emptyList);
		writeMobHealthElements("global");
		config.setProperty("Scan.global", emptyList);
		config.setProperty("Scan.groups", emptyList);
		config.save();
		
	//write world globals
		for(World world : this.getServer().getWorlds())
		{
			String configPath = "worlds." + world.getName();
			config.setProperty("Damage." + configPath, emptyList);
			writeMobHealthElements(configPath + ".global");
			config.setProperty("Scan." + configPath + ".global", emptyList);
			config.setProperty("Scan." + configPath + ".groups", emptyList);
		}
		
	//write default aliases
		String[][] toolAliases = { {"axe", "hoe", "pickaxe", "spade", "sword"}, {"WOOD_", "STONE_", "IRON_", "GOLD_", "DIAMOND_"}};
		for(String toolType : toolAliases[0])
		{
			List<String> combinations = new ArrayList<String>();
			for(String toolMaterial : toolAliases[1])
				combinations.add(toolMaterial + toolType.toUpperCase());
			config.setProperty("Aliases." + toolType, combinations);
		}
		
		config.save();
		config.load();//TODO Necessary?
		log.severe("[" + getDescription().getName() + "] Defaults written!");
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