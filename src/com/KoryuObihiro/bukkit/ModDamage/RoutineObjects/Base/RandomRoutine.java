package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.Random;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract class Chanceroutine extends Routine
{
	protected Chanceroutine(String configString){ super(configString);}
	int chance;
	final Random random = new Random();
}