package com.KoryuObihiro.bukkit.ModDamage;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffectRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.LogicalOperation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;
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
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect.EntityExplode;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect.EntityHeal;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect.EntityHurt;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect.EntitySetAirTicks;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect.EntitySetFireTicks;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect.EntitySetHealth;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect.PlayerSetItem;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect.SlimeSetSize;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.Binomial;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityAirTicksComparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityCoordinateComparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityDrowning;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityExposedToSky;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityFallComparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityFalling;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityFireTicksComparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityHealthComparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityLightComparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityOnBlock;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityOnFire;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityTargetedByOther;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityUnderwater;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventValueComparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerCountComparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerWearing;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerWearingOnly;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerWielding;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.ServerOnlineMode;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.WorldEnvironment;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.WorldTimeRange;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.ArmorSetSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.BiomeSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.EntityTypeSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.EnvironmentSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.PlayerWieldSwitch;
import com.mysql.jdbc.AssertionFailedException;
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
	// -revamp check to show list of loaded elements, use keyword to check specific config
	//  Should be like this:
	//    Damage
	//    Health
	//    Aliases
	//      armor
	//      elements
	//      entities
	//      items
	//      groups
	// -persistence for debug type
	// -count characters in config for message length
	// -get Dispenser attackers
	// -Fishing rod implementation
	// -Autogen world/entitytype switches?
	// -Triggered effects...should be a special type of tag! :D Credit: ricochet1k

	// -find a way to give players ownership of an explosion
	// -Deregister when Bukkit supports!
	// -Client-sided mod for displaying health?
	
	// Ideas
	// -External calls to aliased sets of routines? But...EventInfo would be screwed up. :P
	//--ModDamageElement
	// -Make ModDamageElement do some parsing with Material.name()? (update ArmorSet and CalculationUtility accordingly if this is done)
	
//--Routine utilities
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
	
//Typical plugin stuff...for the most part. :P
	public static Server server;
	private final ModDamageEntityListener entityListener = new ModDamageEntityListener(this);
	public final static Logger log = Logger.getLogger("Minecraft");
	public static LogSetting logSetting = LogSetting.NORMAL;
	public static enum LogSetting
	{ 
		QUIET, NORMAL, VERBOSE;
		public static LogSetting matchSetting(String key)
		{
			for(LogSetting setting : LogSetting.values())
				if(key.equalsIgnoreCase(setting.name()))
						return setting;
				return null;
		}
		private boolean shouldOutput(LogSetting setting)
		{
			if(setting.ordinal() <= this.ordinal())
				return true;
			return false;
		}
	}	
	private static Configuration config;
	private static String errorString_Permissions = ModDamageString(ChatColor.RED) + " You don't have access to that command.";

	protected static int configPages = 0;
	protected static List<String> configStrings_ingame = new ArrayList<String>();
	protected static List<String> configStrings_console = new ArrayList<String>();
	private static boolean hadErrors;
	protected static int additionalConfigChecks = 0;
	
//External-plugin variables
	public static PermissionHandler Permissions = null;
	//private static elRegionsPlugin elRegions = null;
	public static boolean multigroupPermissions = true;	
	public static boolean using_Permissions = false;
	static boolean using_elRegions = false;
	
//General mechanics options
	static boolean negative_Heal;
	
