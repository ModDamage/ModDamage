package com.moddamage.magic.groundblock;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;

public class NoopGroundBlock implements IMagicGroundBlock
{
	@Override
	public Block getGroundBlock(Arrow arrow)
	{
		return null;
	}

}
