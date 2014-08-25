package com.moddamage;

import com.moddamage.backend.ScriptLine;
import com.moddamage.MDLogger.OutputPreset;

import java.util.logging.Level;

// Helper utility for Log Files.
public class LogUtil {
	
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
