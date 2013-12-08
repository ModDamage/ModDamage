package com.ModDamage;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.IDataProvider;

public final class Scoreboards
{
	public static final IDataProvider<Scoreboard> mainScoreboard = new IDataProvider<Scoreboard>() {
			public Class<? extends Scoreboard> provides() {  return Scoreboard.class;  }
			public Scoreboard get(EventData data) {  return Bukkit.getScoreboardManager().getMainScoreboard();  }
		};
		
	public static IDataProvider<Scoreboard> getCurrent(EventInfo info)
	{
		IDataProvider<Scoreboard> sb = info.get(Scoreboard.class, "scoreboard", false);
		if (sb == null) return mainScoreboard;
		return sb;
	}

	

	public static final Map<String, Scoreboard> map = new HashMap<String, Scoreboard>();
	
	public static Scoreboard getNamed(String name)
	{
        Scoreboard sb = map.get(name);
        
        if (sb == null) {
        	sb = Bukkit.getScoreboardManager().getNewScoreboard();
        	map.put(name, sb);
        }
        
        return sb;
	}
}
