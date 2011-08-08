package com.KoryuObihiro.bukkit.ModDamage;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.RangedElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.ArmorAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.BiomeAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.ElementAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.GroupAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.ItemAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.MessageAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.WorldAliaser;
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
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityTypeEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityUnderwater;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventValueComparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerCountComparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerGroup;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerWearing;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerWearingOnly;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerWielding;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.RangedElementEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.ServerOnlineMode;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.WorldEnvironment;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.WorldTimeComparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.ArmorSetSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.BiomeSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.EntityTypeSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.EnvironmentSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.PlayerGroupSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.PlayerWieldSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.RangedElementSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.WorldSwitch;
import com.elbukkit.api.elregions.elRegionsPlugin;
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
	// 0.9.5
	//FIXME World conditional
	//TODO Empty armorSet & material
	// -Command for autogen world/entitytype switches?
	//TODO switch and comparison for wieldquantity
	//TODO switch.conditional
	//TODO switch and conditional for region
	// -if.server.onlineenabled
	// -getAverageLight (area)
	// -check against an itemstack in the player's inventory
	// FIXME Why aren't the patternParts all final? o_o
	// TODO message routines (force aliasing here), end goal is to make this possible:
	/*
	if(eventInfo.shouldScan)
	{
		int displayHealth = (eventInfo.entity_target).getHealth() - ((!(eventInfo.eventDamage < 0 && ModDamage.negative_Heal))?eventInfo.eventDamage:0);
		((Player)eventInfo.entity_attacker).sendMessage(ChatColor.DARK_PURPLE + eventInfo.element_target.getReference() 
				+ "(" + (eventInfo.name_target != null?eventInfo.name_target:("id " + eventInfo.entity_target.getEntityId()))
				+ "): " + Integer.toString((displayHealth < 0)?0:displayHealth));
	}
	*/
	// -Triggered effects...should be a special type of tag! :D Credit: ricochet1k
	// -AoE clearance, block search nearby for Material?
	
	// Ideas
	// -if.entityis.inRegion
	// -if.server.port.#port
	// -switch.spawnreason

	// -for.eventvalue iterations?
	// -foreach (probably want dynamic tags here)
	//    -region
	//    -item in inventory?
	//    -item in hand?
	//    -item by slot?
	//    -health tick?
	
	//--Yet-to-be-plausible:
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
	// -find a way to give players ownership of an explosion
	// -Deregister when Bukkit supports!
	// -Client-sided mod for displaying health?
	
	// Ideas
	// -External calls to aliased sets of routines? But...EventInfo would be screwed up. :P
	//--ModDamageElement
	// -Make ModDamageElement do some parsing with Material.name()? (update ArmorSet and CalculationUtility accordingly if this is done)

	
