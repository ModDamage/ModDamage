package com.ModDamage.Properties;

import com.ModDamage.Parsing.Property.Properties;

import org.bukkit.Location;

public class LocationProps
{
	public static void register()
	{
        Properties.register("x", Location.class, "getX");
        Properties.register("y", Location.class, "getY");
        Properties.register("z", Location.class, "getZ");
        Properties.register("yaw", Location.class, "getYaw");
        Properties.register("pitch", Location.class, "getPitch");

        Properties.register("block", Location.class, "getBlock");
        Properties.register("world", Location.class, "getWorld");
	}
}