//Predefined pattern strings	
	public static final String numberPart = "(?:[0-9]+)";
	public static final String alphanumericPart = "(?:[a-z0-9]+)";
	public static final String potentialAliasPart = "(?:_?[a-z0-9]+)";
	public static final String statementPart = "((?:" + alphanumericPart + ")(?:\\." + alphanumericPart +")*)";
	public static final String entityPart = "(attacker|target)";
	public static String comparisonRegex;
	public static String biomeRegex;
	public static String environmentRegex;
	public static String elementRegex;
	public static String materialRegex;
	public static String armorRegex;
	public static String logicalRegex;
	
	static
	{
		biomeRegex = "(";
		for(Biome biome : Biome.values())
			biomeRegex += biome.name() + "|";
		biomeRegex += potentialAliasPart + ")";
		
		environmentRegex = "(";
		for(Environment environment : Environment.values())
			environmentRegex += environment.name() + "|";
		environmentRegex = environmentRegex.substring(0, environmentRegex.length() - 2) + ")";

		elementRegex = "(";
		for(ModDamageElement element : ModDamageElement.values())
			elementRegex += element.getReference() + "|";
		//elementRegex += potentialAliasPart + ")"; (TODO: For element aliasing)
		elementRegex = elementRegex.substring(0, elementRegex.length() - 2) + ")";
		
		String[] armorParts = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS" };
		materialRegex = armorRegex = "(";
		String tempRegex = "";
		for(Material material : Material.values())
		{
			materialRegex += material.name() + "|";
			for(String part : armorParts)
				if(material.name().endsWith(part))
					tempRegex += material.name() + "|";			
		}
		tempRegex = tempRegex.substring(0, tempRegex.length() - 2);//TODO CHECK THIS
		//"((?:(?:ARMOR)(?:\\*ARMOR))|aliasPart)"
		armorRegex = "((?:(?:" + tempRegex + ")(?:\\*" + tempRegex + ")))";//|" + potentialAliasPart + ")"; (TODO: For armor aliasing.)
		materialRegex += potentialAliasPart + ")";
		
		logicalRegex = "(";
		for(LogicalOperation operation : LogicalOperation.values())
			logicalRegex += operation.name() + "|";
		logicalRegex += potentialAliasPart + ")";		
		
		comparisonRegex = "(";
		for(ComparisonType type : ComparisonType.values())
			comparisonRegex += type.name() + "|" + type.getShortHand() + "|";
		comparisonRegex += ")\\.";
		
		conditionalPattern = Pattern.compile("(if|if_not)\\s+(?:!)?(" + statementPart + "(?:\\s+" + logicalRegex + "\\s+" + statementPart + ")*)", Pattern.CASE_INSENSITIVE);
		switchPattern = Pattern.compile("switch\\." + statementPart, Pattern.CASE_INSENSITIVE);
		effectPattern = Pattern.compile("(?:attacker|target)?effect\\." + statementPart);
	}

//Routine objects
	private static final List<Routine> damageRoutines = new ArrayList<Routine>();
	private static final List<Routine> spawnRoutines = new ArrayList<Routine>();
	private static final List<Routine> deathRoutines = new ArrayList<Routine>();
	private static final List<Routine> foodRoutines = new ArrayList<Routine>();

//Alias objects
	///public static HashMap<String, List<ArmorSet>> armorAliases = new HashMap<String, List<ArmorSet>>();
	public static HashMap<String, List<Material>> itemAliases = new HashMap<String, List<Material>>();
	public static HashMap<String, List<String>> messageAliases = new HashMap<String, List<String>>();
	//public static HashMap<String, List<String>> groupAliases = new HashMap<String, List<String>>();
	//public static HashMap<String, List<ModDamageElement>> mobAliases = new HashMap<String, List<ModDamageElement>>();

