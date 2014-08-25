package com.moddamage.magic.groundblock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;

import com.moddamage.magic.MagicStuff;

public class CBGroundBlock implements IMagicGroundBlock
{
	final Method CraftArrow_getHandle;
	
	final Field NMSEntityArrow_inGround;
	final Field NMSEntityArrow_x;
	final Field NMSEntityArrow_y;
	final Field NMSEntityArrow_z;
	
	public CBGroundBlock()
	{
		Class<?> CraftArrow = MagicStuff.safeClassForName(MagicStuff.obc + ".entity.CraftArrow");
		CraftArrow_getHandle = MagicStuff.safeGetMethod(CraftArrow, "getHandle");
		
		Class<?> NMSEntityArrow = MagicStuff.safeClassForName(MagicStuff.nms + ".EntityArrow");
		NMSEntityArrow_inGround = MagicStuff.safeGetField(NMSEntityArrow, "inGround");
		NMSEntityArrow_x = MagicStuff.safeGetField(NMSEntityArrow, "d");
		NMSEntityArrow_y = MagicStuff.safeGetField(NMSEntityArrow, "e");
		NMSEntityArrow_z = MagicStuff.safeGetField(NMSEntityArrow, "f");
	}

	@Override
	public Block getGroundBlock(Arrow arrow)
	{
		try
		{
			Object handle = CraftArrow_getHandle.invoke(arrow);
			
			boolean inGround = NMSEntityArrow_inGround.getBoolean(handle);
			if (!inGround) return null;
			
			int x = NMSEntityArrow_x.getInt(handle);
			int y = NMSEntityArrow_y.getInt(handle);
			int z = NMSEntityArrow_z.getInt(handle);
			
			return arrow.getWorld().getBlockAt(x, y, z);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
