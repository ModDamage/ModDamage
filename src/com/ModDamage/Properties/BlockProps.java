package com.ModDamage.Properties;

import com.ModDamage.Parsing.Property.Properties;

import org.bukkit.block.Block;

public class BlockProps
{
	public static void register()
	{
		Properties.register("power", Block.class, "getBlockPower");
		Properties.register("light", Block.class, "getLightLevel");
		Properties.register("blocklight", Block.class, "getLightFromBlocks");
		Properties.register("skylight", Block.class, "getLightFromSky");
		Properties.register("typeid", Block.class, "getTypeId", "setTypeId");
		Properties.register("data", Block.class, "getData", "setData");

        Properties.register("type",	Block.class, "getType");
	}
}