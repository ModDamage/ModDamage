package com.ModDamage.Routines.Nested;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routine;

public class LogMessage extends NestedRoutine 
{
	public static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private final IDataProvider<String> logNameDP;
	private final Collection<IDataProvider<String>> messages;
	
	private LogMessage(ScriptLine scriptLine, IDataProvider<String> logNameDP, Collection<IDataProvider<String>> messages)
	{
		super(scriptLine);
		this.logNameDP = logNameDP;
		this.messages = messages;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
		String logName = logNameDP.get(data);
		if (logName == null) return;
		
		File logDir = new File(ModDamage.getPluginConfiguration().plugin.getDataFolder(), "logs");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		
		File log = new File(logDir, logName+".txt");
		
		OutputStream out;
		try
		{
			out = new FileOutputStream(log, true);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return;
		}
		
		PrintWriter pw = new PrintWriter(out);
		
		for(IDataProvider<String> message :  messages) {
			pw.write("[" + formatter.format(new Date()) + "] " + message.get(data) + "\n");
		}
		
		pw.close();
	}
	
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("log\\.(.+)(?::?\\s+(.+))?", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}
	
	protected static class RoutineFactory extends NestedRoutine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			StringMatcher sm = new StringMatcher(matcher.group(1));
			IDataProvider<String> logNameDP = InterpolatedString.parseWord(InterpolatedString.word, sm, info);
			if (logNameDP == null) return null;

			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Log (" + logNameDP + "):" );
			ModDamage.changeIndentation(true);
			
			MessageRoutineBuilder builder = new MessageRoutineBuilder(scriptLine, logNameDP, info);
			
			if (matcher.group(2) != null)
				builder.addString(matcher.group(2));
			
			return builder;
		}
	}
	

	
	private static class MessageRoutineBuilder implements IRoutineBuilder, ScriptLineHandler
	{
		ScriptLine scriptLine;
		IDataProvider<String> logNameDP;
		EventInfo info;
		
		List<IDataProvider<String>> messages = new ArrayList<IDataProvider<String>>();
		
		public MessageRoutineBuilder(ScriptLine scriptLine, IDataProvider<String> logNameDP, EventInfo info)
		{
			this.scriptLine = scriptLine;
			this.logNameDP = logNameDP;
			this.info = info;
		}
		
		public void addString(String str)
		{
			IDataProvider<String> msgDP = DataProvider.parse(info, String.class, str);
			if (msgDP != null) {
				messages.add(msgDP);
				ModDamage.addToLogRecord(OutputPreset.INFO, msgDP.toString());
			}
		}

		@Override
		public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren)
		{
			addString(line.line);
			return null;
		}
		
		@Override
		public void done()
		{
			ModDamage.changeIndentation(false);
		}
		
		@Override
		public ScriptLineHandler getScriptLineHandler()
		{
			return this;
		}
		
		@Override
		public Routine buildRoutine()
		{
			return new LogMessage(scriptLine, logNameDP, messages);
		}
	}
}