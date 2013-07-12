package com.ModDamage.Expressions.Function;

import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.FunctionParser;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.SettableDataProvider;

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
				protected IDataProvider<Boolean> makeProvider(IDataProvider<Player> worldDP, @SuppressWarnings("rawtypes") IDataProvider[] arguments)
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
