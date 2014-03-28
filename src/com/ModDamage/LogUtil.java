package com.ModDamage;

import java.util.logging.Level;

import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.MDLogger.OutputPreset;

// Helper utility for Log Files.
public class LogUtil {

	//Local Log Helpers
	public static void info(BaseConfig config, ScriptLine line, String message) { ModDamage.addToLogRecord(config, OutputPreset.INFO, line, message); }
	public static void info_verbose(BaseConfig config, ScriptLine line, String message) { ModDamage.addToLogRecord(config, OutputPreset.INFO_VERBOSE, line, message); }
	public static void warning(BaseConfig config, ScriptLine line, String message) { ModDamage.addToLogRecord(config, OutputPreset.WARNING, line, message); }
	public static void warning_strong(BaseConfig config, ScriptLine line, String message) { ModDamage.addToLogRecord(config, OutputPreset.WARNING_STRONG, line, message); }
	public static void error(BaseConfig config, ScriptLine line, String message) { ModDamage.addToLogRecord(config, OutputPreset.FAILURE, line, message); }
	public static void constant(BaseConfig config, ScriptLine line, String message) { ModDamage.addToLogRecord(config, OutputPreset.CONSTANT, line, message); }
	public static void console_only(BaseConfig config, ScriptLine line, String message) { ModDamage.addToLogRecord(config, OutputPreset.CONSOLE_ONLY, line, message); }
	
	public static void info(BaseConfig config, String message) { ModDamage.addToLogRecord(config, OutputPreset.INFO, message);	}
	public static void info_verbose(BaseConfig config, String message) { ModDamage.addToLogRecord(config, OutputPreset.INFO_VERBOSE, message); }
	public static void warning(BaseConfig config, String message) { ModDamage.addToLogRecord(config, OutputPreset.WARNING, message); }
	public static void warning_strong(BaseConfig config, String message) { ModDamage.addToLogRecord(config, OutputPreset.WARNING_STRONG, message); }
	public static void error(BaseConfig config, String message) { ModDamage.addToLogRecord(config, OutputPreset.FAILURE, message); }
	public static void constant(BaseConfig config, String message) { ModDamage.addToLogRecord(config, OutputPreset.CONSTANT, ""); }
	public static void console_only(BaseConfig config, String message) { ModDamage.addToLogRecord(config, OutputPreset.CONSOLE_ONLY, ""); }
	public static void printToLog(BaseConfig config, Level level, String message) { ModDamage.printToLog(config, level, message); }
	
	//Global Log Helpers
	public static void info(ScriptLine line, String message) { ModDamage.addToLogRecord(OutputPreset.INFO, line, message); }
	public static void info_verbose(ScriptLine line, String message) { ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, line, message); }
	public static void warning(ScriptLine line, String message) { ModDamage.addToLogRecord(OutputPreset.WARNING, line, message); }
	public static void warning_strong(ScriptLine line, String message) { ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, line, message); }
	public static void error(ScriptLine line, String message) { ModDamage.addToLogRecord(OutputPreset.FAILURE, line, message); }
	public static void constant(ScriptLine line, String message) { ModDamage.addToLogRecord(OutputPreset.CONSTANT, line, message); }
	public static void console_only(ScriptLine line, String message) { ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, line, message); }
	
	public static void info(String message) { ModDamage.addToLogRecord(OutputPreset.INFO, message);	}
	public static void info_verbose(String message) { ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, message); }
	public static void warning(String message) { ModDamage.addToLogRecord(OutputPreset.WARNING, message); }
	public static void warning_strong(String message) { ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, message); }
	public static void error(String message) { ModDamage.addToLogRecord(OutputPreset.FAILURE, message); }
	public static void constant(String message) { ModDamage.addToLogRecord(OutputPreset.CONSTANT, ""); }
	public static void console_only(String message) { ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, ""); }
	public static void printToLog(Level level, String message) { ModDamage.printToLog(level, message); }
}
