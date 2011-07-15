package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Conditional;

import org.bukkit.Server;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;


public abstract class ServerConditionalStatement<T> extends ConditionalStatement
{
	final T value;
	public ServerConditionalStatement(boolean inverted, T value) 
	{
		super(inverted);
		this.value = value;
	}

	Server server = ModDamage.server;
}
