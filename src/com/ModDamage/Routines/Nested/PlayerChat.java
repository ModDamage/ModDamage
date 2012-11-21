package com.ModDamage.Routines.Nested;

import com.ModDamage.Alias.AliasManager;
import com.ModDamage.Alias.CommandAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Expressions.StringExp;
import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerChat extends NestedRoutine
{
	private final List<IDataProvider<String>> messages;
	private final IDataProvider<Player> playerDP;

	private PlayerChat(String configString, IDataProvider<Player> playerDP, List<IDataProvider<String>> messages)
	{
		super(configString);
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

	public static void registerNested()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*)\\.chat", Pattern.CASE_INSENSITIVE), new NestedRoutineBuilder());
	}

	protected static class NestedRoutineBuilder extends RoutineBuilder
	{
		@SuppressWarnings("unchecked")
		@Override
		public PlayerChat getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			if(matcher == null || nestedContent == null)
				return null;

			IDataProvider<Player> playerDP = DataProvider.parse(info, Player.class, matcher.group(1));
			if(playerDP == null) return null;

			List<IDataProvider<String>> messages = StringExp.getStrings(nestedContent, info);
			if (messages == null) return null;


			PlayerChat routine = new PlayerChat(matcher.group(), playerDP, messages);
			routine.reportContents();
			return routine;
		}
	}

	private void reportContents()
	{
		if(messages instanceof List)
		{
			String routineString = "Chat (" + playerDP + ")";
			if(messages.size() > 1)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, routineString + ":" );
				ModDamage.changeIndentation(true);
				for(IDataProvider<String> message : messages)
					ModDamage.addToLogRecord(OutputPreset.INFO, "- \"" + message.toString() + "\"" );
				ModDamage.changeIndentation(false);
			}
			else ModDamage.addToLogRecord(OutputPreset.INFO, routineString + ": \"" + messages.get(0).toString() + "\"" );
		}
		else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Fatal: messages are not in a linked data structure!");//shouldn't happen
	}
}