package com.moddamage.expressions.function;

import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.FunctionParser;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.parsing.SettableDataProvider;

public class PlayerCanSeeFunction extends SettableDataProvider<Boolean, Player>
{
	private final IDataProvider<Player> otherPlayerDP;

	private PlayerCanSeeFunction(IDataProvider<Player> playerDP, IDataProvider<Player> otherPlayerDP)
	{
		super(Player.class, playerDP);
		this.otherPlayerDP = otherPlayerDP;
	}

	@Override
	public Boolean get(Player player, EventData data) throws BailException
	{
		Player otherPlayer = otherPlayerDP.get(data);
		if (otherPlayer == null) return null;
		
		return player.canSee(otherPlayer);
	}
	
	@Override
	public void set(Player player, EventData data, Boolean value) throws BailException
	{
		if (value == null) return;
		
		Player otherPlayer = otherPlayerDP.get(data);
		if (otherPlayer == null) return;
		
		if (value.booleanValue() == true)
			player.showPlayer(otherPlayer);
		else
			player.hidePlayer(otherPlayer);
	}

	@Override
	public boolean isSettable()
	{
		return true;
	}


	@Override
	public Class<Boolean> provides() { return Boolean.class; }

	public static void register()
	{
		DataProvider.register(Boolean.class, Player.class, Pattern.compile("_cansee"), new FunctionParser<Boolean, Player>(Player.class)
			{
				@SuppressWarnings("unchecked")
				@Override
				protected IDataProvider<Boolean> makeProvider(EventInfo info, IDataProvider<Player> worldDP, @SuppressWarnings("rawtypes") IDataProvider[] arguments)
				{
					return new PlayerCanSeeFunction(worldDP, arguments[0]);
				}
			});
	}

	@Override
	public String toString()
	{
		return startDP + "_cansee(" + otherPlayerDP + ")";
	}
}
