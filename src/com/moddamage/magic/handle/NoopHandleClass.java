package com.moddamage.magic.handle;

import org.bukkit.entity.Entity;

public class NoopHandleClass implements IMagicHandleClass
{
	@Override
	public Class<?> getHandleClass(Entity entity)
	{
		return null;
	}
}
