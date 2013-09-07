package com.ModDamage.External.Votifier;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.vexsoftware.votifier.model.VotifierEvent;

public class Vote extends MDEvent
{
	public Vote()
	{
		super(myInfo);
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(
			String.class, "address",
			String.class, "serviceName",
			String.class, "username",
			OfflinePlayer.class, "player",
			String.class, "timestamp"
			);
	
	@EventHandler(priority= EventPriority.HIGHEST)
	public void onVote(VotifierEvent event)
	{
		com.vexsoftware.votifier.model.Vote vote = event.getVote();

		if(!ModDamage.isEnabled || vote == null) return;
		
		EventData data = myInfo.makeData(
				vote.getAddress(),
				vote.getServiceName(),
				vote.getUsername(),
				Bukkit.getServer().getOfflinePlayer(vote.getUsername()),
				vote.getTimeStamp()
				);
		
		if (data != null)
		{
			runRoutines(data);
			
			vote.setAddress(data.get(String.class, data.start));
			vote.setServiceName(data.get(String.class, data.start + 1));
			vote.setUsername(data.get(String.class, data.start + 2));
			vote.setTimeStamp(data.get(String.class, data.start + 3));
		}
	}
	
}
