package com.KoryuObihiro.bukkit.ModDamage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager.PermissionsManager;
import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager.RegionsManager;
import com.KoryuObihiro.bukkit.ModDamage.ModDamageEventHandler.ModDamageEntityListener;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;

/**
 * "ModDamage" for Bukkit
 * 
 * @author Erich Gubler
 * 
 */
public class ModDamage extends JavaPlugin
{
	// TODO 0.9.6 Command for autogen world/entitytype switches?
	// TODO 0.9.6 Autogen empty aliasing nodes
	// FIXME Change conditional term builders to use aliasing!
	// -Triggered effects...should be a special type of tag! :D Credit: ricochet1k
	// -AoE clearance, block search nearby for Material?
	// -find a way to give players ownership of an explosion?
	// -Deregister when Bukkit supports!

	public final int oldestSupportedBuild = 1337;
	public final static Logger log = Logger.getLogger("Minecraft");

	// MD-centric configuration objects
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

		public static void toggleDebugging(Player player)
		{
			switch(DebugSetting.currentSetting)
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

		protected static DebugSetting currentSetting = DebugSetting.NORMAL;
	}

	public enum LoadState
	{
		NOT_LOADED(ChatColor.GRAY + "NO  "), FAILURE(ChatColor.RED + "FAIL"), SUCCESS(ChatColor.GREEN + "YES ");

		private String string;

		private LoadState(String string)
		{
			this.string = string;
		}

		private String statusString()
		{
			return string;
		}

		public static LoadState combineStates(LoadState... states)
		{
			return combineStates(Arrays.asList(states));
		}

		public static LoadState combineStates(Collection<LoadState> loadStates)
		{
			return loadStates.contains(FAILURE) ? LoadState.FAILURE : (loadStates.contains(SUCCESS) ? LoadState.SUCCESS : LoadState.NOT_LOADED);
		}

		protected static LoadState pluginState = LoadState.NOT_LOADED;
	}

	public static boolean isEnabled = false;

	public static String newline = System.getProperty("line.separator");
	private static File modDamageConfigFile;
	private static LinkedHashMap<String, Object> config;
	private static final String errorString_Permissions = chatPrepend(ChatColor.RED) + "You don't have access to that command.";
	private static int configPages = 0;
	private static List<String> configStrings_ingame = new ArrayList<String>();
	private static List<String> configStrings_console = new ArrayList<String>();
	public static int indentation = 0;

	private static ModDamageTagger tagger = null;

	// //////////////////////// INITIALIZATION
	@Override
	public void onEnable()
	{
		// register plugin-related stuff with the server's plugin manager
		ModDamageEntityListener entityListener = ModDamageEventHandler.entityListener;
		Bukkit.getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.PROJECTILE_HIT, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_TAME, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_REGAIN_HEALTH, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_RESPAWN, ModDamageEventHandler.playerListener, Event.Priority.Highest, this);

