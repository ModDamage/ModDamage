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
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.ModDamage.Alias.AliasManager;
import com.ModDamage.Backend.ExternalPluginManager;
import com.ModDamage.Backend.ExternalPluginManager.GroupsManager;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;
import com.ModDamage.Backend.ScriptParser;
import com.ModDamage.Events.Command;
import com.ModDamage.Events.Repeat;
import com.ModDamage.Server.MDServer;

public class PluginConfiguration implements ScriptLineHandler
{
	
	private static final String TM_MAINLOAD = "Entire Reload";
	private static final String TM_EXT_PL_MAN = "External Plugin Manager";
	private static final String TM_SCRIPTLOAD = "Script Loading";
	private static final String TM_MDEvent = "ModDamage Event";
	
	public final static Logger log = Logger.getLogger("Minecraft");
	protected static final String configString_defaultConfigPath = "config.mdscript";

	public final Plugin plugin;
	private final File configFile;
	public static String newline = System.getProperty("line.separator");
	public static int indentation = 0;

	
	
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
	

	public PluginConfiguration(Plugin plugin)
	{
		this.plugin = plugin;
		this.configFile = new File(plugin.getDataFolder(), configString_defaultConfigPath);
	}
	

	// Settings
	DebugSetting currentSetting = DebugSetting.VERBOSE;
	
	int tags_save_interval;
	
	String serverBindaddr;
	int serverPort;
	String serverUsername;
	String serverPassword;
	
	private void resetDefaultSettings()
	{
		currentSetting = DebugSetting.VERBOSE;
		
		tags_save_interval = 200;
		
		serverBindaddr = null;
		serverPort = 8765;
		serverUsername = null;
		serverPassword = null;
		
		
	}
	
	private class SettingsLineHandler implements ScriptLineHandler
	{
		private Pattern settingPattern = Pattern.compile("\\s*([^=]+?)\\s*=\\s*(.*?)\\s*");
		
		@Override
		public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren)
		{
			Matcher m = settingPattern.matcher(line.line);
			if (!m.matches()) {
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid setting: \"" + line.line + "\"");
				return null;
			}
			
