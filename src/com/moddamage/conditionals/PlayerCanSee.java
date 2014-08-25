package com.moddamage.conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

public class PlayerCanSee extends Conditional<Player>
{
	public static final Pattern pattern = Pattern.compile("\\.canSee\\.", Pattern.CASE_INSENSITIVE);
	
	protected final IDataProvider<Player> otherDP;

	public PlayerCanSee(IDataProvider<Player> playerDP, IDataProvider<Player> otherDP)
	{
		super(Player.class, playerDP);
		this.otherDP = otherDP;
	}

	@Override
	public Boolean get(Player player, EventData data) throws BailException
	{
		Player other = otherDP.get(data);
		if (other == null) return null;
		
        return player.canSee(other);
	}
	
	@Override
	public String toString()
	{
		return startDP + ".canSee." + otherDP;
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Player.class, pattern, new IDataParser<Boolean, Player>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
				{
                    IDataProvider<Player> otherDP = DataProvider.parse(info, Player.class, sm.spawn());
                    if (otherDP == null) return null;

                    return sm.acceptIf(new PlayerCanSee(playerDP, otherDP));
				}
			});
	}
}
