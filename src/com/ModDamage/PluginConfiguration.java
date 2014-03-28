package com.ModDamage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.plugin.Plugin;

import com.ModDamage.MDLogger.DebugSetting;
import com.ModDamage.MDLogger.OutputPreset;
import com.ModDamage.ScriptManager.LoadMethod;
import com.ModDamage.Alias.AliasManager;
import com.ModDamage.Backend.ExternalPluginManager;
import com.ModDamage.Backend.ExternalPluginManager.GroupsManager;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;
import com.ModDamage.Backend.ScriptParser;
import com.ModDamage.Events.Command;
import com.ModDamage.Events.Repeat;
import com.ModDamage.Server.MDServer;

public class PluginConfiguration extends BaseConfig implements ScriptLineHandler
{
	private class SettingsLineHandler implements ScriptLineHandler
	{
		private Pattern settingPattern = Pattern.compile("\\s*([^=]+?)\\s*=\\s*(.*?)\\s*");

		@Override
		public void done()
		{
		}

		@Override
		public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren)
		{
			Matcher m = settingPattern.matcher(line.line);
			if (!m.matches()) {
				LogUtil.error("Invalid setting: \"" + line.line + "\"");
				return null;
			}
			
			String name = m.group(1).trim().toLowerCase().replaceAll("\\s+", "-");
			String value = m.group(2).trim();

//			LogUtil.info_verbose("setting: '"+name+"' = '"+value+"'");
			
			/*
			if (name.equals("debugging")) {
				try {
					log.currentSetting = DebugSetting.valueOf(value.toUpperCase());
				}
				catch (IllegalArgumentException e) {
					LogUtil.error("Bad debug level: " + value);
				}
			}
			else if (name.equals("log-file")) {
				logFile = new File(plugin.getDataFolder(), value);
			}
			else if (name.equals("append-logs")) {
				appendLog = Boolean.parseBoolean(value);
			}
			else*/ if (name.equals("disable-death-messages")) {
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
					LogUtil.error("Bad tags save interval: " + value);
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
					LogUtil.error("Bad server port: " + value);
				}
			}
			else if (name.equals("server-username")) {
				serverUsername = value;
			}
			else if (name.equals("server-password")) {
				serverPassword = value;
			}
			else if (name.equals("load-method")) {
				setLoadMethod(ScriptManager.LoadMethod.valueOf(value.replace(" ", "_").toUpperCase()));
			}
			else {
				LogUtil.error("Unknown setting: " + m.group(1));
			}
			
