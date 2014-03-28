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
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.ModDamage.MDLogger.DebugSetting;
import com.ModDamage.MDLogger.OutputPreset;
import com.ModDamage.Alias.AliasManager;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;
import com.ModDamage.Backend.ScriptParser;

/**
 * Note: this class has a natural ordering that may be inconsistent with equals.
 */
public abstract class BaseConfig implements ScriptLineHandler, Comparable<BaseConfig>{
	public enum LoadState
	{
		FAILURE(ChatColor.RED + "FAIL"), NOT_LOADED(ChatColor.GRAY + "NO  "), SUCCESS(ChatColor.GREEN + "YES ");

		public static LoadState combineStates(Collection<LoadState> loadStates)
		{
			return loadStates.contains(FAILURE) ? LoadState.FAILURE : (loadStates.contains(SUCCESS) ? LoadState.SUCCESS : LoadState.NOT_LOADED);
		}

		public static LoadState combineStates(LoadState... states)
		{
			return combineStates(Arrays.asList(states));
		}

		private String string;

		private LoadState(String string){ this.string = string; }

		public String statusString(){ return string; }
	}
	
	private class SettingsLineHandler implements ScriptLineHandler
	{
		BaseConfig $this = BaseConfig.this;
		private Pattern settingPattern = Pattern.compile("\\s*([^=]+?)\\s*=\\s*(.*?)\\s*");

		@Override
		public void done()
		{
		}

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
			
			if (name.equals("debugging")) {
				try {
					log.currentSetting = DebugSetting.valueOf(value.toUpperCase());
				}
				catch (IllegalArgumentException e) {
					LogUtil.error($this, "Bad debug level: " + value);
				}
			}
			else if (name.equals("log-file")) {
				logFile = new File(plugin.getDataFolder(), value);
			}
			else if (name.equals("append-logs")) {
				appendLog = Boolean.parseBoolean(value);
			}
			else if (name.equals("enabled"))
				setEnabled(Boolean.parseBoolean(value));
			else {
				return getSubSettingLineHandler();
			}
			
			return null;
		}
	}
	
	public static final String newline = System.getProperty("line.separator");
	protected static final String TM_EXT_PL_MAN = "External Plugin Manager";
	protected static final String TM_MAINLOAD = "Entire Reload";
	
	protected static final String TM_MDEvent = "ModDamage Event";

	protected static final String TM_SCRIPTLOAD = "Script Loading";
	private static boolean replaceOrAppendInFile(File file, String targetRegex,
			String replaceString) {
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
						contents.append(replaceString ).append( newline);
			
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
	boolean appendLog;
	protected final File configFile;
	protected final String configPath;
	
	private boolean isEnabled;
	
	protected MDLogger log;
	
	File logFile;
	private final String name;
	
	public final Plugin plugin;
	protected LoadState loadState;
	
	public BaseConfig(Plugin plugin, String name, File configFile) {
		this.plugin = plugin;
		this.name = name;
		this.configFile = configFile;
		configPath = configFile.getPath().substring(plugin.getDataFolder().getPath().length());
		log = new MDLogger(this);
	}
	
	public void addToLogRecord(OutputPreset preset, ScriptLine line, String message) { log.addToLogRecord(preset, line, message); }

	public void addToLogRecord(OutputPreset preset, String message) { log.addToLogRecord(preset, message); }

	public void changeIndentation(boolean forward) { log.changeIndentation(forward); }
	
	@Override
	public int compareTo(BaseConfig other) {
		return sgn(other.getPriority() - getPriority());
	}
	
	@Override
	public void done() {
	}
	

	protected String getConfigPath() {
		return configPath;
	}
	public DebugSetting getDebugSetting() { return log.getDebugSetting(); }
	
	protected abstract String getDefaultContents();
	
	public MDLogger getLog() { return log; }

	public String getName() {
		return name;
	}
	public abstract int getPriority();

	protected abstract ScriptLineHandler getSubSettingLineHandler();
	
	public Level getWorstLogMessageLevel() {
		return log.worstLogMessageLevel;
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
						LogUtil.warning(this, line, word0 + " " + words[1] + "is not valid. Possible that the event is not loaded!");
						return null;
					}
					
					return e.getLineHandler(this);
				}
				return null; //This is a bit redundant but there just in case we add more.
			}
		}
		return null;
	}

	public boolean isEnabled() {
		switch (ModDamage.getPluginConfiguration().getLoadMethod()) {
			case MASTER_LIST:
				return true;
			case PRIORITY_PARSE:
				return getPriority() < 0;
			case ENABLED_SETTING:
				default:
				return isEnabled;
		}
	}

	public void printToLog(Level level, String message) { log.printToLog(level, message); }
	
	public abstract boolean reload(boolean reloadAll);

	/**
	 * NOTE: <b> Be sure to add super.resetDefaultSettings() on overriding.</b>
	 */
	protected void resetDefaultSettings() {
		log.currentSetting = DebugSetting.NORMAL;
		logFile = null;
		log.setLogFile(logFile);
	}

	public void resetLoggedMessages() { log.resetLogCount(); }

	public void resetWorstLogMessageLevel() { log.resetWorstLogMessageLevel(); }

	public void setDebugging(Player player, DebugSetting setting) {
		if(setting != null)
		{
			if(!getDebugSetting().equals(setting))
			{
				if(replaceOrAppendInFile(configFile, "debugging:.*", "debugging: " + setting.name().toLowerCase()))
				{
					ModDamage.sendMessage(player, "Changed debug from " + getDebugSetting().name().toLowerCase() + " to " + setting.name().toLowerCase(), ChatColor.GREEN);
					log.setDebugSetting(setting);
				}
				else if(player != null)
					player.sendMessage(ModDamage.chatPrepend(ChatColor.RED) + "Couldn't save changes to " + getName() + ".mdscript"  + ".");
			}
			else ModDamage.sendMessage(player, "Debug already set to " + setting.name().toLowerCase() + "!", ChatColor.RED);
		}
		else printToLog(Level.SEVERE, "Error: bad debug setting sent. Valid settings: normal, quiet, verbose");// shouldn't																								// happen
	}
	
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	private int sgn(int v) {
		if (v == 0)
			return v;
		else if (v > 0)
			return 1;
		else
			return -1;
	}

	public void toggleDebugging(Player player) {
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
		default:
			break;
		}
	}

	// Helper Methods
	protected boolean writeDefaults() {
		addToLogRecord(OutputPreset.INFO, log.logPrepend() + "No configuration file found! Writing a blank config in " + getConfigPath() + "...");
		if(!configFile.exists())
		{
			try
			{
				if(!(configFile.getParentFile().exists() || configFile.getParentFile().mkdirs()) || !configFile.createNewFile())
				{
					printToLog(Level.SEVERE, "Fatal error: could not create " + getConfigPath() + ".");
					return false;
				}
			}
			catch (IOException e)
			{
				printToLog(Level.SEVERE, "Error: could not create new " + getConfigPath() + ".");
				e.printStackTrace();
				return false;
			}
		}
		String contents = getDefaultContents();
		if (contents != null)
			return false;
		
		try {
			Writer writer = new FileWriter(configFile);
			writer.write(getDefaultContents());
			writer.close();

			FileInputStream stream = new FileInputStream(configFile);
			ScriptParser parser = new ScriptParser(this, stream);
			parser.parseScript(this);
			stream.close();
		} 
		catch (IOException e) 
		{
			printToLog(Level.SEVERE, "Error writing to " + getConfigPath() + ".");
		}
		return true;
	}

	public LoadState getLoadState() {
		return loadState;
	}
}
