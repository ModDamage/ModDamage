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
import com.ModDamage.Alias.AliasManager;
import com.ModDamage.Alias.MessageAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Parsing.IDataProvider;

public class LogMessage extends NestedRoutine 
{
	public static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private final IDataProvider<String> logNameDP;
	private final Collection<InterpolatedString> messages;
	
	private LogMessage(String configString, IDataProvider<String> logNameDP, Collection<InterpolatedString> messages)
	{
		super(configString);
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
		
		for(InterpolatedString message :  messages) {
			pw.write("[" + formatter.format(new Date()) + "] " + message.toString(data) + "\n");
		}
		
		pw.close();
	}
	
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("log\\.(.+)", Pattern.CASE_INSENSITIVE), new NestedRoutineBuilder());
	}
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@SuppressWarnings("unchecked")
		@Override
		public LogMessage getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			if(matcher == null || nestedContent == null)
				return null;

			StringMatcher sm = new StringMatcher(matcher.group(1));
			IDataProvider<String> logNameDP = InterpolatedString.parseWord(InterpolatedString.word, sm, info);
			if (logNameDP == null) return null;
			
			List<String> strings = new ArrayList<String>();
			
			if (nestedContent instanceof String)
				strings.add((String)nestedContent);
			else if(nestedContent instanceof List)
				strings.addAll((List<String>) nestedContent);
			else
				return null;
			

			List<InterpolatedString> messages = new ArrayList<InterpolatedString>();
			for(String string : strings)
			{
				if (AliasManager.aliasPattern.matcher(string).matches())
				{
					Collection<InterpolatedString> istrs = MessageAliaser.match(string, info);
					if (istrs != null) 
					{
						messages.addAll(istrs);
						continue;
					}
					
					ModDamage.addToLogRecord(OutputPreset.WARNING, "Unknown message alias: "+string);
				}
				
				messages.add(new InterpolatedString(string, info, true));
			}
			
			
			LogMessage routine = new LogMessage(matcher.group(), logNameDP, messages);
			routine.reportContents();
			return routine;
		}
	}
	
	private void reportContents()
	{
		if(messages instanceof List)
		{
			String routineString = "Log (" + logNameDP + ")";
			List<InterpolatedString> messageList = (List<InterpolatedString>)messages;
			if(messages.size() > 1)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, routineString + ":" );
				ModDamage.changeIndentation(true);
				for(int i = 0; i < messages.size(); i++)
					ModDamage.addToLogRecord(OutputPreset.INFO, "- \"" + messageList.get(i).toString() + "\"" );
				ModDamage.changeIndentation(false);
			}
			else ModDamage.addToLogRecord(OutputPreset.INFO, routineString + ": \"" + messageList.get(0).toString() + "\"" );
		}
		else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Fatal: messages are not in a linked data structure!");//shouldn't happen
	}
}