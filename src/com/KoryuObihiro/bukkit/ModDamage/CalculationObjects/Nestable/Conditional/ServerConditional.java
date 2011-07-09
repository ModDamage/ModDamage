package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import org.bukkit.Server;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;


public abstract class ServerConditional extends ConditionalCalculation
{
	public ServerConditional(boolean inverted, List<ModDamageCalculation> calculations) 
	{
		super(inverted, calculations);
	}

	Server server = ModDamage.server;
}

//XXX This feature will not be implemented until 0.9.5
