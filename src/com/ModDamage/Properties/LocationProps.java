package com.ModDamage.Properties;

import com.ModDamage.EventInfo.Properties;
import org.bukkit.Location;

public class LocationProps
{
	public static void register()
	{
        Properties.register("x", Location.class, "getBlockX");
        Properties.register("y", Location.class, "getBlockY");
        Properties.register("z", Location.class, "getBlockZ");
        Properties.register("yaw", Location.class, "getYaw");
        Properties.register("pitch", Location.class, "getPitch");

        Properties.register("block", Location.class, "getBlock");
        Properties.register("world", Location.class, "getWorld");
	}
}