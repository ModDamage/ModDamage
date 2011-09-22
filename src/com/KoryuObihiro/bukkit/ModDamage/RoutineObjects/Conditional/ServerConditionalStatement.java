package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import org.bukkit.Server;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalStatement;


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