		modDamageConfigFile = new File(this.getDataFolder(), "config.yml");
		reload(true);
		isEnabled = true;
	}

	@Override
	public void onDisable()
	{
		tagger.close();
		log.info(logPrepend() + "disabled.");
	}

	// //COMMAND PARSING ////
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player player = ((sender instanceof Player) ? ((Player) sender) : null);
		if(args.length == 0) sendCommandUsage(player, false);
		else if(args.length >= 0)
		{
			String commandString = "";
			for(String arg : args)
				commandString += " " + arg;
			PluginCommand.handleCommand(player, commandString);
		}
		return true;
	}

	private enum PluginCommand// FIXME TEST!
	{
		CHECK(false, "\\s(?:check|c)(\\s\\d+)?")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				if(player == null)
				{
					sendLogRecord(null, 9001);
					log.info(logPrepend() + "Done.");
				}
				else
				{
					if(matcher.group(1) == null)
					{
						if(hasPermission(player, "moddamage.check"))
							sendLogRecord(player, 0);
					}
					else sendLogRecord(player, Integer.parseInt(matcher.group(1).substring(1)));
				}
			}
		},
		DEBUG(false, "\\sdebug(\\s\\w+)?")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				if(matcher.group(1) != null)
				{
					DebugSetting matchedSetting = DebugSetting.matchSetting(matcher.group(1).substring(1));
					if(matchedSetting != null)
						setDebugging(player, matchedSetting);
					else sendMessage(player, "Invalid debugging mode \"" + matcher.group(1).substring(1) + "\" - modes are \"quiet\", \"normal\", and \"verbose\".", ChatColor.RED);
				}
				else DebugSetting.toggleDebugging(player);
			}
		},
		RELOAD(false, "\\s(?:reload|r)(\\sall)?")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				boolean reloadingAll = matcher.group(1) != null;
				if(player != null) log.info(logPrepend() + "Reload initiated by user " + player.getName() + "...");
				((ModDamage) Bukkit.getPluginManager().getPlugin("ModDamage")).reload(reloadingAll);
				if(player != null)
					switch(LoadState.pluginState)
					{
						case SUCCESS:
							player.sendMessage(chatPrepend(ChatColor.GREEN) + "Reloaded!");
							break;
						case FAILURE:
							player.sendMessage(chatPrepend(ChatColor.YELLOW) + "Reloaded with errors.");
							break;
						case NOT_LOADED:
							player.sendMessage(chatPrepend(ChatColor.GRAY) + "No configuration loaded! Are any routines defined?");
							break;
					}
			}
		},
		STATUS(false, "\\s(?:enable|disable)")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				ModDamage.setPluginStatus(player, matcher.group().equalsIgnoreCase(" enable"));
			}
		},
		TAGS(true, "\\s(?:tags|t)\\s(clear|save)")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				if(matcher.group(1).equalsIgnoreCase("clear"))
					tagger.clear();
				else tagger.save();
				sendMessage(player, "Tags " + matcher.group(1).toLowerCase() + "ed.", ChatColor.GREEN);
			}
		};

		final static List<String> commandInstructions = new ArrayList<String>();
		final boolean needsEnable;
		final Pattern pattern;

		private PluginCommand(boolean needsEnable, String pattern)
		{
			this.needsEnable = needsEnable;
			this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		}

		public static void handleCommand(Player player, String commandString)
		{
			for(PluginCommand command : PluginCommand.values())
			{
				Matcher matcher = command.pattern.matcher(commandString);
				if(matcher.matches() && hasPermission(player, "moddamage." + command.name().toLowerCase()))
				{
					if(!command.needsEnable || isEnabled)
						command.handleCommand(player, matcher);
					else sendMessage(player, "ModDamage must be enabled to use that command.", ChatColor.RED);
					return;
				}
			}
			sendCommandUsage(player, true);
		}

		abstract protected void handleCommand(Player player, Matcher matcher);
	}

	// /// HELPER FUNCTIONS ////

	private static boolean hasPermission(Player player, String permission)
	{
		boolean has = player != null ? ExternalPluginManager.getPermissionsManager().hasPermission(player, "moddamage.reload") : true;
		if(!has) player.sendMessage(errorString_Permissions);
		return has;
	}

	private static void sendMessage(Player player, String message, ChatColor color)
	{
		if(player != null)
			player.sendMessage(chatPrepend(color) + message);
		else log.info(logPrepend() + message);
	}

	private static String chatPrepend(ChatColor color){ return color + "[" + ChatColor.DARK_RED + "Mod" + ChatColor.DARK_BLUE + "Damage" + color + "] ";}

	private static String logPrepend(){ return "[ModDamage] ";}

	private static void setPluginStatus(Player player, boolean status)
	{
		if(status != isEnabled)
		{
			isEnabled = status;
			log.info(logPrepend() + "Plugin " + (isEnabled ? "en" : "dis") + "abled.");
			if(player != null)
				player.sendMessage(chatPrepend(ChatColor.GREEN) + "Plugin " + (isEnabled ? "en" : "dis") + "abled.");
		}
		else sendMessage(player, "Already " + (isEnabled ? "en" : "dis") + "abled!", ChatColor.RED);
	}

	private static void setDebugging(Player player, DebugSetting setting)
	{
		if(setting != null)
		{
			if(!DebugSetting.currentSetting.equals(setting))
			{
				if(replaceOrAppendInFile(modDamageConfigFile, "debugging:.*", "debugging: " + setting.name().toLowerCase()))
				{
					sendMessage(player, "Changed debug from " + DebugSetting.currentSetting.name().toLowerCase() + " to " + setting.name().toLowerCase(), ChatColor.GREEN);
					DebugSetting.currentSetting = setting;
				}
				else if(player != null)
					player.sendMessage(chatPrepend(ChatColor.RED) + "Couldn't save changes to config.yml.");
			}
			else sendMessage(player, "Debug already set to " + setting.name().toLowerCase() + "!", ChatColor.RED);
		}
		else log.severe(logPrepend() + "Error: bad debug setting sent. Valid settings: normal, quiet, verbose");// shouldn't
																												// happen
	}

	private static boolean replaceOrAppendInFile(File file, String targetRegex, String replaceString)
	{
		Pattern targetPattern = Pattern.compile(targetRegex, Pattern.CASE_INSENSITIVE);
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			Matcher matcher;
			StringBuffer contents = new StringBuffer();
			String line;
			boolean changedFlag = false;
			while (reader.ready())
			{
				line = reader.readLine();
				matcher = targetPattern.matcher(line);
				if(matcher.matches())
				{
					changedFlag = true;
					contents.append(matcher.replaceAll(replaceString));
				}
				else contents.append(line);
				contents.append(newline);
			}
			reader.close();
			if(!changedFlag)
				contents.append(replaceString + newline);

			FileWriter writer = new FileWriter(file);
			writer.write(String.valueOf(contents));
			writer.close();
		}
		catch (FileNotFoundException e)
		{

		}
		catch (IOException e)
		{
		}
		return true;
	}

	private static void sendCommandUsage(Player player, boolean forError)
	{
		// TODO Use the PluginCommand enum
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
			log.info("ModDamage commands:\n" + "/moddamage | /md - bring up this help message\n" + "/md check - check configuration\n" + "/md debug [debugType] - change debugging type (quiet, normal, verbose)\n" + "/md disable - disable ModDamage\n" + "/md enable - enable ModDamage\n" + "/md reload - reload configuration");
		}
	}

	// ///////////////// MECHANICS CONFIGURATION
	private void reload(boolean reloadingAll)
	{
		long reloadStartTime = System.nanoTime();
		LoadState.pluginState = LoadState.NOT_LOADED;

		configStrings_ingame.clear();
		configStrings_console.clear();

		ModDamage.addToLogRecord(DebugSetting.QUIET, "[" + this.getDescription().getName() + "] v" + this.getDescription().getVersion() + " loading...", LoadState.SUCCESS);

		// scope this...because I think it looks nicer. :P
		{
			Object configObject = null;
			FileInputStream stream;
			try
			{
				Yaml yaml = new Yaml();
				stream = new FileInputStream(new File(this.getDataFolder(), "config.yml"));
				configObject = yaml.load(stream);
				stream.close();
				if(configObject == null) writeDefaults();
				else config = ConfigLibrary.getStringMap("config.yml", configObject);
			}
			catch (FileNotFoundException e){ writeDefaults();}
			catch (IOException e){ ModDamage.log.severe("Fatal: could not close config.yml!");}
			catch (YAMLException e)
			{
				// TODO 0.9.7 - Any way to catch this without firing off the stacktrace? Request for Bukkit to not
				// auto-load config.
				addToLogRecord(DebugSetting.QUIET, "Error in YAML configuration. Please use valid YAML in config.yml.", LoadState.FAILURE);
				e.printStackTrace();
				LoadState.pluginState = LoadState.FAILURE;
				return;
			}
		}

		if(reloadingAll)
		{
			ExternalPluginManager.reload();
			if(ExternalPluginManager.getPermissionsManager().equals(PermissionsManager.SUPERPERMS))
				ModDamage.addToLogRecord(DebugSetting.QUIET, "[" + this.getDescription().getName() + "] No permissions plugin found.", LoadState.NOT_LOADED);
			else ModDamage.addToLogRecord(DebugSetting.QUIET, "[" + this.getDescription().getName() + "] Permissions: " + ExternalPluginManager.getPermissionsManager().name() + " v" + ExternalPluginManager.getPermissionsManager().getVersion(), LoadState.SUCCESS);
			if(ExternalPluginManager.getRegionsManager().equals(RegionsManager.NONE))
				ModDamage.addToLogRecord(DebugSetting.VERBOSE, "[" + this.getDescription().getName() + "] No regional plugins found.", LoadState.NOT_LOADED);
			else ModDamage.addToLogRecord(DebugSetting.QUIET, "[" + this.getDescription().getName() + "] Regions: " + ExternalPluginManager.getRegionsManager().name() + " v" + ExternalPluginManager.getRegionsManager().getVersion(), LoadState.SUCCESS);

			// Bukkit build check
			String string = getServer().getVersion();
			Matcher matcher = Pattern.compile(".*b([0-9]+)jnks.*", Pattern.CASE_INSENSITIVE).matcher(string);
			if(matcher.matches())
			{
				if(Integer.parseInt(matcher.group(1)) < oldestSupportedBuild)
					addToLogRecord(DebugSetting.QUIET, "Detected Bukkit build " + matcher.group(1) + " - builds " + oldestSupportedBuild + " and older are not supported with this version of ModDamage. Please update your current Bukkit installation.", LoadState.FAILURE);
			}
			else addToLogRecord(DebugSetting.QUIET, logPrepend() + "Either this is a nonstandard/custom build, or the Bukkit builds system has changed. Either way, don't blame Koryu ifstuff breaks.", LoadState.FAILURE);

			if(tagger != null) tagger.close();

			long[] tagConfigIntegers = { ModDamageTagger.defaultInterval, ModDamageTagger.defaultInterval };
			LinkedHashMap<String, Object> tagConfigurationTree = ConfigLibrary.getStringMap("Tagging", config.get("Tagging"));
			if(tagConfigurationTree != null)
			{
				String[] tagConfigStrings = { ConfigLibrary.getCaseInsensitiveKey(tagConfigurationTree, "interval-save"), ConfigLibrary.getCaseInsensitiveKey(tagConfigurationTree, "interval-clean") };
				Object[] tagConfigObjects =	{ tagConfigurationTree.get(tagConfigStrings[0]), tagConfigurationTree.get(tagConfigStrings[1]) };
				for(int i = 0; i < tagConfigObjects.length; i++)
				{
					if(tagConfigObjects[i] != null)
					{
						if(tagConfigObjects[i] instanceof Integer)
							tagConfigIntegers[i] = (Integer)tagConfigObjects[i];
						else addToLogRecord(DebugSetting.QUIET, "Error: Could not read value for Tagging setting \"" + tagConfigStrings[i] + "\"", LoadState.FAILURE);
					}
				}
			}
			tagger = new ModDamageTagger(new File(this.getDataFolder(), "tags.yml"), tagConfigIntegers[0], tagConfigIntegers[1]);
		}

		// load debug settings
		Object debugObject = config.get(ConfigLibrary.getCaseInsensitiveKey(config, "debugging"));
		if(debugObject != null)
		{
			String debugString = (String)debugObject;
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
			ModDamage.DebugSetting.currentSetting = debugSetting;
		}

		// Aliasing
		AliasManager.reload();

		// Routines
		ModDamageEventHandler.reload();
		switch(ModDamageEventHandler.state)
		{
			case NOT_LOADED:
				addToLogRecord(DebugSetting.VERBOSE, "No routines loaded! Are any routines defined?", ModDamageEventHandler.state);
				break;
			case FAILURE:
				addToLogRecord(DebugSetting.QUIET, "One or more errors occurred while loading routines.", ModDamageEventHandler.state);
				break;
			case SUCCESS:
				addToLogRecord(DebugSetting.VERBOSE, "Routines loaded!", ModDamageEventHandler.state);
				break;
		}

		LoadState.pluginState = LoadState.combineStates(ModDamageEventHandler.state, AliasManager.getState());

		String sendThis = null;
		switch(LoadState.pluginState)
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

		addToLogRecord(DebugSetting.VERBOSE, "Reload operation took " + (System.nanoTime() - reloadStartTime) + " nanoseconds.", LoadState.NOT_LOADED);
	}

	private boolean writeDefaults()
	{
		addToLogRecord(DebugSetting.QUIET, logPrepend() + "No configuration file found! Writing a blank config...", LoadState.NOT_LOADED);

		try
		{
			if(!modDamageConfigFile.createNewFile())
				ModDamage.log.severe("FFffuuuu");// TODO REMOVE ME
		}
		catch (IOException e)
		{
			ModDamage.log.severe("Error: could not create new config.yml.");
			e.printStackTrace();
		}

		String outputString = "#Auto-generated config at " + (new Date()).toString() + "." + newline + "#See the [wiki](https://github.com/KoryuObihiro/ModDamage/wiki) for more information." + newline + AliasManager.nodeName + ":";

		for(AliasManager aliasType : AliasManager.values())
		{
			outputString += newline + "    " + aliasType.getAliaser().getName() + ":";
			if(aliasType.equals(AliasManager.Material))
			{
				String[][] toolAliases = { { "axe", "hoe", "pickaxe", "spade", "sword" }, { "WOOD_", "STONE_", "IRON_", "GOLD_", "DIAMOND_" } };
				for(String toolType : toolAliases[0])
				{
					outputString += newline + "        " + toolType + ":";
					for(String toolMaterial : toolAliases[1])
						outputString += newline + "            - '" + toolMaterial + toolType.toUpperCase() + "'";
				}
			}
			//TODO Add more defaults?
		}

		outputString += newline + newline + "#Events";
		for(ModDamageEventHandler eventType : ModDamageEventHandler.values())
			outputString += newline + eventType.name() + ":";

		outputString += newline + newline + "#Miscellaneous configuration";
		outputString += newline + "debugging: normal";
		outputString += newline + "Tagging: #These intervals should be tinkered with ONLY ifyou understand the implications.";
		outputString += newline + "    interval-save: " + ModDamageTagger.defaultInterval;
		outputString += newline + "    interval-clean: " + ModDamageTagger.defaultInterval;
		log.info(logPrepend() + "Completed auto-generation of config.yml.");

		try
		{
			Writer writer = new FileWriter(modDamageConfigFile);
			writer.write(outputString);
			writer.close();

			FileInputStream stream = new FileInputStream(modDamageConfigFile);
			config = ConfigLibrary.getStringMap("config.yml", (new Yaml()).load(stream));
			stream.close();
		}
		catch (IOException e)
		{
			ModDamage.log.severe("Error writing to config.yml.");
		}
		return true;
	}

	// TODO 0.9.7 Implement a reload hook for other plugins, make /md r reload routine library.

	// // LOGGING ////
	// TODO Make an object that stores everything, but still prints according to debug settings.
	public static void addToLogRecord(DebugSetting outputSetting, String string, LoadState loadState)
	{
		// if(loadState.equals(LoadState.FAILURE)) state_plugin = LoadState.FAILURE;//TODO REMOVE ME.
		if(DebugSetting.currentSetting.shouldOutput(outputSetting))
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
					while (ingameString.length() > 50)
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
		configPages = configStrings_ingame.size() / 9 + (configStrings_ingame.size() % 9 > 0 ? 1 : 0);
	}

	// Spout GUI?
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
				player.sendMessage(ModDamage.chatPrepend(ChatColor.GOLD) + "Log Record: (" + pageNumber + "/" + configPages + ")");
				for(int i = (9 * (pageNumber - 1)); i < (configStrings_ingame.size() < (9 * pageNumber) ? configStrings_ingame.size() : (9 * pageNumber)); i++)
					player.sendMessage(ChatColor.DARK_AQUA + configStrings_ingame.get(i));
				return true;
			}
		}
		else
		{
			// TODO 0.9.6 - Unify the placement, output according to the RoutineManager and the AliasManager.
			player.sendMessage(ModDamage.chatPrepend(ChatColor.GOLD) + "Config Overview: " + LoadState.pluginState.statusString() + ChatColor.GOLD + " (Total pages: " + configPages + ")");
			player.sendMessage(ChatColor.AQUA + "Aliases:    " + AliasManager.getState().statusString() + "        " + ChatColor.DARK_GRAY + "Routines: " + ModDamageEventHandler.state.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Armor:        " + AliasManager.Armor.getSpecificLoadState() + "     " + ChatColor.DARK_GREEN + "Damage: " + ModDamageEventHandler.Damage.specificLoadState.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Element:     " + AliasManager.Element.getSpecificLoadState().statusString() + "       " + ChatColor.DARK_GREEN + "Death:  " + ModDamageEventHandler.Death.specificLoadState.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Group:        " + AliasManager.Group.getSpecificLoadState().statusString() + "     " + ChatColor.DARK_GREEN + "Food:  " + ModDamageEventHandler.Food.specificLoadState.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Material:    " + AliasManager.Material.getSpecificLoadState().statusString() + "      " + ChatColor.DARK_GREEN + "ProjectileHit:  " + ModDamageEventHandler.ProjectileHit.specificLoadState.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Message:   " + AliasManager.Message.getSpecificLoadState().statusString() + "        " + ChatColor.DARK_GREEN + "Spawn:  " + ModDamageEventHandler.Spawn.specificLoadState.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Region:   " + AliasManager.Region.getSpecificLoadState().statusString() + "        " + ChatColor.DARK_GREEN + "Tame:  " + ModDamageEventHandler.Tame.specificLoadState.statusString());
			player.sendMessage(ChatColor.DARK_AQUA + "   Routine:   " + AliasManager.Routine.getSpecificLoadState().statusString() + "        Condition:  " + AliasManager.Routine.getSpecificLoadState().statusString());
			String bottomString = null;
			switch(LoadState.pluginState)
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

	// // CONFIG MATCHING ////
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

	public static DebugSetting getDebugSetting(){ return DebugSetting.currentSetting;}

	public static ModDamageTagger getTagger(){ return tagger;}

	public static LinkedHashMap<String, Object> getConfigMap(){ return config;}
}