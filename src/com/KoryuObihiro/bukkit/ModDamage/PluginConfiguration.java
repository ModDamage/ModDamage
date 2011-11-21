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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager.PermissionsManager;
import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager.RegionsManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;

//TODO Deprecate LoadState, reduce the size of DebugSetting
public class PluginConfiguration
{
	public final int oldestSupportedBuild;
	public final static Logger log = Logger.getLogger("Minecraft");
	private final Plugin plugin;
	private final File configFile;
	private int configPages = 0;
	private LinkedHashMap<String, Object> configMap;
	public static String newline = System.getProperty("line.separator");
	private List<String> configStrings_ingame = new ArrayList<String>();
	private List<String> configStrings_console = new ArrayList<String>();
	private List<OutputPreset> configStrings_filters = new ArrayList<OutputPreset>();
	public static int indentation = 0;

	protected DebugSetting currentSetting = DebugSetting.NORMAL;
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
		private LoadState(String string){ this.string = string;}

		private String statusString(){ return string;}

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
		WARNING(DebugSetting.VERBOSE, ChatColor.YELLOW, Level.WARNING);
		
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

	public static String getCaseInsensitiveKey(LinkedHashMap<String, Object> map, String key)
	{
		for(String someKey : map.keySet())
			if(someKey.equalsIgnoreCase(key))
				return someKey;
		return null;
	}

	public PluginConfiguration(Plugin plugin, int oldestSupportedBuild)
	{
		this.plugin = plugin;
		this.configFile = new File(plugin.getDataFolder(), "config.yml");
		this.oldestSupportedBuild = oldestSupportedBuild;
	}

	public void reload(boolean reloadingAll)
	{
		long reloadStartTime = System.nanoTime();
		LoadState.pluginState = LoadState.NOT_LOADED;

		configStrings_ingame.clear();
		configStrings_console.clear();
		configStrings_filters.clear();

		addToLogRecord(OutputPreset.CONSTANT, "[" + plugin.getDescription().getName() + "] v" + plugin.getDescription().getVersion() + " loading...");

		// scope this...because I think it looks nicer. :P
		{
			Object configObject = null;
			FileInputStream stream;
			try
			{
				Yaml yaml = new Yaml();
				stream = new FileInputStream(configFile);
				configObject = yaml.load(stream);
				stream.close();
				if(configObject == null) writeDefaults();
				else configMap = castToStringMap("config.yml", configObject);
			}
			catch (FileNotFoundException e){ writeDefaults();}
			catch (IOException e){ log.severe("Fatal: could not close config.yml!");}
			catch (YAMLException e)
			{
				// TODO 0.9.7 - Any way to catch this without firing off the stacktrace? Request for Bukkit to not
				// auto-load config.
				addToLogRecord(OutputPreset.FAILURE, "Error in YAML configuration. Please use valid YAML in config.yml.");
				e.printStackTrace();
				LoadState.pluginState = LoadState.FAILURE;
				return;
			}
		}

		if(reloadingAll)
		{
			ExternalPluginManager.reload();
			if(ExternalPluginManager.getPermissionsManager().equals(PermissionsManager.SUPERPERMS))
				addToLogRecord(OutputPreset.WARNING, "[" + plugin.getDescription().getName() + "] No permissions plugin found.");
			else addToLogRecord(OutputPreset.CONSTANT, "[" + plugin.getDescription().getName() + "] Permissions: " + ExternalPluginManager.getPermissionsManager().name() + " v" + ExternalPluginManager.getPermissionsManager().getVersion());
			if(ExternalPluginManager.getRegionsManager().equals(RegionsManager.NONE))
				addToLogRecord(OutputPreset.WARNING, "[" + plugin.getDescription().getName() + "] No regional plugins found.");
			else addToLogRecord(OutputPreset.CONSTANT, "[" + plugin.getDescription().getName() + "] Regions: " + ExternalPluginManager.getRegionsManager().name() + " v" + ExternalPluginManager.getRegionsManager().getVersion());

		// Bukkit build check
			String string = Bukkit.getVersion();
			Matcher matcher = Pattern.compile(".*b([0-9]+)jnks.*", Pattern.CASE_INSENSITIVE).matcher(string);
			if(matcher.matches())
			{
				if(Integer.parseInt(matcher.group(1)) < oldestSupportedBuild)
					addToLogRecord(OutputPreset.FAILURE, "Detected Bukkit build " + matcher.group(1) + " - builds " + oldestSupportedBuild + " and older are not supported with this version of " + plugin.getDescription().getName() + ". Please update your current Bukkit installation.");
			}
			else addToLogRecord(OutputPreset.FAILURE, logPrepend() + "Either this is a nonstandard/custom build, or the Bukkit builds system has changed. Either way, don't blame Koryu if stuff breaks.");
		}

		// load debug settings
		Object debugObject = configMap.get(getCaseInsensitiveKey(configMap, "debugging"));
		if(debugObject != null)
		{
			DebugSetting debugSetting = DebugSetting.valueOf(debugObject.toString().toUpperCase());
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
					log.info(logPrepend() + "Debug string \"" + debugObject.toString() + "\" not recognized - defaulting to \"normal\".");
					debugSetting = DebugSetting.NORMAL;
					break;
			}
			currentSetting = debugSetting;
		}
		
		// Aliasing
		AliasManager.reload();

		// Routines
		ModDamageEventHandler.reload();

