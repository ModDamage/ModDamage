package com.ModDamage.External.TabAPI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.mcsg.double0negative.tabapi.TabAPI;

import com.ModDamage.LogUtil;
import com.ModDamage.ModDamage;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routine;
import com.ModDamage.Routines.Nested.NestedRoutine;

public class SetTabPriority extends Routine 
{
	private final IDataProvider<Player> playerDP;
	IDataProvider<Integer> priorityDP;
	
	private SetTabPriority(ScriptLine scriptLine, IDataProvider<Player> playerDP, IDataProvider<Integer> priorityDP)
	{
		super(scriptLine);
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
		NestedRoutine.registerRoutine(Pattern.compile("(.+?)\\.settabpriority: (.+)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}
	
	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher m, ScriptLine scriptLine, EventInfo info)
		{
			IDataProvider<Player> playerDP = DataProvider.parse(info, Player.class, m.group(1));
			if(playerDP == null) return null;
			
			
			IDataProvider<Integer> priorityDP = DataProvider.parse(info, Integer.class, m.group(2)); if (priorityDP == null) return null;
			

			LogUtil.info("SetTabPriority: " + playerDP + ": " + priorityDP);
			
			return new RoutineBuilder(new SetTabPriority(scriptLine, playerDP, priorityDP));
		}
	}
}