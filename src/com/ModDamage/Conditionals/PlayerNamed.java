package com.ModDamage.Conditionals;

import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerNamed extends Conditional<Player>
{
	public static final Pattern pattern = Pattern.compile("\\.named\\.([\\[\\]\\w,]+)", Pattern.CASE_INSENSITIVE);

    public static void register()
    {
        DataProvider.register(Boolean.class, Player.class, pattern, new IDataParser<Boolean, Player>()
        {
            @Override
            public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
            {
                String[] names = m.group(1).split(",");

                return new PlayerNamed(playerDP, names);
            }
        });
    }

	protected final String[] names;

	public PlayerNamed(IDataProvider<Player> playerDP, String[] names)
	{
		super(Player.class, playerDP);
		this.names = names;
	}

	@Override
	public Boolean get(Player player, EventData data)
	{
		String name = player.getName();
        for (String n : names) {
            if (name.equalsIgnoreCase(n))
                return true;
        }
        return false;
	}
	
	@Override
	public String toString()
	{
		return startDP + ".named." + Utils.joinBy(",", names);
	}
}
