package com.ModDamage.Events.Player;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class Login extends MDEvent implements Listener
{
	public Login() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Player.class,	"player",
			World.class,	"world",
			String.class,	"hostname",
			String.class,	"kickmessage",
			PlayerLoginEvent.Result.class, "result");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onJoin(PlayerLoginEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		Player player = event.getPlayer();
		EventData data = myInfo.makeData(
				player,
				player.getWorld(),
				event.getHostname(),
				event.getKickMessage(),
				event.getResult());
		
		runRoutines(data);
		
		event.setKickMessage(data.get(String.class, 3));
		event.setResult(data.get(PlayerLoginEvent.Result.class, 4));
	}
}
