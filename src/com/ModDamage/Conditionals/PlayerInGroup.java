package com.ModDamage.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Alias.GroupAliaser;
import com.ModDamage.Backend.ExternalPluginManager;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;

public class PlayerInGroup extends Conditional<Player>
{
	public static final Pattern pattern = Pattern.compile("\\.(?:in)?group\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	private final Collection<String> groups;
	
	public PlayerInGroup(IDataProvider<Player> playerDP, Collection<String> groups)
	{
		super(Player.class, playerDP);
		this.groups = groups;
	}
	@Override
	public Boolean get(Player player, EventData data)
	{
		for(String group : ExternalPluginManager.getGroupsManager().getGroups(player))
			if(groups.contains(group))
				return true;
		return false;
	}
	
	@Override
	public String toString()
	{
		return startDP + ".group." + Utils.joinBy(",", groups);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Player.class, pattern, new IDataParser<Boolean, Player>()
			{
				@Override
				public IDataProvider<Boolean> parse(ScriptLine scriptLine, EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
				{
					Collection<String> matchedGroups = GroupAliaser.match(scriptLine, m.group(1));
					if(!matchedGroups.isEmpty())
						return new PlayerInGroup(playerDP, matchedGroups);
					return null;
				}
			});
	}
}
