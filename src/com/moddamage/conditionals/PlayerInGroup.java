package com.moddamage.conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.alias.GroupAliaser;
import com.moddamage.backend.ExternalPluginManager;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

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
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
				{
					Collection<String> matchedGroups = GroupAliaser.match(m.group(1));
					if(!matchedGroups.isEmpty())
						return new PlayerInGroup(playerDP, matchedGroups);
					return null;
				}
			});
	}
}
