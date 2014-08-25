package com.moddamage.conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.alias.ArmorAliaser;
import com.moddamage.backend.ArmorSet;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

public class PlayerWearing extends Conditional<Player>
{
	public static final Pattern pattern = Pattern.compile("\\.(?:is)?wearing(only)?\\.([\\w*]+)", Pattern.CASE_INSENSITIVE);
	
	private final boolean only;
	private final Collection<ArmorSet> armorSets;
	
	public PlayerWearing(IDataProvider<Player> playerDP, boolean only, Collection<ArmorSet> armorSets)
	{
		super(Player.class, playerDP);
		this.only = only;
		this.armorSets = armorSets;
	}
	@Override
	public Boolean get(Player player, EventData data)
	{
		ArmorSet playerSet = new ArmorSet(player);
		if(playerSet != null)
			for(ArmorSet armorSet : armorSets)
				if(only? armorSet.equals(playerSet) : armorSet.contains(playerSet))
					return true;
		return false;
	}
	
	@Override
	public String toString()
	{
		return startDP + ".wearing" + (only? "only":"") + "." + Utils.joinBy(",", armorSets);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Player.class, pattern, new IDataParser<Boolean, Player>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
				{
					Collection<ArmorSet> armorSet = ArmorAliaser.match(m.group(2));
					if(armorSet.isEmpty()) return null;
					
					return new PlayerWearing(playerDP, m.group(1) != null, armorSet);
				}
			});
	}
}