//Typical plugin stuff...for the most part. :P
	public static Server server;
	private final ModDamageEntityListener entityListener = new ModDamageEntityListener(this);
	public final static Logger log = Logger.getLogger("Minecraft");
	public static DebugSetting debugSetting = DebugSetting.NORMAL;
	public static enum DebugSetting
	{
		QUIET, NORMAL, CONSOLE, VERBOSE;
		public static DebugSetting matchSetting(String key)
		{
			for(DebugSetting setting : DebugSetting.values())
				if(key.equalsIgnoreCase(setting.name()))
						return setting;
				return null;
		}
		private boolean shouldOutput(DebugSetting setting)
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
	protected static int additionalConfigChecks = 0;
	
//External-plugin variables
	public static PermissionHandler Permissions = null;
	private static elRegionsPlugin elRegions = null;
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
	public static final String entityRegex = "(attacker|target)";
	public static String comparisonRegex;
	public static String biomeRegex;
	public static String environmentRegex;
	public static String elementRegex;
	public static String materialRegex;
	public static String armorRegex;
	public static String rangedElementRegex;
	public static String logicalRegex;
	private static Pattern conditionalPattern;
	private static Pattern effectPattern;
	private static Pattern switchPattern;
	
	private static HashMap<Pattern, Method> registeredBaseRoutines = new HashMap<Pattern, Method>();
	
	static
	{
		biomeRegex = "(";
		for(Biome biome : Biome.values())
			biomeRegex += biome.name() + "|";
		biomeRegex += potentialAliasPart + ")";
		
		environmentRegex = "(";
		for(Environment environment : Environment.values())
			environmentRegex += environment.name() + "|";
		environmentRegex = environmentRegex.substring(0, environmentRegex.length() - 1) + ")";

		elementRegex = "(";
		for(ModDamageElement element : ModDamageElement.values())
			elementRegex += element.getReference() + "|";
		elementRegex += potentialAliasPart + ")";
		
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
		tempRegex = tempRegex.substring(0, tempRegex.length() - 1);
		//"((?:(?:ARMOR)(?:\\*ARMOR))|aliasPart)"
		armorRegex = "((?:(?:" + tempRegex + ")(?:\\*" + tempRegex + "){0,3})|" + potentialAliasPart + ")";
		materialRegex += potentialAliasPart + ")";
		
		logicalRegex = "(";
		for(LogicalOperation operation : LogicalOperation.values())
			logicalRegex += operation.name() + "|";
		logicalRegex += potentialAliasPart + ")";		
		
		comparisonRegex = "(";
		for(ComparisonType type : ComparisonType.values())
			comparisonRegex += type.name() + "|";
		comparisonRegex = comparisonRegex.substring(0, comparisonRegex.length() - 1) + ")";
		
		rangedElementRegex = "(";
		for(RangedElement type : RangedElement.values())
			rangedElementRegex += type.name() + "|";
		rangedElementRegex += ")";
		
		conditionalPattern = Pattern.compile("(if|if_not)\\s+(?:!)?(" + statementPart + "(?:\\s+" + logicalRegex + "\\s+" + statementPart + ")*)", Pattern.CASE_INSENSITIVE);
		switchPattern = Pattern.compile("switch\\." + statementPart, Pattern.CASE_INSENSITIVE);
		effectPattern = Pattern.compile("((?:attacker|target)?effect\\." + statementPart + ")");
	}

//LoadStates
	public enum LoadState
	{
		NOT_LOADED(ChatColor.GRAY + "NO  "), 
		FAILURE(ChatColor.RED + "FAIL"), 
		SUCCESS(ChatColor.GREEN + "YES ");
		
		private String string;
		private LoadState(String string){ this.string = string;}
		private String statusString(){ return string;}
		private static LoadState combineStates(LoadState...loadStates)
		{
			LoadState returnState = LoadState.NOT_LOADED;
			for(LoadState state : loadStates)
			{
				if(state.equals(LoadState.FAILURE))
					return LoadState.FAILURE;
				else if(state.equals(LoadState.SUCCESS))
					returnState = SUCCESS;
			}
			return returnState;
		}
	}

//Routine objects
	static final List<Routine> damageRoutines = new ArrayList<Routine>();
	static final List<Routine> spawnRoutines = new ArrayList<Routine>();
	static final List<Routine> deathRoutines = new ArrayList<Routine>();
	static final List<Routine> foodRoutines = new ArrayList<Routine>();
	private static LoadState state_damageRoutines = LoadState.NOT_LOADED;
	private static LoadState state_spawnRoutines = LoadState.NOT_LOADED;
	private static LoadState state_deathRoutines = LoadState.NOT_LOADED;
	private static LoadState state_foodRoutines = LoadState.NOT_LOADED;
	private static LoadState state_routines = LoadState.NOT_LOADED;
	
//Alias objects
	private static ArmorAliaser armorAliaser = new ArmorAliaser();
	private static BiomeAliaser biomeAliaser = new BiomeAliaser();
	private static ElementAliaser elementAliaser = new ElementAliaser();
	private static GroupAliaser groupAliaser = new GroupAliaser();
	private static ItemAliaser itemAliaser = new ItemAliaser();
	private static MessageAliaser messageAliaser = new MessageAliaser();
	private static WorldAliaser worldAliaser = new WorldAliaser();
	private static LoadState state_armorAliases = LoadState.NOT_LOADED;
	private static LoadState state_biomeAliases = LoadState.NOT_LOADED;
	private static LoadState state_elementAliases = LoadState.NOT_LOADED;
	private static LoadState state_itemAliases = LoadState.NOT_LOADED;
	private static LoadState state_groupAliases = LoadState.NOT_LOADED;
	private static LoadState state_messageAliases = LoadState.NOT_LOADED;
	private static LoadState state_worldAliases = LoadState.NOT_LOADED;
	private static LoadState state_aliases = LoadState.NOT_LOADED;
	
	private static LoadState state_plugin = LoadState.NOT_LOADED;
	public static boolean isEnabled = false;
	
////////////////////////// INITIALIZATION
	@Override
	public void onEnable() 
	{
		//XXX REMOVE UPON 0.9.5 RELEASE
		log.warning("WARNING: This is an experimental build of ModDamage 0.9.5. Do not use this JAR if you value server stability or are not a tester.");
		//END REMOVE
		
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
		elRegions = (elRegionsPlugin) this.getServer().getPluginManager().getPlugin("elRegions");
		if (elRegions != null) 
		{
			using_elRegions = true;
		    log.info("[" + getDescription().getName() + "] Found elRegions v" + elRegions.getDescription().getVersion());
		}
		
	//Event registration
		//register plugin-related stuff with the server's plugin manager
		server.getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Event.Priority.Highest, this);
		server.getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, this);
		server.getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Highest, this);
		server.getPluginManager().registerEvent(Event.Type.ENTITY_REGAIN_HEALTH, entityListener, Event.Priority.Highest, this);
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
		RangedElementEvaluation.register(this);
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
		EntityTypeEvaluation.register(this);
		EntityUnderwater.register(this);
		EventValueComparison.register(this);
		PlayerGroup.register(this);
		PlayerWearing.register(this);
		PlayerWearingOnly.register(this);
		PlayerWielding.register(this);
		//World
		WorldTimeComparison.register(this);
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
		PlayerGroupSwitch.register(this);
		PlayerWieldSwitch.register(this);
		RangedElementSwitch.register(this);
		WorldSwitch.register(this);
		
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
					sendCommandUsage(player, false);
					return true;
				}
				else if(args.length >= 0)
				{
					if(args[0].equalsIgnoreCase("debug") || args[0].equalsIgnoreCase("d"))
						{
							if(fromConsole || hasPermission(player, "moddamage.debug"))
							{
								if(args.length == 1) toggleDebugging(player);
								else if(args.length == 2)
								{
									DebugSetting matchedSetting = DebugSetting.matchSetting(args[1]);
									if(matchedSetting != null)
										setDebugging(player, matchedSetting);
									else
									{
										sendCommandUsage(player, true);
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
										player.sendMessage(ModDamageString(ChatColor.GRAY) + " No configuration loaded! Are any routines defined?");
										break;
								}
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
					if( isEnabled)
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
									sendCommandUsage(player, true);
								}
								return true;
							}
						}
						else
						{
							sendCommandUsage(player, true);
							return true;
						}
					}
					else if(player == null)
						log.info("[" + getDescription().getName() + "] ModDamage must be enabled to use that command.");
					else player.sendMessage(ModDamageString(ChatColor.RED) + " ModDamage must be enabled to use that command.");
					return true;
				}
			}
		sendCommandUsage(player, true);
		return true;
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
		itemAliaser.clear();
		
		state_routines = state_damageRoutines = state_spawnRoutines = state_aliases = state_itemAliases = state_messageAliases = LoadState.NOT_LOADED; //TODO UPDATE
		configStrings_ingame.clear();
		configStrings_console.clear();
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

	private void sendCommandUsage(Player player, boolean forError) 
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
			DebugSetting debugSetting = DebugSetting.matchSetting(debugString);
			switch(debugSetting)
			{
				case QUIET: 
					log.info("[" + getDescription().getName()+ "] \"Quiet\" mode active - suppressing debug messages and warnings.");
					break;
				case NORMAL: 
					log.info("[" + getDescription().getName()+ "] Debugging active.");
					break;
				case VERBOSE: 
					log.info("[" + getDescription().getName()+ "] Verbose debugging active.");
					break;
				default: 
					log.info("[" + getDescription().getName()+ "] Debug string not recognized - defaulting to \"normal\" settings.");
					debugSetting = DebugSetting.NORMAL;
					break;
			}
			ModDamage.debugSetting = debugSetting;
		}

	//Item aliasing
		state_armorAliases = loadAliases("Aliases.Armor", armorAliaser);
		state_biomeAliases = loadAliases("Aliases.Biome", biomeAliaser);
		state_elementAliases = loadAliases("Aliases.Element", elementAliaser);
		state_itemAliases = loadAliases("Aliases.Item", itemAliaser);
		state_groupAliases = loadAliases("Aliases.Group", groupAliaser);
		state_messageAliases = loadAliases("Aliases.Message", messageAliaser);
		state_worldAliases = loadAliases("Aliases.World", worldAliaser);
		state_aliases = LoadState.combineStates(state_armorAliases, state_elementAliases, state_groupAliases, state_itemAliases, state_messageAliases, state_worldAliases);
		if(!state_aliases.equals(LoadState.NOT_LOADED))
			addToConfig(state_aliases.equals(LoadState.SUCCESS)?DebugSetting.VERBOSE:DebugSetting.QUIET, 0, state_aliases.equals(LoadState.SUCCESS)?"Aliases loaded!":"One or more errors occured while loading aliases.", state_aliases);
		else addToConfig(DebugSetting.VERBOSE,  0, "No aliases loaded! Are any aliases defined?", LoadState.NOT_LOADED);
		
	//routines
		state_damageRoutines = loadRoutines("Damage", damageRoutines);
		state_spawnRoutines = loadRoutines("MobHealth", spawnRoutines);
		state_foodRoutines = loadRoutines("Food", foodRoutines);
		state_routines = LoadState.combineStates(state_damageRoutines, state_foodRoutines, state_spawnRoutines);

		state_plugin = LoadState.combineStates(state_aliases, state_routines);
		
	//single-property config
		negative_Heal = config.getBoolean("negativeHeal", false);
		if(debugSetting.shouldOutput(negative_Heal?DebugSetting.NORMAL:DebugSetting.VERBOSE))
			log.info("[" + getDescription().getName()+ "] Negative-damage healing " + (negative_Heal?"en":"dis") + "abled.");
		
		config.load(); //Discard any changes made to the file by the above reads.
		
		String sendThis = null;
		switch(state_plugin)
		{
			case NOT_LOADED:
				sendThis = "No configuration loaded.";
				break;
			case FAILURE:
				sendThis = "Loaded configuration with one or more errors.";
				break;
			case SUCCESS:
				sendThis = "Finished loading configuration.";
				break;
		}
		log.info("[" + getDescription().getName() + "] " + sendThis);
		
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
	
	private LoadState loadRoutines(String loadType, List<Routine> routineList)
	{
		LoadState relevantState = LoadState.NOT_LOADED;
		List<Object> routineObjects = config.getList(loadType);
		if(routineObjects != null)
		{
			relevantState = LoadState.SUCCESS;
			addToConfig(DebugSetting.VERBOSE, 0, loadType + " configuration found, parsing...", LoadState.SUCCESS);
			LoadState[] stateMachine = {relevantState};//We use a single-cell array here because the enum is ASSIGNED later - this doesn't work if we want to operate by reference.
			List<Routine> calculations = parse(routineObjects, loadType, stateMachine);
			relevantState = stateMachine[0];
			
			if(!calculations.isEmpty() && !relevantState.equals(LoadState.FAILURE))
			{
				routineList.addAll(calculations);
				relevantState = LoadState.SUCCESS;
			}
		}
		return relevantState;
	}
	
	private LoadState loadAliases(String loadType, Aliaser<?> aliaser)
	{
		LoadState relevantState = LoadState.NOT_LOADED;
		List<String> aliases = config.getKeys(loadType);
		if(aliases != null)
		{
			relevantState = LoadState.SUCCESS;
			addToConfig(DebugSetting.VERBOSE, 0, aliaser.getName() + " aliases found, parsing...", LoadState.SUCCESS);
			for(String alias : aliases)
			{
				List<String> values = config.getStringList(loadType + "." + alias, new ArrayList<String>());
				if(values.isEmpty())
					addToConfig(DebugSetting.VERBOSE, 0, "Found empty " + loadType.toLowerCase() + " alias \"" + alias + "\", ignoring...", LoadState.NOT_LOADED);
				else if(!aliaser.addAlias(alias, values))
					relevantState = LoadState.FAILURE;
			}
		}
		return relevantState;
	}
	
