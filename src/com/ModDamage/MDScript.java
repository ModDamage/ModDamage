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
import com.ModDamage.Alias.AliasManager;
import com.ModDamage.Backend.ExternalPluginManager;
import com.ModDamage.Backend.ExternalPluginManager.GroupsManager;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;
import com.ModDamage.Backend.ScriptParser;
import com.ModDamage.Events.Command;
import com.ModDamage.Events.Repeat;

public class MDScript extends BaseConfig {

	protected int priority;

	public MDScript(Plugin plugin, String name, File configFile) {
		super(plugin, name, configFile);
	}

	@Override
	public boolean reload(boolean reloadAll) {
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

		if(reloadAll)
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
		catch (IOException e){ printToLog(Level.SEVERE, "Fatal: could not close " + getName() + ".mdscript" + "!"); }
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
				log.addToLogRecord(OutputPreset.INFO, "File Logging for '" + getName() + ".mdscript' is enabled.");
			else
				log.addToLogRecord(OutputPreset.FAILURE, "File logging failed to load for '" + getName() + ".mdscript'.");
		}
		else
			log.addToLogRecord(OutputPreset.INFO, "File logging for '" + getName() + ".mdscript' is disabled.");
		
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

	@Override
	protected String getDefaultContents() {
		StringBuilder outputString = new StringBuilder().append("#Auto-generated script at ").append((new Date()).toString()).append(".").append(newline).append("#See the wiki at https://github.com/ModDamage/ModDamage/wiki for more information.").append(newline);
		
		outputString.append(newline).append(newline).append("Settings");
		outputString.append( newline ).append( "\tdebugging = normal");
		outputString.append( newline).append( newline ).append( "\t##Priority for script order. Runs from Lowest to highest. 0 is the absolute first to run.");
		outputString.append( newline ).append( "\tpriority = 1");
		
		outputString.append( newline ).append( "\t## File Logging settings.");
		outputString.append( newline ).append( "\t## To Enable File Logging. Uncomment both lines below.");
		outputString.append( newline ).append( "\t##log file = config.log");
		outputString.append( newline ).append( "\t##append logs = yes");
		
		
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

	@Override
	public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren) {
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
	
	
	
	@Override
	public int getPriority() {
		return priority;
	}
	
	private class SettingsLineHandler implements ScriptLineHandler
	{
		MDScript $this = MDScript.this;
		private Pattern settingPattern = Pattern.compile("\\s*([^=]+?)\\s*=\\s*(.*?)\\s*");

		@Override
		public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren) {
			Matcher m = settingPattern.matcher(line.line);
			if (!m.matches()) {
				LogUtil.error($this, "Invalid setting: \"" + line.line + "\"");
				return null;
			}
			
			String name = m.group(1).trim().toLowerCase().replaceAll("\\s+", "-");
			String value = m.group(2).trim();
			
			LogUtil.info_verbose($this, "setting: '"+name+"' = '"+value+"'");
			
			if (name.equals("priority")) {
				try {
					MDScript.this.priority = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					MDScript.this.priority = -1;
				}
			}
			else {
				LogUtil.error($this, "Unknown setting: " + m.group(1));
			}
			
			return null;
		}

		@Override
		public void done()
		{
		}
	}

	@Override
	protected ScriptLineHandler getSubSettingLineHandler() {
		return new SettingsLineHandler();
	}

}