			return null;
		}
	}
	protected static final String configString_defaultConfigPath = "config.mdscript";
	public static final String newline = System.getProperty("line.separator");
	private static final String TM_EXT_PL_MAN = "External Plugin Manager";
	
	private static final String TM_MAINLOAD = "Entire Reload";

	private static final String TM_MDEvent = "ModDamage Event";

	private static final String TM_SCRIPTLOAD = "Script Loading";
	
	boolean appendLog;
	
	private LoadMethod loadMethod;
	File logFile;
	String serverBindaddr;
	String serverPassword;
	
	int serverPort;
	String serverUsername;
	
	int tags_save_interval;
	
	public PluginConfiguration(Plugin plugin)
	{
		super(plugin, "global", new File(plugin.getDataFolder(), configString_defaultConfigPath));
	}
	
	@Override
	public void done()
	{
	}
	
	@Override
	protected String getDefaultContents() {
		StringBuilder outputString = new StringBuilder().append("#Auto-generated config at " ).append( (new Date()).toString() ).append( "." ).append( newline ).append( "#See the wiki at https://github.com/ModDamage/ModDamage/wiki for more information." ).append( newline);

		outputString.append( newline ).append( newline ).append(  "Settings");
		outputString.append( newline ).append( "\t## This defines the type of loading style.");
		outputString.append( newline ).append( "\t## Valid options are: PRIORITY_PARSE, ENABLED_SETTING, MASTER_LIST ");
		outputString.append( newline ).append( "\t## MASTER_LIST: uses priorities for execution order and then this file has a list of scripts to load.");
		outputString.append( newline ).append( "\t## ENABLED_SETTING: Each script has a setting called enabled. If true it will load this file.");
		outputString.append( newline ).append( "\t## PRIORITY_PARSE: Same as enabled setting but uses the priority number. If the number is 0 or less it will be considered disabled.");
		outputString.append( newline ).append( "load-method = MASTER_LIST" );
		
		
		outputString.append( newline ).append( "\t## Port probably has to be larger than 1024");
		outputString.append( newline ).append( "\t## Uncomment the following to enable the server");
		outputString.append( newline ).append( "\t## bindaddr should be left empty if you want the server to be accessable from anywhere");
		outputString.append( newline ).append( "\t#server-bindaddr = ");
		outputString.append( newline ).append( "\t#server port = 8765");
		outputString.append( newline ).append( "\t#server username = mdadmin");
		outputString.append( newline ).append( "\t#server password = nuggets");

		outputString.append( newline);
		outputString.append( newline ).append( "\tdebugging = normal");
		outputString.append( newline ).append( "\tdisable death messages = no");
		outputString.append( newline ).append( "\tdisable join messages = no");
		outputString.append( newline ).append( "\tdisable quit messages = no");
		outputString.append( newline ).append( "\tdisable kick messages = no");
		outputString.append( newline ).append( "\t#This interval should be tinkered with ONLY if you understand the implications.");
		outputString.append( newline ).append( "\ttags save interval = " ).append( tags_save_interval);
		
		outputString.append( newline ).append( "\t## File Logging settings.");
		outputString.append( newline ).append( "\t## To Enable File Logging. Uncomment both lines below.");
		outputString.append( newline ).append( "\t##log file = config.log");
		outputString.append( newline ).append( "\t##append logs = yes");
		
//		outputString.append( newline ).append( newline ).append( "#Debug File Logging");
//		outputString.append( newline ).append( "#Uncomment the following to enable file logging");
//		outputString.append( newline ).append( "#Logging:");
//		outputString.append( newline ).append( "#    " ).append( "file: " ).append( "config" ).append( ".log");
//		outputString.append( newline ).append( "#    " ).append( "append: true");
		
		outputString.append( newline ).append( newline ).append( "Aliases");
		for(AliasManager aliasType : AliasManager.values())
		{
			outputString.append( newline ).append( "\t" ).append( aliasType.name() ).append( "");
			switch(aliasType)
			{
				case Material:
					String[][] toolAliases = {
							{ "axe", "hoe", "pickaxe", "spade", "sword" },
							{ "WOOD_", "STONE_", "IRON_", "GOLD_", "DIAMOND_" } };
					for(String toolType : toolAliases[0])
					{
						outputString.append( newline ).append( "\t\t" ).append( toolType ).append( "");
						for(String toolMaterial : toolAliases[1])
							outputString.append( newline ).append( "\t\t\t" ).append( toolMaterial ).append( toolType.toUpperCase());
					}
					break;
					
				default: break;
			}
		}
		

		outputString.append( newline ).append( "# Events");
		for (Entry<String, List<MDEvent>> category : MDEvent.eventCategories.entrySet())
		{
			outputString.append( newline ).append( "## ").append(category.getKey()).append(" Events");
			for (MDEvent event : category.getValue())
				outputString.append( newline ).append( "on " ).append( event.name());
			outputString.append( newline);
		}
		return outputString.toString();
	}

	public LoadMethod getLoadMethod() {
		return loadMethod;
	}
	
	public MDLogger getLog() { return log; }

	public String getName() {
		return "config.yml";
	}

	@Override
	public int getPriority() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected ScriptLineHandler getSubSettingLineHandler() {
		return new SettingsLineHandler();
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
				if (hasChildren) {
					MDEvent e = MDEvent.getEvent(words[1]);
					if (e == null) {
						LogUtil.warning(line, word0 + " " + words[1] + "is not valid. Possible that the event is not loaded!");
						return null;
					}
					
					return e.getLineHandler(this);
				}
				return null;
			}
		}
		return null;
	}

	public boolean reload(boolean reloadingAll)
	{
		StopWatch sw = new StopWatch();
		sw.start(TM_MAINLOAD);
		loadState = LoadState.NOT_LOADED;
		resetLoggedMessages();
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
			ScriptParser parser = new ScriptParser(this, stream);
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
		
		boolean loggingEnabled = false;
			if (logFile != null)
			{
				loggingEnabled = true;
				log.setLogFile(null);
			}
			log.setLogFile(logFile, appendLog);
		
		if (loggingEnabled)
		{
			if (log.log.getHandlers().length > 0)
				log.addToLogRecord(OutputPreset.INFO, "File Logging for 'config.yml' is enabled.");
			else
				log.addToLogRecord(OutputPreset.FAILURE, "File logging failed to load for '" + "config.yml" + "'.");
		}
		else
			log.addToLogRecord(OutputPreset.INFO, "File logging for '" + "config.yml" + "' is disabled.");
		
		// Default message settings
		if(MDEvent.disableDeathMessages)
			LogUtil.constant("Vanilla death messages disabled.");
		else
			LogUtil.info_verbose("Vanilla death messages enabled.");
		
		if(MDEvent.disableJoinMessages)
			LogUtil.constant("Vanilla join messages disabled.");
		else
			LogUtil.info_verbose("Vanilla join messages enabled.");
		
		if(MDEvent.disableQuitMessages)
			LogUtil.constant("Vanilla quit messages disabled.");
		else
			LogUtil.info_verbose("Vanilla quit messages enabled.");
		
		if(MDEvent.disableKickMessages)
			LogUtil.constant("Vanilla kick messages disabled.");
		else
			LogUtil.info_verbose("Vanilla kick messages enabled.");
		

		if(serverUsername != null && serverPassword != null) {
			LogUtil.constant("Web server starting on port "+ (serverBindaddr != null? serverBindaddr : "*") +":"+ serverPort);
			MDServer.startServer(serverBindaddr, serverPort, serverUsername, serverPassword);
		} else
			LogUtil.info_verbose("Web server not started");
		
		
		loadState = LoadState.combineStates(MDEvent.getCombinedLoadStates(this), AliasManager.getState());
		
		double time = sw.stop(TM_MAINLOAD);
		String timer = "(" + time + " \u00b5s) ";
		
		addToLogRecord(OutputPreset.INFO_VERBOSE, "Timings:");
		
		changeIndentation(true);
		
		addToLogRecord(OutputPreset.INFO_VERBOSE, "Event Loading: " + (sw.time(TM_MDEvent)/1000) + " \u00b5s) ");
		addToLogRecord(OutputPreset.INFO_VERBOSE, "External Event Manager: "+ (sw.time(TM_EXT_PL_MAN)/1000) + " \u00b5s");
		addToLogRecord(OutputPreset.INFO_VERBOSE, "Script Loading: " + (sw.time(TM_SCRIPTLOAD)/1000) + " \u00b5s) ");
		
		changeIndentation(false);
		
		switch(loadState)
		{
			case NOT_LOADED:
				addToLogRecord(OutputPreset.CONSTANT, log.logPrepend() + timer + "No configuration loaded.");
				break;
			case FAILURE:
				addToLogRecord(OutputPreset.CONSTANT, log.logPrepend() + timer + "Loaded configuration with one or more errors.");
				break;
			case SUCCESS:
				int worstValue = log.worstLogMessageLevel.intValue();
				
				if (worstValue >= Level.SEVERE.intValue()) {
					addToLogRecord(OutputPreset.CONSTANT, log.logPrepend() + timer + "Finished loading configuration with errors.");
				}
				else if (worstValue >= Level.WARNING.intValue()) {
					addToLogRecord(OutputPreset.CONSTANT, log.logPrepend() + timer + "Finished loading configuration with warnings.");
				}
				else if (worstValue >= Level.INFO.intValue()) {
					addToLogRecord(OutputPreset.CONSTANT, log.logPrepend() + timer + "Finished loading configuration.");
				}
				else {
					addToLogRecord(OutputPreset.CONSTANT, log.logPrepend() + timer + "Weird reload: " + log.worstLogMessageLevel);
				}
				
				break;
				
			default: throw new Error("Unknown state: "+loadState+" $PC280");
		}
		
		if (getDebugSetting() == DebugSetting.QUIET && log.logMessagesSoFar >= log.maxLogMessagesToShow)
			printToLog(Level.INFO, "Suppressed "+(log.logMessagesSoFar-log.maxLogMessagesToShow)+" error messages");
		
		return true;
	}
	
	

	
	
	
	
	// Helper Methods
	
	protected void resetDefaultSettings()
	{
		/*log.currentSetting = DebugSetting.NORMAL;
		 * logFile = null;
		 * log.setLogFile(logFile);
		 *  
		 * This above commented out code is in the BaseConfig class.
		 * Please call the super method to implement.
		 */
		super.resetDefaultSettings();
		tags_save_interval = 200;
		
		serverBindaddr = null;
		serverPort = 8765;
		serverUsername = null;
		serverPassword = null;
		
		
	}
	
	public void setLoadMethod(LoadMethod loadMethod) {
		this.loadMethod = loadMethod;
	}
}