//// ROUTINE PARSING ////
	//Parse commands recursively for different command strings the handlers pass
	//TODO To use null-passing, check before/after nulls to determine whether nothing was there to begin with, use a pointer to the same set of routines. Implement this in 0.9.6
	public static List<Routine> parse(List<Object> routineStrings, String loadType, LoadState[] currentState){ return parse(routineStrings, loadType, 0, currentState);}
	@SuppressWarnings("unchecked")
	private static List<Routine> parse(Object object, String loadType, int nestCount, LoadState[] resultingState)
	{
		LoadState currentState = LoadState.SUCCESS;
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
				if(routine != null) routines.add(routine);
				currentState = (routine != null)?currentState:LoadState.FAILURE;
				addToConfig((routine != null)?DebugSetting.NORMAL:DebugSetting.QUIET, nestCount, (routine != null?"Routine:":"Couldn't match base routine string") + " \"" + (String)object + "\"", currentState);
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
							addToConfig(DebugSetting.CONSOLE, nestCount, "", LoadState.SUCCESS);
							addToConfig(DebugSetting.NORMAL, nestCount, "Conditional: \"" + key + "\"", LoadState.SUCCESS);
							ConditionalRoutine routine = ConditionalRoutine.getNew(conditionalMatcher, parse(someHashMap.get(key), loadType, nestCount + 1, resultingState));
							if(routine != null)
							{
								routines.add(routine);
								addToConfig(DebugSetting.VERBOSE, nestCount, "End Conditional \"" + key + "\"\n", currentState);
							}
							else
							{
								currentState = LoadState.FAILURE;
								addToConfig(DebugSetting.QUIET, 0, "Invalid Conditional"+ " \"" + key + "\"", currentState);
							}
						}
						else if(effectMatcher.matches())
						{
							addToConfig(DebugSetting.CONSOLE, nestCount, "", LoadState.SUCCESS);
							addToConfig(DebugSetting.NORMAL, nestCount, "CalculatedEffect: \"" + key + "\"", LoadState.SUCCESS);
							CalculatedEffectRoutine<?> routine = CalculatedEffectRoutine.getNew(effectMatcher, parse(someHashMap.get(key), loadType, nestCount + 1, resultingState));
							if(routine != null)
							{
								routines.add(routine);
								addToConfig(DebugSetting.VERBOSE, nestCount, "End CalculatedEffect \"" + key + "\"\n", currentState);
							}
							else
							{
								currentState = LoadState.FAILURE;
								addToConfig(DebugSetting.QUIET, 0, "Invalid CalculatedEffect \"" + key + "\"", currentState);
							}
						}
						else if(switchMatcher.matches())
						{					
							LinkedHashMap<String, Object> anotherHashMap = (someHashMap.get(key) instanceof LinkedHashMap?(LinkedHashMap<String, Object>)someHashMap.get(key):null);
							if(anotherHashMap != null)
							{
								addToConfig(DebugSetting.CONSOLE, nestCount, "", LoadState.SUCCESS);
								addToConfig(DebugSetting.NORMAL, nestCount, "Switch: \"" + key + "\"", LoadState.SUCCESS);
								LinkedHashMap<String, List<Routine>> routineHashMap = new LinkedHashMap<String, List<Routine>>();
								SwitchRoutine<?> routine = null;
								for(String anotherKey : anotherHashMap.keySet())
								{
									addToConfig(DebugSetting.CONSOLE, nestCount, "", LoadState.SUCCESS);
									addToConfig(DebugSetting.NORMAL, nestCount, " case: \"" + anotherKey + "\"", LoadState.SUCCESS);
									routineHashMap.put(anotherKey, parse(anotherHashMap.get(anotherKey), loadType, nestCount + 1, resultingState));
									addToConfig(DebugSetting.VERBOSE, nestCount, "End case \"" + anotherKey + "\"\n", LoadState.SUCCESS);
								}
								routine = SwitchRoutine.getNew(switchMatcher, routineHashMap);
								if(routine != null)
								{
									if(routine.isLoaded) routines.add(routine);
									else 
									{
										currentState = LoadState.FAILURE;
										for(String caseName : routine.failedCases)
											addToConfig(DebugSetting.QUIET, 0, "Error: invalid case \"" + caseName + "\"", currentState);
									}
									addToConfig(DebugSetting.VERBOSE, nestCount, "End Switch \"" + key + "\"", LoadState.SUCCESS);
								}
								else
								{
									currentState = LoadState.FAILURE;
									addToConfig(DebugSetting.QUIET, 0, "Error: invalid Switch \"" + key + "\"", currentState);
								}
							}
						}
						else 
						{
							currentState = LoadState.FAILURE;
							addToConfig(DebugSetting.QUIET, 0, " No match found for nested node \"" + key + "\"", currentState);							
						}
					}
				else
				{
					currentState = LoadState.FAILURE;
					addToConfig(DebugSetting.QUIET, nestCount, "Parse error: bad nested routine.", currentState);				
				} 
			}
			else if(object instanceof List)
			{
				for(Object nestedObject : (List<Object>)object)
					routines.addAll(parse(nestedObject, loadType, nestCount, resultingState));
			}
			else
			{
				currentState = LoadState.FAILURE;
				addToConfig(DebugSetting.QUIET, nestCount, "Parse error: object " + object.toString() + " of type " + object.getClass().getName(), currentState);
			}
		}
		else 
		{
			currentState = LoadState.FAILURE;
			addToConfig(DebugSetting.QUIET, nestCount, "Parse error: null", currentState);
		}
		if(currentState.equals(LoadState.FAILURE))
			resultingState[0] = LoadState.FAILURE;
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
				method.invoke(null, (Matcher)null, (List<Routine>)null);
				register(CalculatedEffectRoutine.registeredStatements, method, syntax);
			}
			else log.severe("Method getNew not found for statement " + routineClass.getName());
		}
		catch(AssertionFailedException e){ log.severe("[ModDamage] Error: getNew doesn't return class " + routineClass.getName() + "!");}
		catch(SecurityException e){ log.severe("[ModDamage] Error: getNew isn't public for class " + routineClass.getName() + "!");}
		catch(NullPointerException e){ log.severe("[ModDamage] Error: getNew for class " + routineClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have matching method getNew(Matcher, List)!");} 
		catch (IllegalAccessException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have valid getNew() method!");} 
	}
	
	private static void register(HashMap<Pattern, Method> registry, Method method, Pattern syntax)
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
			if(debugSetting.shouldOutput(DebugSetting.VERBOSE)) log.info("[ModDamage] Registering class " + method.getClass().getName() + " with pattern " + syntax.pattern());
		}
	}
		
