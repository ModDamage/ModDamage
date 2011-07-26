package com.KoryuObihiro.bukkit.ModDamage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.RangedElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility.LogSetting;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Addition;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.DiceRoll;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.DiceRollAddition;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Division;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.DivisionAddition;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.IntervalRange;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.LiteralRange;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Message;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Multiplication;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Set;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.ArmorSetSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.BiomeSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.EntityTypeSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.EnvironmentSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.PlayerWieldSwitch;
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
	// -count characters in config for message length
	// -get Dispenser attackers
	// -Fishing rod implementation
	// -Autogen world/entitytype switches?
	// -Triggered effects...should be a special type of tag! :D Credit: ricochet1k

	// -find a way to give players ownership of an explosion
	// -Deregister when Bukkit supports!
	// -Client-sided mod for displaying health?
	
	//--API
	// -Write tut, code requirements, and regex guidelines for using this library
	// -Print plugin name at fault when calculation registry fails
	// Ideas
	// -External calls to aliased sets of routines? But...EventInfo would be screwed up. :P
	//--ModDamageElement
	// -Make ModDamageElement do some parsing with Material.name()? (update ArmorSet and CalculationUtility accordingly if this is done)
	
	//--RoutineUtility
	// 0.9.5
	// -Refactor config to contain errors and display classes allocated - add to config strings regardless
	// -Use warnings inside constructors (pass routineUtility)
	// FIXME Why aren't the patternParts all final? o_o
	// TODO message routines (force aliasing here), don't forget this nasty thing:
	/*
	if(eventInfo.shouldScan)
	{
		int displayHealth = (eventInfo.entity_target).getHealth() - ((!(eventInfo.eventDamage < 0 && ModDamage.negative_Heal))?eventInfo.eventDamage:0);
		((Player)eventInfo.entity_attacker).sendMessage(ChatColor.DARK_PURPLE + eventInfo.element_target.getReference() 
				+ "(" + (eventInfo.name_target != null?eventInfo.name_target:("id " + eventInfo.entity_target.getEntityId()))
				+ "): " + Integer.toString((displayHealth < 0)?0:displayHealth));
	}
	*/
	// -Make sure that Slimes work for EntityTargetedByOther[ - they failed in a previous RB.
	// 0.9.6
	// -AoE clearance, block search nearby for Material?
	
	// Ideas
	// -check against an itemstack in the player's inventory
	// -if.entityis.inRegion
	// -if.server.onlineenabled
	// -if.server.port.#port
	// -switch.region
	// -switch.spawnreason
	// -switch.wieldquantity
	// -
	// -for.#
	// -for.eventvalue
	// -foreach
	// -Area pieces too!
	
	//--Yet-to-be-plausible:
	// -switch.conditional
	// -tag.$aliasName
	// -ability to clear non-static tags
	// -aliases (dynamic: $ and static: _): //Check in config allocation for existing static!
	//   -armor
	//   -elements
	//   -entities
	//   -items
	//   -groups
	// -event keyword (_event)
	// -External: tag entities with an alias ($)
	// -External: check entity tags
	
	//plugin-related
	private static boolean isEnabled = false;
	public static Server server;
	private final ModDamageEntityListener entityListener = new ModDamageEntityListener(this);
	private final static Logger log = Logger.getLogger("Minecraft");
	private RoutineUtility routineUtility;
	public static PermissionHandler Permissions = null;
	//private static elRegionsPlugin elRegions = null;
	private static Configuration config;
	private static String errorString_Permissions = ModDamageString(ChatColor.RED) + " You don't have access to that command.";
	
	//External Configuration
	public static boolean multigroupPermissions = true;	
	public static boolean using_Permissions = false;
	public static boolean using_elRegions = false;
	public static boolean negative_Heal;

	private static boolean damageRoutinesLoaded = false;
	private static boolean spawnRoutinesLoaded = false;
	private final List<Routine> damageRoutines = new ArrayList<Routine>();
	private final List<Routine> spawnRoutines = new ArrayList<Routine>();
	
	///public static HashMap<String, List<ArmorSet>> armorAliases = new HashMap<String, List<ArmorSet>>();
	public static HashMap<String, List<Material>> itemAliases = new HashMap<String, List<Material>>();
	//public static HashMap<String, List<String>> groupAliases = new HashMap<String, List<String>>();
	//public static HashMap<String, List<String>> messageAliases = new HashMap<String, List<String>>();
	//public static HashMap<String, List<ModDamageElement>> mobAliases = new HashMap<String, List<ModDamageElement>>();

	private static boolean aliasesLoaded = false;
	
	
