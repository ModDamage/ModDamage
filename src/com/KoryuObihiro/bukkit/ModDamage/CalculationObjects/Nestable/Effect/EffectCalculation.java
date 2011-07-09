package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.NestableCalculation;

abstract public class EffectCalculation<AffectedClass, InputType> extends NestableCalculation 
{
	final InputType value;
	final boolean useCalculations;
	final AffectedClass affectedObject;
	final boolean useEventObject;
	EffectCalculation(AffectedClass affectedObject, List<ModDamageCalculation> calculations)
	{
		super(calculations);
		this.useCalculations = true;
		this.value = null;
		this.affectedObject = affectedObject;
		this.useEventObject = true;
	}
	
	EffectCalculation(AffectedClass affectedObject, InputType value)
	{
		super(null);
		this.useCalculations = false;
		this.value = value;
		this.affectedObject = affectedObject;
		this.useEventObject = true;
	}
	
	EffectCalculation(InputType value)
	{
		super(null);
		this.useCalculations = false;
		this.value = value;
		this.affectedObject = null;
		this.useEventObject = false;
	}
	
	EffectCalculation(List<ModDamageCalculation> calculations)
	{
		super(calculations);
		this.useCalculations = true;
		this.value = null;
		this.affectedObject = null;
		this.useEventObject = false;
	}
	
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{
		AffectedClass someObject = (useEventObject?getAffectedObject(eventInfo):affectedObject);
		if(someObject != null)
			applyEffect(someObject, (useCalculations?calculateInputValue(eventInfo):value));
	}


	@Override
	public void calculate(SpawnEventInfo eventInfo)
	{
		AffectedClass someObject = (useEventObject?getAffectedObject(eventInfo):affectedObject);
		if(someObject != null)
			applyEffect(someObject, (useCalculations?calculateInputValue(eventInfo):value));
	}


	abstract void applyEffect(AffectedClass affectedObject, InputType input);

	abstract protected AffectedClass getAffectedObject(DamageEventInfo eventInfo);
	abstract protected AffectedClass getAffectedObject(SpawnEventInfo eventInfo);
	
	abstract protected InputType calculateInputValue(DamageEventInfo eventInfo);
	abstract protected InputType calculateInputValue(SpawnEventInfo eventInfo);
}
