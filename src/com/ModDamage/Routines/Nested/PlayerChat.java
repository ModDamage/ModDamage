package com.ModDamage.Routines.Nested;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.LogUtil;
import com.ModDamage.ModDamage;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.LiteralString;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routine;

public class PlayerChat extends Routine
{
	private final List<IDataProvider<String>> messages;
	private final IDataProvider<Player> playerDP;

	private PlayerChat(ScriptLine scriptLine, IDataProvider<Player> playerDP, List<IDataProvider<String>> messages)
	{
		super(scriptLine);
		this.playerDP = playerDP;
		this.messages = messages;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Player player = playerDP.get(data);
		if (player == null) return;
		for(IDataProvider<String> message : messages)
		{
			player.chat(message.get(data));
		}
	}

	public static final Pattern targetEndPattern = Pattern.compile("\\.(_\\w+)|:?\\s+|(?:$)");
	
	public static void registerNested()
	{
		Routine.registerRoutine(Pattern.compile("(.*?)\\.chat(?::?\\s+(.+))?", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}

	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			IDataProvider<Player> playerDP = DataProvider.parse(info, Player.class, matcher.group(1));
			if(playerDP == null) return null;


			LogUtil.info("Chat (" + playerDP + "):" );
			ModDamage.changeIndentation(true);
			
			ChatRoutineBuilder builder = new ChatRoutineBuilder(scriptLine, playerDP, info);
			
			if (matcher.group(2) != null)
				builder.addString(matcher.group(2));
			
			return builder;
		}
	}
	
	private static class ChatRoutineBuilder implements IRoutineBuilder, ScriptLineHandler
	{
		ScriptLine scriptLine;
		IDataProvider<Player> playerDP;
		EventInfo info;
		
		List<IDataProvider<String>> messages = new ArrayList<IDataProvider<String>>();
		
		public ChatRoutineBuilder(ScriptLine scriptLine, IDataProvider<Player> playerDP, EventInfo info)
		{
			this.scriptLine = scriptLine;
			this.playerDP = playerDP;
			this.info = info;
		}
		
		public void addString(String str)
		{
			IDataProvider<String> msgDP = DataProvider.parse(info, String.class, str);
			if (msgDP != null) {
				if (msgDP instanceof LiteralString) {
					((LiteralString) msgDP).colorize();
				}
				messages.add(msgDP);
				LogUtil.info(msgDP.toString());
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
			return new PlayerChat(scriptLine, playerDP, messages);
		}
	}
}