////////////////////////// INITIALIZATION
	@Override
	public void onEnable() 
	{
		ModDamage.server = getServer();
		routineUtility = new RoutineUtility(log);
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
		
		/*
		elRegions = (elRegionsPlugin) this.getServer().getPluginManager().getPlugin("elRegions");
		if (elRegions != null) 
		{
			using_elRegions = true;
		    log.info("[" + getDescription().getName() + "] Found elRegions v" + elRegions.getDescription().getVersion());
		}
		*/
		
		//register plugin-related stuff with the server's plugin manager
		server.getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, this);
		server.getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Event.Priority.Highest, this);
		
		//register MD routines
//Base Calculations
		Addition.register(routineUtility);
		DiceRoll.register(routineUtility);
		DiceRollAddition.register(routineUtility);
		Division.register(routineUtility);
		DivisionAddition.register(routineUtility);
		IntervalRange.register(routineUtility);
		LiteralRange.register(routineUtility);
		Multiplication.register(routineUtility);
		Set.register(routineUtility);	
		Message.register(routineUtility);
		/*
//Nestable Calculations
	//Conditionals
		Binomial.register(routineUtility);
		//Entity
		EntityAirTicksComparison.register(routineUtility);
		EntityBiome.register(routineUtility);
		EntityCoordinateComparison.register(routineUtility);
		EntityDrowning.register(routineUtility);
		EntityExposedToSky.register(routineUtility);
		EntityFallComparison.register(routineUtility);
		EntityFalling.register(routineUtility);
		EntityFireTicksComparison.register(routineUtility);
		EntityHealthComparison.register(routineUtility);
		EntityLightComparison.register(routineUtility);
		EntityOnBlock.register(routineUtility);
		EntityOnFire.register(routineUtility);
		EntityTargetedByOther.register(routineUtility);
		EntityUnderwater.register(routineUtility);
		EventValueComparison.register(routineUtility);
		PlayerWearing.register(routineUtility);
		PlayerWearingOnly.register(routineUtility);
		PlayerWielding.register(routineUtility);
		//World
		WorldTime.register(routineUtility);
		WorldEnvironment.register(routineUtility);
		//Server
		ServerOnlineMode.register(routineUtility);
		ServerPlayerCount.register(routineUtility);
		//Event
		EventValueComparison.register(routineUtility);
	//Effects
		EntityExplode.register(routineUtility);
		EntityHeal.register(routineUtility);
		EntityReflect.register(routineUtility);
		EntitySetAirTicks.register(routineUtility);
		EntitySetFireTicks.register(routineUtility);
		EntitySetHealth.register(routineUtility);
		PlayerSetItem.register(routineUtility);
		SlimeSetSize.register(routineUtility);
		*/
	//Switches
		ArmorSetSwitch.register(routineUtility);
		BiomeSwitch.register(routineUtility);
		EntityTypeSwitch.register(routineUtility);
		EnvironmentSwitch.register(routineUtility);
		PlayerWieldSwitch.register(routineUtility);
		config = this.getConfiguration();
		reload();
		isEnabled = isLoaded();
	}

	@Override
	public void onDisable(){ log.info("[" + getDescription().getName() + "] disabled.");}

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
								if(args.length == 1) RoutineUtility.toggleLogging(player);
								else if(args.length == 2)
								{
									LogSetting matchedSetting = LogSetting.matchSetting(args[1]);
									if(matchedSetting != null)
									{
										String sendThis = "Changed debug mode from " + RoutineUtility.logSetting.name().toLowerCase() + " to " + matchedSetting.name().toLowerCase();
										log.info("[" + getDescription().getName() + "] " + sendThis);
										if(!fromConsole) player.sendMessage(ChatColor.GREEN + sendThis);
										RoutineUtility.logSetting = matchedSetting;
									}
									else
									{
										sendUsage(player, true);
										return true;
									}
									return true;
								}
							}
							else player.sendMessage(errorString_Permissions);
							return true;
						}
						else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r"))
						{
							if(fromConsole) reload();
							else if(hasPermission(player, "moddamage.reload")) 
							{
								log.info("[" + getDescription().getName() + "] Reload initiated by user " + player.getName() + "...");
								reload();
								if(isLoaded()) player.sendMessage(ModDamageString(ChatColor.GREEN) + " Reloaded!");
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
								RoutineUtility.sendConfig(null, 9001);
								log.info("[" + getDescription().getName() + "] Done.");
							}
							else if(args.length == 1)
							{
								//Send everything if from console
								//Send list of loaded worlds
								if(hasPermission(player, "moddamage.check"))
								{
									RoutineUtility.sendConfig(player, 1);
								}
								else player.sendMessage(errorString_Permissions);
								return true;
							}
							//md check worldname || md check int
							else if(args.length == 2)
							{
								try
								{
									RoutineUtility.sendConfig(player, Integer.parseInt(args[1]));
								} 
								catch(NumberFormatException e)
								{
									sendUsage(player, true);
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

//// EVENT FUNCTIONS ////
	public void handleDamageEvent(EntityDamageEvent event) 
	{
		LivingEntity ent_damaged = (LivingEntity)event.getEntity();
		if(isLoaded() && ent_damaged.getNoDamageTicks() <= 40)
		{
			DamageEventInfo eventInfo = null;
			if(ModDamageElement.matchNonlivingElement(event.getCause()) != null)
				eventInfo = new DamageEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), null, ModDamageElement.matchNonlivingElement(event.getCause()), null, event.getDamage());
			else if(event instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
				//TODO Make this compatible with dispensers!
				LivingEntity ent_damager = (LivingEntity)event_EE.getDamager();
				RangedElement rangedElement = (event instanceof EntityDamageByProjectileEvent?RangedElement.matchElement(((EntityDamageByProjectileEvent)event).getProjectile()):null);
				eventInfo = new DamageEventInfo(ent_damaged, ModDamageElement.matchMobType(ent_damaged), ent_damager, ModDamageElement.matchMobType(ent_damager), rangedElement, event.getDamage());
			}
			else{ log.severe("[" + getDescription().getName() + "] Error! Unhandled damage event. Is this plugin up-to-date?");}
			for(Routine routine : damageRoutines)
				routine.run(eventInfo);
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
			SpawnEventInfo eventInfo = new SpawnEventInfo(entity);

			if(eventInfo.element != null)
				for(Routine routine : spawnRoutines)
					routine.run(eventInfo);
			
			entity.setHealth(eventInfo.eventHealth);
			event.setCancelled(entity.getHealth() <= 0);
		}
	}
	
///// HELPER FUNCTIONS ////
	private boolean isLoaded(){ return damageRoutinesLoaded || spawnRoutinesLoaded || aliasesLoaded;}
	
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
	
	protected void clear() 
	{
		damageRoutines.clear();
		spawnRoutines.clear();
		itemAliases.clear();
		damageRoutinesLoaded = spawnRoutinesLoaded = false;
	}
	
//// PLUGIN CONFIGURATION ////
	private void setPluginStatus(Player player, boolean sentEnable) 
	{
		if(sentEnable)
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
	private void reload()
	{
		damageRoutines.clear();
		spawnRoutines.clear();
		RoutineUtility.clear();
		config.load();
	//get plugin config.yml...if it doesn't exist, create it.
		if(!(new File(this.getDataFolder(), "config.yml")).exists()) writeDefaults();
	//load debug settings
		String debugString = config.getString("debugging");
		if(debugString != null)
		{
			LogSetting logSetting = LogSetting.matchSetting(debugString);
			switch(logSetting)
			{
				case QUIET: 
					log.info("[" + getDescription().getName()+ "] \"Quiet\" mode active - suppressing debug messages and warnings.");
					RoutineUtility.setLogging(logSetting);
					break;
				case NORMAL: 
					log.info("[" + getDescription().getName()+ "] Debugging active.");
					RoutineUtility.setLogging(logSetting);
					break;
				case VERBOSE: 
					log.info("[" + getDescription().getName()+ "] Verbose debugging active.");
					RoutineUtility.setLogging(logSetting);
					break;
				default: 
					log.info("[" + getDescription().getName()+ "] Debug string not recognized - defaulting to \"normal\" settings.");
					RoutineUtility.setLogging(LogSetting.NORMAL);
					break;
			}
		}
	//routines
		damageRoutinesLoaded = loadRoutines("Damage", damageRoutines);
		spawnRoutinesLoaded = loadRoutines("MobHealth", spawnRoutines);
	//single-property config
		negative_Heal = config.getBoolean("negativeHeal", false);
		if(RoutineUtility.shouldOutput(LogSetting.VERBOSE))
			log.info("[" + getDescription().getName()+ "] Negative-damage healing " + (negative_Heal?"en":"dis") + "abled.");
		
		aliasesLoaded = loadAliases();
		if(aliasesLoaded)
		{
			if(RoutineUtility.shouldOutput(LogSetting.VERBOSE)) log.info("Aliases loaded!");
		}
		else log.warning("No aliases loaded! Are any aliases defined?");
		
		config.load(); //Discard any changes made to the file by the above reads.
		log.info("[" + getDescription().getName() + "] " + (isLoaded()?"Finished loading configuration.":"Error loading configuration."));
	}

	private void writeDefaults() 
	{
		log.severe("[" + getDescription().getName() + "] No configuration file found! Writing a blank config...");
		config.setProperty("debugging", "normal");
		config.setProperty("Damage", null);
		config.setProperty("Spawn", null);
		
		String[][] toolAliases = { {"axe", "hoe", "pickaxe", "spade", "sword"}, {"WOOD_", "STONE_", "IRON_", "GOLD_", "DIAMOND_"}};
		for(String toolType : toolAliases[0])
		{
			List<String> combinations = new ArrayList<String>();
			for(String toolMaterial : toolAliases[1])
				combinations.add(toolMaterial + toolType.toUpperCase());
			config.setProperty("Aliases.Item." + toolType, combinations);
		}

		config.save();
		log.severe("[" + getDescription().getName() + "] Defaults written to config.yml!");
	}

	protected boolean loadRoutines(String loadType, List<Routine> routineList)
	{
		boolean loadedSomething = false;
		List<Object> routineObjects = config.getList(loadType);
		if(routineObjects != null)
		{
			if(RoutineUtility.shouldOutput(LogSetting.VERBOSE)) log.info(loadType + " configuration found, parsing...");
			List<Routine> calculations = RoutineUtility.parse(routineObjects, loadType);
			if(!calculations.isEmpty())
			{
				routineList.addAll(calculations);
				loadedSomething = true;
			}
		}
		return loadedSomething;
	}
	
//// ALIASING ////
	protected boolean loadAliases()
	{
		ConfigurationNode aliasNode = config.getNode("Aliases");
		boolean addedSomething = false;
		if(aliasNode != null)
		{
			ConfigurationNode itemNode = aliasNode.getNode("Item");
			if(itemNode != null)
			{
				List<String> aliasKeys = aliasNode.getKeys("Item");
				for(String alias : aliasKeys)
				{
					List<Material> aliasValues = new ArrayList<Material>();
					boolean validAlias = false;
					for(String itemString : itemNode.getStringList(alias, new ArrayList<String>()))
					{
						List<Material> matchedValues = matchItemAlias(itemString);
						if(!matchedValues.isEmpty())
						{
							aliasValues.addAll(matchedValues);
							validAlias = true;
						}
						else 
						{
							RoutineUtility.addToConfig(LogSetting.QUIET, 0, "No matching alias or material name \"" + itemString + "\"" + alias, true);
							matchedValues.clear();
							validAlias = false;
						}
					}
					if(validAlias)
					{
						addedSomething = true;
						itemAliases.put("_" + alias, aliasValues);
						RoutineUtility.addToConfig(LogSetting.NORMAL, 0, "Created alias \"" + alias + "\"", false);
						for(Material material : aliasValues)
							RoutineUtility.addToConfig(LogSetting.VERBOSE, 0, "Adding " + material.name(), false);
					}
					else RoutineUtility.addToConfig(LogSetting.QUIET, 0, "Failed to create alias " + alias, true);
				}
			}
		}
		return addedSomething;
	}
	//public boolean addAlias(ConfigurationNode node, String targetNodeName, 
	//public <T> List<T> getItems(List<Class<T>> 
	
	public boolean addItemAlias(String key, List<String> values)
	{
		if(itemAliases.containsKey(key)) return false;
		List<Material> matchedItems = new ArrayList<Material>();
		for(String value : values)
		{
			List<Material> matchedTerm = matchItemAlias(value);
			if(!matchedTerm.isEmpty())
				for(Material material : matchedTerm)
					matchedItems.add(material);
			else return false;
		}
		itemAliases.put(key, matchedItems);
		return true;
	}
	
	public static List<Material> matchItemAlias(String key)
	{
		if(itemAliases.containsKey(key.toLowerCase())) return itemAliases.get(key);
		Material material = Material.matchMaterial(key);
		if(material != null) return Arrays.asList(material);
		return new ArrayList<Material>();
	}
}