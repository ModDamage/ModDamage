package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;

import org.bukkit.entity.Slime;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class SlimeSetSize extends EffectCalculation<Slime, Integer>
{
	final boolean forAttacker;
	public SlimeSetSize(boolean forAttacker, List<ModDamageCalculation> calculations)
	{
		super(calculations);
		this.forAttacker = forAttacker;
	}
	
	public SlimeSetSize(boolean forAttacker, int power)
	{
		super(power);
		this.forAttacker = forAttacker;
	}

	@Override
	void applyEffect(Slime affectedObject, Integer input){ affectedObject.setSize(input);}
	@Override
	protected Integer calculateInputValue(DamageEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventDamage, temp2;
		eventInfo.eventDamage = 0;
		doCalculations(eventInfo);
		temp2 = eventInfo.eventDamage;
		eventInfo.eventDamage = temp1;
		return temp2;
	}

	@Override
	protected Integer calculateInputValue(SpawnEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventHealth, temp2;
		eventInfo.eventHealth = 0;
		doCalculations(eventInfo);
		temp2 = eventInfo.eventHealth;
		eventInfo.eventHealth = temp1;
		return temp2;
	}

	@Override
	protected Slime getAffectedObject(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.element_attacker:eventInfo.element_target).equals(DamageElement.MOB_SLIME)?((Slime)(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target)):null;}

	@Override
	protected Slime getAffectedObject(SpawnEventInfo eventInfo){ return (eventInfo.element.equals(DamageElement.MOB_SLIME))?((Slime)eventInfo.entity):null;}	
}
