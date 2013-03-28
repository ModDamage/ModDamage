package com.ModDamage.External.TabAPI;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.mcsg.double0negative.tabapi.TabAPI;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Routines.Nested.NestedRoutine;

public class SetTabString extends NestedRoutine 
{
	private final IDataProvider<Player> playerDP;
	IDataProvider<Integer> xDP, yDP, pingDP;
	private final InterpolatedString string;
	
	private SetTabString(String configString, IDataProvider<Player> playerDP, IDataProvider<Integer> xDP, IDataProvider<Integer> yDP, IDataProvider<Integer> pingDP, InterpolatedString string)
	{
		super(configString);
		this.playerDP = playerDP;
		this.xDP = xDP;
		this.yDP = yDP;
		this.pingDP = pingDP;
		this.string = string;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
		Player player = playerDP.get(data);   if (player == null) return;
		
		Integer x = xDP.get(data);   if (x == null) return;
		Integer y = yDP.get(data);   if (y == null) return;
		Integer ping = null;
		if (pingDP != null) {
			ping = pingDP.get(data);   if (ping == null) return;
		}
		
		String str = string.toString(data); if (str == null) return;
		
		if (ping == null)
			TabAPI.setTabString(ModDamage.getPluginConfiguration().plugin, player, x, y, str);
		else
			TabAPI.setTabString(ModDamage.getPluginConfiguration().plugin, player, x, y, str, ping);
	}
	
	public static void registerNested()
	{
		NestedRoutine.registerRoutine(Pattern.compile("([^\\.]+)\\.settabstring\\.(.+)", Pattern.CASE_INSENSITIVE), new NestedRoutineBuilder());
	}
	
	private static Pattern seperatorPattern = Pattern.compile("\\s*[\\.,]\\s*");
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@SuppressWarnings("unchecked")
		@Override
		public SetTabString getNew(Matcher m, Object nestedContent, EventInfo info)
		{
			IDataProvider<Player> playerDP = DataProvider.parse(info, Player.class, m.group(1));
			if(playerDP == null) return null;
			

			StringMatcher sm = new StringMatcher(m.group(2));
			
			IDataProvider<Integer> xDP = DataProvider.parse(info, Integer.class, sm.spawn()); if (xDP == null) return null;
			if (!sm.matchesFront(seperatorPattern)) return null;
			IDataProvider<Integer> yDP = DataProvider.parse(info, Integer.class, sm.spawn()); if (yDP == null) return null;
			
			IDataProvider<Integer> pingDP = null;
			if (!sm.isEmpty()) {
				if (!sm.matchesFront(seperatorPattern)) return null;
				pingDP = DataProvider.parse(info, Integer.class, sm.spawn()); if (pingDP == null) return null;
				
				if (!sm.isEmpty()) return null;
			}

			String str;
			if (nestedContent instanceof String)
				str = (String)nestedContent;
			else if(nestedContent instanceof List && ((List<String>) nestedContent).size() == 1)
				str = ((List<String>) nestedContent).get(0);
			else
				return null;
			
			InterpolatedString istr = new InterpolatedString(str, info, true);
			

			ModDamage.addToLogRecord(OutputPreset.INFO, "SetTabString: " + playerDP + " " + xDP + ", " + yDP + (pingDP == null? "" : (", " + pingDP)) + ": " + istr);
			
			return new SetTabString(m.group(), playerDP, xDP, yDP, pingDP, istr);
		}
	}
}