package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.NestableCalculation;

abstract public class EffectCalculation<AffectedClass> extends NestableCalculation 
{
	final AffectedClass affectedObject;
	final boolean useEventObject;
	EffectCalculation(AffectedClass affectedObject, List<ModDamageCalculation> calculations)
	{
		super(calculations);
		this.affectedObject = affectedObject;
		this.useEventObject = true;
	}	
	EffectCalculation(List<ModDamageCalculation> calculations)
	{
		super(calculations);
		this.affectedObject = null;
		this.useEventObject = false;
	}
	
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{
		AffectedClass someObject = (useEventObject?getAffectedObject(eventInfo):affectedObject);
		if(someObject != null)
			applyEffect(someObject, calculateInputValue(eventInfo));
	}

	@Override
	public void calculate(SpawnEventInfo eventInfo)
	{
		AffectedClass someObject = (useEventObject?getAffectedObject(eventInfo):affectedObject);
		if(someObject != null)
			applyEffect(someObject, calculateInputValue(eventInfo));
	}

	abstract void applyEffect(AffectedClass affectedObject, int input);

	abstract protected AffectedClass getAffectedObject(DamageEventInfo eventInfo);
	abstract protected AffectedClass getAffectedObject(SpawnEventInfo eventInfo);
	
	protected int calculateInputValue(DamageEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventDamage, temp2;
		eventInfo.eventDamage = 0;
		doCalculations(eventInfo);
		temp2 = eventInfo.eventDamage;
		eventInfo.eventDamage = temp1;
		return temp2;
	}

	protected int calculateInputValue(SpawnEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventHealth, temp2;
		eventInfo.eventHealth = 0;
		doCalculations(eventInfo);
		temp2 = eventInfo.eventHealth;
		eventInfo.eventHealth = temp1;
		return temp2;
	}
}
