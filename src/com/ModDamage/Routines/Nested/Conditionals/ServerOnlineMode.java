package com.ModDamage.Routines.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class ServerOnlineMode extends Conditional
{
	public static final Pattern pattern = Pattern.compile("server\\.onlineMode", Pattern.CASE_INSENSITIVE);
	protected ServerOnlineMode(String configString)
	{
		super(configString);
	}
	
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		return Bukkit.getOnlineMode();
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public ServerOnlineMode getNew(Matcher matcher, EventInfo info)
		{
			return new ServerOnlineMode(matcher.group());
		}
	}
}
