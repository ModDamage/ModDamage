package com.ModDamage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import com.ModDamage.Alias.AliasManager;
import com.ModDamage.Backend.ExternalPluginManager;
import com.ModDamage.Backend.ExternalPluginManager.GroupsManager;
import com.ModDamage.Backend.ExternalPluginManager.RegionsManager;
import com.ModDamage.Events.Init;

public class PluginConfiguration
{
	public final static Logger log = Logger.getLogger("Minecraft");
	protected static final String configString_defaultConfigPath = "config.yml";

	public final Plugin plugin;
	private final File configFile;
//	private int configPages = 0;
	private LinkedHashMap<String, Object> configMap;
	public static String newline = System.getProperty("line.separator");
//	private List<String> configStrings_ingame = new ArrayList<String>();
//	private List<OutputPreset> configStrings_consoleFilters = new ArrayList<OutputPreset>();
//	private List<String> configStrings_console = new ArrayList<String>();
//	private List<OutputPreset> configStrings_ingameFilters = new ArrayList<OutputPreset>();
	public static int indentation = 0;

	DebugSetting currentSetting = DebugSetting.VERBOSE;
	public static enum DebugSetting
	{
		QUIET, NORMAL, CONSOLE, VERBOSE;
		public boolean shouldOutput(DebugSetting setting)
		{
			if(setting.ordinal() <= this.ordinal())
				return true;
			return false;
		}
	}

	public enum LoadState
	{
		NOT_LOADED(ChatColor.GRAY + "NO  "), FAILURE(ChatColor.RED + "FAIL"), SUCCESS(ChatColor.GREEN + "YES ");

		private String string;
		private LoadState(String string){ this.string = string; }

		public String statusString(){ return string; }

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
	
	public static enum OutputPreset
	{
		CONSOLE_ONLY(DebugSetting.CONSOLE, null, Level.INFO),
		CONSTANT(DebugSetting.QUIET, ChatColor.LIGHT_PURPLE, Level.INFO),
		FAILURE(DebugSetting.QUIET, ChatColor.RED, Level.SEVERE),
		INFO(DebugSetting.NORMAL, ChatColor.GREEN, Level.INFO),
		INFO_VERBOSE(DebugSetting.VERBOSE, ChatColor.AQUA, Level.INFO),
		WARNING(DebugSetting.VERBOSE, ChatColor.YELLOW, Level.WARNING),
		WARNING_STRONG(DebugSetting.NORMAL, ChatColor.YELLOW, Level.WARNING);
		
		protected final DebugSetting debugSetting;
		protected final ChatColor color;
		protected final Level level;
		private OutputPreset(DebugSetting debugSetting, ChatColor color, Level level)
		{
			this.debugSetting = debugSetting;
			this.color = color;
			this.level = level;
		}
	}
	
	public static boolean hasCaseInsensitiveValue(LinkedHashMap<String, Object> map, String key)
	{
		for(String mapKey : map.keySet())
			if(mapKey.equalsIgnoreCase(key))
				return true;
		return false;
	}
	
	public static Object getCaseInsensitiveValue(LinkedHashMap<String, Object> map, String key)
	{
		for(Entry<String, Object> entry : map.entrySet())
			if(entry.getKey().equalsIgnoreCase(key))
				return entry.getValue();
		return null;
	}
	
	public static boolean getBooleanValue(LinkedHashMap<String, Object> map, String key, boolean default_)
	{
		Object debugObject = getCaseInsensitiveValue(map, key);
		if(debugObject != null)
		{
			if(debugObject instanceof Boolean)
				return (Boolean)debugObject;
			else
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: expected boolean value for "+key+", but got an " + debugObject.getClass().getName());
		}
		return default_;
	}

	public PluginConfiguration(Plugin plugin)
	{
		this.plugin = plugin;
		this.configFile = new File(plugin.getDataFolder(), configString_defaultConfigPath);
	}

