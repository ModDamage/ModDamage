package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class PlayerHasPermission extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.haspermission\\.([\\w.]+)", Pattern.CASE_INSENSITIVE);
	
	private final DataRef<Player> playerRef;
	private final String permission;
	
	public PlayerHasPermission(String configString, DataRef<Player> entityRef, String permission)
	{
		super(configString);
		this.playerRef = entityRef;
		this.permission = permission;
	}
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
 	{
		Player player = playerRef.get(data);
		if (player == null) return false;
		return player.hasPermission(permission);
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public PlayerHasPermission getNew(Matcher matcher, EventInfo info)
		{
			DataRef<Player> playerRef = info.get(Player.class, matcher.group(1).toLowerCase());
			if(playerRef == null) return null;
			
			return new PlayerHasPermission(matcher.group(), playerRef, matcher.group(2));
		}
	}
}
