package com.moddamage;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.moddamage.backend.ScriptLine;

public class MDLogger {
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

//	private BaseConfig config;
	private Plugin plugin;
	
	public Level worstLogMessageLevel = Level.INFO;
	
	protected DebugSetting currentSetting = DebugSetting.VERBOSE;
	
	private FileHandler filehandle = null;
	
	private int indentation = 0;
	
	public final Logger log;
	protected File logFile;
	
	public int logMessagesSoFar = 0;
	public int maxLogMessagesToShow = 50;
	
	private Formatter formatter;
	
	public MDLogger(final PluginConfiguration config)
	{
//		this.config = config;
		this.plugin = config.plugin;
		log = plugin.getLogger();
		formatter = new Formatter() {
			@Override
			public String format(LogRecord record) {
				StringBuilder b = new StringBuilder().append('[').append(config.name()).append("] [").append(String.format("%1$-10s", record.getLevel().toString())).append("] ");
				String name = plugin.getDescription().getPrefix();
				if (name == null)
					name = plugin.getName();
				
				String pat = "\\[" + name + "\\] ";
				b.append(String.format(record.getMessage().replaceFirst(pat, ""), record.getParameters())).append(PluginConfiguration.newline).toString(); //StringBuilder is much more effecient then string concat.
				return b.toString();
			}
		};
	}
	
	public void addToLogRecord(OutputPreset preset, ScriptLine line, String message)
	{
		addToLogRecord(preset, line.lineNumber + ": " + message);
	}
	
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
		if (worstLogMessageLevel == null || preset.level.intValue() > worstLogMessageLevel.intValue())
			worstLogMessageLevel = preset.level;

		if(getDebugSetting().shouldOutput(preset.debugSetting)) {
			if (getDebugSetting() != DebugSetting.QUIET || logMessagesSoFar < maxLogMessagesToShow) {
				String nestIndentation = "";
				for(int i = 0; i < indentation; i++)
					nestIndentation += "    ";
//				configStrings_console.add(nestIndentation + message);
//				configStrings_consoleFilters.add(preset);
				
				log.log(preset.level, nestIndentation + message);
			}
			logMessagesSoFar ++;
		}
	}
	
	public DebugSetting getDebugSetting(){ return currentSetting;}
	
	protected int getIndentation(){ return indentation;}
	
	public File getLogFile(){ return logFile; }
	
	public void changeIndentation(boolean inc)
	{
		if (inc)
			indentation++;
		else
			indentation--;
	}
	
	
	public String logPrepend(){ return "[" + plugin.getDescription().getName() + "] "; }
	
	public void setDebugSetting(DebugSetting level)
	{
		if (level != null)
			currentSetting = level;
	}
	
	public void setLogFile(File file){ setLogFile(file, false); }
	public void setLogFile(File file, boolean append){ setLogFile(((file != null)?file.getPath():null), append); }
	
	public FileHandler craftFileHandler(String path, boolean append)
	{
		FileHandler fh = null;
		try {
			fh = new FileHandler(path, append);
			fh.setFormatter(formatter);
			return fh;
		} catch (IOException e)	{
			return null;
		}
	}
	
	public void setLogFile(String path, boolean append)
	{
		if (filehandle != null)
		{
			filehandle.flush();
			filehandle.close();
			log.removeHandler(filehandle);
		
		}
		if (path != null)
		{	
			filehandle = craftFileHandler(path, append);
			if (filehandle != null)
				log.addHandler(filehandle);
		}
	}
	
	public void printToLog(Level level, String message){ log.log(level, "[" + plugin.getDescription().getName() + "] " + message); }
	
	public void resetLogCount(){
		logMessagesSoFar = 0;
	}
	
	public void resetWorstLogMessageLevel() {
		worstLogMessageLevel = Level.INFO;
	}
	
}