		LoadState.pluginState = LoadState.combineStates(ModDamageEventHandler.state, AliasManager.getState());
		switch(LoadState.pluginState)
		{
			case NOT_LOADED:
				addToLogRecord(OutputPreset.CONSTANT, logPrepend() + "No configuration loaded.");
				break;
			case FAILURE:
				addToLogRecord(OutputPreset.CONSTANT, logPrepend() + "Loaded configuration with one or more errors.");
				break;
			case SUCCESS:
				addToLogRecord(OutputPreset.CONSTANT, logPrepend() + "Finished loading configuration.");
				break;
		}

		addToLogRecord(OutputPreset.WARNING, "Reload operation took " + (System.nanoTime() - reloadStartTime) + " nanoseconds.");
	}

	private boolean writeDefaults()
	{
		addToLogRecord(OutputPreset.INFO, logPrepend() + "No configuration file found! Writing a blank config...");

		//FIXME Could be better.
		if(!configFile.exists())
		{
			try
			{
				if(!configFile.createNewFile())
				{
					log.severe("Fatal error: could not create config.yml.");
					return false;
				}
			}
			catch (IOException e)
			{
				log.severe("Error: could not create new config.yml.");
				e.printStackTrace();
				return false;
			}
		}

		String outputString = "#Auto-generated config at " + (new Date()).toString() + "." + newline + "#See the [wiki](https://github.com/KoryuObihiro/ModDamage/wiki) for more information." + newline + AliasManager.nodeName + ":";

		for(AliasManager aliasType : AliasManager.values())
		{
			outputString += newline + "    " + aliasType.name() + ":";
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
		printToLog(Level.INFO, "Completed auto-generation of config.yml.");

		try
		{
			Writer writer = new FileWriter(configFile);
			writer.write(outputString);
			writer.close();

			FileInputStream stream = new FileInputStream(configFile);
			configMap = castToStringMap("config.yml", (new Yaml()).load(stream));
			stream.close();
		}
		catch (IOException e)
		{
			log.severe("Error writing to config.yml.");
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
			addToLogRecord(OutputPreset.FAILURE, "Error: expected map of values for \"" + targetName + "\"");
		}
		else addToLogRecord(OutputPreset.WARNING, "Warning: nothing found for \"" + targetName + "\"");
		return null;
	}
	
	public void addToLogRecord(OutputPreset preset, String message)
	{
		// if(loadState.equals(LoadState.FAILURE)) state_plugin = LoadState.FAILURE;//TODO REMOVE ME.
		if(message.length() > 50)
		{
			String ingameString = message;
			configStrings_ingame.add(indentation + "] " + preset.color + ingameString.substring(0, 49));
			ingameString = ingameString.substring(49);
			while (ingameString.length() > 50)
			{
				configStrings_ingame.add("     " + preset.color + ingameString.substring(0, 49));
				ingameString = ingameString.substring(49);
			}
			configStrings_ingame.add("     " + preset.color + ingameString);
		}
		else configStrings_ingame.add(indentation + "] " + preset.color + message);

		String nestIndentation = "";
		for(int i = 0; i < indentation; i++)
			nestIndentation += "    ";
		configStrings_console.add(nestIndentation + message);
		configStrings_filters.add(preset);
		configPages = configStrings_ingame.size() / 9 + (configStrings_ingame.size() % 9 > 0 ? 1 : 0);

		if(currentSetting.shouldOutput(preset.debugSetting))
			log.log(preset.level, nestIndentation + message);
	}

	// Spout GUI?
	public boolean sendLogRecord(Player player, int pageNumber)
	{
		if(player == null)
		{
			for(int i = 0; i < configStrings_console.size(); i++)
				if(currentSetting.shouldOutput(configStrings_filters.get(i).debugSetting))
					log.log(configStrings_filters.get(i).level, configStrings_console.get(i));
			return true;
		}
		else if(pageNumber > 0)
		{
			if(pageNumber <= configPages)
			{
				player.sendMessage(ModDamage.chatPrepend(ChatColor.GOLD) + "Log Record: (" + pageNumber + "/" + configPages + ")");
				for(int i = (9 * (pageNumber - 1)); i < (configStrings_ingame.size() < (9 * pageNumber) ? configStrings_ingame.size() : (9 * pageNumber)); i++)
					if(!configStrings_filters.get(i).equals(DebugSetting.CONSOLE) && currentSetting.shouldOutput(configStrings_filters.get(i).debugSetting))
						player.sendMessage(configStrings_filters.get(i).color + configStrings_ingame.get(i));
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

	public void toggleDebugging(Player player)
	{
		switch(currentSetting)
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
	
	public void setDebugging(Player player, DebugSetting setting)
	{
		if(setting != null)
		{
			if(!currentSetting.equals(setting))
			{
				if(replaceOrAppendInFile(configFile, "debugging:.*", "debugging: " + setting.name().toLowerCase()))
				{
					ModDamage.sendMessage(player, "Changed debug from " + currentSetting.name().toLowerCase() + " to " + setting.name().toLowerCase(), ChatColor.GREEN);
					currentSetting = setting;
				}
				else if(player != null)
					player.sendMessage(ModDamage.chatPrepend(ChatColor.RED) + "Couldn't save changes to config.yml.");
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
	
	private String logPrepend(){ return "[" + plugin.getDescription().getName() + "] ";}
	public void printToLog(Level level, String message){ log.log(level, "[" + plugin.getDescription().getName() + "] " + message);}

	public LinkedHashMap<String, Object> getConfigMap(){ return configMap;}
}
