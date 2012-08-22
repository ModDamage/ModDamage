package com.ModDamage.Variables.Int;

import org.bukkit.block.Block;

import com.ModDamage.EventInfo.Var;

public class BlockProps
{
//	@Var static Integer power(Block block)
//	{
//		return block.getBlockPower();
//	}
//
//	@Var static Integer light(Block block)
//	{
//		return (int) block.getLightLevel();
//	}
//
//	@Var static Integer blocklight(Block block)
//	{
//		return (int) block.getLightFromBlocks();
//	}
//
//	@Var static Integer skylight(Block block)
//	{
//		return (int) block.getLightFromSky();
//	}

	@Var static Integer type(Block block)
	{
		return block.getTypeId();
	}

//	@Var static Integer data(Block block)
//	{
//		return (int) block.getData();
//	}
}