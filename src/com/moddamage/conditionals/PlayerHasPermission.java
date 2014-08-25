package com.moddamage.conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.backend.BailException;
import com.moddamage.expressions.InterpolatedString;
import org.bukkit.entity.Player;

import com.moddamage.StringMatcher;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

public class PlayerHasPermission extends Conditional<Player>
{
	public static final Pattern pattern = Pattern.compile("\\.haspermission\\.", Pattern.CASE_INSENSITIVE);
	
	private final IDataProvider<String> permissionDP;
	
	public PlayerHasPermission(IDataProvider<Player> playerDP, IDataProvider<String> permissionDP)
	{
		super(Player.class, playerDP);
		this.permissionDP = permissionDP;
	}
	@Override
	public Boolean get(Player player, EventData data) throws BailException
 	{
		return player.hasPermission(permissionDP.get(data));
	}
	
	@Override
	public String toString()
	{
		return startDP + ".haspermission." + permissionDP;
	}

    static final Pattern wordPattern = Pattern.compile("[\\w.]+");
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Player.class, pattern, new IDataParser<Boolean, Player>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
				{
                    IDataProvider<String> permissionDP = InterpolatedString.parseWord(wordPattern, sm.spawn(), info);
                    if (permissionDP == null) return null;

                    sm.accept();
					return new PlayerHasPermission(playerDP, permissionDP);
				}
			});
	}
}
