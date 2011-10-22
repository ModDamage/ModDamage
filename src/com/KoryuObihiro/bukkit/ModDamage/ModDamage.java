package com.KoryuObihiro.bukkit.ModDamage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager.PermissionsManager;
import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager.RegionsManager;
import com.KoryuObihiro.bukkit.ModDamage.ModDamageEntityListener.EventType;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageTag;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.ArmorAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.BiomeAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.ElementAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.GroupAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.MaterialAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.MessageAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RegionAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.WorldAliaser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Message.DynamicMessage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

/**
 * "ModDamage" for Bukkit
 * 
 * @author Erich Gubler
 *
 */
public class ModDamage extends JavaPlugin
{
	//TODO 0.9.6 Command for autogen world/entitytype switches?
	//TODO 0.9.6 switch.conditional
	//TODO 0.9.6 Make the Scan message possible.
	// -Triggered effects...should be a special type of tag! :D Credit: ricochet1k
	// -AoE clearance, block search nearby for Material?
	
	// Crazy Ideas 
	// -----------
	//
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
	// -ability to clear non-static tag
	// -External: tag entities with an alias ($)
	// -External: check entity tags
	// -find a way to give players ownership of an explosion?
	// -Deregister when Bukkit supports!
	
	// -External calls to aliased sets of routines? But...EventInfo would be screwed up. :P

	public final int oldestSupportedBuild = 1317;
	private final ModDamageEntityListener entityListener = new ModDamageEntityListener(this);
	public final static Logger log = Logger.getLogger("Minecraft");
	private static DebugSetting debugSetting = DebugSetting.NORMAL;
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
		public boolean shouldOutput(DebugSetting setting)
		{
			if(setting.ordinal() <= this.ordinal())
				return true;
			return false;
		}
	}
	public enum LoadState
	{
		NOT_LOADED(ChatColor.GRAY + "NO  "), 
		FAILURE(ChatColor.RED + "FAIL"), 
		SUCCESS(ChatColor.GREEN + "YES ");
		
		private String string;
		private LoadState(String string){ this.string = string;}
		private String statusString(){ return string;}
		private static LoadState combineStates(List<LoadState> eventStates)
		{
			LoadState returnState = LoadState.NOT_LOADED;
			for(LoadState state : eventStates)
			{
				if(state.equals(LoadState.FAILURE))
					return LoadState.FAILURE;
				else if(state.equals(LoadState.SUCCESS))
					returnState = SUCCESS;
			}
			return returnState;
		}
	}

	private static Configuration config;
	private static final String errorString_Permissions = ModDamageString(ChatColor.RED) + " You don't have access to that command.";
	private static int configPages = 0;
	private static List<String> configStrings_ingame = new ArrayList<String>();
	private static List<String> configStrings_console = new ArrayList<String>();
	public static int indentation = 0;
	
	//misc
	public static final List<String> emptyList = new ArrayList<String>();
	
//Routine objects
	public static final RoutineManager routineManager = new RoutineManager();
	public static class RoutineManager
	{
		private final HashMap<EventType, List<Routine>> eventRoutines = new HashMap<EventType, List<Routine>>();
		private final HashMap<EventType, LoadState> eventStates = new HashMap<EventType, LoadState>();
		private LoadState state = LoadState.NOT_LOADED;
	
		public List<Routine> getRoutines(EventType eventType){ return eventRoutines.get(eventType);}
		
		protected LoadState getState(EventType eventType){ return eventStates.get(eventType);}
		
		protected void reload()
		{
			addToLogRecord(DebugSetting.VERBOSE, "Loading routines...", LoadState.SUCCESS);
			ModDamage.indentation++;
			eventRoutines.clear();
			eventStates.clear();
			state = LoadState.NOT_LOADED;
			for(EventType eventType : EventType.values())
			{
				ModDamage.indentation++;
				List<?> routineObjects = null;
				for(String key : config.getKeys())
					if(key.equalsIgnoreCase(eventType.name()))
					{
						routineObjects = (List<?>)config.getList(key);
						break;
					}
				if(routineObjects != null)
				{
					addToLogRecord(DebugSetting.NORMAL, eventType.name() + " configuration:", LoadState.SUCCESS);
					ModDamage.indentation++;
					LoadState[] stateMachine = { LoadState.SUCCESS };//We use a single-cell array here because the enum is assigned later.
					List<Routine> routines = RoutineAliaser.parse(routineObjects, stateMachine);
					ModDamage.indentation--;
					eventStates.put(eventType, stateMachine[0]);
					
					if(!routines.isEmpty() && !eventStates.get(eventType).equals(LoadState.FAILURE))
						eventRoutines.put(eventType, routines);
					else  eventRoutines.put(eventType, new ArrayList<Routine>());
				}
				else
				{
					eventRoutines.put(eventType, new ArrayList<Routine>());
					eventStates.put(eventType, LoadState.NOT_LOADED);
				}
				switch(eventStates.get(eventType))
				{
					case NOT_LOADED:
						addToLogRecord(DebugSetting.VERBOSE, eventType.name() + " configuration not found.", LoadState.NOT_LOADED);
						break;
					case FAILURE:
						addToLogRecord(DebugSetting.QUIET, "Error in " + eventType.name() + " configuration.", LoadState.FAILURE);
						break;
					case SUCCESS:
						addToLogRecord(DebugSetting.NORMAL, "End " + eventType.name() + " configuration.", LoadState.SUCCESS);
						break;
				}
				ModDamage.indentation--;
			}
			ModDamage.indentation--;
			state = LoadState.combineStates(new ArrayList<LoadState>(eventStates.values()));
		}
	}
	
