package com.ModDamage.Properties;

import org.bukkit.OfflinePlayer;

import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.Property.Properties;

public class OfflinePlayerProps
{
	public static void register()
	{
		Properties.register("isOnline", OfflinePlayer.class, "isOnline");
		Properties.register("isBanned", OfflinePlayer.class, "isBanned", "setBanned");
		Properties.register("isWhitelisted", OfflinePlayer.class, "isWhitelisted", "setWhitelisted");
		Properties.register("isOp", OfflinePlayer.class, "isOp", "setOp");
		Properties.register("hasPlayedBefore", OfflinePlayer.class, "hasPlayedBefore");

        DataProvider.registerTransformer(OfflinePlayer.class,"getPlayer");
	}
}