	public boolean reload(boolean reloadingAll)
	{
		long reloadStartTime = System.nanoTime();
		LoadState.pluginState = LoadState.NOT_LOADED;
		logMessagesSoFar = 0;

//		configStrings_ingame.clear();
//		configStrings_ingameFilters.clear();
//		configStrings_console.clear();
//		configStrings_consoleFilters.clear();

		addToLogRecord(OutputPreset.CONSTANT, "[" + plugin.getDescription().getName() + "] v" + plugin.getDescription().getVersion() + " loading...");

		// scope this...because it looks nicer?
		{
			Object configObject = null;
			FileInputStream stream;
			try
			{
				Yaml yaml = new Yaml();
				stream = new FileInputStream(configFile);
				configObject = yaml.load(stream);
				stream.close();
				if(configObject == null)
				{
					if(!writeDefaults())
						return false;
				}
				else configMap = castToStringMap(configString_defaultConfigPath, configObject);
			}
			catch (FileNotFoundException e)
			{
				if(!writeDefaults())
					return false;
			}
			catch (IOException e){ printToLog(Level.SEVERE, "Fatal: could not close " + configString_defaultConfigPath + "!"); }
			catch (YAMLException e)
			{
				// TODO 0.9.7 - Any way to catch this without firing off the stacktrace? Request for Bukkit to not
				// auto-load config.
				addToLogRecord(OutputPreset.FAILURE, logPrepend() + "Error in YAML configuration. Please use valid YAML in " + configString_defaultConfigPath + ".");
				addToLogRecord(OutputPreset.FAILURE, e.toString());
				LoadState.pluginState = LoadState.FAILURE;
				return false;
			}
		}

		if(reloadingAll)
		{
			ExternalPluginManager.reload();
			if(ExternalPluginManager.getGroupsManager() == GroupsManager.None)
				addToLogRecord(OutputPreset.INFO_VERBOSE, "Permissions: No permissions plugin found.");
			else
				addToLogRecord(OutputPreset.CONSTANT, "Permissions: " + ExternalPluginManager.getGroupsManager().name() + " v" + GroupsManager.getVersion());
			
			if(ExternalPluginManager.getRegionsManager() == RegionsManager.NONE)
				addToLogRecord(OutputPreset.INFO_VERBOSE, "Regions: No regional plugins found.");
			else
				addToLogRecord(OutputPreset.CONSTANT, "Regions: " + ExternalPluginManager.getRegionsManager().name() + " v" + ExternalPluginManager.getRegionsManager().getVersion());
			
			if(ExternalPluginManager.getMcMMOPlugin() == null)
				addToLogRecord(OutputPreset.INFO_VERBOSE, "mcMMO: Plugin not found.");
			else
				addToLogRecord(OutputPreset.CONSTANT, "mcMMO: Using version " + ExternalPluginManager.getMcMMOPlugin().getDescription().getVersion());
		}

	// load debug settings
		Object debugObject = getCaseInsensitiveValue(configMap, "debugging");
		if(debugObject != null)
		{
			String configuration_debug = debugObject.toString().toUpperCase();
			this.currentSetting = null;
			for(DebugSetting setting : DebugSetting.values())
				if(configuration_debug.equalsIgnoreCase(setting.name()))
					this.currentSetting = setting;
			switch(getDebugSetting())
			{
				case QUIET:
					addToLogRecord(OutputPreset.INFO, "\"Quiet\" mode active - suppressing noncritical debug messages and warnings.");
					break;
				case NORMAL:
					addToLogRecord(OutputPreset.INFO, "Debugging active.");
					break;
				case VERBOSE:
					addToLogRecord(OutputPreset.INFO, "Verbose debugging active.");
					break;
				default:
					addToLogRecord(OutputPreset.WARNING_STRONG, "Debug string \"" + debugObject.toString() + "\" not recognized - defaulting to \"normal\".");
					this.currentSetting = DebugSetting.NORMAL;
					break;
			}
		}
		
		// Default message settings
		MDEvent.disableDeathMessages = getBooleanValue(configMap, "disable-deathmessages", false);
		if(MDEvent.disableDeathMessages)
			ModDamage.addToLogRecord(OutputPreset.CONSTANT, "Vanilla death messages disabled.");
		else
			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Vanilla death messages enabled.");
		
		MDEvent.disableJoinMessages = getBooleanValue(configMap, "disable-joinmessages", false);
		if(MDEvent.disableJoinMessages)
			ModDamage.addToLogRecord(OutputPreset.CONSTANT, "Vanilla join messages disabled.");
		else
			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Vanilla join messages enabled.");
		
		MDEvent.disableQuitMessages = getBooleanValue(configMap, "disable-quitmessages", false);
		if(MDEvent.disableQuitMessages)
			ModDamage.addToLogRecord(OutputPreset.CONSTANT, "Vanilla quit messages disabled.");
		else
			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Vanilla quit messages enabled.");
		
		// Aliasing
		AliasManager.reload();

		// Routines
		MDEvent.reload();

		LoadState.pluginState = LoadState.combineStates(MDEvent.state, AliasManager.getState());
		
		String timer = "(" + (System.nanoTime() - reloadStartTime)/1000 + " μs) ";
		
		switch(LoadState.pluginState)
		{
			case NOT_LOADED:
				addToLogRecord(OutputPreset.CONSTANT, logPrepend() + timer + "No configuration loaded.");
				break;
			case FAILURE:
				addToLogRecord(OutputPreset.CONSTANT, logPrepend() + timer + "Loaded configuration with one or more errors.");
				break;
			case SUCCESS:
				addToLogRecord(OutputPreset.CONSTANT, logPrepend() + timer + "Finished loading configuration.");
				break;
				
			default: throw new Error("Unknown state: "+LoadState.pluginState+" $PC280");
		}
		
		if (getDebugSetting() == DebugSetting.QUIET && logMessagesSoFar >= maxLogMessagesToShow)
			log.log(Level.INFO, "Suppressed "+(logMessagesSoFar-maxLogMessagesToShow)+" error messages");

		Init.initAll();
		
		return true;
	}