//Alias objects
	private static ArmorAliaser armorAliaser = new ArmorAliaser();
	private static BiomeAliaser biomeAliaser = new BiomeAliaser();
	private static ElementAliaser elementAliaser = new ElementAliaser();
	private static GroupAliaser groupAliaser = new GroupAliaser();
	private static MaterialAliaser materialAliaser = new MaterialAliaser();
	private static MessageAliaser messageAliaser = new MessageAliaser();
	private static RegionAliaser regionAliaser = new RegionAliaser();
	private static RoutineAliaser routineAliaser = new RoutineAliaser();
	private static WorldAliaser worldAliaser = new WorldAliaser();
	private static LoadState state_aliases = LoadState.NOT_LOADED;
	
	private static LoadState state_plugin = LoadState.NOT_LOADED;
	public static boolean isEnabled = false;
	
////////////////////////// INITIALIZATION
	@Override
	public void onEnable() 
	{
		
	//Event registration
		//register plugin-related stuff with the server's plugin manager
		Bukkit.getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.PROJECTILE_HIT, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_TAME, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_REGAIN_HEALTH, entityListener, Event.Priority.Highest, this);
		this.config = this.getConfiguration();
		reload(true);
		isEnabled = true;
	}

	@Override
	public void onDisable(){ log.info(logPrepend() + "disabled.");}

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
							if(fromConsole || ExternalPluginManager.getPermissionsManager().hasPermission(player, "moddamage.debug"))
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
							if(args.length == 1 || args.length == 2)
							{
								boolean reloadingAll = args.length == 2 && args[1].equalsIgnoreCase("all");
								if(fromConsole) reload(reloadingAll);
								else if(ExternalPluginManager.getPermissionsManager().hasPermission(player, "moddamage.reload")) 
								{
									log.info(logPrepend() + "Reload initiated by user " + player.getName() + "...");
									reload(reloadingAll);
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
							}
							return true;
						}
						else if(args[0].equalsIgnoreCase("enable"))
						{
							if(fromConsole || ExternalPluginManager.getPermissionsManager().hasPermission(player, "moddamage.enable"))
								setPluginStatus(player, true);
							else player.sendMessage(errorString_Permissions);
							return true;
						}
						else if(args[0].equalsIgnoreCase("disable"))
						{
							if(fromConsole || ExternalPluginManager.getPermissionsManager().hasPermission(player, "moddamage.disable"))
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
								sendLogRecord(null, 9001);
								log.info(logPrepend() + "Done.");
							}
							else if(args.length == 1)
							{
								if(ExternalPluginManager.getPermissionsManager().hasPermission(player, "moddamage.check"))
									sendLogRecord(player, 0);
								else player.sendMessage(errorString_Permissions);
							}
							//md check int
							else if(args.length == 2)
							{
								try
								{
									sendLogRecord(player, Integer.parseInt(args[1]));
								} 
								catch(NumberFormatException e)
								{
									sendCommandUsage(player, true);
								}
							}
						}
						else
						{
							sendCommandUsage(player, true);
						}
					}
					else if(player == null)
						log.info(logPrepend() + "ModDamage must be enabled to use that command.");
					else player.sendMessage(ModDamageString(ChatColor.RED) + " ModDamage must be enabled to use that command.");
					return true;
				}
			}
		sendCommandUsage(player, true);
		return true;
	}
	
