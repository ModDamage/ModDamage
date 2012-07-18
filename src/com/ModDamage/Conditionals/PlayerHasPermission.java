package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class PlayerHasPermission extends Conditional<Player>
{
	public static final Pattern pattern = Pattern.compile("\\.haspermission\\.([\\w.]+)", Pattern.CASE_INSENSITIVE);
	
	private final String permission;
	
	public PlayerHasPermission(IDataProvider<Player> playerDP, String permission)
	{
		super(Player.class, playerDP);
		this.permission = permission;
	}
	@Override
	public Boolean get(Player player, EventData data)
 	{
		return player.hasPermission(permission);
	}
	
	@Override
	public String toString()
	{
		return startDP + ".haspermission." + permission;
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Player.class, pattern, new IDataParser<Boolean, Player>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
				{
					return new PlayerHasPermission(playerDP, m.group(1));
				}
			});
	}
}