//LoadStates
	private enum LoadState
	{
		NOT_LOADED(ChatColor.BLACK + "NO"), 
		FAILURE(ChatColor.RED + "FAIL"), 
		SUCCESS(ChatColor.GREEN + "YES");
		
		private String string;
		private LoadState(String string){ this.string = string;}
		private String statusString(){ return string;}
	}
	public static LoadState state_damageRoutines = LoadState.NOT_LOADED;
	public static LoadState state_spawnRoutines = LoadState.NOT_LOADED;
	public static LoadState state_deathRoutines = LoadState.NOT_LOADED;
	public static LoadState state_foodRoutines = LoadState.NOT_LOADED;
	public static LoadState state_routines = LoadState.NOT_LOADED;
	
	public static LoadState state_itemAliases = LoadState.NOT_LOADED;
	public static LoadState state_messageAliases = LoadState.NOT_LOADED;
	public static LoadState state_aliases = LoadState.NOT_LOADED;
	
	public static LoadState state_plugin = LoadState.NOT_LOADED;
	public static boolean isEnabled = false;
	
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
		
	//ELREGIONS
		/*TODO
		elRegions = (elRegionsPlugin) this.getServer().getPluginManager().getPlugin("elRegions");
		if (elRegions != null) 
		{
			using_elRegions = true;
		    log.info("[" + getDescription().getName() + "] Found elRegions v" + elRegions.getDescription().getVersion());
		}
		*/
		
	//Event registration
		//register plugin-related stuff with the server's plugin manager
		server.getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Event.Priority.Highest, this);
		server.getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, this);
		//TODO INTEGRATE THESE
		//server.getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Highest, this);
		//server.getPluginManager().registerEvent(Event.Type.ENTITY_REGAIN_HEALTH, entityListener, Event.Priority.Highest, this);
		//server.getPluginManager().registerEvent(Event.Type.ENTITY_TARGET, entityListener, Event.Priority.Highest, this);
		
		//register MD routines
//Base Calculations
		Addition.register(this);
		DiceRoll.register(this);
		DiceRollAddition.register(this);
		Division.register(this);
		DivisionAddition.register(this);
		IntervalRange.register(this);
		LiteralRange.register(this);
		Multiplication.register(this);
		Set.register(this);	
		Message.register(this);