	private boolean writeDefaults()
	{
		addToLogRecord(OutputPreset.INFO, logPrepend() + "No configuration file found! Writing a blank config in " + configString_defaultConfigPath + "...");
		if(!configFile.exists())
		{
			try
			{
				if(!(configFile.getParentFile().exists() || configFile.getParentFile().mkdirs()) || !configFile.createNewFile())
				{
					printToLog(Level.SEVERE, "Fatal error: could not create " + configString_defaultConfigPath + ".");
					return false;
				}
			}
			catch (IOException e)
			{
				printToLog(Level.SEVERE, "Error: could not create new " + configString_defaultConfigPath + ".");
				e.printStackTrace();
				return false;
			}
		}

		String outputString = "#Auto-generated config at " + (new Date()).toString() + "." + newline + "#See the wiki at https://github.com/ModDamage/ModDamage/wiki for more information." + newline;


		outputString += newline + "# Events";
		for (Entry<String, MDEvent[]> category : MDEvent.eventCategories.entrySet())
		{
			outputString += newline + "## "+category.getKey()+" Events";
			for (MDEvent event : category.getValue())
				outputString += newline + event.name() + ":";
			outputString += newline;
		}
		
		
		outputString += newline + newline + "Aliases:";
		for(AliasManager aliasType : AliasManager.values())
		{
			outputString += newline + "    " + aliasType.name() + ":";
			switch(aliasType)
			{
				case Material:
					String[][] toolAliases = {
							{ "axe", "hoe", "pickaxe", "spade", "sword" },
							{ "WOOD_", "STONE_", "IRON_", "GOLD_", "DIAMOND_" } };
					for(String toolType : toolAliases[0])
					{
						outputString += newline + "        " + toolType + ":";
						for(String toolMaterial : toolAliases[1])
							outputString += newline + "            - '" + toolMaterial + toolType.toUpperCase() + "'";
					}
					break;
					
				default: break;
			}
		}

		outputString += newline + newline +  "#Miscellaneous configuration";
		outputString += newline + "debugging: normal";
		outputString += newline + "disable-deathMessages: false";
		outputString += newline + "disable-joinMessages: false";
		outputString += newline + "disable-quitMessages: false";
		outputString += newline + "Tagging: #These intervals should be tinkered with ONLY if you understand the implications.";
		outputString += newline + "    interval-save: " + ModDamageTagger.defaultInterval;
		outputString += newline + "    interval-clean: " + ModDamageTagger.defaultInterval;
		printToLog(Level.INFO, "Completed auto-generation of " + configString_defaultConfigPath + ".");

		try
		{
			Writer writer = new FileWriter(configFile);
			writer.write(outputString);
			writer.close();

			FileInputStream stream = new FileInputStream(configFile);
			configMap = castToStringMap("" + configString_defaultConfigPath + "", (new Yaml()).load(stream));
			stream.close();
		}
		catch (IOException e)
		{
			printToLog(Level.SEVERE, "Error writing to " + configString_defaultConfigPath + ".");
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, Object> castToStringMap(String targetName, Object object)
	{
		if(object != null)
		{
			if(object instanceof LinkedHashMap)
				return (LinkedHashMap<String, Object>)object;
			addToLogRecord(OutputPreset.FAILURE, "Error: expected map of values for \"" + targetName + "\": "+object.getClass().getCanonicalName()+" :: "+object.toString());
		}
		else addToLogRecord(OutputPreset.WARNING, "No configuration values found for \"" + targetName + "\"");
		return null;
	}
	
	public static int maxLogMessagesToShow = 50;
	public static int logMessagesSoFar = 0;
	
	public void addToLogRecord(OutputPreset preset, String message)
	{
//		if(message.length() > 50)
//		{
//			configStrings_ingame.add(preset.color + "" +  indentation + "] " + message.substring(0, 49));
//			configStrings_ingameFilters.add(preset);
//			String ingameString = message.substring(49);
//			while (ingameString.length() > 50)
//			{
//				configStrings_ingame.add("     " + preset.color + ingameString.substring(0, 49));
//				configStrings_ingameFilters.add(preset);
//				ingameString = ingameString.substring(49);
//			}
//			configStrings_ingame.add("     " + preset.color + ingameString);
//			configStrings_ingameFilters.add(preset);
//		}
//		else
//		{
//			configStrings_ingame.add(preset.color + "" + indentation + "] " + message);
//			configStrings_ingameFilters.add(preset);
//		}
//		configPages = configStrings_ingame.size() / 9 + (configStrings_ingame.size() % 9 > 0 ? 1 : 0);
//
		String nestIndentation = "";
		for(int i = 0; i < indentation; i++)
			nestIndentation += "    ";
//		configStrings_console.add(nestIndentation + message);
//		configStrings_consoleFilters.add(preset);

		if(getDebugSetting().shouldOutput(preset.debugSetting)) {
			if (getDebugSetting() != DebugSetting.QUIET || logMessagesSoFar < maxLogMessagesToShow)
				log.log(preset.level, nestIndentation + message);
			logMessagesSoFar ++;
		}
	}

	// Spout GUI?
//	public boolean sendLogRecord(Player player, int pageNumber)
//	{
//		if(player == null)
//		{
//			for(int i = 0; i < configStrings_console.size(); i++)
//				if(getDebugSetting().shouldOutput(configStrings_consoleFilters.get(i).debugSetting))
//					log.log(configStrings_consoleFilters.get(i).level, configStrings_console.get(i));
//			return true;
//		}
//		else if(pageNumber > 0)
//		{
//			if(pageNumber <= configPages)
//			{
//				player.sendMessage(ModDamage.chatPrepend(ChatColor.GOLD) + "Log Record: (" + pageNumber + "/" + configPages + ")");
//				for(int i = (9 * (pageNumber - 1)); i < (configStrings_ingame.size() < (9 * pageNumber) ? configStrings_ingame.size() : (9 * pageNumber)); i++)
//					if(!configStrings_ingameFilters.get(i).equals(OutputPreset.CONSOLE_ONLY) && getDebugSetting().shouldOutput(configStrings_ingameFilters.get(i).debugSetting))
//						player.sendMessage(configStrings_ingame.get(i));
//				return true;
//			}
//		}
//		else
//		{
//			player.sendMessage(ModDamage.chatPrepend(ChatColor.GOLD) + "Config Overview: " + LoadState.pluginState.statusString() + ChatColor.GOLD + " (Total pages: " + configPages + ")");
//			player.sendMessage(ChatColor.AQUA + "Aliases:    " + AliasManager.getState().statusString() + "        " + ChatColor.DARK_GRAY + "Routines: " + MDEvent.state.statusString());
////			player.sendMessage(ChatColor.DARK_AQUA + " Armor: " + AliasManager.Armor.getSpecificLoadState().statusString() + " " + ChatColor.DARK_GREEN + "Damage: " + ModDamageEventHandler.Damage.specificLoadState.statusString());
////			player.sendMessage(ChatColor.DARK_AQUA + " Element: " + AliasManager.Type.getSpecificLoadState().statusString() + " " + ChatColor.DARK_GREEN + "Death: " + ModDamageEventHandler.Death.specificLoadState.statusString());
////			player.sendMessage(ChatColor.DARK_AQUA + " Group: " + AliasManager.Group.getSpecificLoadState().statusString() + " " + ChatColor.DARK_GREEN + "Heal: " + ModDamageEventHandler.Heal.specificLoadState.statusString());
////			player.sendMessage(ChatColor.DARK_AQUA + " Material: " + AliasManager.Material.getSpecificLoadState().statusString() + " " + ChatColor.DARK_GREEN + "ProjectileHit: " + ModDamageEventHandler.ProjectileHit.specificLoadState.statusString());
////			player.sendMessage(ChatColor.DARK_AQUA + " Message: " + AliasManager.Message.getSpecificLoadState().statusString() + " " + ChatColor.DARK_GREEN + "Spawn: " + ModDamageEventHandler.Spawn.specificLoadState.statusString());
////			player.sendMessage(ChatColor.DARK_AQUA + " Region: " + AliasManager.Region.getSpecificLoadState().statusString() + " " + ChatColor.DARK_GREEN + "Tame: " + ModDamageEventHandler.Tame.specificLoadState.statusString());
////			player.sendMessage(ChatColor.DARK_AQUA + " Routine: " + AliasManager.Routine.getSpecificLoadState().statusString());
//			player.sendMessage(ChatColor.DARK_AQUA + "   Routine:    " + AliasManager.Routine.getSpecificLoadState().statusString());
//			String bottomString = null;
//			switch(LoadState.pluginState)
//			{
//				case NOT_LOADED:
//					bottomString = ChatColor.GRAY + "No configuration found.";
//					break;
//				case FAILURE:
//					bottomString = ChatColor.DARK_RED + "There were one or more read errors in config.";
//					break;
//				case SUCCESS:
//					bottomString = ChatColor.GREEN + "No errors loading configuration!";
//					break;
//					
//				default: throw new Error("Unknown state: "+LoadState.pluginState+" $PC466");
//			}
//			player.sendMessage(bottomString);
//		}
//		return false;
//	}

	public void toggleDebugging(Player player)
	{
		switch(getDebugSetting())
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
				
			default: break;
		}
	}
	
	public void setDebugging(Player player, DebugSetting setting)
	{
		if(setting != null)
		{
			if(!getDebugSetting().equals(setting))
			{
				if(replaceOrAppendInFile(configFile, "debugging:.*", "debugging: " + setting.name().toLowerCase()))
				{
					ModDamage.sendMessage(player, "Changed debug from " + getDebugSetting().name().toLowerCase() + " to " + setting.name().toLowerCase(), ChatColor.GREEN);
					this.currentSetting = setting;
				}
				else if(player != null)
					player.sendMessage(ModDamage.chatPrepend(ChatColor.RED) + "Couldn't save changes to " + configString_defaultConfigPath + ".");
			}
			else ModDamage.sendMessage(player, "Debug already set to " + setting.name().toLowerCase() + "!", ChatColor.RED);
		}
		else printToLog(Level.SEVERE, "Error: bad debug setting sent. Valid settings: normal, quiet, verbose");// shouldn't																								// happen
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
	
	public String logPrepend(){ return "[" + plugin.getDescription().getName() + "] "; }
	public void printToLog(Level level, String message){ log.log(level, "[" + plugin.getDescription().getName() + "] " + message); }

	public LinkedHashMap<String, Object> getConfigMap(){ return configMap; }

	public DebugSetting getDebugSetting()
	{
		return currentSetting;
	}
}
