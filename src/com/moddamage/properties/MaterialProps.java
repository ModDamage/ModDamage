package com.moddamage.properties;

import com.moddamage.parsing.property.Properties;

import org.bukkit.Material;

public class MaterialProps
{
    public static void register()
    {
        Properties.register("isSolid", Material.class, "isSolid");
        Properties.register("isRecord", Material.class, "isRecord");
    }
}