///// HELPER FUNCTIONS ////
	public static String ModDamageString(ChatColor color){ return color + "[" + ChatColor.DARK_RED + "Mod" + ChatColor.DARK_BLUE + "Damage" + color + "]";}
	public static String logPrepend(){ return "[ModDamage] ";}
	
//// PLUGIN CONFIGURATION ////
	private void setPluginStatus(Player player, boolean sentEnable) 
	{
		if(sentEnable)
		{
			if(isEnabled)
			{
				if(player != null) player.sendMessage(ModDamageString(ChatColor.RED) + " Already enabled!");
				else log.info(logPrepend() + "Already enabled!");
			}
			else
			{
				isEnabled = true;
				log.info(logPrepend() + "Plugin enabled.");
				if(player != null) player.sendMessage(ModDamageString(ChatColor.GREEN) + " Plugin enabled.");
			}
		}
		else 
		{
			if(isEnabled)
			{
				isEnabled = false;
				log.info(logPrepend() + "Plugin disabled.");
				if(player != null) player.sendMessage(ModDamageString(ChatColor.GREEN) + " Plugin disabled.");
			}
			else
			{
				if(player != null) player.sendMessage(ModDamageString(ChatColor.RED) + " Already disabled!");
				else log.info(logPrepend() + "Already disabled!");
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
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/md (disable|enable) - disable/enable ModDamage");
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
	private void reload(boolean reloadingAll)
	{
		armorAliaser.clear();
		biomeAliaser.clear();
		elementAliaser.clear();
		groupAliaser.clear();
		materialAliaser.clear();
		messageAliaser.clear();
		regionAliaser.clear();
		routineAliaser.clear();
		worldAliaser.clear();
		state_plugin = state_aliases = LoadState.NOT_LOADED;
		
		ModDamageTag.reload(null);//FIXME
		
		configStrings_ingame.clear();
		configStrings_console.clear();
		
		ModDamage.addToLogRecord(DebugSetting.QUIET, "[" + this.getDescription().getName() + "] v" + this.getDescription().getVersion()  + " loading...", LoadState.SUCCESS);
		
		if(reloadingAll)
		{
			ExternalPluginManager.reload();
			if(ExternalPluginManager.getPermissionsManager().equals(PermissionsManager.SUPERPERMS))
				ModDamage.addToLogRecord(DebugSetting.QUIET, "[" + this.getDescription().getName() + "] No permissions plugin found.", LoadState.NOT_LOADED);
			else ModDamage.addToLogRecord(DebugSetting.QUIET, "[" + this.getDescription().getName() + "] Permissions: " + ExternalPluginManager.getPermissionsManager().name() + " v" + ExternalPluginManager.getPermissionsManager().getVersion(), LoadState.SUCCESS);
			if(ExternalPluginManager.getRegionsManager().equals(RegionsManager.NONE))
				ModDamage.addToLogRecord(DebugSetting.VERBOSE, "[" + this.getDescription().getName() + "] No regional plugins found.", LoadState.NOT_LOADED);
			else ModDamage.addToLogRecord(DebugSetting.QUIET, "[" + this.getDescription().getName() + "] Regions: " + ExternalPluginManager.getRegionsManager().name() + " v" + ExternalPluginManager.getRegionsManager().getVersion(), LoadState.SUCCESS);
			
		//Bukkit build check
			String string = getServer().getVersion();
			Matcher matcher = Pattern.compile(".*b([0-9]+)jnks.*", Pattern.CASE_INSENSITIVE).matcher(string);
			if(matcher.matches())
			{
				if(Integer.parseInt(matcher.group(1)) < oldestSupportedBuild)
					addToLogRecord(DebugSetting.QUIET, "Detected Bukkit build " + matcher.group(1) + " - builds " + oldestSupportedBuild + " and older are not supported with this version of ModDamage. Please update your current Bukkit installation.", LoadState.FAILURE);
			}
			else addToLogRecord(DebugSetting.QUIET, logPrepend() + "Either this is a nonstandard/custom build, or the Bukkit builds system has changed. Either way, don't blame Koryu if stuff breaks.", LoadState.FAILURE);
		}
		
		try{ config.load();}
		catch(Exception e)
		{
			//TODO 0.9.7 - Any way to catch this without firing off the stacktrace? Request for Bukkit to not auto-load config.
			addToLogRecord(DebugSetting.QUIET, "Error in YAML configuration.", LoadState.FAILURE);
			e.printStackTrace();
			/*
			for(StackTraceElement element : e.getStackTrace())
			{
			    addToConfig(DebugSetting.QUIET, 0, element.toString(), LoadState.FAILURE);
			}
			*/
			state_plugin = LoadState.FAILURE;
		    return;
		}
		
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
					log.info(logPrepend() + "\"Quiet\" mode active - suppressing noncritical debug messages and warnings.");
					break;
				case NORMAL: 
					log.info(logPrepend() + "Debugging active.");
					break;
				case VERBOSE: 
					log.info(logPrepend() + "Verbose debugging active.");
					break;
				default: 
					log.info(logPrepend() + "Debug string not recognized - defaulting to \"normal\" settings.");
					debugSetting = DebugSetting.NORMAL;
					break;
			}
			ModDamage.debugSetting = debugSetting;
		}

	//Aliasing
		addToLogRecord(DebugSetting.VERBOSE, "Loading aliases...", LoadState.SUCCESS);
		for(String key : config.getKeys())
			if(key.equalsIgnoreCase("Aliases"))
			{
				ConfigurationNode aliasesNode = config.getNode(key);
				if(!aliasesNode.getKeys().isEmpty())
				{
					ModDamage.indentation++;
					List<LoadState> list = Arrays.asList(armorAliaser.load(aliasesNode), biomeAliaser.load(aliasesNode), elementAliaser.load(aliasesNode), materialAliaser.load(aliasesNode), groupAliaser.load(aliasesNode), messageAliaser.load(aliasesNode), regionAliaser.load(aliasesNode), routineAliaser.load(aliasesNode), worldAliaser.load(aliasesNode));
					state_aliases = LoadState.combineStates(list);
					ModDamage.indentation--;
					switch(state_aliases)
					{
						case NOT_LOADED:
							addToLogRecord(DebugSetting.VERBOSE, "No aliases loaded! Are any aliases defined?", state_aliases);
							break;
						case FAILURE:
							addToLogRecord(DebugSetting.QUIET, "One or more errors occurred while loading aliases.", state_aliases);
							break;
						case SUCCESS:
							addToLogRecord(DebugSetting.VERBOSE, "Aliases loaded!", state_aliases);
							break;
					}
					break;
				}
				else addToLogRecord(DebugSetting.VERBOSE, "No Aliases node found.", LoadState.NOT_LOADED);
			}
		
	//Routines
		routineManager.reload();
		switch(routineManager.state)
		{
			case NOT_LOADED:
				addToLogRecord(DebugSetting.VERBOSE, "No routines loaded! Are any routines defined?", state_aliases);
				break;
			case FAILURE:
				addToLogRecord(DebugSetting.QUIET, "One or more errors occurred while loading routines.", state_aliases);
				break;
			case SUCCESS:
				addToLogRecord(DebugSetting.VERBOSE, "Routines loaded!", state_aliases);
				break;
		}

		state_plugin = LoadState.combineStates(Arrays.asList(state_aliases, routineManager.state));
		
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
		log.info(logPrepend() + "" + sendThis);
	}

	private void writeDefaults() 
	{
		addToLogRecord(DebugSetting.QUIET, logPrepend() + "No configuration file found! Writing a blank config...", LoadState.NOT_LOADED);
		config.setHeader("#Auto-generated config");
		config.setProperty("debugging", "normal");
		for(EventType eventType : EventType.values())
			config.setProperty(eventType.name(), null);
		
		String[][] toolAliases = { {"axe", "hoe", "pickaxe", "spade", "sword"}, {"WOOD_", "STONE_", "IRON_", "GOLD_", "DIAMOND_"}};
		for(String toolType : toolAliases[0])
		{
			List<String> combinations = new ArrayList<String>();
			for(String toolMaterial : toolAliases[1])
				combinations.add(toolMaterial + toolType.toUpperCase());
			config.setProperty("Aliases." + materialAliaser.getName() + "." + toolType, combinations);
		}
		log.info(logPrepend() + "Completed auto-generation of config.yml.");
		config.save();
	}
	
	//TODO 0.9.7 Implement a reload hook for other plugins, make /md r reload routine library.
		
//// LOGGING ////
	
	private static void setDebugging(Player player, DebugSetting setting)
	{ 
		if(setting != null) 
		{
			if(!debugSetting.equals(setting))
			{
				String sendThis = "Changed debug from " + debugSetting.name().toLowerCase() + " to " + setting.name().toLowerCase();
				log.info(logPrepend() + sendThis);
				if(player != null) player.sendMessage(ModDamageString(ChatColor.GREEN) + " " + sendThis);
				debugSetting = setting;
				config.setProperty("debugging", debugSetting.name().toLowerCase());
			}
			else
			{
				log.info(logPrepend() + "Debug already set to " + setting.name().toLowerCase() + "!");
				if(player != null) player.sendMessage(ModDamageString(ChatColor.GREEN) + " Debug already set to " + setting.name().toLowerCase() + "!");
			}
		}
		else log.severe(logPrepend() + "Error: bad debug setting. Valid settings: normal, quiet, verbose");//shouldn't happen
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
	
	//TODO Make an object that stores everything, but still prints according to debug settings.
	public static void addToLogRecord(DebugSetting outputSetting, String string, LoadState loadState)
	{
		//if(loadState.equals(LoadState.FAILURE)) state_plugin = LoadState.FAILURE;//TODO REMOVE ME.
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
					configStrings_ingame.add(ModDamage.indentation + "] " + color + ingameString.substring(0, 49));
					ingameString = ingameString.substring(49);
					while(ingameString.length() > 50)
					{
						configStrings_ingame.add("     " + color + ingameString.substring(0, 49));
						ingameString = ingameString.substring(49);
					}
					configStrings_ingame.add("     " + color + ingameString);
				}
				else configStrings_ingame.add(ModDamage.indentation + "] " + color + string);
			}

			String nestIndentation = "";
			for(int i = 0; i < ModDamage.indentation; i++)
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
					log.severe(nestIndentation + string);
					break;
			}
		}
		configPages = configStrings_ingame.size()/9 + (configStrings_ingame.size()%9 > 0?1:0);
	}

	//Spout GUI?
	private static boolean sendLogRecord(Player player, int pageNumber)
	{
		if(player == null)
		{
			String printString = logPrepend() + "Complete log record for this server:";
			for(String configString : configStrings_console)
				printString += "\n" + configString;
			log.info(printString);
			return true;
		}
		else if(pageNumber > 0)
		{
			if(pageNumber <= configPages)
			{
				player.sendMessage(ModDamage.ModDamageString(ChatColor.GOLD) + " Log Record: (" + pageNumber + "/" + configPages + ")");
				for(int i = (9 * (pageNumber - 1)); i < (configStrings_ingame.size() < (9 * pageNumber)?configStrings_ingame.size():(9 * pageNumber)); i++)
					player.sendMessage(ChatColor.DARK_AQUA + configStrings_ingame.get(i));
				return true;
			}
		}
		else
		{
			//TODO 0.9.6 - Unify the placement, output according to the RoutineManager and the AliasManager.
			player.sendMessage(ModDamage.ModDamageString(ChatColor.GOLD) + " Config Overview: " + state_plugin.statusString() + ChatColor.GOLD + " (Total pages: " + configPages + ")");
			player.sendMessage(ChatColor.AQUA + "Aliases:    " + state_aliases.statusString() + "        " + ChatColor.DARK_GRAY + "Routines: " + routineManager.state.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Armor:        " + armorAliaser.getLoadState().statusString() + "     " + ChatColor.DARK_GREEN + "Damage: " + routineManager.getState(EventType.Damage).statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Element:     " + elementAliaser.getLoadState().statusString() + "       " + ChatColor.DARK_GREEN + "Death:  " + routineManager.getState(EventType.Death).statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Group:        " + groupAliaser.getLoadState().statusString() + "     " + ChatColor.DARK_GREEN + "Food:  " + routineManager.getState(EventType.Food).statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Material:    " + materialAliaser.getLoadState().statusString() + "      " + ChatColor.DARK_GREEN + "ProjectileHit:  " + routineManager.getState(EventType.ProjectileHit).statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Message:   " + messageAliaser.getLoadState().statusString() + "        " + ChatColor.DARK_GREEN + "Spawn:  " + routineManager.getState(EventType.Spawn).statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Region:   " + regionAliaser.getLoadState().statusString()+ "        " + ChatColor.DARK_GREEN + "Tame:  " + routineManager.getState(EventType.Tame).statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Routine:   " + routineAliaser.getLoadState().statusString());
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
		return false;
	}
	
//// CONFIG MATCHING ////	
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
	public static List<Material> matchMaterialAlias(String key){ return materialAliaser.matchAlias(key);}
	public static List<String> matchGroupAlias(String key){ return groupAliaser.matchAlias(key);}
	public static List<DynamicMessage> matchMessageAlias(String key){ return messageAliaser.matchAlias(key);}
	public static List<String> matchRegionAlias(String key){ return regionAliaser.matchAlias(key);}
	public static List<Routine> matchRoutineAlias(String key){ return routineAliaser.matchAlias(key);}
	public static List<String> matchWorldAlias(String key){ return worldAliaser.matchAlias(key);}

	public static DebugSetting getDebugSetting() { return debugSetting;}
}