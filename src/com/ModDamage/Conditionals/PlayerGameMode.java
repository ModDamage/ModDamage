package com.ModDamage.Conditionals;

import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.StringMatcher;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerGameMode extends Conditional<Player>
{
	public static final Pattern pattern = Pattern.compile("\\.gamemode\\.(\\w+)", Pattern.CASE_INSENSITIVE);

	protected final GameMode gameMode;

	public PlayerGameMode(IDataProvider<Player> playerDP, GameMode gameMode)
	{
		super(Player.class, playerDP);
		this.gameMode = gameMode;
	}

	@Override
	public Boolean get(Player player, EventData data)
	{
		return player.getGameMode() == gameMode;
	}
	
	@Override
	public String toString()
	{
		return startDP + ".gamemode." + gameMode;
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Player.class, pattern, new IDataParser<Boolean, Player>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
				{
                    try
                    {
                        GameMode gameMode = GameMode.valueOf(m.group(1).toUpperCase());

                        return new PlayerGameMode(playerDP, gameMode);
                    }
                    catch(IllegalArgumentException e)
                    {
                        return null;
                    }
				}
			});
	}
}