//Nestable Calculations
	//Conditionals
		Binomial.register(this);
		//Entity
		EntityAirTicksComparison.register(this);
		EntityBiome.register(this);
		EntityCoordinateComparison.register(this);
		EntityDrowning.register(this);
		EntityExposedToSky.register(this);
		EntityFallComparison.register(this);
		EntityFalling.register(this);
		EntityFireTicksComparison.register(this);
		EntityHealthComparison.register(this);
		EntityLightComparison.register(this);
		EntityOnBlock.register(this);
		EntityOnFire.register(this);
		EntityTargetedByOther.register(this);
		EntityUnderwater.register(this);
		EventValueComparison.register(this);
		PlayerWearing.register(this);
		PlayerWearingOnly.register(this);
		PlayerWielding.register(this);
		//World
		WorldTimeRange.register(this);
		WorldEnvironment.register(this);
		//Server
		ServerOnlineMode.register(this);
		PlayerCountComparison.register(this);
		//Event
		EventValueComparison.register(this);
	//Effects
		EntityExplode.register(this);
		EntityHeal.register(this);
		EntityHurt.register(this);
		EntitySetAirTicks.register(this);
		EntitySetFireTicks.register(this);
		EntitySetHealth.register(this);
		PlayerSetItem.register(this);
		SlimeSetSize.register(this);
	//Switches
		ArmorSetSwitch.register(this);
		BiomeSwitch.register(this);
		EntityTypeSwitch.register(this);
		EnvironmentSwitch.register(this);
		PlayerWieldSwitch.register(this);
		
		config = this.getConfiguration();
		reload();
		isEnabled = true;
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
								if(args.length == 1) toggleLogging(player);
								else if(args.length == 2)
								{
									LogSetting matchedSetting = LogSetting.matchSetting(args[1]);
									if(matchedSetting != null)
									{
										String sendThis = "Changed debugging from " + logSetting.name().toLowerCase() + " to " + matchedSetting.name().toLowerCase();
										log.info("[" + getDescription().getName() + "] " + sendThis);
										if(!fromConsole) player.sendMessage(ChatColor.GREEN + sendThis);
										logSetting = matchedSetting;
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
								switch(state_plugin)
								{
									case SUCCESS: 
										player.sendMessage(ModDamageString(ChatColor.GREEN) + " Reloaded!");
										break;
									case FAILURE: 
										player.sendMessage(ModDamageString(ChatColor.YELLOW) + " Reloaded with errors.");
										break;
									case NOT_LOADED: 
										player.sendMessage(ModDamageString(ChatColor.BLACK) + " No configuration loaded! Are any routines defined?");
										break;
								}
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
					if(!state_plugin.equals(LoadState.NOT_LOADED))
					{
						if(args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("c"))
						{
							//md check
							if(fromConsole)
							{
								sendConfig(null, 9001);
								log.info("[" + getDescription().getName() + "] Done.");
							}
							else if(args.length == 1)
							{
								if(hasPermission(player, "moddamage.check"))
									sendConfig(player, 0);
								else player.sendMessage(errorString_Permissions);
								return true;
							}
							//md check int
							else if(args.length == 2)
							{
								try
								{
									sendConfig(player, Integer.parseInt(args[1]));
								} 
								catch(NumberFormatException e)
								{
									sendUsage(player, true);
								}
								return true;
							}
							else if(args.length == 3)
							{
								if(args[1].equalsIgnoreCase("alias")) //TODO Polish me.
									for(String alias : itemAliases.keySet())
										if(args[2].equalsIgnoreCase(alias))
										{
											for(Material material : itemAliases.get(alias))
												log.info(material.name());// Don't just make this log.
											break;
										}
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
	//FIXME Gotta be a more dynamic way to do this...but it hardly matters at this level.
	public void executeRoutines_Damage(AttackerEventInfo eventInfo) 
	{
		for(Routine routine : damageRoutines)
			routine.run(eventInfo);
	}

	public void executeRoutines_Death(TargetEventInfo eventInfo)
	{
		for(Routine routine : deathRoutines)
			routine.run(eventInfo);
	}

	public void executeRoutines_Food(TargetEventInfo eventInfo)
	{
		for(Routine routine : foodRoutines)
			routine.run(eventInfo);
	}

	public void executeRoutines_Spawn(TargetEventInfo eventInfo)
	{
		for(Routine routine : spawnRoutines)
			routine.run(eventInfo);
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
	
	protected void clear() 
	{
		damageRoutines.clear();
		spawnRoutines.clear();
		itemAliases.clear();
		state_damageRoutines = state_spawnRoutines = LoadState.NOT_LOADED;
		configStrings_ingame.clear();
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
		clear();
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
					setLogging(logSetting);
					break;
				case NORMAL: 
					log.info("[" + getDescription().getName()+ "] Debugging active.");
					setLogging(logSetting);
					break;
				case VERBOSE: 
					log.info("[" + getDescription().getName()+ "] Verbose debugging active.");
					setLogging(logSetting);
					break;
				default: 
					log.info("[" + getDescription().getName()+ "] Debug string not recognized - defaulting to \"normal\" settings.");
					setLogging(LogSetting.NORMAL);
					break;
			}
		}

	//Item aliasing
		state_aliases = loadAliases();
		if(!state_aliases.equals(LoadState.NOT_LOADED))
		{
			if(logSetting.shouldOutput(LogSetting.VERBOSE)) log.info("Aliases loaded!");
		}
		else log.warning("No aliases loaded! Are any aliases defined?");
		
	//routines
		loadRoutines("Damage", damageRoutines, state_damageRoutines);
		loadRoutines("MobHealth", spawnRoutines, state_spawnRoutines);
		
	//single-property config
		negative_Heal = config.getBoolean("negativeHeal", false);
		if(logSetting.shouldOutput(LogSetting.VERBOSE))
			log.info("[" + getDescription().getName()+ "] Negative-damage healing " + (negative_Heal?"en":"dis") + "abled.");
		
		config.load(); //Discard any changes made to the file by the above reads.
		log.info("[" + getDescription().getName() + "] " + (!state_routines.equals(LoadState.NOT_LOADED)?"Finished loading configuration.":"No configuration defined! Is this on purpose?"));
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

/////////////////// ROUTINE LOADING
	private static HashMap<Pattern, Method> registeredBaseRoutines = new HashMap<Pattern, Method>();
	private static Pattern conditionalPattern;
	private static Pattern effectPattern;
	private static Pattern switchPattern;
	
	protected void loadRoutines(String loadType, List<Routine> routineList, LoadState relevantState)
	{
		List<Object> routineObjects = config.getList(loadType);
		if(routineObjects != null)
		{
			if(logSetting.shouldOutput(LogSetting.VERBOSE)) log.info(loadType + " configuration found, parsing...");
			List<Routine> calculations = parse(routineObjects, loadType, relevantState);
			if(!calculations.isEmpty() && !relevantState.equals(LoadState.FAILURE))
			{
				routineList.addAll(calculations);
				relevantState = LoadState.SUCCESS;
			}
		}
	}
	
//// ALIASING ////
	protected LoadState loadAliases()
	{
		ConfigurationNode aliasNode = config.getNode("Aliases");
		LoadState state = LoadState.NOT_LOADED;
		if(aliasNode != null)
		{
			ConfigurationNode itemNode = aliasNode.getNode("Item");
			if(itemNode != null)
			{
				List<String> aliasKeys = aliasNode.getKeys("Item");
				for(String alias : aliasKeys)
				{
					boolean validAlias = addItemAlias("_" + alias, itemNode.getStringList(alias, new ArrayList<String>()));
					if(validAlias)
					{
						if(!state.equals(LoadState.FAILURE)) state = LoadState.SUCCESS;
						addToConfig(LogSetting.NORMAL, 0, "Created alias \"" + alias + "\"", false);
					}
					else
					{
						state = LoadState.FAILURE;
						addToConfig(LogSetting.QUIET, 0, "Failed to create alias \"" + alias + "\"", true);
					}
				}
			}
		}
		return state;
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
				{
					if(!matchedItems.contains(material))
					{
						addToConfig(LogSetting.VERBOSE, 1, "Adding " + material.name(), false);
						matchedItems.add(material);
					}
					else
					{
						addToConfig(LogSetting.NORMAL, 1, "Error: " + material.name(), true);
					}
				}
			else
			{
				addToConfig(LogSetting.QUIET, 1, "No matching alias or material name \"" + value + "\"", true);
				return false;
			}
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
	
	//Parse commands recursively for different command strings the handlers pass
	//TODO To use null-passing, check before/after nulls to determine whether nothing was there to begin with, use a pointer to the same set of routines. Implement this in 0.9.6
	public static List<Routine> parse(List<Object> routineStrings, String loadType){ return parse(routineStrings, loadType, 0);}
	@SuppressWarnings("unchecked")
	private static List<Routine> parse(Object object, String loadType, int nestCount)
	{
		List<Routine> routines = new ArrayList<Routine>();
		if(object != null)
			{
			if(object instanceof String)
			{
				Routine routine = null;
				for(Pattern pattern : registeredBaseRoutines.keySet())
				{
					Matcher matcher = pattern.matcher((String)object);
					if(matcher.matches())
					{
						try
						{
							routine = (Routine)registeredBaseRoutines.get(pattern).invoke(null, matcher);
							break;
						}
						catch(Exception e){ e.printStackTrace();}
					}
				}
				if(routine != null)
				{
					addToConfig(LogSetting.NORMAL, nestCount, "Routine: \"" + (String)object + "\"", false);
					routines.add(routine);
				}
				else addToConfig(LogSetting.QUIET, nestCount, "Couldn't match base routine string \"" + (String)object +"\"", true);
				
			}
			else if(object instanceof LinkedHashMap)
			{
				HashMap<String, Object> someHashMap = (HashMap<String, Object>)object;//A properly-formatted nested routine is a LinkedHashMap with only one key.
				if(someHashMap.keySet().size() == 1)
					for(String key : someHashMap.keySet())
					{
						Matcher conditionalMatcher = conditionalPattern.matcher(key);
						Matcher switchMatcher = switchPattern.matcher(key);
						Matcher effectMatcher = effectPattern.matcher(key);
						if(conditionalMatcher.matches())
						{
							addToConfig(LogSetting.VERBOSE, nestCount, "", false);
							addToConfig(LogSetting.NORMAL, nestCount, "Conditional: \"" + key + "\"", false);
							ConditionalRoutine routine = ConditionalRoutine.getNew(conditionalMatcher, parse(someHashMap.get(key), loadType, nestCount + 1));
							if(routine != null)
							{
								routines.add(routine);
								addToConfig(LogSetting.VERBOSE, nestCount, "End Conditional \"" + key + "\"\n", false);
							}
							else addToConfig(LogSetting.QUIET, nestCount, "Invalid Conditional \"" + key + "\"", true); //TODO Debug for individual statements, similar to bad switch cases
						}
						else if(effectMatcher.matches())
						{
							addToConfig(LogSetting.VERBOSE, nestCount, "", false);
							addToConfig(LogSetting.NORMAL, nestCount, "CalculatedEffect: \"" + key + "\"", false);
							CalculatedEffectRoutine<?> routine = CalculatedEffectRoutine.getNew(effectMatcher, parse(someHashMap.get(key), loadType, nestCount + 1));
							if(routine != null)
							{
								routines.add(routine);
								addToConfig(LogSetting.VERBOSE, nestCount, "End CalculatedEffect \"" + key + "\"\n", false);
							}
							else addToConfig(LogSetting.QUIET, nestCount, "Error: invalid CalculatedEffect \"" + key + "\"", true);
						}
						else if(switchMatcher.matches())
						{					
							LinkedHashMap<String, Object> anotherHashMap = (someHashMap.get(key) instanceof LinkedHashMap?(LinkedHashMap<String, Object>)someHashMap.get(key):null);
							if(anotherHashMap != null)
							{
								addToConfig(LogSetting.VERBOSE, nestCount, "", false);
								addToConfig(LogSetting.NORMAL, nestCount, "Switch: \"" + key + "\"", false);
								LinkedHashMap<String, List<Routine>> routineHashMap = new LinkedHashMap<String, List<Routine>>();
								SwitchRoutine<?> routine = null;
								for(String anotherKey : anotherHashMap.keySet())
								{
									addToConfig(LogSetting.VERBOSE, nestCount, "", false);
									addToConfig(LogSetting.NORMAL, nestCount, " case: \"" + anotherKey + "\"", false);
									routineHashMap.put(anotherKey, parse(anotherHashMap.get(anotherKey), loadType, nestCount + 1));
									addToConfig(LogSetting.VERBOSE, nestCount, "End case \"" + anotherKey + "\"\n", false);
								}
								routine = SwitchRoutine.getNew(switchMatcher, routineHashMap);
								if(routine != null)
								{
									if(routine.isLoaded) routines.add(routine);
									else addToConfig(LogSetting.QUIET, nestCount, "Error: invalid case \"" + routine.failedCase + "\"", true);
								}
								else addToConfig(LogSetting.QUIET, nestCount, "Error: invalid Switch \"" + key + "\"", true);
							}
							addToConfig(LogSetting.VERBOSE, nestCount, "End Switch \"" + key + "\"", false);
						}
						else addToConfig(LogSetting.QUIET, nestCount, " No match found for nested node \"" + key + "\"", true);
					}
				else addToConfig(LogSetting.QUIET, nestCount, "Parse error: bad nested routine.", true);
			}
			else if(object instanceof List)
			{
				for(Object nestedObject : (List<Object>)object)
					routines.addAll(parse(nestedObject, loadType, nestCount));
			}
			else addToConfig(LogSetting.QUIET, nestCount, "Parse error: object " + object.toString() + " of type " + object.getClass().getName(), true);
		}
		else addToConfig(LogSetting.QUIET, nestCount, "Parse error: null", true);
		return routines;
	}
	
	public void registerBase(Class<? extends Routine> routineClass, Pattern syntax)
	{
		try
		{
			Method method = routineClass.getMethod("getNew", Matcher.class);
			if(method != null)
			{
				assert(method.getReturnType().equals(routineClass));
				method.invoke(null, (Matcher)null);
				register(registeredBaseRoutines, method, syntax);
			}
			else log.severe("Method getNew not found for statement " + routineClass.getName());
		}
		catch(AssertionFailedException e){ log.severe("[ModDamage] Error: getNew doesn't return class " + routineClass.getName() + "!");}
		catch(SecurityException e){ log.severe("[ModDamage] Error: getNew isn't public for class " + routineClass.getName() + "!");}
		catch(NullPointerException e){ log.severe("[ModDamage] Error: getNew for class " + routineClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have matching method getNew(Matcher)!");} 
		catch (IllegalAccessException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have valid getNew() method!");}
	}

	public static void registerConditional(Class<? extends ConditionalStatement> statementClass, Pattern syntax)
	{
		try
		{
			Method method = statementClass.getMethod("getNew", Matcher.class);
			if(method != null)
			{
				assert(method.getReturnType().equals(statementClass));
				method.invoke(null, (Matcher)null);
				register(ConditionalRoutine.registeredStatements, method, syntax);
			}
			else log.severe("Method getNew not found for statement " + statementClass.getName());
		}
		catch(AssertionFailedException e){ log.severe("[ModDamage] Error: getNew doesn't return class " + statementClass.getName() + "!");}
		catch(SecurityException e){ log.severe("[ModDamage] Error: getNew isn't public for class " + statementClass.getName() + "!");}
		catch(NullPointerException e){ log.severe("[ModDamage] Error: getNew for class " + statementClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have matching method getNew(Matcher)!");} 
		catch (IllegalAccessException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
	}

	public static void registerSwitch(Class<? extends SwitchRoutine<?>> statementClass, Pattern syntax)
	{
		try
		{
			Method method = statementClass.getMethod("getNew", Matcher.class, LinkedHashMap.class);
			if(method != null)
			{
				assert(method.getReturnType().equals(statementClass));
				method.invoke(null, (Matcher)null, (LinkedHashMap<String, List<Routine>>)null);
				register(SwitchRoutine.registeredStatements, method, syntax);
			}
			else log.severe("Method getNew not found for statement " + statementClass.getName());
		}
		catch(AssertionFailedException e){ log.severe("[ModDamage] Error: getNew doesn't return class " + statementClass.getName() + "!");}
		catch(SecurityException e){ log.severe("[ModDamage] Error: getNew isn't public for class " + statementClass.getName() + "!");}
		catch(NullPointerException e){ log.severe("[ModDamage] Error: getNew for class " + statementClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have matching method getNew(Matcher, LinkedHashMap)!");} 
		catch (IllegalAccessException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
	}
	
	public static void registerEffect(Class<? extends CalculatedEffectRoutine<?>> routineClass, Pattern syntax)
	{
		try
		{
			Method method = routineClass.getMethod("getNew", Matcher.class, List.class);
			if(method != null)//XXX Is this necessary?
			{
				assert(method.getReturnType().equals(routineClass));
				method.invoke(null, (Matcher)null);
				register(CalculatedEffectRoutine.registeredStatements, method, syntax);
			}
			else log.severe("Method getNew not found for statement " + routineClass.getName());
		}
		catch(AssertionFailedException e){ log.severe("[ModDamage] Error: getNew doesn't return class " + routineClass.getName() + "!");}
		catch(SecurityException e){ log.severe("[ModDamage] Error: getNew isn't public for class " + routineClass.getName() + "!");}
		catch(NullPointerException e){ log.severe("[ModDamage] Error: getNew for class " + routineClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have matching method getNew(Matcher)!");} 
		catch (IllegalAccessException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have valid getNew() method!");} 
	}
	
	public static void register(HashMap<Pattern, Method> registry, Method method, Pattern syntax)
	{
		boolean successfullyRegistered = false;
		if(syntax != null)
		{
			registry.put(syntax, method);	
			successfullyRegistered = true;
		}
		else log.severe("[ModDamage] Error: Bad regex for registering class \"" + method.getClass().getName() + "\"!");
		if(successfullyRegistered)
		{
			if(logSetting.shouldOutput(LogSetting.VERBOSE)) log.info("[ModDamage] Registering class " + method.getClass().getName() + " with pattern " + syntax.pattern());
		}
	}
		
//// LOGGING ////
	public static void setLogging(LogSetting setting)
	{ 
		if(setting != null) 
			logSetting = setting;
		else log.severe("[ModDamage] Error: bad debug setting. Valid settings: normal, quiet, verbose");
	}
	
	public static void toggleLogging(Player player) 
	{
		LogSetting nextSetting = null; //shouldn't stay like this.
		switch(logSetting)
		{
			case QUIET: 
				nextSetting = LogSetting.NORMAL;
				break;
				
			case NORMAL:
				nextSetting = LogSetting.VERBOSE;
				break;
				
			case VERBOSE:
				nextSetting = LogSetting.QUIET;
				break;
		}
		String sendThis = "Changed debug from " + logSetting.name().toLowerCase() + " to " + nextSetting.name().toLowerCase();
		log.info("[ModDamage] " + sendThis);
		if(player != null) player.sendMessage(ChatColor.GREEN + sendThis);
		logSetting = nextSetting;
	}
	
	public static void addToConfig(LogSetting outputSetting, int nestCount, String string, boolean severe)
	{
		if(severe) hadErrors = true;
		if(logSetting.shouldOutput(outputSetting))
		{
			configStrings_ingame.add(nestCount + "] " + (severe?ChatColor.RED:ChatColor.AQUA) + string);
			if(severe) log.severe(string);
			else 
			{
				String nestIndentation = "";
				for(int i = 0; i < nestCount; i++)
					nestIndentation += "    ";
				configStrings_console.add(nestIndentation + string);
				log.info(nestIndentation + string);
			}
		}
		configPages = configStrings_ingame.size()/9 + (configStrings_ingame.size()%9 > 0?1:0);
	}

	public static boolean sendConfig(Player player, int pageNumber)
	{
		if(player == null)
		{
			String printString = "[ModDamage] Complete configuration for this server:";
			for(String configString : configStrings_console)
				printString += "\n" + configString;
			log.info(printString);
			return true;
		}
		else if(pageNumber > 0)
		{
			if(pageNumber <= configPages)
			{
				player.sendMessage(ModDamage.ModDamageString(ChatColor.GOLD) + " Configuration: (" + pageNumber + "/" + (configPages + additionalConfigChecks) + ")");
				for(int i = (9 * (pageNumber - 1)); i < (configStrings_ingame.size() < (9 * pageNumber)?configStrings_ingame.size():(9 * pageNumber)); i++)
					player.sendMessage(ChatColor.DARK_AQUA + configStrings_ingame.get(i));
				return true;
			}
		}
		else
		{
			player.sendMessage(ModDamage.ModDamageString(ChatColor.GOLD) + " Configuration: Overview (Pages: " + configPages + ")");
			player.sendMessage(ChatColor.DARK_AQUA + "Aliases:" + "\t\t" + "Routines:");
			player.sendMessage(ChatColor.DARK_AQUA + "Item aliases:" + state_itemAliases.statusString() + "     " + ChatColor.DARK_AQUA + "Damage Routines:" + state_damageRoutines.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "Message aliases:" + state_messageAliases.statusString() + "    " + ChatColor.DARK_AQUA + "Spawn Routines:" + state_spawnRoutines.statusString());
			//player.sendMessage(ChatColor.DARK_AQUA + "Armor aliases:" + activationString(armorAliasesLoaded) + "\t" + ChatColor.DARK_AQUA + "Food Routines:" + activationString(loaded_damageRoutines));
			player.sendMessage(hadErrors?ChatColor.DARK_RED + "There were one or more read errors in config.":ChatColor.GREEN + "No errors loading configuration!");	
		}
		//TODO: Else for configured aliases/routine types.
		//TODO: Yellow for the failed, but active, loads. :D
		return false;
	}
	
//// INGAME MATCHING ////	
	//Frankly, most of the stuff below should be considered for implementation into Bukkit. :<
	public static Biome matchBiome(String biomeName)
	{
		for(Biome biome : Biome.values())
			if(biomeName.equalsIgnoreCase(biome.name()))
				return biome;
		return null;
	}
	
	public static Environment matchEnvironment(String environmentName)
	{
		for(Environment environment : Environment.values())
			if(environmentName.equalsIgnoreCase(environment.name()))
				return environment;
		return null;
	}
}