package com.ModDamage.External.TabAPI;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.mcsg.double0negative.tabapi.TabAPI;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Nested.NestedRoutine;

public class SetTabPriority extends NestedRoutine 
{
	private final IDataProvider<Player> playerDP;
	IDataProvider<Integer> priorityDP;
	
	private SetTabPriority(String configString, IDataProvider<Player> playerDP, IDataProvider<Integer> priorityDP)
	{
		super(configString);
		this.playerDP = playerDP;
		this.priorityDP = priorityDP;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
		Player player = playerDP.get(data);   if (player == null) return;
		
		Integer priority = priorityDP.get(data);   if (priority == null) return;
		
		TabAPI.setPriority(ModDamage.getPluginConfiguration().plugin, player, priority);
	}
	
	public static void registerNested()
	{
		NestedRoutine.registerRoutine(Pattern.compile("([^\\.]+)\\.settabpriority", Pattern.CASE_INSENSITIVE), new NestedRoutineBuilder());
	}
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@SuppressWarnings("unchecked")
		@Override
		public SetTabPriority getNew(Matcher m, Object nestedContent, EventInfo info)
		{
			IDataProvider<Player> playerDP = DataProvider.parse(info, Player.class, m.group(1));
			if(playerDP == null) return null;
			
			
			String str;
			if (nestedContent instanceof String)
				str = (String)nestedContent;
			else if(nestedContent instanceof List && ((List<String>) nestedContent).size() == 1)
				str = ((List<String>) nestedContent).get(0);
			else
				return null;

			IDataProvider<Integer> priorityDP = DataProvider.parse(info, Integer.class, str); if (priorityDP == null) return null;
			

			ModDamage.addToLogRecord(OutputPreset.INFO, "SetTabPriority: " + playerDP + ": " + priorityDP);
			
			return new SetTabPriority(m.group(), playerDP, priorityDP);
		}
	}
}