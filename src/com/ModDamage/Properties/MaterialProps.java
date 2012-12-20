package com.ModDamage.Properties;

import com.ModDamage.Parsing.Property.Properties;

import org.bukkit.Material;

public class MaterialProps
{
    public static void register()
    {
        Properties.register("isSolid", Material.class, "isSolid");
        Properties.register("isRecord", Material.class, "isRecord");
    }
}