//// LOGGING ////
	private static void setDebugging(Player player, DebugSetting setting)
	{ 
		if(setting != null) 
		{
			if(!debugSetting.equals(setting))
			{
				String sendThis = "Changed debug from " + debugSetting.name().toLowerCase() + " to " + setting.name().toLowerCase();
				log.info("[ModDamage] " + sendThis);
				if(player != null) player.sendMessage(ModDamageString(ChatColor.GREEN) + " " + sendThis);
				debugSetting = setting;
				config.setProperty("debugging", debugSetting.name().toLowerCase());
				config.save();
			}
			else
			{
				log.info("[ModDamage] Debug already set to " + setting.name().toLowerCase() + "!");
				if(player != null) player.sendMessage(ModDamageString(ChatColor.GREEN) + " Debug already set to " + setting.name().toLowerCase() + "!");
			}
		}
		else log.severe("[ModDamage] Error: bad debug setting. Valid settings: normal, quiet, verbose");//shouldn't happen
	}
	
	private static void toggleDebugging(Player player) 
	{
		switch(debugSetting)
		{
			case QUIET: 
				setDebugging(player, DebugSetting.NORMAL);
				break;
				
			case NORMAL:
				setDebugging(player, DebugSetting.VERBOSE);
				break;
				
			case VERBOSE:
				setDebugging(player, DebugSetting.QUIET);
				break;
		}
	}
	
	public static void addToConfig(DebugSetting outputSetting, int nestCount, String string, LoadState loadState)
	{
		if(loadState.equals(LoadState.FAILURE)) state_plugin = LoadState.FAILURE;
		if(debugSetting.shouldOutput(outputSetting))
		{
			ChatColor color = null;
			switch(loadState)
			{
				case NOT_LOADED:
					color = ChatColor.GRAY;
					break;
				case FAILURE:
					color = ChatColor.RED;
					break;
				case SUCCESS:
					color = ChatColor.AQUA;
					break;
			}
			if(!outputSetting.equals(DebugSetting.CONSOLE))
			{
				if(string.length() > 50)
				{
					String ingameString = string;
					configStrings_ingame.add(nestCount + "] " + color + ingameString.substring(0, 49));
					ingameString = ingameString.substring(49);
					while(ingameString.length() > 50)
					{
						configStrings_ingame.add("     " + color + ingameString.substring(0, 49));
						ingameString = ingameString.substring(49);
					}
					configStrings_ingame.add("     " + color + ingameString);
				}
				else configStrings_ingame.add(nestCount + "] " + color + string);
			}

			String nestIndentation = "";
			for(int i = 0; i < nestCount; i++)
				nestIndentation += "    ";
			configStrings_console.add(nestIndentation + string);
			
			switch(loadState)
			{
				case NOT_LOADED:
					log.warning(nestIndentation + string);
					break;
				case SUCCESS:
					log.info(nestIndentation + string);
					break;
				case FAILURE:
					log.severe(string);
					break;
			}
		}
		configPages = configStrings_ingame.size()/9 + (configStrings_ingame.size()%9 > 0?1:0);
	}

	private static boolean sendConfig(Player player, int pageNumber)
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
			player.sendMessage(ModDamage.ModDamageString(ChatColor.GOLD) + " Config Overview: " + state_plugin.statusString() + ChatColor.GOLD + " (Total pages: " + configPages + ")");
			player.sendMessage(ChatColor.AQUA + "Aliases:    " + state_aliases.statusString() + "        " + ChatColor.DARK_GRAY + "Routines: " + state_routines.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Armor:        " + state_armorAliases.statusString() + "     " + ChatColor.DARK_GREEN + "Damage: " + state_damageRoutines.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Element:     " + state_elementAliases.statusString() + "       " + ChatColor.DARK_GREEN + "Death:  " + state_deathRoutines.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Group:        " + state_groupAliases.statusString() + "     " + ChatColor.DARK_GREEN + "Food:  " + state_foodRoutines.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Item:        " + state_itemAliases.statusString() + "      " + ChatColor.DARK_GREEN + "Spawn:  " + state_spawnRoutines.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Message:   " + state_messageAliases.statusString() + "        " + ChatColor.DARK_AQUA + "Biome:  " + state_biomeAliases.statusString());
			String bottomString = null;
			switch(state_plugin)
			{
				case NOT_LOADED:
					bottomString = ChatColor.GRAY + "No configuration found.";
					break;
				case FAILURE:
					bottomString = ChatColor.DARK_RED + "There were one or more read errors in config.";
					break;
				case SUCCESS:
					bottomString = ChatColor.GREEN + "No errors loading configuration!";
					break;
			}
			player.sendMessage(bottomString);	
		}
		//TODO: Else for configured aliases/routine types.
		return false;
	}
	
//// INGAME MATCHING ////
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

	public static List<ArmorSet> matchArmorAlias(String key){ return armorAliaser.matchAlias(key);}
	public static List<Biome> matchBiomeAlias(String key){ return biomeAliaser.matchAlias(key);}
	public static List<ModDamageElement> matchElementAlias(String key){ return elementAliaser.matchAlias(key);}
	public static List<Material> matchItemAlias(String key){ return itemAliaser.matchAlias(key);}
	public static List<String> matchGroupAlias(String key){ return groupAliaser.matchAlias(key);}
	public static List<String> matchMessageAlias(String key){ return messageAliaser.matchAlias(key);}
	public static List<String> matchWorldAlias(String key){ return worldAliaser.matchAlias(key);}
}