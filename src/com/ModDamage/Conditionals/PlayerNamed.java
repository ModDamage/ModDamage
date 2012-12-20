package com.ModDamage.Conditionals;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerNamed extends Conditional<Player>
{
	public static final Pattern pattern = Pattern.compile("\\.named\\.", Pattern.CASE_INSENSITIVE);
    public static final Pattern wordPattern = Pattern.compile("[\\[\\]\\w]+");

    public static void register()
    {
        DataProvider.register(Boolean.class, Player.class, pattern, new IDataParser<Boolean, Player>()
        {
            @Override
            public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
            {
                Collection<IDataProvider<String>> names = InterpolatedString.parseWordList(wordPattern, InterpolatedString.comma, sm, info);

                return new PlayerNamed(playerDP, names);
            }
        });
    }

	protected final Collection<IDataProvider<String>> names;

	public PlayerNamed(IDataProvider<Player> playerDP, Collection<IDataProvider<String>> names)
	{
		super(Player.class, playerDP);
		this.names = names;
	}

	@Override
	public Boolean get(Player player, EventData data) throws BailException
	{
		String name = player.getName();
        for (IDataProvider<String> n : names) {
            if (name.equalsIgnoreCase(n.get(data)))
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