			String name = m.group(1).trim().toLowerCase().replaceAll("\\s+", "-");
			String value = m.group(2).trim();

			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "setting: '"+name+"' = '"+value+"'");
			
			
			if (name.equals("debugging")) {
				try {
					currentSetting = DebugSetting.valueOf(value.toUpperCase());
				}
				catch (IllegalArgumentException e) {
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad debug level: " + value);
				}
			}
			else if (name.equals("disable-death-messages")) {
				MDEvent.disableDeathMessages = Boolean.parseBoolean(value);
			}
			else if (name.equals("disable-join-messages")) {
				MDEvent.disableJoinMessages = Boolean.parseBoolean(value);
			}
			else if (name.equals("disable-quit-messages")) {
				MDEvent.disableQuitMessages = Boolean.parseBoolean(value);
			}
			else if (name.equals("disable-kick-messages")) {
				MDEvent.disableKickMessages = Boolean.parseBoolean(value);
			}
			else if (name.equals("tags-save-interval")) {
				try {
					tags_save_interval = Integer.parseInt(value);
				}
				catch (NumberFormatException e) {
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad tags save interval: " + value);
				}
			}
			else if (name.equals("server-bindaddr")) {
				serverBindaddr = value;
			}
			else if (name.equals("server-port")) {
				try {
					serverPort = Integer.parseInt(value);
				}
				catch (NumberFormatException e) {
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad server port: " + value);
				}
			}
			else if (name.equals("server-username")) {
				serverUsername = value;
			}
			else if (name.equals("server-password")) {
				serverPassword = value;
			}
			else {
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown setting: " + m.group(1));
			}
			
			return null;
		}

		@Override
		public void done()
		{
		}
	}

	@Override
	public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren)
	{
		String[] words = line.line.split("\\s+");
		if (words.length == 0) return null;
		
		String word0 = words[0].toLowerCase();
		if (words.length == 1)
		{
			if (word0.equals("aliases")) {
				return AliasManager.getLineHandler();
			}
			else if (word0.equals("settings")) {
				return new SettingsLineHandler();
			}
		}
		else
		{
			if (word0.equals("on") && words.length == 2) {
				if (hasChildren)
					return MDEvent.getEvent(words[1]).getLineHandler();
				return null;
			}
		}
		return null;
	}

	@Override
	public void done()
	{
	}

	public boolean reload(boolean reloadingAll)
	{
		StopWatch sw = new StopWatch();
		sw.start(TM_MAINLOAD);
		LoadState.pluginState = LoadState.NOT_LOADED;
		logMessagesSoFar = 0;

		resetWorstLogMessageLevel();
		resetDefaultSettings();
		Command.instance.reset();
		Repeat.instance.reset();
		MDEvent.unregisterEvents();
		MDEvent.clearEvents();
		

		addToLogRecord(OutputPreset.CONSTANT, "[" + plugin.getDescription().getName() + "] v" + plugin.getDescription().getVersion() + " loading...");

		if(reloadingAll)
		{
			sw.start(TM_EXT_PL_MAN);
			MDEvent.registerVanillaEvents();
			ExternalPluginManager.reload();
			if(ExternalPluginManager.getGroupsManager() == GroupsManager.None)
				addToLogRecord(OutputPreset.INFO_VERBOSE, "Permissions: No permissions plugin found.");
			else
				addToLogRecord(OutputPreset.CONSTANT, "Permissions: " + ExternalPluginManager.getGroupsManager().name() + " v" + GroupsManager.getVersion());
			
			if(ExternalPluginManager.regionsManagers.isEmpty())
				addToLogRecord(OutputPreset.INFO_VERBOSE, "Region Plugins: No regional plugins found.");
			else
				addToLogRecord(OutputPreset.CONSTANT, "Region Plugins: " + Utils.joinBy(", ", ExternalPluginManager.regionsManagers));
			
			if(ExternalPluginManager.getMcMMOPlugin() == null)
				addToLogRecord(OutputPreset.INFO_VERBOSE, "mcMMO: Plugin not found.");
			else
				addToLogRecord(OutputPreset.CONSTANT, "mcMMO: Using version " + ExternalPluginManager.getMcMMOPlugin().getDescription().getVersion());
			
			sw.stop(TM_EXT_PL_MAN);
		}

		
		sw.start(TM_SCRIPTLOAD);
		FileInputStream stream = null;
		try
		{
			stream = new FileInputStream(configFile);
			ScriptParser parser = new ScriptParser(stream);
			parser.parseScript(this);
		}
		catch (FileNotFoundException e)
		{
			if(!writeDefaults())
				return false;
		}
		catch (IOException e){ printToLog(Level.SEVERE, "Fatal: could not close " + configString_defaultConfigPath + "!"); }
		finally {
			if (stream != null) {
				try {
					stream.close();
				}
				catch (IOException e) { }
			}
		}
		sw.stop(TM_SCRIPTLOAD);
		
		sw.start(TM_MDEvent);
		MDEvent.registerEvents();
		sw.stop(TM_MDEvent);
		
		// Default message settings
		if(MDEvent.disableDeathMessages)
			ModDamage.addToLogRecord(OutputPreset.CONSTANT, "Vanilla death messages disabled.");
		else
			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Vanilla death messages enabled.");
		
		if(MDEvent.disableJoinMessages)
			ModDamage.addToLogRecord(OutputPreset.CONSTANT, "Vanilla join messages disabled.");
		else
			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Vanilla join messages enabled.");
		
		if(MDEvent.disableQuitMessages)
			ModDamage.addToLogRecord(OutputPreset.CONSTANT, "Vanilla quit messages disabled.");
		else
			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Vanilla quit messages enabled.");
		
		if(MDEvent.disableKickMessages)
			ModDamage.addToLogRecord(OutputPreset.CONSTANT, "Vanilla kick messages disabled.");
		else
			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Vanilla kick messages enabled.");
		

		if(serverUsername != null && serverPassword != null) {
			ModDamage.addToLogRecord(OutputPreset.CONSTANT, "Web server starting on port "+ (serverBindaddr != null? serverBindaddr : "*") +":"+ serverPort);
			MDServer.startServer(serverBindaddr, serverPort, serverUsername, serverPassword);
		} else
			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Web server not started");
		
		
		LoadState.pluginState = LoadState.combineStates(MDEvent.combinedLoadState, AliasManager.getState());
		
		double time = sw.stop(TM_MAINLOAD);
		String timer = "(" + time + " \u00b5s) ";
		
		addToLogRecord(OutputPreset.INFO_VERBOSE, "Timings:");
		
		changeIndentation(true);
		
		addToLogRecord(OutputPreset.INFO_VERBOSE, "Event Loading: " + (sw.time(TM_MDEvent)/1000) + " \u00b5s) ");
		addToLogRecord(OutputPreset.INFO_VERBOSE, "External Event Manager: "+ (sw.time(TM_EXT_PL_MAN)/1000) + " \u00b5s");
		addToLogRecord(OutputPreset.INFO_VERBOSE, "Script Loading: " + (sw.time(TM_SCRIPTLOAD)/1000) + " \u00b5s) ");
		
		changeIndentation(false);
		
		switch(LoadState.pluginState)
		{
			case NOT_LOADED:
				addToLogRecord(OutputPreset.CONSTANT, logPrepend() + timer + "No configuration loaded.");
				break;
			case FAILURE:
				addToLogRecord(OutputPreset.CONSTANT, logPrepend() + timer + "Loaded configuration with one or more errors.");
				break;
			case SUCCESS:
				int worstValue = worstLogMessageLevel.intValue();
				
				if (worstValue >= Level.SEVERE.intValue()) {
					addToLogRecord(OutputPreset.CONSTANT, logPrepend() + timer + "Finished loading configuration with errors.");
				}
				else if (worstValue >= Level.WARNING.intValue()) {
					addToLogRecord(OutputPreset.CONSTANT, logPrepend() + timer + "Finished loading configuration with warnings.");
				}
				else if (worstValue >= Level.INFO.intValue()) {
					addToLogRecord(OutputPreset.CONSTANT, logPrepend() + timer + "Finished loading configuration.");
				}
				else {
					addToLogRecord(OutputPreset.CONSTANT, logPrepend() + timer + "Weird reload: " + worstLogMessageLevel);
				}
				
				break;
				
			default: throw new Error("Unknown state: "+LoadState.pluginState+" $PC280");
		}
		
		if (getDebugSetting() == DebugSetting.QUIET && logMessagesSoFar >= maxLogMessagesToShow)
			log.log(Level.INFO, "Suppressed "+(logMessagesSoFar-maxLogMessagesToShow)+" error messages");
		
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


		outputString += newline + newline +  "Settings";
		outputString += newline + "\t## Port probably has to be larger than 1024";
		outputString += newline + "\t## Uncomment the following to enable the server";
		outputString += newline + "\t## bindaddr should be left empty if you want the server to be accessable from anywhere";
		outputString += newline + "\t#server-bindaddr = ";
		outputString += newline + "\t#server port = 8765";
		outputString += newline + "\t#server username = mdadmin";
		outputString += newline + "\t#server password = nuggets";

		outputString += newline;
		outputString += newline + "\tdebugging = normal";
		outputString += newline + "\tdisable death messages = no";
		outputString += newline + "\tdisable join messages = no";
		outputString += newline + "\tdisable quit messages = no";
		outputString += newline + "\tdisable kick messages = no";
		outputString += newline + "\t#This interval should be tinkered with ONLY if you understand the implications.";
		outputString += newline + "\ttags save interval = " + tags_save_interval;

		
		outputString += newline + newline + "Aliases";
		for(AliasManager aliasType : AliasManager.values())
		{
			outputString += newline + "\t" + aliasType.name() + "";
			switch(aliasType)
			{
				case Material:
					String[][] toolAliases = {
							{ "axe", "hoe", "pickaxe", "spade", "sword" },
							{ "WOOD_", "STONE_", "IRON_", "GOLD_", "DIAMOND_" } };
					for(String toolType : toolAliases[0])
					{
						outputString += newline + "\t\t" + toolType + "";
						for(String toolMaterial : toolAliases[1])
							outputString += newline + "\t\t\t" + toolMaterial + toolType.toUpperCase();
					}
					break;
					
				default: break;
			}
		}
		

		outputString += newline + "# Events";
		for (Entry<String, List<MDEvent>> category : MDEvent.eventCategories.entrySet())
		{
			outputString += newline + "## "+category.getKey()+" Events";
			for (MDEvent event : category.getValue())
				outputString += newline + "on " + event.name();
			outputString += newline;
		}
		
		
		printToLog(Level.INFO, "Completed auto-generation of " + configString_defaultConfigPath + ".");

		try
		{
			Writer writer = new FileWriter(configFile);
			writer.write(outputString);
			writer.close();

			FileInputStream stream = new FileInputStream(configFile);
			ScriptParser parser = new ScriptParser(stream);
			parser.parseScript(this);
			stream.close();
		}
		catch (IOException e)
		{
			printToLog(Level.SEVERE, "Error writing to " + configString_defaultConfigPath + ".");
		}
		return true;
	}
	
	
	public static int maxLogMessagesToShow = 50;
	public static int logMessagesSoFar = 0;
	
	public Level worstLogMessageLevel;
	
	public void resetWorstLogMessageLevel() {
		worstLogMessageLevel = Level.INFO;
	}
	
	public void addToLogRecord(OutputPreset preset, ScriptLine line, String message)
	{
		addToLogRecord(preset, line.lineNumber + ": " + message);
	}
	
	public void addToLogRecord(OutputPreset preset, String message)
	{


		if(getDebugSetting().shouldOutput(preset.debugSetting)) {
			if (preset.level.intValue() > worstLogMessageLevel.intValue())
				worstLogMessageLevel = preset.level;
			if (getDebugSetting() != DebugSetting.QUIET || logMessagesSoFar < maxLogMessagesToShow) {
				String nestIndentation = "";
				for(int i = 0; i < indentation; i++)
					nestIndentation += "    ";
				
				log.log(preset.level, nestIndentation + message);
			}
			logMessagesSoFar ++;
		}
	}

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
			StringBuffer contents = new StringBuffer((int) file.length());
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

	public DebugSetting getDebugSetting()
	{
		return currentSetting;
	}
	
	public static void changeIndentation(boolean forward)
	{
		if (forward) indentation++;
		else indentation--;
	}
}
