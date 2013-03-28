package com.ModDamage.External.TabAPI;

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
import com.ModDamage.Routines.Routine;
import com.ModDamage.Routines.Nested.NestedRoutine;

public class UpdateTab extends NestedRoutine 
{
	private final IDataProvider<Player> playerDP;
	
	private UpdateTab(String configString, IDataProvider<Player> playerDP)
	{
		super(configString);
		this.playerDP = playerDP;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
		Player player = playerDP.get(data);   if (player == null) return;
		
		TabAPI.updatePlayer(player);
	}
	
	public static void registerRoutine()
	{
		Routine.registerRoutine(Pattern.compile("([^\\.]+)\\.updatetab", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public UpdateTab getNew(Matcher m, EventInfo info)
		{
			IDataProvider<Player> playerDP = DataProvider.parse(info, Player.class, m.group(1));
			if(playerDP == null) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "UpdateTab: " + playerDP);
			
			return new UpdateTab(m.group(), playerDP);
		}
	}
}