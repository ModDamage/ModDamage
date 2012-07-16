package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class PlayerStatus extends Conditional<Player>
{
	public static final Pattern pattern = Pattern.compile("\\.is("+ Utils.joinBy("|", StatusType.values()) +")", Pattern.CASE_INSENSITIVE);
	
	private enum StatusType
	{
		/*Blocking
		{
			@Override
			public boolean isTrue(Entity entity){ return ((Player)entity).is() <= 0; }
		},FIXME Get this into Bukkit if not already present. */
		Sleeping
		{
			@Override
			public boolean isTrue(Player player){ return player.isSleeping(); }
		},
		Sneaking
		{
			@Override
			public boolean isTrue(Player player){ return player.isSneaking(); }
		},
		Sprinting
		{
			@Override
			public boolean isTrue(Player player){ return player.isSprinting(); }
		};
		
		abstract public boolean isTrue(Player player);
	}

	private final StatusType statusType;
	
	protected PlayerStatus(IDataProvider<?> playerDP, StatusType statusType)
	{
		super(Player.class, playerDP);
		this.statusType = statusType;
	}

	@Override
	public Boolean get(Player player, EventData data)
	{
		return statusType.isTrue(player);
	}
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Entity.class, pattern, new IDataParser<Boolean>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<?> entityDP, Matcher m, StringMatcher sm)
				{
					StatusType statusType = null;
					for(StatusType type : StatusType.values())
						if(m.group(1).equalsIgnoreCase(type.name()))
								statusType = type;
					if(statusType == null) return null;
					
					return new PlayerStatus(entityDP, statusType);
				}
			});
	}
}
